package add.dataflow.async;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;

/**
 * AsyncGenericAcc component for the UFV asynchronous data flow simulator.<br>
 * The component implements a generic accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncGenericAcc extends AsyncGenericI {

    private int acc;
    private int counter;

    /**
     * Object Constructor.
     */
    public AsyncGenericAcc() {
        super();
        this.acc = 0;
        this.counter = getImmediate();
        setCompName("Generic_ACC");
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    @Override
    public void reset(double time) {
        Signal signalDout0;
        Signal signalAckLeft0;
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
        setAcc(0);
        setCounter(getImmediate());
        setString();
        userReset(time);
    }

    /**
     * Method responsible for performing the accumulation or not.
     *
     * @param data - Value to be used for the computation.
     */
    protected void accumulate(int data) {
        setString();
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method. It will execute the reset() method if their respective
     * entries order it. It will update the output with the ACC value when the
     * computation finishes.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDin0, signalDout0, signalAckLeft0, signalReqRight0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
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
        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqLeft0().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckRight0().getValueOrU();

                //Tratamento da saída
                //Máquina de saída
                signalDout0 = getPortDout0().getSignal();
                signalReqRight0 = getPortReqRight0().getSignal();
                switch (getFsmOut0()) {
                    case IDLE:
                        if (isDataReadyIn0() != isDataReadyOut0() && valueAckRight0.is_0()) {
                            setFsmOut0(fsmStates.WRITE);

                            setDataReadyOut0(!isDataReadyOut0());

                            switch (getControlIn0()) {
                                case DONE:
                                    dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setString();
                                    break;
                                case STOP:
                                    dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                                    getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                    nextReqR0 = new StdLogic1164(3);
                                    getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));

                                    setString();
                                    break;
                                case VALID:
                                    if (getCounter() > 1) {
                                        accumulate(getDataIn0());
                                        setCounter(getCounter() - 1);

                                        dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                                    } else {
                                        accumulate(getDataIn0());
                                        dOut0.setValue(createDataOut(getAcc(), dataCtrl.VALID));
                                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                                        setCounter(getImmediate());
                                        setAcc(0);
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
     * @return the acc
     */
    public int getAcc() {
        return acc;
    }

    /**
     * @param acc the acc to set
     */
    public void setAcc(int acc) {
        this.acc = acc;
    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
}
