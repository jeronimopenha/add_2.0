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
 * AsyncFgate component for the UFV asynchronous data flow simulator.<br>
 * The component aims to select one of its data inputs to be passed to the
 * output according to the value contained in its control port.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncFgate extends AsyncGenericUn {

    protected final int BRANCH_VECTOR_WIDTH = 1;

    private PortStdLogicVector portBranch;

    private PortStdLogic1164 portReqBranch;
    private PortStdLogic1164 portAckBranch;

    private branchCtrl branch;

    private dataCtrl controlBranch;

    private boolean branchReady;

    private fsmStates fsmBranch;

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
    public AsyncFgate() {
        super();
        this.branchReady = false;
        this.fsmBranch = fsmStates.IDLE;
        setCompName("FGATE");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortReqLeft0(new PortStdLogic1164(this, "reqleft0", Port.IN, null));
        setPortReqBranch(new PortStdLogic1164(this, "reqbranch", Port.IN, null));
        setPortReqRight0(new PortStdLogic1164(this, "reqright0", Port.OUT, null));
        setPortAckLeft0(new PortStdLogic1164(this, "ackleft0", Port.OUT, null));
        setPortAckBranch(new PortStdLogic1164(this, "ackbranch", Port.OUT, null));
        setPortAckRight0(new PortStdLogic1164(this, "ackright0", Port.IN, null));
        setPortBranch(new PortStdLogicVector(this, "branch", Port.IN, null, BRANCH_VECTOR_WIDTH));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));

        ports = new Port[10];
        ports[0] = getPortClk();
        ports[1] = getPortReqLeft0();
        ports[2] = getPortReqBranch();
        ports[3] = getPortReqRight0();
        ports[4] = getPortAckLeft0();
        ports[5] = getPortAckBranch();
        ports[6] = getPortAckRight0();
        ports[7] = getPortBranch();
        ports[8] = getPortDin0();
        ports[9] = getPortDout0();
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
        Signal signalAckLeft0;
        Signal signalAckBranch;
        Signal signalReqRight0;

        //para portDout
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }

        //para portAckLeft
        if ((signalAckLeft0 = getPortAckLeft0().getSignal()) != null) {
            StdLogic1164 nextAckL0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckLeft0()));
        }

        //para portAckBranch
        if ((signalAckBranch = getPortAckBranch().getSignal()) != null) {
            StdLogic1164 nextAckBranch = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckBranch, time, nextAckBranch, getPortBranch()));
        }

        //para portReqRight
        if ((signalReqRight0 = getPortReqRight0().getSignal()) != null) {
            StdLogic1164 nextReqR0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));
        }
        setDataReadyIn0(false);
        setBranchReady(false);
        setDataReadyOut0(false);
        setDataIn0(0);
        setBranch(branchCtrl.ELSE);
        setFsmIn0(fsmStates.IDLE);
        setFsmBranch(fsmStates.IDLE);
        setFsmOut0(fsmStates.IDLE);
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

        Signal signalDin0, signalDout0, signalAckLeft0, signalReqRight0;
        Signal signalAckBranch;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqBranch().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckBranch().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortBranch().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogic1164 nextAckL0;
        StdLogic1164 nextAckBranch;
        StdLogic1164 nextReqR0;
        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqLeft0().getValueOrU();
                StdLogic1164 valueReqBranch = getPortReqBranch().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckRight0().getValueOrU();

                //Tratamento da saída
                //Máquina de saída
                signalDout0 = getPortDout0().getSignal();
                signalReqRight0 = getPortReqRight0().getSignal();
                switch (getFsmOut0()) {
                    case IDLE:
                        if (isDataReadyIn0() != isDataReadyOut0() && isBranchReady() != isDataReadyOut0() && valueAckRight0.is_0()) {
                            setFsmOut0(fsmStates.WRITE);

                            setDataReadyOut0(!isDataReadyOut0());

                            switch (getControlIn0()) {
                                case DONE:
                                    dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setString("DONE");
                                    break;
                                case STOP:
                                    dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setString("STOP");
                                    break;
                                case VALID:
                                    if (getBranch() == branchCtrl.ELSE) {
                                        dOut0.setValue(createDataOut(getDataIn0(), dataCtrl.VALID));
                                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                        nextReqR0 = new StdLogic1164(3);
                                        getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                        setString(Integer.toString(getDataIn0()));
                                    }
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
                //Entrada 1
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

                //Entrada 3 - branch
                signalAckBranch = getPortAckBranch().getSignal();
                switch (getFsmBranch()) {
                    case IDLE:
                        if (isBranchReady() == isDataReadyOut0() && valueReqBranch.is_1()) {
                            setFsmBranch(fsmStates.READ);

                            StdLogicVector bIn = (StdLogicVector) getPortBranch().getValue();

                            if (bIn.getValue() == branchCtrl.IF.getValue()) {
                                setBranch(branchCtrl.IF);
                            } else if (bIn.getValue() == branchCtrl.ELSE.getValue()) {
                                setBranch(branchCtrl.ELSE);
                            }
                            setBranchReady(!isBranchReady());

                            nextAckBranch = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckBranch, time, nextAckBranch, getPortAckBranch()));
                        }
                        break;
                    case READ:
                        if (!valueReqBranch.is_1()) {
                            setFsmBranch(fsmStates.IDLE);

                            nextAckBranch = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckBranch, time, nextAckBranch, getPortAckBranch()));
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
        portsymbol.initialize("0 600 " + getPortReqBranch().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 1200 " + getPortAckBranch().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 2400 " + getPortReqLeft0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 3000 " + getPortAckLeft0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + getPortReqRight0().getName());
        this.symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1200 " + getPortAckRight0().getName());
        this.symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1800 " + getPortBranch().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1800 " + getPortDout0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 3600 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -600 " + getName());
        this.symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 3000 2 " + getComponentType());
        this.symbol.addMember(lblComponentType);

        setStringLabel(new Label());
        getStringLabel().initialize("0 -200 " + getS());
        this.symbol.addMember(getStringLabel());
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

    /**
     * @return the portReqBranch
     */
    public PortStdLogic1164 getPortReqBranch() {
        return portReqBranch;
    }

    /**
     * @param portReqBranch the portReqBranch to set
     */
    public void setPortReqBranch(PortStdLogic1164 portReqBranch) {
        this.portReqBranch = portReqBranch;
    }

    /**
     * @return the portAckBranch
     */
    public PortStdLogic1164 getPortAckBranch() {
        return portAckBranch;
    }

    /**
     * @param portAckBranch the portAckBranch to set
     */
    public void setPortAckBranch(PortStdLogic1164 portAckBranch) {
        this.portAckBranch = portAckBranch;
    }

    /**
     * @return the branch
     */
    public branchCtrl getBranch() {
        return branch;
    }

    /**
     * @param branch the branch to set
     */
    public void setBranch(branchCtrl branch) {
        this.branch = branch;
    }

    /**
     * @return the controlBranch
     */
    public dataCtrl getControlBranch() {
        return controlBranch;
    }

    /**
     * @param controlBranch the controlBranch to set
     */
    public void setControlBranch(dataCtrl controlBranch) {
        this.controlBranch = controlBranch;
    }

    /**
     * @return the branchReady
     */
    public boolean isBranchReady() {
        return branchReady;
    }

    /**
     * @param branchReady the branchReady to set
     */
    public void setBranchReady(boolean branchReady) {
        this.branchReady = branchReady;
    }

    /**
     * @return the fsmBranch
     */
    public fsmStates getFsmBranch() {
        return fsmBranch;
    }

    /**
     * @param fsmBranch the fsmBranch to set
     */
    public void setFsmBranch(fsmStates fsmBranch) {
        this.fsmBranch = fsmBranch;
    }
}
