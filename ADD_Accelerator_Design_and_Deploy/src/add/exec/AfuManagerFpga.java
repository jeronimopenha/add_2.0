package add.exec;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JOptionPane;

public class AfuManagerFpga extends TimerTask {

    private final int AFU_INF_SIZE = 96;

    SerialPort serialPort;

    private int rate;
    private int[] afuInfo = new int[AFU_INF_SIZE];

    private int numInputBuffers;
    private int numOutputBuffers;
    private int numAFUs;
    private int runningMask;

    private OutputStream outputBuffer;
    private InputStream inputBuffer;

    private String portCOM;

    private Timer supervisor;

    private LinkedBlockingQueue dataQueue;

    private boolean hwInfoBusy = true;
    private boolean debug = false;
    private boolean commitedWorkspace;
    private boolean flagDoneAll;

    private Map<Integer, AFUFpga> AFUs = new TreeMap<>();

    private ArrayList<Integer> listAfuId = new ArrayList<>();

    public boolean dsm[];

    public AfuManagerFpga(String portCOM) {
        commitedWorkspace = false;
        this.initialize(portCOM);
        readInfoHwAfu();
        createWorkspace();
        createAFUs();
    }

    public AfuManagerFpga(String portCOM, boolean debug) {
        commitedWorkspace = false;
        this.debug = debug;
        this.initialize(portCOM);
        readInfoHwAfu();
        createWorkspace();
        createAFUs();
    }

    /**
     * Médoto que verifica se a comunicação com a porta serial está ok
     */
    private void initialize(String portCOM) {
        this.dataQueue = new LinkedBlockingQueue();
        this.supervisor = new Timer();
        this.supervisor.schedule(this, 1000, 10);

        this.portCOM = portCOM;
        this.rate = 57600;//115200;
        try {
            //Define uma variável portId do tipo CommPortIdentifier para realizar a comunicação serial
            CommPortIdentifier portId = null;
            try {
                //Tenta verificar se a porta COM informada existe
                portId = CommPortIdentifier.getPortIdentifier(this.portCOM);
            } catch (NoSuchPortException npe) {
                //Caso a porta COM não exista será exibido um erro 
                JOptionPane.showMessageDialog(null, "COM Port Not Found.",
                        "COM Porta", JOptionPane.PLAIN_MESSAGE);
            }
            //Abre a porta COM 
            serialPort = (SerialPort) portId.open("Serial communication", this.rate);
            outputBuffer = serialPort.getOutputStream();
            inputBuffer = serialPort.getInputStream();
            serialPort.setSerialPortParams(this.rate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_2,
                    SerialPort.PARITY_NONE);

            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent spe) {
                    int newData = 0;

                    switch (spe.getEventType()) {
                        case SerialPortEvent.BI:
                        case SerialPortEvent.OE:
                        case SerialPortEvent.FE:
                        case SerialPortEvent.PE:
                        case SerialPortEvent.CD:
                        case SerialPortEvent.CTS:
                        case SerialPortEvent.DSR:
                        case SerialPortEvent.RI:
                        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                            break;
                        case SerialPortEvent.DATA_AVAILABLE:
                            while (newData != -1) {
                                try {
                                    dataQueue.add(inputBuffer.read());
                                } catch (IOException ioe) {
                                    System.out.println("Serial read error: " + ioe);
                                }
                            }
                            break;
                    }
                }
            });
            serialPort.notifyOnDataAvailable(true);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        } catch (PortInUseException | UnsupportedCommOperationException | HeadlessException | IOException | TooManyListenersException e) {
        }
    }

    private void readInfoHwAfu() {
        getHwInfo();
    }

    public void waitDataQueueNotEmpty(LinkedBlockingQueue dataQueue) {
        while (dataQueue.isEmpty()) {
            try {
                Thread.currentThread().sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    private void setDataOutBuffers(int data, int mdata) {
        int idxOutIni = 0, afuid = 0;

        //descobrir a AFU e idxini para o outputbuffer
        for (int i = 0; i < numAFUs; i++) {
            idxOutIni += afuInfo[(i * 3 + 2)];
            if (idxOutIni > mdata) {
                afuid = i;
                idxOutIni = idxOutIni - afuInfo[(i * 3 + 2)];
                break;
            }
        }
        int nBits = afuInfo[afuid];
        AFUFpga afu = AFUs.get(afuid);
        int[] buffer = afu.outputBuffers.get(mdata - idxOutIni);

        //inserir os dados de acordo com a qtde de bits de cada afu
        switch (nBits) {
            case 32:
                if (afu.idxBuffersOut[mdata - idxOutIni] < buffer.length) {
                    buffer[afu.idxBuffersOut[mdata - idxOutIni]] = data;
                    afu.idxBuffersOut[mdata - idxOutIni]++;
                }
                break;
        }
        //System.out.print(data + " ");
    }

    private void sendDataInputBuffers(int mdata) {

        int idxInIni = 0, afuid = 0;

        //descobrir a AFU e idxini para o outputbuffer
        for (int i = 0; i < numAFUs; i++) {
            idxInIni += afuInfo[(i * 3 + 1)];
            if (idxInIni > mdata) {
                afuid = i;
                idxInIni = idxInIni - afuInfo[(i * 3 + 1)];
                break;
            }
        }
        int nBits = afuInfo[afuid];
        AFUFpga afu = AFUs.get(afuid);
        int[] buffer = afu.inputBuffers.get(mdata - idxInIni);

        switch (nBits) {
            case 32:
                for (int i = 0; i < 4; i++) {
                    int dataToSend = (buffer[afu.idxBuffersIn[mdata - idxInIni]] >> (8 * i) & 0xff);
                    sendData((byte) dataToSend);
                }
                afu.idxBuffersIn[mdata - idxInIni]++;
                break;
        }
    }

    //class TimerTaskHanler extends TimerTask {
    @Override
    public void run() {
        if (!dataQueue.isEmpty()) {
            int protocolValue;
            protocolValue = (int) dataQueue.poll();

            switch (protocolValue) {
                case 1: // HW_INFO
                    waitDataQueueNotEmpty(dataQueue);
                    numAFUs = (int) dataQueue.poll();
                    int counter = 0;
                    afuInfo = new int[(numAFUs * 3)];
                    dsm = new boolean[(numAFUs * 3)];

                    while (counter < numAFUs * 3) {
                        waitDataQueueNotEmpty(dataQueue);
                        afuInfo[counter] = (int) dataQueue.poll();
                        counter++;
                    }

                    hwInfoBusy = false;

                    /*
                        * depuração
                     */
                    if (debug) {
                        System.out.println("qtde_AFUS = " + numAFUs);
                        System.out.println("");
                        for (int i = 0; i < numAFUs * 3; i = i + 3) {
                            System.out.println("AFU" + i / 3);
                            System.out.println("qtde_bits = " + afuInfo[i]);
                            System.out.println("qtde_in = " + afuInfo[i + 1]);
                            System.out.println("qtde_out = " + afuInfo[i + 2]);
                            System.out.println();
                        }
                    }
                    /*
                        * depuração
                     */
                    break;
                case 4: // RECEIVED DATA
                    waitDataQueueNotEmpty(dataQueue);
                    int mdata = (int) dataQueue.poll();
                    int data = 0;

                    if (debug) {
                        System.out.println();
                        System.out.println("data received");
                        System.out.println("mdata = " + mdata);
                        System.out.printf("data = ");
                    }
                    for (int i = 0; i < 4; i++) {
                        waitDataQueueNotEmpty(dataQueue);
                        data = data | (((int) dataQueue.poll()) << (i * 8));
                    }
                    if (debug) {
                        System.out.print(data);
                        System.out.println();
                    }

                    setDataOutBuffers(data, mdata);
                    break;
                case 5: // REQUEST / SEND DATA
                    waitDataQueueNotEmpty(dataQueue);
                    int req_mdata = (int) dataQueue.poll();
                    if (debug) {
                        System.out.println();
                        System.out.println("data request");
                        System.out.println("mdata = " + req_mdata);
                        System.out.println("Sending DATA = " + 0 + "  MDATA = " + req_mdata);
                    }
                    sendData((byte) 4);
                    sendData((byte) req_mdata);
                    sendDataInputBuffers(req_mdata);
                    break;
                case 8: //DSM
                    int tamDsm = getNumInputBuffers() + getNumOutputBuffers() + getNumAFUs();
                    boolean[] dsm = new boolean[tamDsm];
                    int idxDsm = 0;

                    for (int i = 0; i < dsm.length; i++) {
                        dsm[i] = false;
                    }
                    for (int i = 0; i < (int) Math.ceil(tamDsm / 8.0); i++) {
                        waitDataQueueNotEmpty(dataQueue);
                        int dsmWord = (int) dataQueue.poll();
                        //System.out.print(Integer.toBinaryString(dsmWord) + " ");
                        if (debug) {
                            System.out.println(Integer.toBinaryString(dsmWord));
                        }
                        for (int j = 0; j < 8; j++) {
                            if (j < dsm.length) {
                                dsm[idxDsm] = (dsmWord & 1) == 1;
                                dsmWord >>= 1;
                                idxDsm++;
                            } else {
                                break;
                            }
                        }
                    }
                    //System.out.println("");
                    int idxAfu = 0;
                    idxDsm = 0;

                    while (idxAfu < numAFUs) {
                        AFUFpga afu = AFUs.get(idxAfu);
                        afu.afuDone = dsm[idxDsm];
                        idxDsm++;
                        for (int i = 0; i < afuInfo[idxAfu * 3 + 1]; i++) {
                            afu.rdDone[i] = dsm[idxDsm];
                            idxDsm++;
                        }
                        for (int i = 0; i < afuInfo[idxAfu * 3 + 2]; i++) {
                            afu.wrDone[i] = dsm[idxDsm];
                            idxDsm++;
                        }
                        idxAfu++;
                    }
                    break;
                default://em caso de erro
                    System.out.println(Integer.toBinaryString((int) protocolValue));
            }
        }
    }
    //}

    /**
     * Método que fecha a comunicação com a porta serial
     */
    public synchronized void close() {
        try {
            if (this.serialPort != null) {
                this.serialPort.removeEventListener();
                this.serialPort.close();
            }
            inputBuffer.close();
            outputBuffer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "COM port could not be closed.",
                    "Close COM port", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * @param data - Valor a ser enviado pela porta serial
     */
    public void sendData(byte data) {
        try {
            outputBuffer.flush();
            outputBuffer.write(data);//escreve o valor na porta serial para ser enviado
            Thread.sleep(2);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ",
                    "Enviar dados", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void getHwInfo() {
        sendData((byte) 1);
        this.hwInfoBusy = true;
        while (hwInfoBusy == true) {
            try {
                Thread.currentThread().sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    public void resetAfus(int resetMask) {
        sendData((byte) 2);
        for (int i = 0; i < 4; i++) {
            sendData((byte) ((resetMask >> (i * 8)) & 0xff));
        }
        sendData((byte) 2);
        for (int i = 0; i < 4; i++) {
            sendData((byte) 0);
        }
    }

    public void startAFUs(int startMask) {
        resetAfus(startMask);
        runningMask = startMask;
        if (!workspaceIsCommited()) {
            commitWorkspace();
        }
        sendData((byte) 3);
        for (int i = 0; i < 4; i++) {
            sendData((byte) ((startMask >> (i * 8)) & 0xff));
        }
    }

    public void stopAFUs(int stopMask) {
        int stopMaskInv = ~stopMask;
        sendData((byte) 3);
        for (int i = 0; i < 4; i++) {
            sendData((byte) ((stopMaskInv >> (i * 8)) & 0xff));
        }
    }

    public void sendInConfiguration(int conf, int id) {
        sendData((byte) 6);
        sendData((byte) id);
        for (int i = 0; i < 4; i++) {
            sendData((byte) ((conf >> (i * 8)) & 0xff));
        }
    }

    public void sendOutConfiguration(int conf, int id) {
        sendData((byte) 7);
        sendData((byte) id);
        for (int i = 0; i < 4; i++) {
            sendData((byte) ((conf >> (i * 8)) & 0xff));
        }
    }

    public AFUFpga getAFU(int id) {
        if (id < getNumAFUs()) {
            return AFUs.get(id);
        } else {
            JOptionPane.showMessageDialog(null, "AFU ID invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void simFreeBuffer(int numBytes) {
        setCommitedWorkspace(false);
    }

    public void createAFUs() {
        getAFUs().clear();
        setNumInputBuffers(0);
        setNumOutputBuffers(0);

        for (int i = 0; i < numAFUs; i++) {
            int afuNumBits = afuInfo[i * 3];
            int afuNumInputBuffers = afuInfo[i * 3 + 1];
            int afuNumOutputBuffers = afuInfo[(i * 3) + 2];
            setNumInputBuffers(getNumInputBuffers() + afuNumInputBuffers);
            setNumOutputBuffers(getNumOutputBuffers() + afuNumOutputBuffers);

            AFUFpga afu = new AFUFpga(this, i, afuNumBits, afuNumInputBuffers, afuNumOutputBuffers);

            for (int j = 0; j < numAFUs; j++) {
                afu.idxBuffersIn = new int[afuInfo[j * 3 + 1]];
                afu.idxBuffersOut = new int[afuInfo[j * 3 + 2]];
            }

            getAFUs().put(i, afu);
        }
    }

    public void clear() {

    }

    public void commitWorkspace() {
        AFUFpga afu;
        int idxIn = 0, idxOut = 0;
        for (int i = 0; i < numAFUs; i++) {
            int qtdeIn, qtdeOut;
            afu = getAFU(i);
            qtdeIn = afu.getNumInputBuffer();
            for (int j = 0; j < qtdeIn; j++) {
                sendInConfiguration(afu.getInputBuffer(j).length / (32 / afu.getNumBits()), idxIn);
                idxIn++;
            }
            qtdeOut = afu.getNumOutputBuffer();
            for (int j = 0; j < qtdeOut; j++) {
                sendOutConfiguration(afu.getOutputBuffer(j).length / (32 / afu.getNumBits()), idxOut);
                idxOut++;
            }
        }
    }

    public void waitAllDone(long timeWaitMax) {
        boolean flagDone = false;

        while (!flagDone && timeWaitMax > 0) {
            flagDone = true;
            for (int i = 0; i < 32; i++) {
                if (((runningMask >> i) & 0x1) == 1) {
                    if (!dsm[i * 3 + 2]) {
                        flagDone = false;
                    }
                }
            }
        }
        stopAFUs(0);
        setDoneAll(flagDone);
    }

    public boolean isDoneAll() {
        return this.flagDoneAll;
    }

    public void setDoneAll(boolean doneAll) {
        boolean[] afuDoneInfo = new boolean[AFU_INF_SIZE];

        this.flagDoneAll = doneAll;

        Set<Integer> keys = AFUs.keySet();
        for (Integer key : keys) {
            AFUFpga afu = AFUs.get(key);
            afu.setDone(dsm[afu.getId() * 3 + 2]);
        }
    }

    /**
     * @return the commitedWorkspace
     */
    public boolean workspaceIsCommited() {
        return commitedWorkspace;
    }

    public boolean workspaceIscommited() {
        return false;

    }

    void printWorkspace() {

    }

    void printDSM() {

    }

    void printStatics() {

    }

    void printInfoAFUManager() {

    }

    public int getNumClConf() {
        //nothing to do here in simulator
        return 0;

    }

    public int getNumClDSM() {
        //nothing to do here in simulator
        return 0;

    }

    public boolean AFUIsSimulated() {
        //nothing to do here in simulator
        return true;
    }

    public int/*uint32_t*/ readCSR(int regID) {
        //nothing to do here in simulator
        return 0;
    }

    public void createWorkspace() {
        //nothing to do here in simulator
    }

    public void updateWorkspace() {
        //nothing to do here in simulator
    }

    public void writeCSR(/*uint32_t*/int regID, /*uint32_t*/ int val) {
        //nothing to do here in simulator
    }

    /**
     * @return the numInputBuffers
     */
    public int getNumInputBuffers() {
        return numInputBuffers;
    }

    /**
     * @param numInputBuffers the numInputBuffers to set
     */
    public void setNumInputBuffers(int numInputBuffers) {
        this.numInputBuffers = numInputBuffers;
    }

    /**
     * @return the numOutputBuffers
     */
    public int getNumOutputBuffers() {
        return numOutputBuffers;
    }

    /**
     * @param numOutputBuffers the numOutputBuffers to set
     */
    public void setNumOutputBuffers(int numOutputBuffers) {
        this.numOutputBuffers = numOutputBuffers;
    }

    /**
     * @return the numAFUs
     */
    public int getNumAFUs() {
        return numAFUs;
    }

    /**
     * @param numAFUs the numAFUs to set
     */
    public void setNumAFUs(int numAFUs) {
        this.numAFUs = numAFUs;
    }

    /**
     * @param commitedWorkspace the commitedWorkspace to set
     */
    public void setCommitedWorkspace(boolean commitedWorkspace) {
        this.commitedWorkspace = commitedWorkspace;
    }

    /**
     * @return the AFUs
     */
    public Map<Integer, AFUFpga> getAFUs() {
        return AFUs;
    }

    /**
     * @param AFUs the AFUs to set
     */
    public void setAFUs(Map<Integer, AFUFpga> AFUs) {
        this.AFUs = AFUs;
    }

    /**
     * @return the listAfuId
     */
    private ArrayList<Integer> getListAfuId() {
        return listAfuId;
    }
}
