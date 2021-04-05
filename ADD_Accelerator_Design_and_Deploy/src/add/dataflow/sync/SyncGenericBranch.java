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
 * SyncGenericBranch component for the UFV synchronous data flow simulator.<br>
 * The component creates the basis for other components with an input and that
 * make a comparison between the inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncGenericBranch extends SyncGenericBin {

    protected final int BRANCH_VECTOR_WIDTH = 1;

    private PortStdLogicVector portBOut;

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
    public SyncGenericBranch() {
        super();
        setCompName("GEN_CMP");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDin1(new PortStdLogicVector(this, "din1", Port.IN, null, getRealWidth()));
        setPortBOut(new PortStdLogicVector(this, "bout", Port.OUT, null, BRANCH_VECTOR_WIDTH));

        ports = new Port[4];
        ports[0] = getPortClk();
        ports[1] = getPortDin0();
        ports[2] = getPortDin1();
        ports[3] = getPortBOut();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     *
     * @param time
     */
    @Override
    public void reset(double time) {
        Signal signalBOut;
        //para portBOut
        if ((signalBOut = getPortBOut().getSignal()) != null) {
            StdLogicVector branch = new StdLogicVector(1, branchCtrl.ELSE.getValue());
            getSimulator().scheduleEvent(new SimEvent(signalBOut, time, branch, getPortBOut()));
        }
        setS("NULL");
        setString(getS());
        userReset(time);
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute(int
     * data1, int data2) method. It will execute the reset() method if their
     * respective entries order it. It will update the output with the
     * compute(int data1, int data2) method result.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDin0, signalDin1, signalBOut;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBOut().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector branch;

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {
                signalDin0 = getPortDin0().getSignal();
                signalDin1 = getPortDin1().getSignal();
                signalBOut = getPortBOut().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();
                StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();

                dataCtrl ctrl;
                if (getCtrl(dIn0.getValue()) == dataCtrl.DONE || getCtrl(dIn1.getValue()) == dataCtrl.DONE) {
                    ctrl = dataCtrl.DONE;
                } else if (getCtrl(dIn0.getValue()) == dataCtrl.STOP || getCtrl(dIn1.getValue()) == dataCtrl.STOP) {
                    ctrl = dataCtrl.STOP;
                } else {
                    ctrl = getCtrl(dIn0.getValue());
                }

                switch (ctrl) {
                    case DONE:
                        branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                        branch.setValue(branchCtrl.ELSE.getValue());
                        getSimulator().scheduleEvent(new SimEvent(signalBOut, time, branch, getPortBOut()));

                        setString("DONE");
                        break;
                    case STOP:
                        branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                        branch.setValue(branchCtrl.ELSE.getValue());
                        getSimulator().scheduleEvent(new SimEvent(signalBOut, time, branch, getPortBOut()));

                        setString("STOP");
                        break;
                    case VALID:
                        branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                        branch.setValue(compute(getData(dIn0.getValue()), getData(dIn1.getValue())));
                        getSimulator().scheduleEvent(new SimEvent(signalBOut, time, branch, getPortBOut()));

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
        bbr.initialize("0 -900 1800 1800");
        this.symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1800");
        this.symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 1800 " + getPortClk().getName());
        this.symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 600 " + getPortBOut().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1200 " + getPortDin1().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -600 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 900 2 " + getComponentType());
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
     * @return the portBOut
     */
    public PortStdLogicVector getPortBOut() {
        return portBOut;
    }

    /**
     * @param portBOut the portBOut to set
     */
    public void setPortBOut(PortStdLogicVector portBOut) {
        this.portBOut = portBOut;
    }

}
