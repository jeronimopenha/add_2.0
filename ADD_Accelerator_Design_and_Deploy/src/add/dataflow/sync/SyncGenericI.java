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
 * SyncGenericI component for the UFV synchronous data flow simulator.<br>
 * The component creates the basis for other components with an input and that
 * perform the computation with an immediate.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncGenericI extends AddGenericRtlibObject {

    private Label strLblId;

    //32 bits for configuration where the least significant 24 is the VALUE 
    //and the 8 most significant is the device ID.
    private Label strLblImmediate;
    private Label lblName;

    private String componentType;

    private PortStdLogic1164 portClk;

    private PortStdLogicVector portDin0;
    private PortStdLogicVector portDout0;

    private int id;
    private long immediate;

    private long maskData;

    /**
     * copy(): This function is used to create a clone of this RTLIB object,
     * including the values for width (n_bits), current value (vector),
     * propagation delay, and version ID.
     * <p>
     */
    @Override
    public AddSimObject copy() {
        SyncGenericI tmp = null;
        try {
            tmp = this.getClass().newInstance();
            tmp.setEditor(this.getEditor());
            tmp.setVisible(this.isVisible());
            tmp.setName(this.getName());
            tmp.setClassLoader(this.getClassLoader());
            tmp.setWidth(this.n_bits - 2);
            tmp.setDelay(this.getDelay());
            tmp.setVersionId(this.getVersionId());
            tmp.setStrLblId(this.getStrLblId());
            tmp.setStrLblImmediate(this.getStrLblImmediate());
            tmp.setLblName(this.getLblName());
            tmp.setComponentType(this.getComponentType());
            tmp.setAfuId(this.getAfuId());
            tmp.setStart(this.isStart());
            return (AddSimObject) tmp;
        } catch (IllegalAccessException | InstantiationException e) {
            message("-E- Internal error in SyncGenericI.copy(): " + e);
            jfig.utils.ExceptionTracer.trace(e);
            return null;
        }
    }

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
    public SyncGenericI() {
        super();
        this.id = 1;
        this.immediate = id;
        setCompName("GEN_I");
        setWidth(16);
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
     */
    public void setString() {
        getStrLblId().setText("ID=" + Integer.toString(getId()));
        getStrLblImmediate().setText("IM=" + Integer.toString((int) getImmediate()));

        getLblName().setText(getName());
        getSymbol().painter.paint(getSymbol(), 100);
    }

    /**
     * Method responsible for updating the component symbol.
     *
     * @param s - Symbol passed automatically.
     */
    @Override
    public void setSymbol(Symbol s) {
        symbol = s;
    }

    /**
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data Value to be used for the computation.
     * @param immediate Immediate.
     * @return - Return of computation
     */
    public long compute(long data, long immediate) {
        setString();
        return data;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     */
    public void reset(double time) {
        Signal signalDout0;
        //para portDout0
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }
        setString();
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
     * @param l - String to be set in component name.
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
        try {
            int control = (int) (d >> getWidth());
            switch (control) {
                case 0:
                    return dataCtrl.STOP;
                case 1:
                    return dataCtrl.VALID;
                case 2:
                    return dataCtrl.DONE;
                default:
                    return dataCtrl.OPT;
            }
        } catch (Exception e) {
            return dataCtrl.OPT;
        }
    }

    /**
     * Method responsible for extracting the input bus data signal.
     *
     * @param data Input Bus Content
     * @return The data value
     */
    public long getData(long data) {
        return signalExtensor((long) (data & getMaskData()));
    }

    /**
     * Method responsible for correcting the number sign.
     *
     * @param data Given that it has the signal to be corrected if necessary.
     * @return Given with the correct signal.
     */
    public long signalExtensor(long data) {
        if ((data >> (getWidth() - 1)) == 1) {
            data = data | (long) ~getMaskData();
        }
        return data;
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method. It will execute the reset() methods if their respective
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

                        setString();
                        break;
                    case STOP:
                        dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                        setString();
                        break;
                    case VALID:

                        long result = compute(getData(dIn.getValue()), getData((long) getImmediate()));

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
            "Component's index (except 0):", "id",
            "Component's immediate", "immediate",
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name, bus width, component's id and immediate.");
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
                JOptionPane.showMessageDialog(null, "GenericI.setWidth: The component bus size can not be changed "
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
                    JOptionPane.showMessageDialog(null, "GenericI.setWidth: illegal argument!",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    n = this.n_bits - 2; // default width
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "GenericI.setWidth: illegal argument" + e,
                    "Warning", JOptionPane.ERROR_MESSAGE);
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

    public void setNbits(int n) {
        this.n_bits = n;
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
        setWidth(n - 2);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param s the id to set
     */
    public void setId(String s) {
        int n;
        try {
            n = Integer.parseInt(s);
            if (n == 0) {
                JOptionPane.showMessageDialog(null, "GenericI.setId: illegal argument\nusing an id 1 instead!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                n = 1; // default width
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "GenericI.setId: illegal argument" + e
                    + "\nusing an id 1 instead!", "Error", JOptionPane.ERROR_MESSAGE);

            n = 1; // default width
        }
        setId(n);
        setString();
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the immediate
     */
    public long getImmediate() {
        return immediate;
    }

    /**
     * @param s the immediate to set
     */
    public void setImmediate(String s) {
        long n;
        try {
            n = Long.parseLong(s);
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "GenericI.setImmediate: illegal argument" + e
                    + "\nusing an immediate = id value!", "Error", JOptionPane.ERROR_MESSAGE);
            n = getId(); // default width
        }
        setImmediate(n);
        setString();
    }

    /**
     * @param immediate the immediate to set
     */
    public void setImmediate(long immediate) {
        this.immediate = immediate;
    }

    /**
     * Method responsible for indicating to the simulator that the component's
     * symbol will be constructed dynamically by the constructDynamicSymbol()
     * method, or will be read from a file of the same name as the ".sym"
     * extension.
     *
     * @return - TRUE means that the symbol will be made dynamically.
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
        symbol = new Symbol();
        symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 -1300 1800 1200");
        symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1200");
        symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 1200 " + getPortClk().getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin0().getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 600 " + getPortDout0().getName());
        symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -1000 " + getName());
        symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 600 2 " + getComponentType());
        symbol.addMember(lblComponentType);

        setStrLblId(new Label());
        getStrLblId().initialize("0 -600 ID=" + Integer.toString(getId()));
        symbol.addMember(getStrLblId());

        setStrLblImmediate(new Label());
        getStrLblImmediate().initialize("0 -200 IM=" + Integer.toString((int) getImmediate()));
        symbol.addMember(getStrLblImmediate());

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
                + " " + getId()
                + " " + getImmediate()
                + " " + getAfuId());
    }

    /**
     * Method responsible for reading the component settings in the file saved
     * by the simulator.
     *
     * @param s - Settings for the component read from the file saved by the
     * simulator.
     * @return - Returns true if the settings are read successfully.
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
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(18);
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 6:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setAfuId(st.nextToken());
                    constructStandardValues();
                    constructPorts();
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
     * @return the strLblId
     */
    public Label getStrLblId() {
        return strLblId;
    }

    /**
     * @param strLblId the strLblId to set
     */
    public void setStrLblId(Label strLblId) {
        this.strLblId = strLblId;
    }

    /**
     * @return the strLblImmediate
     */
    public Label getStrLblImmediate() {
        return strLblImmediate;
    }

    /**
     * @param strLblImmediate the strLblImmediate to set
     */
    public void setStrLblImmediate(Label strLblImmediate) {
        this.strLblImmediate = strLblImmediate;
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
