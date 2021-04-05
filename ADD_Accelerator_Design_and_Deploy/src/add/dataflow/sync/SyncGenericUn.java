package add.dataflow.sync;

import add.dataflow.base.AddGenericRtlibObject;
import add.dataflow.base.AddSimObject;
import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent;
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
 * SyncGenericUn component for the UFV synchronous data flow simulator.<br>
 * The component creates the basis for other components with one input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncGenericUn extends AddGenericRtlibObject {

    private Label stringLabel;
    private Label lblName;

    private String componentType;
    private String s;

    private PortStdLogic1164 portClk;

    private PortStdLogicVector portDin0;
    private PortStdLogicVector portDout0;

    private long maskData;

    /**
     * Enumerator for the processing control protocol.
     *
     */
    protected enum dataCtrl {
        STOP(0), VALID(1), DONE(2), OPT(3);
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
    public SyncGenericUn() {
        super();
        setCompName("GEN_UN");
        setWidth(16);
    }

    /**
     * copy(): This function is used to create a clone of this RTLIB object,
     * including the values for width (n_bits), current value (vector),
     * propagation delay, and version ID.
     * <p>
     */
    @Override
    public AddSimObject copy() {
        SyncGenericUn tmp = null;
        try {
            tmp = this.getClass().newInstance();
            tmp.setEditor(this.getEditor());
            tmp.setVisible(this.isVisible());
            tmp.setName(this.getName());
            tmp.setClassLoader(this.getClassLoader());
            tmp.setWidth(this.n_bits - 2);
            tmp.setDelay(this.getDelay());
            tmp.setVersionId(this.getVersionId());
            tmp.setStringLabel(this.getStringLabel());
            tmp.setLblName(this.getLblName());
            tmp.setComponentType(this.getComponentType());
            tmp.setS(this.getS());
            tmp.setAfuId(this.getAfuId());
            tmp.setStart(this.isStart());
            return (AddSimObject) tmp;
        } catch (IllegalAccessException | InstantiationException e) {
            message("-E- Internal error in SyncGenericUn.copy(): " + e);
            jfig.utils.ExceptionTracer.trace(e);
            return null;
        }
    }

    public void setParam(String s) {

        constructStandardValues();
        constructPorts();
        updateSymbol();
        initMasks();
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));

        ports = new Port[3];
        ports[0] = getPortClk();
        ports[1] = getPortDin0();
        ports[2] = getPortDout0();
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
    public long compute(long data) {
        setString(Integer.toString((int) data));
        return data;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     *
     * @param time
     */
    public void reset(double time) {
        Signal signalDout0;
        //para portDout0
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }

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
            case 3:
                return dataCtrl.OPT;
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
    public long getData(long d) {
        return signalExtensor((long) (d & getMaskData()));
    }

    /**
     * Method responsible for correcting the number sign.
     *
     * @param data Given that it has the signal to be corrected if necessary.
     * @return Given with the correct signal.
     */
    public long signalExtensor(long data) {
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

        Signal signalDin0, signalDout0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                signalDin0 = getPortDin0().getSignal();
                signalDout0 = getPortDout0().getSignal();
                StdLogicVector dIn = (StdLogicVector) signalDin0.getValue();

                switch (getCtrl(dIn.getValue())) {
                    case DONE:
                        dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                        setString("DONE");
                        break;
                    case STOP:
                        dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                        setString("STOP");
                        break;
                    case VALID:

                        if (getName().equals("i51")) {
                            int a = 1;
                        }

                        long result = compute(getData(dIn.getValue()));

                        dOut0.setValue(createDataOut(result, dataCtrl.VALID));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
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
                JOptionPane.showMessageDialog(null, "GenericUn.setWidth: The component bus size can not be changed "
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
                    JOptionPane.showMessageDialog(null, "GenericUn.setWidth: illegal argument!",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    n = this.n_bits - 2; // default width
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "GenericUn.setWidth: illegal argument" + e,
                     "Error", JOptionPane.ERROR_MESSAGE);
            n = this.n_bits - 2; // default width
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
        if (isConnected() && _n != getWidth()) {
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
        bbr.initialize("0 -900 1800 1200");
        this.symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1200");
        this.symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 1200 " + getPortClk().getName());
        this.symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 600 " + getPortDout0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -600 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 600 2 " + getComponentType());
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
