package add.dataflow.sync;

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

/**
 * SyncMux component for the UFV synchronous data flow simulator.<br>
 * The component aims to select one of its data inputs to be passed to the
 * output according to the value contained in its control port.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncMux extends SyncGenericBin {

    protected final int BRANCH_VECTOR_WIDTH = 1;

    private PortStdLogicVector portBIn;

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
    public SyncMux() {
        super();
        setCompName("MUX");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortBIn(new PortStdLogicVector(this, "bin0", Port.IN, null, BRANCH_VECTOR_WIDTH));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDin1(new PortStdLogicVector(this, "din1", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));

        ports = new Port[5];
        ports[0] = getPortClk();
        ports[1] = getPortBIn();
        ports[2] = getPortDin0();
        ports[3] = getPortDin1();
        ports[4] = getPortDout0();
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked If the sinam branch is high level, the input 1 data will go to
     * the output, otherwise the input 2 data will go to output.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDin10, signalDin1, signalDout0, signalBIn;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBIn().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {
                signalDin10 = getPortDin0().getSignal();
                signalDin1 = getPortDin1().getSignal();
                signalDout0 = getPortDout0().getSignal();
                signalBIn = getPortBIn().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin10.getValue();
                StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();
                StdLogicVector valueBranch = (StdLogicVector) signalBIn.getValue();

                dataCtrl ctrl;
                if (getCtrl(dIn0.getValue()) == dataCtrl.VALID || getCtrl(dIn1.getValue()) == dataCtrl.VALID) {
                    ctrl = dataCtrl.VALID;
                } else if (getCtrl(dIn0.getValue()) == dataCtrl.STOP || getCtrl(dIn1.getValue()) == dataCtrl.STOP) {
                    ctrl = dataCtrl.STOP;
                } else {
                    ctrl = dataCtrl.DONE;
                }

                switch (ctrl) {
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
                        if (valueBranch.getValue() == branchCtrl.IF.getValue() && getCtrl(dIn0.getValue()) == dataCtrl.VALID) {
                            dOut0.setValue(createDataOut(getData(dIn0.getValue()), dataCtrl.VALID));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                            setString(Integer.toString((int) getData(dIn0.getValue())));
                        } else if (valueBranch.getValue() == branchCtrl.ELSE.getValue() && getCtrl(dIn1.getValue()) == dataCtrl.VALID) {
                            dOut0.setValue(createDataOut(getData(dIn1.getValue()), dataCtrl.VALID));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                            setString(Integer.toString((int) getData(dIn1.getValue())));
                        }
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
        busportsymbol.initialize("0 0 " + getPortBIn().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 600 " + getPortDout0().getName());
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
     * @return the portBIn
     */
    public PortStdLogicVector getPortBIn() {
        return portBIn;
    }

    /**
     * @param portBIn the portBIn to set
     */
    public void setPortBIn(PortStdLogicVector portBIn) {
        this.portBIn = portBIn;
    }
}
