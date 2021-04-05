package add.dataflow.async;

import add.dataflow.base.AddGenericRtlibObject;
import hades.models.PortStdLogic1164;
import hades.models.StdLogic1164;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent1164;
import hades.symbols.BboxRectangle;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * AsyncCpBranch component for the UFV asynchronous data flow simulator.<br>
 * The purpose of the component is to replicate the input value to its two
 * outputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncCpBranch extends AddGenericRtlibObject {

    protected final int IF = 3;
    protected final int ELSE = 2;
    protected final int NOTHING = 0;

    private Label stringLabel;
    private Label lblName;

    private String componentType;
    private String s;

    private PortStdLogic1164 portClk;
    private PortStdLogic1164 portReqL0;
    private PortStdLogic1164 portAckL0;
    private PortStdLogic1164 portBIn0;
    private PortStdLogic1164 portReqR0;
    private PortStdLogic1164 portReqR1;
    private PortStdLogic1164 portAckR0;
    private PortStdLogic1164 portAckR1;
    private PortStdLogic1164 portBranchOut0;
    private PortStdLogic1164 portBranchOut1;

    private int branchIn;

    private boolean branchReadyIn0;
    private boolean branchReadyOut0;
    private boolean branchReadyOut1;

    private fsmStates fsmIn0;
    private fsmStates fsmOut0;
    private fsmStates fsmOut1;

    private long maskData;

    protected enum fsmStates {
        IDLE, READ, WRITE;
    }

    /**
     * Object Constructor.
     */
    public AsyncCpBranch() {
        super();

        this.branchReadyIn0 = false;
        this.branchReadyOut0 = false;
        this.branchReadyOut1 = false;
        this.fsmIn0 = fsmStates.IDLE;
        this.fsmOut0 = fsmStates.IDLE;
        this.fsmOut1 = fsmStates.IDLE;
        setCompName("CP_BR");
        setWidth(16);
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortReqL0(new PortStdLogic1164(this, "reqleft0", Port.IN, null));
        setPortReqR0(new PortStdLogic1164(this, "reqright0", Port.OUT, null));
        setPortReqR1(new PortStdLogic1164(this, "reqright1", Port.OUT, null));
        setPortAckL0(new PortStdLogic1164(this, "ackleft0", Port.OUT, null));
        setPortAckR0(new PortStdLogic1164(this, "ackright0", Port.IN, null));
        setPortAckR1(new PortStdLogic1164(this, "ackright1", Port.IN, null));
        setPortBIn0(new PortStdLogic1164(this, "branchin", Port.IN, null));
        setPortBranchOut0(new PortStdLogic1164(this, "branchout0", Port.OUT, null));
        setPortBranchOut1(new PortStdLogic1164(this, "branchout1", Port.OUT, null));

        ports = new Port[10];
        ports[0] = getPortClk();
        ports[1] = getPortReqL0();
        ports[2] = getPortReqR0();
        ports[3] = getPortReqR1();
        ports[4] = getPortAckL0();
        ports[5] = getPortAckR0();
        ports[6] = getPortAckR1();
        ports[7] = getPortBIn0();
        ports[8] = getPortBranchOut0();
        ports[9] = getPortBranchOut1();
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
     * level.In this case it clears the text displayed by the component.
     *
     */
    public void reset(double time) {
        Signal signalBranchOut0;
        Signal signalBranchOut1;
        Signal signalAckLeft0;
        Signal signalReqRight0;
        Signal signalReqRight1;

        //para portBranchOut0
        if ((signalBranchOut0 = getPortBranchOut0().getSignal()) != null) {
            StdLogic1164 branchOut0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalBranchOut0, time, branchOut0, getPortBranchOut0()));
        }

        //para portBranchOut1
        if ((signalBranchOut1 = getPortBranchOut1().getSignal()) != null) {
            StdLogic1164 branchOut1 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalBranchOut1, time, branchOut1, getPortBranchOut1()));
        }

        //para portAckL0
        if ((signalAckLeft0 = getPortAckL0().getSignal()) != null) {
            StdLogic1164 nextAckL0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckL0()));
        }

        //para portReqR0
        if ((signalReqRight0 = getPortReqR0().getSignal()) != null) {
            StdLogic1164 nextReqR0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
        }

        //para portReqR1
        if ((signalReqRight1 = getPortReqR1().getSignal()) != null) {
            StdLogic1164 nextReqR1 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqR1()));
        }
        setBranchReadyIn0(false);
        setBranchReadyOut0(false);
        setBranchReadyOut1(false);
        setBranchIn(0);
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

        Signal signalBranchIn0, signalBranchOut0, signalBranchOut1, signalAckLeft0;
        Signal signalReqRight0, signalReqRight1;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBIn0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBranchOut0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBranchOut1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqL0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqR0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqR1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckL0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckR0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckR1().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogic1164 nextAckL0;
        StdLogic1164 nextReqR0;
        StdLogic1164 nextReqR1;
        StdLogic1164 branchOut0;
        StdLogic1164 branchOut1;

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqL0().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckR0().getValueOrU();
                StdLogic1164 valueAckRight1 = getPortAckR1().getValueOrU();

                //Tratamento da saída
                //Máquina de saída 1
                signalBranchOut0 = getPortBranchOut0().getSignal();
                signalReqRight0 = getPortReqR0().getSignal();
                switch (getFsmOut0()) {
                    case IDLE:
                        if (isBranchReadyIn0() != isBranchReadyOut0() && valueAckRight0.is_0()) {
                            setFsmOut0(fsmStates.WRITE);

                            branchOut0 = new StdLogic1164(getBranchIn());
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalBranchOut0, time, branchOut0, getPortBranchOut0()));

                            nextReqR0 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));

                            setBranchReadyOut0(!isBranchReadyOut0());

                            setString(Integer.toString(getBranchIn()));
                        }
                        break;
                    case WRITE:
                        if (valueAckRight0.is_1()) {
                            setFsmOut0(fsmStates.IDLE);

                            nextReqR0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqR0()));
                        }
                        break;
                }

                //Máquina de saída 2
                signalBranchOut1 = getPortBranchOut1().getSignal();
                signalReqRight1 = getPortReqR1().getSignal();
                switch (getFsmOut1()) {
                    case IDLE:
                        if (isBranchReadyIn0() != isBranchReadyOut1() && valueAckRight1.is_0()) {
                            setFsmOut1(fsmStates.WRITE);

                            branchOut1 = new StdLogic1164(getBranchIn());
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalBranchOut1, time, branchOut1, getPortBranchOut1()));

                            nextReqR1 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqR1()));

                            setBranchReadyOut1(!isBranchReadyOut1());

                            setString(Integer.toString(getBranchIn()));

                        }
                        break;
                    case WRITE:
                        if (valueAckRight1.is_1()) {
                            setFsmOut1(fsmStates.IDLE);

                            nextReqR1 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight1, time, nextReqR1, getPortReqR1()));
                        }
                        break;
                }
                //********************************************************************************* 

                //Tratamento das entradas
                //Entrada
                signalAckLeft0 = getPortAckL0().getSignal();
                switch (getFsmIn0()) {
                    case IDLE:
                        if (isBranchReadyIn0() == isBranchReadyOut0() && isBranchReadyIn0() == isBranchReadyOut1() && valueReqLeft0.is_1()) {
                            setFsmIn0(fsmStates.READ);
                            signalBranchIn0 = getPortBIn0().getSignal();

                            StdLogic1164 bIn = getPortBIn0().getValueOrU();

                            if (bIn.is_1()) {
                                setBranchIn(this.IF);
                            } else if (bIn.is_0()) {
                                setBranchIn(this.ELSE);
                            }

                            setBranchReadyIn0(!isBranchReadyIn0());

                            nextAckL0 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckL0()));
                        }
                        break;
                    case READ:
                        if (!valueReqLeft0.is_1()) {
                            setFsmIn0(fsmStates.IDLE);

                            nextAckL0 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckL0()));
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
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name and bus width.");
        propertySheet.setVisible(true);
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

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 4200 " + getPortClk().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 600 " + getPortReqL0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 1200 " + getPortAckL0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 1800 " + getPortBIn0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + getPortReqR0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1200 " + getPortAckR0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1800 " + getPortBranchOut0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 2400 " + getPortReqR1().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 3000 " + getPortAckR1().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 3600 " + getPortBranchOut1().getName());
        this.symbol.addMember(portsymbol);

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
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
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
     * @return the portReqL0
     */
    public PortStdLogic1164 getPortReqL0() {
        return portReqL0;
    }

    /**
     * @param portReqL0 the portReqL0 to set
     */
    public void setPortReqL0(PortStdLogic1164 portReqL0) {
        this.portReqL0 = portReqL0;
    }

    /**
     * @return the portAckL0
     */
    public PortStdLogic1164 getPortAckL0() {
        return portAckL0;
    }

    /**
     * @param portAckL0 the portAckL0 to set
     */
    public void setPortAckL0(PortStdLogic1164 portAckL0) {
        this.portAckL0 = portAckL0;
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
     * @return the portReqR1
     */
    public PortStdLogic1164 getPortReqR1() {
        return portReqR1;
    }

    /**
     * @param portReqR1 the portReqR1 to set
     */
    public void setPortReqR1(PortStdLogic1164 portReqR1) {
        this.portReqR1 = portReqR1;
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
     * @return the portAckR1
     */
    public PortStdLogic1164 getPortAckR1() {
        return portAckR1;
    }

    /**
     * @param portAckR1 the portAckR1 to set
     */
    public void setPortAckR1(PortStdLogic1164 portAckR1) {
        this.portAckR1 = portAckR1;
    }

    /**
     * @return the portDin
     */
    public PortStdLogic1164 getPortBIn0() {
        return portBIn0;
    }

    /**
     * @param portBIn0 the portDin to set
     */
    public void setPortBIn0(PortStdLogic1164 portBIn0) {
        this.portBIn0 = portBIn0;
    }

    /**
     * @return the portBranchOut0
     */
    public PortStdLogic1164 getPortBranchOut0() {
        return portBranchOut0;
    }

    /**
     * @param portBranchOut0 the portBranchOut0 to set
     */
    public void setPortBranchOut0(PortStdLogic1164 portBranchOut0) {
        this.portBranchOut0 = portBranchOut0;
    }

    /**
     * @return the portBranchOut1
     */
    public PortStdLogic1164 getPortBranchOut1() {
        return portBranchOut1;
    }

    /**
     * @param portBranchOut1 the portBranchOut1 to set
     */
    public void setPortBranchOut1(PortStdLogic1164 portBranchOut1) {
        this.portBranchOut1 = portBranchOut1;
    }

    /**
     * @return the branchIn
     */
    public int getBranchIn() {
        return branchIn;
    }

    /**
     * @param branchIn the branchIn to set
     */
    public void setBranchIn(int branchIn) {
        this.branchIn = branchIn;
    }

    /**
     * @return the branchReadyIn0
     */
    public boolean isBranchReadyIn0() {
        return branchReadyIn0;
    }

    /**
     * @param branchReadyIn0 the branchReadyIn0 to set
     */
    public void setBranchReadyIn0(boolean branchReadyIn0) {
        this.branchReadyIn0 = branchReadyIn0;
    }

    /**
     * @return the branchReadyOut0
     */
    public boolean isBranchReadyOut0() {
        return branchReadyOut0;
    }

    /**
     * @param branchReadyOut0 the branchReadyOut0 to set
     */
    public void setBranchReadyOut0(boolean branchReadyOut0) {
        this.branchReadyOut0 = branchReadyOut0;
    }

    /**
     * @return the branchReadyOut1
     */
    public boolean isBranchReadyOut1() {
        return branchReadyOut1;
    }

    /**
     * @param branchReadyOut1 the branchReadyOut1 to set
     */
    public void setBranchReadyOut1(boolean branchReadyOut1) {
        this.branchReadyOut1 = branchReadyOut1;
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
