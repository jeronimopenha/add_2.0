/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package add.dataflow.async;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;
import hades.utils.StringTokenizer;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;

/**
 *
 * @author hector
 */
public class AsyncGrn extends AsyncGenericUn {

    private int nodes;
    private int neededWords;
    private int counterInWords;
    private int atractorTransient;
    private int atractorPeriod;

    private boolean[] s0;
    private boolean[] s1;

    private boolean firstProcessFinished;

    public AsyncGrn() {
        super();
        setCompName("GRN");
        setWidth(32);
        nodes = 1;
        neededWords = (int) Math.ceil((double) nodes / 32);
        counterInWords = 0;
        atractorTransient = 0;
        atractorPeriod = 0;
        firstProcessFinished = false;
    }

    @Override
    public int compute(int data) {
        return super.compute(data); //To change body of generated methods, choose Tools | Templates.
    }

    public void Pass(boolean vet[]) {
        boolean[] aux = new boolean[vet.length];

        for (int i = 0; i < aux.length; i++) {
            aux[i] = vet[i];
        }
        vet[0] = aux[41];
        vet[1] = aux[21];
        vet[2] = aux[41] && !aux[33];
        vet[3] = aux[36] && !(aux[37] || aux[7]);
        vet[4] = ((aux[51] || aux[34]) && aux[37]) && !aux[3];
        vet[5] = !(aux[21] && aux[1]);
        vet[6] = (aux[49] || aux[32]) && !(aux[34] || aux[37]);
        vet[7] = (aux[8] || aux[9]) && !aux[22];
        vet[8] = aux[18] && !(aux[11] || aux[33]);
        vet[9] = aux[14] && !(aux[22] || aux[33]);
        vet[10] = aux[47] && !aux[48];
        vet[11] = aux[32];
        vet[12] = aux[43] && aux[53];
        vet[13] = (aux[5] || aux[49] || aux[27]) && !aux[21];
        vet[14] = aux[31];
        vet[15] = aux[35];
        vet[16] = aux[29];
        vet[17] = aux[65];
        vet[18] = aux[53] || aux[17];
        vet[19] = aux[16];
        vet[20] = aux[60];
        vet[21] = !(aux[15] || aux[3]);
        vet[22] = (aux[32] || aux[49]) && !aux[44];
        vet[23] = !aux[24];
        vet[24] = (aux[3] || (aux[43] && aux[53]));
        vet[25] = aux[20] && !aux[50];
        vet[26] = aux[2] || aux[30];
        vet[27] = ((aux[5] || aux[16]) && aux[26]) && !aux[21];
        vet[28] = (aux[34] && aux[3]) && !(aux[21] || aux[0]);
        vet[29] = aux[39] || aux[41];
        vet[30] = aux[10] || aux[52] || aux[53];
        vet[31] = (aux[4] || aux[51] || aux[10]) && !aux[6];
        vet[32] = !aux[23];
        vet[33] = (aux[34] || aux[45]) && !(aux[21] || aux[7]);
        vet[34] = (aux[38] || aux[26] || aux[0]) && !aux[28];
        vet[35] = aux[12];
        vet[36] = (aux[15] || aux[40]) && !aux[38];
        vet[37] = aux[10] && !aux[3];
        vet[38] = aux[34] && !(aux[32] || aux[27]);
        vet[39] = aux[10] || aux[40];
        vet[40] = aux[15] || aux[20];
        vet[41] = aux[53] && !aux[42];
        vet[42] = aux[32] || aux[49];
        vet[43] = aux[48];
        vet[44] = aux[31];
        vet[45] = aux[52] && !aux[27];
        vet[46] = aux[45] || aux[32];
        vet[47] = aux[34] || aux[18];
        vet[48] = aux[16] || aux[53];
        vet[49] = aux[25];
        vet[50] = aux[49];
        vet[51] = aux[8] && !aux[6];
        vet[52] = aux[58] && !aux[46];
        vet[53] = aux[55];
        vet[54] = (aux[63] || aux[66]) && !aux[60];
        vet[55] = aux[59];
        vet[56] = aux[61] && !(aux[64] || aux[58]);
        vet[57] = (aux[62] || aux[64]) && !(aux[63] || aux[58] || aux[61]);
        vet[58] = aux[54];
        vet[59] = (aux[64] || aux[67]) && !aux[63];
        vet[60] = aux[59] || aux[66] || aux[32];
        vet[61] = aux[66] || aux[56];
        vet[62] = aux[66] || aux[59];
        vet[63] = aux[54] || aux[56];
        vet[64] = aux[57] || aux[65];
        vet[65] = aux[64] && !aux[58];
        vet[66] = (aux[67] || aux[55]) && !aux[63];
        vet[67] = aux[32];
        vet[68] = (aux[19] && aux[13]) && !(aux[33] || aux[7]);
        vet[69] = aux[7];
    }

    @Override
    public void userReset(double time) {
        setCounterInWords(0);
        setFsmOut0(fsmStates.READ);
        setAtractorTransient(0);
        setAtractorPeriod(0);
        setFirstProcessFinished(false);
    }

    @Override
    public void evaluate(Object arg) {
        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDin0, signalDout0, signalAckLeft0, signalReqRight0;

        boolean isX = false;

        if (getPortClk().getSignal() == null) {
            isX = true;
        } else if (getPortDin0().getSignal() == null) {
            isX = true;
        } else if (getPortDout0().getSignal() == null) {
            isX = true;
        } else if (getPortReqLeft0().getSignal() == null) {
            isX = true;
        } else if (getPortReqRight0().getSignal() == null) {
            isX = true;
        } else if (getPortAckLeft0().getSignal() == null) {
            isX = true;
        } else if (getPortAckRight0().getSignal() == null) {
            isX = true;
        }

        StdLogic1164 nextAckL0;
        StdLogic1164 nextReqR0;
        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (isX) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();
            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqLeft0().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckRight0().getValueOrU();

                //Tratamento da saída
                //Máquina de saída
                signalDout0 = getPortDout0().getSignal();
                signalReqRight0 = getPortReqRight0().getSignal();

                switch (getFsmOut0()) {
                    case READ:
                        if (isDataReadyIn0() != isDataReadyOut0() && valueAckRight0.is_0()) {
                            switch (getControlIn0()) {
                                case DONE:
                                    dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setFsmOut0(fsmStates.WRITE);

                                    setString("DONE");
                                    break;
                                case VALID:
                                    int idx = 0;
                                    for (int i = getCounterInWords() * 32; i < (getCounterInWords() * 32) + 32; i++) {
                                        if (i < getS0().length) {
                                            if (((getDataIn0() >> idx) & 1) == 1) {
                                                this.s0[i] = true;
                                                this.s1[i] = true;
                                            } else {
                                                this.s0[i] = false;
                                                this.s1[i] = false;
                                            }
                                            idx++;
                                        } else {
                                            break;
                                        }
                                    }

                                    setString("READING");

                                    if (getCounterInWords() < getNeededWords() - 1) {
                                        setDataReadyOut0(!isDataReadyOut0());
                                        setCounterInWords(getCounterInWords() + 1);
                                    } else {
                                        setFsmOut0(fsmStates.PROCESS);
                                        Pass(s0);
                                        Pass(s1);
                                        Pass(s1);
                                    }
                                    break;
                            }
                        }
                        break;
                    case PROCESS:
                        setString("PROCESSING");

                        boolean equal = true;

                        for (int i = 0; i < s0.length; i++) {
                            if (s0[i] != s1[i]) {
                                equal = false;
                                break;
                            }
                        }

                        if (!equal && !isFirstProcessFinished()) {
                            setAtractorTransient(getAtractorTransient() + 1);
                            Pass(s0);
                            Pass(s1);
                            Pass(s1);

                        } else if (equal && !isFirstProcessFinished()) {
                            setFirstProcessFinished(true);
                            Pass(s0);
                        } else if (!equal && isFirstProcessFinished()) {
                            setAtractorPeriod(getAtractorPeriod() + 1);
                            Pass(s0);
                        } else {
                            int period, width, dataOut;
                            period = getAtractorPeriod() & 0xffff;
                            width = getAtractorTransient() & 0xffff;
                            dataOut = (period << 16) | width;

                            dOut0.setValue(createDataOut(dataOut, dataCtrl.VALID));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                            nextReqR0 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                            setFsmOut0(fsmStates.WRITE);

                            setDataReadyOut0(!isDataReadyOut0());

                            setString("VALID");

                            setCounterInWords(0);
                            setAtractorTransient(0);
                            setAtractorPeriod(0);
                            setFirstProcessFinished(false);
                        }

                        break;
                    case WRITE:
                        if (valueAckRight0.is_1()) {
                            setFsmOut0(fsmStates.READ);

                            nextReqR0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));
                        }
                        break;
                }
                //********************************************************************************* 

                //Tratamento das entradas
                //Entrada
                signalAckLeft0 = getPortAckLeft0().getSignal();
                switch (getFsmIn0()) {
                    case IDLE:
                        if (isDataReadyIn0() == isDataReadyOut0() && valueReqLeft0.is_1()) {
                            setFsmIn0(fsmStates.READ);
                            signalDin0 = getPortDin0().getSignal();
                            StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();

                            setDataIn0(getData(dIn0.getValue()));
                            setControlIn0(getCtrl(dIn0.getValue()));
                            setDataReadyIn0(!isDataReadyIn0());

                            nextAckL0 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckLeft0()));
                        }
                        break;
                    case READ:
                        if (!valueReqLeft0.is_1()) {
                            setFsmIn0(fsmStates.IDLE);

                            nextAckL0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckLeft0()));
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void configure() {
        String[] fields = {"Instance name:", "name",
            "Numbers of nodes:", "nodes",
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name and bus width.");
        propertySheet.setVisible(true);
    }

    /**
     * @return the nodes
     */
    public int getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(String nodes) {
        int n;
        try {
            n = Integer.parseInt(nodes);
            if (n < 0) {
                JOptionPane.showMessageDialog(null, "AsyncGrn.setNodes: illegal argument\nusing a standard value of 1 instead!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                n = 1;
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "AsyncGrn.setNodes: illegal argument" + e
                    + "\nusing a standard value of 1 instead!", "Error", JOptionPane.ERROR_MESSAGE);
            n = 1; // default width
        }
        setNodes(n);
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(int nodes) {
        this.nodes = nodes;
        setNeededWords((int) Math.ceil((double) nodes / 32));
        setS0(new boolean[nodes]);
        setS1(new boolean[nodes]);
    }

    /**
     * Method responsible for writing component settings to the file saved by
     * the simulator.
     *
     * @param ps -Simulator writing object.
     */
    @Override
    public void write(java.io.PrintWriter ps) {
        ps.print(" " + getVersionId()
                + " " + getRealWidth()
                + " " + getDelay()
                + " " + getNodes()
                + " " + getAfuId());
    }

    /**
     * Method responsible for reading the component settings in the file saved
     * by the simulator.
     *
     * @param s Settings for the component read from the file saved by the
     * simulator.
     * @return Returns true if the settings are read successfully.
     */
    @Override
    public boolean initialize(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int n_tokens = st.countTokens();
        try {
            switch (n_tokens) {
                case 0:
                    setVersionId(1001);
                    setRealWidth(18);
                    setNodes(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(18);
                    setNodes(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setNodes(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setNodes(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setNodes(Integer.parseInt(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setNodes(Integer.parseInt(st.nextToken()));
                    setAfuId(st.nextToken());
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                default:
                    throw new Exception("invalid number of arguments");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, toString() + ".initialize(): " + e + " " + s,
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @return the s0
     */
    public boolean[] getS0() {
        return s0;
    }

    /**
     * @param s0 the s0 to set
     */
    public void setS0(boolean[] s0) {
        this.s0 = s0;
    }

    /**
     * @return the s1
     */
    public boolean[] getS1() {
        return s1;
    }

    /**
     * @param s1 the s1 to set
     */
    public void setS1(boolean[] s1) {
        this.s1 = s1;
    }

    /**
     * @return the neededWords
     */
    public int getNeededWords() {
        return neededWords;
    }

    /**
     * @param neededWords the neededWords to set
     */
    public void setNeededWords(int neededWords) {
        this.neededWords = neededWords;
    }

    /**
     * @return the counterInWords
     */
    public int getCounterInWords() {
        return counterInWords;
    }

    /**
     * @param counterInWords the counterInWords to set
     */
    public void setCounterInWords(int counterInWords) {
        this.counterInWords = counterInWords;
    }

    /**
     * @return the atractorTransient
     */
    public int getAtractorTransient() {
        return atractorTransient;
    }

    /**
     * @param atractorTransient the atractorTransient to set
     */
    public void setAtractorTransient(int atractorTransient) {
        this.atractorTransient = atractorTransient;
    }

    /**
     * @return the firstProcessFinished
     */
    public boolean isFirstProcessFinished() {
        return firstProcessFinished;
    }

    /**
     * @param firstProcessFinished the firstProcessFinished to set
     */
    public void setFirstProcessFinished(boolean firstProcessFinished) {
        this.firstProcessFinished = firstProcessFinished;
    }

    /**
     * @return the atractorPeriod
     */
    public int getAtractorPeriod() {
        return atractorPeriod;
    }

    /**
     * @param atractorPeriod the atractorPeriod to set
     */
    public void setAtractorPeriod(int atractorPeriod) {
        this.atractorPeriod = atractorPeriod;
    }
}
