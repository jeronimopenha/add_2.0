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
 * AsyncCpData component for the UFV asynchronous data flow simulator.<br>
 * The purpose of the component is to replicate the input value to its two
 * outputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncCpData extends AddGenericRtlibObject {

    private Label stringLabel;
    private Label lblName;

    private String componentType;
    private String s;

    private PortStdLogic1164 portClk;
    private PortStdLogic1164 portReqLeft0;
    private PortStdLogic1164 portAckLeft0;
    private PortStdLogic1164 portReqRight0;
    private PortStdLogic1164 portReqRight1;
    private PortStdLogic1164 portAckRight0;
    private PortStdLogic1164 portAckRight1;

    private PortStdLogicVector portDin0;
    private PortStdLogicVector portDout0;
    private PortStdLogicVector portDout1;

    private int dataIn0;

    private dataCtrl controlIn0;

    private boolean dataReadyIn0;
    private boolean dataReadyOut0;
    private boolean dataReadyOut1;

    private fsmStates fsmIn0;
    private fsmStates fsmOut0;
    private fsmStates fsmOut1;

    private long maskData;

    protected enum fsmStates {
        IDLE, READ, WRITE;
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
     */
    public AsyncCpData() {
        super();

        this.dataReadyIn0 = false;
        this.dataReadyOut0 = false;
        this.dataReadyOut1 = false;
        this.fsmIn0 = fsmStates.IDLE;
        this.fsmOut0 = fsmStates.IDLE;
        this.fsmOut1 = fsmStates.IDLE;
        setCompName("CP_DATA");
        setWidth(16);
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortReqLeft0(new PortStdLogic1164(this, "reqleft0", Port.IN, null));
        setPortReqRight0(new PortStdLogic1164(this, "reqright0", Port.OUT, null));
        setPortReqRight1(new PortStdLogic1164(this, "reqright1", Port.OUT, null));
        setPortAckLeft0(new PortStdLogic1164(this, "ackleft0", Port.OUT, null));
        setPortAckRight0(new PortStdLogic1164(this, "ackright0", Port.IN, null));
        setPortAckRight1(new PortStdLogic1164(this, "ackright1", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));
        setPortDout1(new PortStdLogicVector(this, "dout1", Port.OUT, null, getRealWidth()));

        ports = new Port[10];
        ports[0] = getPortClk();
        ports[1] = getPortReqLeft0();
        ports[2] = getPortReqRight0();
        ports[3] = getPortReqRight1();
        ports[4] = getPortAckLeft0();
        ports[5] = getPortAckRight0();
        ports[6] = getPortAckRight1();
        ports[7] = getPortDin0();
        ports[8] = getPortDout0();
        ports[9] = getPortDout1();
    }

    /**
     * Method responsible for initiate the masks for data and control execution.
     *
     */
    public void initMasks() {
        for (long i = 0; i < getWidth(); i++) {
            setMaskData(getMaskData() | ((long) 1 << i));
        }
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
     * Method responsible for the computation of the output.
     *
     * @param data Value to be used for the computation related to input.
     * @return Return of computation
     */
    public int compute(int data) {
        setString(Integer.toString(data));
        return data;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     *
     * @param time
     */
    @Override
    public void reset(double time) {
        Signal signalDout0;
        Signal signalDout1;
        Signal signalAckLeft0;
        Signal signalReqRight0;
        Signal signalReqRight1;

        //para portDout0
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }

        //para portDout1
        if ((signalDout1 = getPortDout1().getSignal()) != null) {
            StdLogicVector dOut1 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));
        }

        //para portAckLeft0
        if ((signalAckLeft0 = getPortAckLeft0().getSignal()) != null) {
            StdLogic1164 nextAckL0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckLeft0()));
        }

        //para portReqRight0
        if ((signalReqRight0 = getPortReqRight0().getSignal()) != null) {
            StdLogic1164 nextReqR0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));
        }

        //para portReqRight1
        if ((signalReqRight1 = getPortReqRight1().getSignal()) != null) {
            StdLogic1164 nextReqR1 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqRight1()));
        }
        setDataReadyIn0(false);
        setDataReadyOut0(false);
        setDataReadyOut1(false);
        setDataIn0(0);
        setFsmIn0(fsmStates.IDLE);
        setFsmOut0(fsmStates.IDLE);
        setFsmOut1(fsmStates.IDLE);
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
     * Method responsible for extracting the input bus control signal.
     *
     * @param d Input Bus Content
     * @return The control value
     */
    public dataCtrl getCtrl(long d) {
        int control = (int) (d >> getWidth());
        switch (control) {
            case 0:
                return dataCtrl.STOP;
            case 1:
                return dataCtrl.VALID;
            case 2:
                return dataCtrl.DONE;
            default:
                return dataCtrl.STOP;
        }
    }

    /**
     * Method responsible for extracting the input bus data signal.
     *
     * @param d Input Bus Content
     * @return The data value
     */
    public int getData(long d) {
        return signalExtensor((int) (d & getMaskData()));
    }

    /**
     * Method responsible for correcting the number sign.
     *
     * @param data Given that it has the signal to be corrected if necessary.
     * @return Given with the correct signal.
     */
    public int signalExtensor(int data) {
        if ((data >> (getWidth() - 1)) == 1) {
            data = data | (int) ~getMaskData();
        }
        return data;
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method. It will execute the reset() method if their respective
     * entries order it. It will update the output with the compute(int data)
     * method result.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDin0, signalDout0, signalDout1, signalAckLeft0;
        Signal signalReqRight0, signalReqRight1;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqRight1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckRight1().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogic1164 nextAckL0;
        StdLogic1164 nextReqR0;
        StdLogic1164 nextReqR1;
        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());
        StdLogicVector dOut1 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqLeft0().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckRight0().getValueOrU();
                StdLogic1164 valueAckRight1 = getPortAckRight1().getValueOrU();

                //Tratamento da saída
                //Máquina de saída 1
                signalDout0 = getPortDout0().getSignal();
                signalReqRight0 = getPortReqRight0().getSignal();
                switch (getFsmOut0()) {
                    case IDLE:
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
                                case STOP:
                                    dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setFsmOut0(fsmStates.WRITE);

                                    setString("STOP");
                                    break;
                                case VALID:
                                    dOut0.setValue(createDataOut(getDataIn0(), dataCtrl.VALID));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setDataReadyOut0(!isDataReadyOut0());

                                    setFsmOut0(fsmStates.WRITE);

                                    setString(Integer.toString(getDataIn0()));
                                    break;
                            }
                        }
                        break;
                    case WRITE:
                        if (valueAckRight0.is_1()) {
                            setFsmOut0(fsmStates.IDLE);

                            nextReqR0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));
                        }
                        break;
                }

                //Máquina de saída 2
                signalDout1 = getPortDout1().getSignal();
                signalReqRight1 = getPortReqRight1().getSignal();
                switch (getFsmOut1()) {
                    case IDLE:
                        if (isDataReadyIn0() != isDataReadyOut1() && valueAckRight1.is_0()) {

                            switch (getControlIn0()) {
                                case DONE:
                                    setFsmOut1(fsmStates.WRITE);

                                    dOut1.setValue(createDataOut(0, dataCtrl.DONE));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                                    nextReqR1 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqRight1()));

                                    setString("DONE");
                                    break;
                                case STOP:
                                    setFsmOut1(fsmStates.WRITE);

                                    dOut1.setValue(createDataOut(0, dataCtrl.STOP));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                                    nextReqR1 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqRight1()));

                                    setString("STOP");
                                    break;
                                case VALID:
                                    setFsmOut1(fsmStates.WRITE);

                                    dOut1.setValue(createDataOut(getDataIn0(), dataCtrl.VALID));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                                    nextReqR1 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqRight1()));

                                    setDataReadyOut1(!isDataReadyOut1());

                                    setString(Integer.toString(getDataIn0()));

                                    break;
                            }
                        }
                        break;
                    case WRITE:
                        if (valueAckRight1.is_1()) {
                            setFsmOut1(fsmStates.IDLE);

                            nextReqR1 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqRight1()));
                        }
                        break;
                }
                //********************************************************************************* 

                //Tratamento das entradas
                //Entrada 1
                signalAckLeft0 = getPortAckLeft0().getSignal();
                switch (getFsmIn0()) {
                    case IDLE:
                        if (isDataReadyIn0() == isDataReadyOut0() && isDataReadyIn0() == isDataReadyOut1() && valueReqLeft0.is_1()) {
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
        propertySheet.setHelpText("Specify instance name and bus width.");
        propertySheet.setVisible(true);
    }

    /**
     * Method responsible for changing bus size according to user settings.
     *
     * @param s String that brings the size of the bus chosen by the user.
     */
    @Override
    public void setWidth(String s) {
        int n;
        try {
            n = Integer.parseInt(s);
            if (isConnected() && n != getWidth()) {
                JOptionPane.showMessageDialog(null, "CpData.setWidth: The component bus size can not be changed "
                        + "\nwhile it is connected to another component.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            switch (n) {
                case 1:
                case 2:
                case 4:
                case 8:
                case 16:
                case 32:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "CpData.setWidth: illegal argument\nusing a width of 16 bits instead!",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    n = 16; // default width
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "CpData.setWidth: illegal argument" + e
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
            JOptionPane.showMessageDialog(null, "Cannot change the width of an connected ADD-Tool object!",
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
        bbr.initialize("0 -900 1800 4200");
        this.symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 4200");
        this.symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 4200 " + getPortClk().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 600 " + getPortReqLeft0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 1200 " + getPortAckLeft0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + getPortReqRight0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1200 " + getPortAckRight0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 2400 " + getPortReqRight1().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 3000 " + getPortAckRight1().getName());
        this.symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1800 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1800 " + getPortDout0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 3600 " + getPortDout1().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -600 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 2100 2 " + getComponentType());
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
     * @return the portReqLeft0
     */
    public PortStdLogic1164 getPortReqLeft0() {
        return portReqLeft0;
    }

    /**
     * @param portReqLeft0 the portReqLeft0 to set
     */
    public void setPortReqLeft0(PortStdLogic1164 portReqLeft0) {
        this.portReqLeft0 = portReqLeft0;
    }

    /**
     * @return the portAckLeft0
     */
    public PortStdLogic1164 getPortAckLeft0() {
        return portAckLeft0;
    }

    /**
     * @param portAckLeft0 the portAckLeft0 to set
     */
    public void setPortAckLeft0(PortStdLogic1164 portAckLeft0) {
        this.portAckLeft0 = portAckLeft0;
    }

    /**
     * @return the portReqRight0
     */
    public PortStdLogic1164 getPortReqRight0() {
        return portReqRight0;
    }

    /**
     * @param portReqRight0 the portReqRight0 to set
     */
    public void setPortReqRight0(PortStdLogic1164 portReqRight0) {
        this.portReqRight0 = portReqRight0;
    }

    /**
     * @return the portReqRight1
     */
    public PortStdLogic1164 getPortReqRight1() {
        return portReqRight1;
    }

    /**
     * @param portReqRight1 the portReqRight1 to set
     */
    public void setPortReqRight1(PortStdLogic1164 portReqRight1) {
        this.portReqRight1 = portReqRight1;
    }

    /**
     * @return the portAckRight0
     */
    public PortStdLogic1164 getPortAckRight0() {
        return portAckRight0;
    }

    /**
     * @param portAckRight0 the portAckRight0 to set
     */
    public void setPortAckRight0(PortStdLogic1164 portAckRight0) {
        this.portAckRight0 = portAckRight0;
    }

    /**
     * @return the portAckRight1
     */
    public PortStdLogic1164 getPortAckRight1() {
        return portAckRight1;
    }

    /**
     * @param portAckRight1 the portAckRight1 to set
     */
    public void setPortAckRight1(PortStdLogic1164 portAckRight1) {
        this.portAckRight1 = portAckRight1;
    }

    /**
     * @return the portDin0
     */
    public PortStdLogicVector getPortDin0() {
        return portDin0;
    }

    /**
     * @param portDin0 the portDin0 to set
     */
    public void setPortDin0(PortStdLogicVector portDin0) {
        this.portDin0 = portDin0;
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
     * @return the portDout1
     */
    public PortStdLogicVector getPortDout1() {
        return portDout1;
    }

    /**
     * @param portDout1 the portDout1 to set
     */
    public void setPortDout1(PortStdLogicVector portDout1) {
        this.portDout1 = portDout1;
    }

    /**
     * @return the dataIn0
     */
    public int getDataIn0() {
        return dataIn0;
    }

    /**
     * @param dataIn0 the dataIn0 to set
     */
    public void setDataIn0(int dataIn0) {
        this.dataIn0 = dataIn0;
    }

    /**
     * @return the controlIn0
     */
    public dataCtrl getControlIn0() {
        return controlIn0;
    }

    /**
     * @param controlIn0 the controlIn0 to set
     */
    public void setControlIn0(dataCtrl controlIn0) {
        this.controlIn0 = controlIn0;
    }

    /**
     * @return the dataReadyIn0
     */
    public boolean isDataReadyIn0() {
        return dataReadyIn0;
    }

    /**
     * @param dataReadyIn0 the dataReadyIn0 to set
     */
    public void setDataReadyIn0(boolean dataReadyIn0) {
        this.dataReadyIn0 = dataReadyIn0;
    }

    /**
     * @return the dataReadyOut0
     */
    public boolean isDataReadyOut0() {
        return dataReadyOut0;
    }

    /**
     * @param dataReadyOut0 the dataReadyOut0 to set
     */
    public void setDataReadyOut0(boolean dataReadyOut0) {
        this.dataReadyOut0 = dataReadyOut0;
    }

    /**
     * @return the dataReadyOut1
     */
    public boolean isDataReadyOut1() {
        return dataReadyOut1;
    }

    /**
     * @param dataReadyOut1 the dataReadyOut1 to set
     */
    public void setDataReadyOut1(boolean dataReadyOut1) {
        this.dataReadyOut1 = dataReadyOut1;
    }

    /**
     * @return the fsmIn0
     */
    public fsmStates getFsmIn0() {
        return fsmIn0;
    }

    /**
     * @param fsmIn0 the fsmIn0 to set
     */
    public void setFsmIn0(fsmStates fsmIn0) {
        this.fsmIn0 = fsmIn0;
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

    /**
     * @return the fsmOut1
     */
    public fsmStates getFsmOut1() {
        return fsmOut1;
    }

    /**
     * @param fsmOut1 the fsmOut1 to set
     */
    public void setFsmOut1(fsmStates fsmOut1) {
        this.fsmOut1 = fsmOut1;
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
}
