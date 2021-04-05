package add.dataflow.sync;

import add.dataflow.base.AddSimObject;
import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.signals.SignalStdLogicVector;
import hades.simulator.Port;
import hades.simulator.SimEvent;
import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;

/**
 * SyncGenericBranchI component for the UFV synchronous data flow simulator.<br>
 * The component creates the basis for other components with an input and that
 * make a comparison with a (immediate) constant.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncGenericBranchI extends SyncGenericI {

    protected final int BRANCH_VECTOR_WIDTH = 1;

    private PortStdLogicVector portBOut0;

    /**
     * Enumerator for the processing control protocol.
     *
     */
    protected enum branchCtrl {
        IF(1), ELSE(0);
        private final byte value;

        branchCtrl(int valor) {
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
    public SyncGenericBranchI() {
        super();
        setId(1);
        setImmediate(getId());
        setCompName("G_COMPI");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortBOut0(new PortStdLogicVector(this, "bout0", Port.OUT, null, BRANCH_VECTOR_WIDTH));

        ports = new Port[3];
        ports[0] = getPortClk();
        ports[1] = getPortDin0();
        ports[2] = getPortBOut0();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     *
     * @param time
     */
    @Override
    public void reset(double time) {
        Signal signalBOut0;
        //para portBOut0
        if ((signalBOut0 = getPortBOut0().getSignal()) != null) {
            StdLogicVector branch = new StdLogicVector(1, branchCtrl.ELSE.getValue());
            getSimulator().scheduleEvent(new SimEvent(signalBOut0, time, branch, getPortBOut0()));
        }
        setString();
        userReset(time);
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

        Signal signalDin0, signalBOut0;

        boolean hasDisconnectedPorts = false;

        if ((getPortClk().getSignal()) == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBOut0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector branch;

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                signalDin0 = getPortDin0().getSignal();
                signalBOut0 = getPortBOut0().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();

                switch (getCtrl(dIn0.getValue())) {
                    case DONE:
                        branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                        branch.setValue(branchCtrl.ELSE.getValue());
                        getSimulator().scheduleEvent(new SimEvent(signalBOut0, time, branch, getPortBOut0()));

                        setString();
                        break;
                    case STOP:
                        branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                        branch.setValue(branchCtrl.ELSE.getValue());
                        getSimulator().scheduleEvent(new SimEvent(signalBOut0, time, branch, getPortBOut0()));

                        setString();
                        break;
                    case VALID:
                        branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                        branch.setValue(compute(getData(dIn0.getValue()), getData((long) getImmediate())));
                        getSimulator().scheduleEvent(new SimEvent(signalBOut0, time, branch, getPortBOut0()));
                        break;
                }
            }
        }
    }

    /**
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        this.symbol = new Symbol();
        this.symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("-0 -1300 1800 1200");
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
        busportsymbol.initialize("1800 600 " + getPortBOut0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -1000 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 600 2 " + getComponentType());
        this.symbol.addMember(lblComponentType);

        setStrLblId(new Label());
        getStrLblId().initialize("0 -600 ID=" + Integer.toString(getId()));
        this.symbol.addMember(getStrLblId());

        setStrLblImmediate(new Label());
        getStrLblImmediate().initialize("0 -200 IM=" + Integer.toString((int) getImmediate()));
        this.symbol.addMember(getStrLblImmediate());

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

    public SignalStdLogicVector connectTo(AddSimObject targetObj, String targetPort) {
        SignalStdLogicVector signal = null;
        if (getEditor().getDesign().getComponent(targetObj.getName()) == null) {
            return signal;
        }
        if (getPort("bout0").getSignal() == null) {
            signal = new SignalStdLogicVector();
            signal.connect(getPort("bout0"));
            signal.connect(targetObj.getPort(targetPort));
            getEditor().getDesign().addSignal(signal);
        } else {
            signal = (SignalStdLogicVector) getPort("bout0").getSignal();
            signal.connect(targetObj.getPort(targetPort));
        }
        return signal;
    }

    /**
     * @return the portBOut0
     */
    public PortStdLogicVector getPortBOut0() {
        return portBOut0;
    }

    /**
     * @param portBOut0 the portBOut0 to set
     */
    public void setPortBOut0(PortStdLogicVector portBOut0) {
        this.portBOut0 = portBOut0;
    }
}
