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
 * SyncDemux component for the UFV synchronous data flow simulator.<br>
 * The purpose of the component is to choose which output will receive the input
 * value according to the signal from the "branch" port.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncDemux extends SyncGenericUn {

    protected final int BRANCH_VECTOR_WIDTH = 1;

    private PortStdLogicVector portDout1;
    private PortStdLogicVector portBIn0;

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
    public SyncDemux() {
        super();
        setCompName("DEMUX");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortBIn0(new PortStdLogicVector(this, "bin0", Port.IN, null, BRANCH_VECTOR_WIDTH));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.IN, null, getRealWidth()));
        setPortDout1(new PortStdLogicVector(this, "dout1", Port.IN, null, getRealWidth()));

        ports = new Port[5];
        ports[0] = getPortClk();
        ports[1] = getPortBIn0();
        ports[2] = getPortDin0();
        ports[3] = getPortDout0();
        ports[4] = getPortDout1();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     *
     * @param time
     */
    public void reset(double time) {
        Signal signalDout0;
        Signal signalDout1;
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

        setS("NULL");
        setString(getS());
        userReset(time);
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

        Signal signalDin0, signalDout0, signalDout1;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBIn0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());
        StdLogicVector dOut1 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {
                signalDin0 = getPortDin0().getSignal();
                signalDout0 = getPortDout0().getSignal();
                signalDout1 = getPortDout1().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();
                StdLogicVector valueBranch = (StdLogicVector) getPortBIn0().getValue();

                switch (getCtrl(dIn0.getValue())) {
                    case DONE:
                        dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                        dOut1.setValue(createDataOut(0, dataCtrl.DONE));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                        setString("DONE");
                        break;
                    case STOP:
                        dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                        dOut1.setValue(createDataOut(0, dataCtrl.STOP));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                        setString("STOP");
                        break;
                    case VALID:
                        if (valueBranch.getValue() == branchCtrl.IF.getValue()) {
                            dOut0.setValue(dIn0.getValue());
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                            setString(Integer.toString((int) getData(dIn0.getValue())));

                            dOut1.setValue(createDataOut(0, dataCtrl.STOP));
                            getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));
                            setString(Integer.toString((int) getData(dIn0.getValue())));
                        } else if (valueBranch.getValue() == branchCtrl.ELSE.getValue()) {
                            dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                            setString(Integer.toString((int) getData(dIn0.getValue())));

                            dOut1.setValue(dIn0.getValue());
                            getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));
                            setString(Integer.toString((int) getData(dIn0.getValue())));
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
        busportsymbol.initialize("0 0 " + getPortBIn0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 600 " + getPortDout0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1200 " + getPortDout1().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin0().getName());
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
     * @return the portBIn0
     */
    public PortStdLogicVector getPortBIn0() {
        return portBIn0;
    }

    /**
     * @param portBIn0 the portBIn0 to set
     */
    public void setPortBIn0(PortStdLogicVector portBIn0) {
        this.portBIn0 = portBIn0;
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
}
