package add.dataflow.async;

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

/**
 * AsyncGenericBranchI component for the UFV asynchronous data flow
 * simulator.<br>
 * The component creates the basis for other components with an input and that
 * make a comparison with a (immediate) constant.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncGenericBranchI extends AsyncGenericI {

    protected final int BRANCH_VECTOR_WIDTH = 1;

    private PortStdLogicVector portBranch;

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
    public AsyncGenericBranchI() {
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
        setPortReqLeft0(new PortStdLogic1164(this, "reqleft0", Port.IN, null));
        setPortReqRight0(new PortStdLogic1164(this, "reqright0", Port.OUT, null));
        setPortAckLeft0(new PortStdLogic1164(this, "ackleft0", Port.OUT, null));
        setPortAckRight0(new PortStdLogic1164(this, "ackright0", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortBranch(new PortStdLogicVector(this, "branch", Port.OUT, null, BRANCH_VECTOR_WIDTH));

        ports = new Port[7];
        ports[0] = getPortClk();
        ports[1] = getPortReqLeft0();
        ports[2] = getPortReqRight0();
        ports[3] = getPortAckLeft0();
        ports[4] = getPortAckRight0();
        ports[5] = getPortDin0();
        ports[6] = getPortBranch();
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
        Signal signalBranch;
        Signal signalAckLeft0;
        Signal signalReqRight0;

        //para portBranch
        if ((signalBranch = getPortBranch().getSignal()) != null) {
            StdLogicVector branch = new StdLogicVector(1, branchCtrl.ELSE.getValue());
            getSimulator().scheduleEvent(new SimEvent(signalBranch, time, branch, getPortBranch()));
        }

        //para portAckLeft
        if ((signalAckLeft0 = getPortAckLeft0().getSignal()) != null) {
            StdLogic1164 nextAckL0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckLeft0()));
        }

        //para portReqRight
        if ((signalReqRight0 = getPortReqRight0().getSignal()) != null) {
            StdLogic1164 nextReqR0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));
        }
        setDataReadyIn0(false);
        setDataReadyOut0(false);
        setDataIn0(0);
        setFsmIn0(fsmStates.IDLE);
        setFsmOut0(fsmStates.IDLE);
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

        Signal signalDin0, signalBranch, signalAckLeft0, signalReqRight0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBranch().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogic1164 nextAckL0;
        StdLogic1164 nextReqR0;
        StdLogicVector branch;

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqLeft0().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckRight0().getValueOrU();

                //Tratamento da saída
                //Máquina de saída
                signalBranch = getPortBranch().getSignal();
                signalReqRight0 = getPortReqRight0().getSignal();
                switch (getFsmOut0()) {
                    case IDLE:
                        if (isDataReadyIn0() != isDataReadyOut0() && valueAckRight0.is_0()) {
                            setFsmOut0(fsmStates.WRITE);

                            setDataReadyOut0(!isDataReadyOut0());

                            switch (getControlIn0()) {
                                case DONE:
                                    setString();
                                    break;
                                case STOP:
                                    setString();
                                    break;
                                case VALID:
                                    branch = new StdLogicVector(BRANCH_VECTOR_WIDTH);
                                    branch.setValue(compute(getDataIn0(), getData((long) getImmediate())));
                                    getSimulator().scheduleEvent(new SimEvent(signalBranch, time, branch, getPortBranch()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

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
                //********************************************************************************* 

                //Tratamento das entradas
                //Entrada
                signalAckLeft0 = getPortAckLeft0().getSignal();
                switch (getFsmIn0()) {
                    case IDLE:
                        if (isDataReadyIn0() == isDataReadyOut0() && valueReqLeft0.is_1()) {
                            setFsmIn0(fsmStates.READ);
                            signalDin0 = getPortDin0().getSignal();
                            StdLogicVector dIn = (StdLogicVector) signalDin0.getValue();

                            setDataIn0(getData(dIn.getValue()));
                            setControlIn0(getCtrl(dIn.getValue()));
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
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        this.symbol = new Symbol();
        this.symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 -1300 1800 2400");
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

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1800 " + getPortBranch().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1800 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -1000 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 1200 2 " + getComponentType());
        this.symbol.addMember(lblComponentType);

        setStrLblId(new Label());
        getStrLblId().initialize("0 -600 ID=" + Integer.toString(getId()));
        this.symbol.addMember(getStrLblId());

        setStrLblImmediate(new Label());
        getStrLblImmediate().initialize("0 -200 IM=" + Integer.toString(getImmediate()));
        this.symbol.addMember(getStrLblImmediate());
    }

    /**
     * @return the portBranch
     */
    public PortStdLogicVector getPortBranch() {
        return portBranch;
    }

    /**
     * @param portBranch the portBranch to set
     */
    public void setPortBranch(PortStdLogicVector portBranch) {
        this.portBranch = portBranch;
    }
}
