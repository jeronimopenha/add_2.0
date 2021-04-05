package add.dataflow.async;

import add.dataflow.base.AddGenericRtlibObject;
import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;
import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;

/**
 * "AsyncIn" component for the UFV asynchronous data flow simulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncIn extends AddGenericRtlibObject {

    private Label stringLabel;
    private Label lblName;

    private String componentType;
    private String s;

    private PortStdLogic1164 portClk;

    private PortStdLogicVector portDout0;
    private PortStdLogic1164 portAckR0;
    private PortStdLogic1164 portReqR0;

    private int[] vectorIn;
    private int[] vectorConf;
    private int idxDin, idxConf;

    private long maskData;

    private fsmStates fsmOut0;

    protected enum fsmStates {
        IDLE, WRITE, DONE;
    }

    /**
     * Enumerator for the processing control protocol.
     *
     */
    protected enum dataCtrl {
        STOP(0), VALID(1), DONE(2);
        private final byte value;

        dataCtrl(int valor) {
            this.value = (byte) valor;
        }

        /**
         * @return the value.
         */
        public int getValue() {
            return (int) value;
        }
    }

    /**
     * Object Constructor.
     *
     */
    public AsyncIn() {
        super();
        this.idxDin = 0;
        this.idxConf = 0;
        this.fsmOut0 = fsmStates.IDLE;

        this.vectorConf = new int[1];
        this.vectorConf[0] = 0x00000201;//Sends the value 2 for the component with ID 1 by default.

        this.vectorIn = new int[20];
        for (int i = 2; i < this.vectorIn.length; i++) {
            this.vectorIn[i] = (int) (Math.random() * 10);
        }

        setCompName("IN");
        setWidth(16);
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortAckR0(new PortStdLogic1164(this, "ackr0", Port.IN, null));
        setPortReqR0(new PortStdLogic1164(this, "reqr0", Port.OUT, null));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));

        ports = new Port[4];

        ports[0] = getPortClk();
        ports[1] = getPortAckR0();
        ports[2] = getPortReqR0();
        ports[3] = getPortDout0();
    }

    /**
     * Method responsible for initiate the masks for data and control execution.
     *
     */
    public void initMasks() {
        setMaskData(((long) 1 << getWidth()) - 1);
    }

    /**
     * Method responsible for updating the text displayed by the component.
     *
     * @param s Text to be updated.
     */
    public void setString(String s) {
        setS(s);
        getStringLabel().setText(s);
        getLblName().setText(getName());
        getSymbol().painter.paint(getSymbol(), 100);
    }

    /**
     * Method responsible for updating the component symbol.
     *
     * @param s Symbol to be set.
     */
    @Override
    public void setSymbol(Symbol s) {
        this.symbol = s;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.AsyncIn this case it clears the text displayed by the component.
     *
     */
    public void reset(double time) {
        Signal signalDout0;
        Signal signalReqRight0;

        //para portDout0
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }

        //para portReqR0
        if ((signalReqRight0 = getPortReqR0().getSignal()) != null) {
            StdLogic1164 nextReqR0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
        }
        setFsmOut0(fsmStates.IDLE);
        setIdxDin(0);
        setIdxConf(0);
        setS("NULL");
        setString(getS());
        userReset(time);
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    public void userReset(double time) {
    }

    /**
     * Method responsible for changing the label that displays the name of the
     * component.
     *
     * @param l String to be showed.
     */
    public void setCompName(String l) {
        if (l.equals("")) {
            setComponentType(".");
        } else {
            setComponentType(l);
        }
    }

    /**
     * Method responsible for merging the data to be sent with the processing
     * control on the same bus.
     *
     * @param data Data to be merged.
     * @param ctrl Process control to be merged.
     * @return Data to be sent over the bus comprises the data and control
     */
    public long createDataOut(long data, dataCtrl ctrl) {
        long dataOut;
        dataOut = ((long) data & getMaskData()) | ((long) ctrl.getValue() << (getWidth()));
        return dataOut;
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. AsyncIn this case, it will
     * be checked whether the ports are connected. It Will pass the vector data
     * to the output.
     *
     * @param arg an arbitrary object argument.
     */
    @Override
    public void evaluate(Object arg) {

        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDout0, signalReqRight0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqR0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckR0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogic1164 nextReqR0;
        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                //Tratamento da saída
                //Máquina de saída
                signalDout0 = getPortDout0().getSignal();
                signalReqRight0 = getPortReqR0().getSignal();
                StdLogic1164 valueAckRight = getPortAckR0().getValueOrU();
                switch (getFsmOut0()) {
                    case IDLE:
                        if (getIdxDin() < getVectorIn().length) {
                            setFsmOut0(fsmStates.WRITE);

                            dOut0.setValue(createDataOut(getVectorIn()[getIdxDin()], dataCtrl.VALID));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                            nextReqR0 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
                            setString(Integer.toString(getVectorIn()[getIdxDin()]));

                            setIdxDin(getIdxDin() + 1);
                        } else {
                            setFsmOut0(fsmStates.DONE);

                            dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                            nextReqR0 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
                            setString(dataCtrl.DONE.toString());
                        }
                        break;
                    case WRITE:
                        if (valueAckRight.is_1()) {
                            setFsmOut0(fsmStates.IDLE);

                            nextReqR0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
                        }
                        break;
                    case DONE:
                        if (valueAckRight.is_1()) {
                            setFsmOut0(fsmStates.DONE);

                            nextReqR0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
                        }
                        break;
                }
            }
        }
    }

    /**
     * Method responsible for opening the settings window for the component.
     *
     */
    @Override
    public void configure() {
        String[] fields = {"Instance name:", "name",
            "Number of bits [1, 2, 4, 8, 16 or 32]:", "width",
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name, bus width and group number.");
        propertySheet.setVisible(true);
    }

    /**
     * Method responsible for changing bus size according to user settings.
     *
     * @param s String that brings the size of the bus chosen by the user.
     */
    @Override
    public void setWidth(String s
    ) {
        int n;
        try {
            if (isConnected() && !(Integer.parseInt(s) == getWidth())) {
                JOptionPane.showMessageDialog(null, "In.setWidth: The component bus size can not be changed "
                        + "\nwhile it is connected to another component.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            n = Integer.parseInt(s);
            switch (n) {
                case 1:
                case 2:
                case 4:
                case 8:
                case 16:
                case 32:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "In.setWidth: illegal argument\nusing a width of 16 bits instead!",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    n = 16; // default width
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "In.setWidth: illegal argument" + e
                    + "\nusing a width of 16 bits instead!", "Error", JOptionPane.ERROR_MESSAGE);
            n = 16; // default width
        }
        setWidth(n);
    }

    /**
     * Method responsible for changing bus size according to user settings.
     *
     * @param _n Integer that brings the size of the bus chosen by the user
     */
    @Override
    public void setWidth(int _n) {
        if (isConnected()) {
            JOptionPane.showMessageDialog(null, "Cannot change the width of an connected -Tool object!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if ((getWidth() < 1) || (getWidth() > 32)) {
            JOptionPane.showMessageDialog(null, "Bus width out of range [1..32], using 16 instead!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.n_bits = 16 + 2;
        } else {
            this.n_bits = _n + 2;
        }
        constructStandardValues();
        constructPorts();
        updateSymbol();
        initMasks();
    }

    /**
     * Returns the size of the data bus.
     *
     * @return Size of the data bus.
     */
    @Override
    public int getWidth() {
        return this.n_bits - 2;
    }

    /**
     * Returns the size of the data bus plus the two control bits.
     *
     * @return Size of the data bus plus the two control bits
     */
    public int getRealWidth() {
        return this.n_bits;
    }

    /**
     *
     * @param n - The n_bits to set
     */
    public void setRealWidth(int n) {
        this.n_bits = n;
    }

    /**
     * @param afuId String that brings the afuId to set
     */
    public void setAfuId(String afuId) {
        int n;
        try {
            n = Integer.parseInt(afuId);
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "In.setAfuId: illegal argument" + e
                    + "\nusing '0' for afuId instead!", "Error", JOptionPane.ERROR_MESSAGE);
            n = 0; // default width
        }
        setAfuId(n);
    }

    /**
     * Method responsible for indicating to the simulator that the component's
     * symbol will be constructed dynamically by the constructDynamicSymbol()
     * method, or will be read from a file of the same name as the ".sym"
     * extension.
     *
     * @return TRUE means that the symbol will be built dynamically.
     */
    @Override
    public boolean needsDynamicSymbol() {
        return true;
    }

    /**
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        this.symbol = new Symbol();
        this.symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 -900 1800 2400");
        this.symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 2400");
        this.symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 2400 " + getPortClk().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + getPortReqR0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1200 " + getPortAckR0().getName());
        this.symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1800 " + getPortDout0().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -600 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 1200 2 " + getComponentType());
        this.symbol.addMember(lblComponentType);

        setStringLabel(new Label());
        getStringLabel().initialize("0 -200 " + getS());
        this.symbol.addMember(getStringLabel());
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
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(18);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setAfuId(Integer.parseInt(st.nextToken()));
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
     * @return the stringLabel
     */
    public Label getStringLabel() {
        return stringLabel;
    }

    /**
     * @param stringLabel the stringLabel to set
     */
    public void setStringLabel(Label stringLabel) {
        this.stringLabel = stringLabel;
    }

    /**
     * @return the lblName
     */
    public Label getLblName() {
        return lblName;
    }

    /**
     * @param lblName the lblName to set
     */
    public void setLblName(Label lblName) {
        this.lblName = lblName;
    }

    /**
     * @return the componentType
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * @param componentType the componentType to set
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    /**
     * @return the s
     */
    public String getS() {
        return s;
    }

    /**
     * @param s the s to set
     */
    public void setS(String s) {
        this.s = s;
    }

    /**
     * @return the portClk
     */
    public PortStdLogic1164 getPortClk() {
        return portClk;
    }

    /**
     * @param portClk the portClk to set
     */
    public void setPortClk(PortStdLogic1164 portClk) {
        this.portClk = portClk;
    }

    /**
     * @return the portDout0
     */
    public PortStdLogicVector getPortDout0() {
        return portDout0;
    }

    /**
     * @param portDout0 the portDout0 to set
     */
    public void setPortDout0(PortStdLogicVector portDout0) {
        this.portDout0 = portDout0;
    }

    /**
     * @return the portAckR0
     */
    public PortStdLogic1164 getPortAckR0() {
        return portAckR0;
    }

    /**
     * @param portAckR0 the portAckR0 to set
     */
    public void setPortAckR0(PortStdLogic1164 portAckR0) {
        this.portAckR0 = portAckR0;
    }

    /**
     * @return the portReqR0
     */
    public PortStdLogic1164 getPortReqR0() {
        return portReqR0;
    }

    /**
     * @param portReqR0 the portReqR0 to set
     */
    public void setPortReqR0(PortStdLogic1164 portReqR0) {
        this.portReqR0 = portReqR0;
    }

    /**
     * @return the vectorIn
     */
    public int[] getVectorIn() {
        return vectorIn;
    }

    /**
     * @param vectorIn the vectorIn to set
     */
    public void setVectorIn(int[] vectorIn) {
        this.vectorIn = vectorIn;
    }

    /**
     * @return the vectorConf
     */
    public int[] getVectorConf() {
        return vectorConf;
    }

    /**
     * @param vectorConf the vectorConf to set
     */
    public void setVectorConf(int[] vectorConf) {
        this.vectorConf = vectorConf;
    }

    /**
     * @return the idxDin
     */
    public int getIdxDin() {
        return idxDin;
    }

    /**
     * @param idxDin the idxDin to set
     */
    public void setIdxDin(int idxDin) {
        this.idxDin = idxDin;
    }

    /**
     * @return the idxConf
     */
    public int getIdxConf() {
        return idxConf;
    }

    /**
     * @param idxConf the idxConf to set
     */
    public void setIdxConf(int idxConf) {
        this.idxConf = idxConf;
    }

    /**
     * @return the maskData
     */
    public long getMaskData() {
        return maskData;
    }

    /**
     * @param maskData the maskData to set
     */
    public void setMaskData(long maskData) {
        this.maskData = maskData;
    }

    /**
     * @return the fsmOut0
     */
    public fsmStates getFsmOut0() {
        return fsmOut0;
    }

    /**
     * @param fsmOut0 the fsmOut0 to set
     */
    public void setFsmOut0(fsmStates fsmOut0) {
        this.fsmOut0 = fsmOut0;
    }
}
