package add.dataflow.async;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;

/**
 * AsyncMerge component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for choosing which of the inputs to pass to the
 * output depending on the value of R_IN1 and R_IN2.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncMerge extends AsyncGenericBin {

    /**
     * Object Constructor.
     */
    public AsyncMerge() {
        super();
        setCompName("MERGE");
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     *
     */
    @Override
    public void reset(double time) {
        Signal signalDout0;
        Signal signalAckLeft0;
        Signal signalAckLeft1;
        Signal signalReqRight0;

        //para portDout0
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }

        //para portAckLeft0
        if ((signalAckLeft0 = getPortAckLeft0().getSignal()) != null) {
            StdLogic1164 nextAckL0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft0, time, nextAckL0, getPortAckLeft0()));
        }

        //para portAckLeft1
        if ((signalAckLeft1 = getPortAckLeft1().getSignal()) != null) {
            StdLogic1164 nextAckL1 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft1, time, nextAckL1, getPortAckLeft1()));
        }

        //para portReqRight0
        if ((signalReqRight0 = getPortReqRight0().getSignal()) != null) {
            StdLogic1164 nextReqR0 = new StdLogic1164(2);
            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalReqRight0, time, nextReqR0, getPortReqRight0()));
        }
        setDataReadyIn0(false);
        setDataReadyIn1(false);
        setDataReadyOut0(false);
        setDataIn0(0);
        setDataIn1(0);
        setFsmIn0(fsmStates.IDLE);
        setFsmIn1(fsmStates.IDLE);
        setFsmOut0(fsmStates.IDLE);
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

        Signal signalDin0, signalDin1, signalDout0, signalAckLeft0, signalReqRight0;
        Signal signalAckLeft1;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqLeft1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckLeft0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckLeft1().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortReqRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortAckRight0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogic1164 nextAckL0;
        StdLogic1164 nextAckL1;
        StdLogic1164 nextReqR0;
        StdLogicVector dOut0 = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                StdLogic1164 valueReqLeft0 = getPortReqLeft0().getValueOrU();
                StdLogic1164 valueReqLeft1 = getPortReqLeft1().getValueOrU();
                StdLogic1164 valueAckRight0 = getPortAckRight0().getValueOrU();

                //Tratamento da saída
                //Máquina de saída
                signalDout0 = getPortDout0().getSignal();
                signalReqRight0 = getPortReqRight0().getSignal();
                switch (getFsmOut0()) {
                    case IDLE:
                        if (isDataReadyIn0() != isDataReadyOut0() && isDataReadyIn1() != isDataReadyOut0() && valueAckRight0.is_0()) {
                            setFsmOut0(fsmStates.WRITE);

                            setDataReadyOut0(!isDataReadyOut0());

                            dataCtrl ctrl;
                            if (getControlIn0() == dataCtrl.VALID || getControlIn1() == dataCtrl.VALID) {
                                ctrl = dataCtrl.VALID;
                            } else if (getControlIn0() == dataCtrl.DONE || getControlIn1() == dataCtrl.DONE) {
                                ctrl = dataCtrl.DONE;
                            } else {
                                ctrl = dataCtrl.STOP;
                            }

                            switch (ctrl) {
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
                                    if (getControlIn0() == dataCtrl.VALID) {
                                        dOut0.setValue(getDataIn0());
                                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                                    } else if (getControlIn0() == dataCtrl.VALID) {
                                        dOut0.setValue(getDataIn1());
                                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                                    }

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
                //Entrada 1
                signalAckLeft0 = getPortAckLeft0().getSignal();
                switch (getFsmIn0()) {
                    case IDLE:
                        if (isDataReadyIn0() == isDataReadyOut0() && valueReqLeft0.is_1()) {
                            setFsmIn0(fsmStates.READ);
                            signalDin0 = getPortDin0().getSignal();
                            StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();

                            setDataIn0(getData(dIn0.getValue()));
                            setControlIn0(getCtrl(dIn0.getValue()));
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

                //Entrada 2
                signalAckLeft1 = getPortAckLeft1().getSignal();
                switch (getFsmIn1()) {
                    case IDLE:
                        if (isDataReadyIn1() == isDataReadyOut0() && valueReqLeft1.is_1()) {
                            setFsmIn1(fsmStates.READ);
                            signalDin1 = getPortDin1().getSignal();
                            StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();

                            setDataIn1(getData(dIn1.getValue()));
                            setControlIn1(getCtrl(dIn1.getValue()));
                            setDataReadyIn1(!isDataReadyIn1());

                            nextAckL1 = new StdLogic1164(3);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft1, time, nextAckL1, getPortAckLeft1()));
                        }
                        break;
                    case READ:
                        if (!valueReqLeft1.is_1()) {
                            setFsmIn1(fsmStates.IDLE);

                            nextAckL1 = new StdLogic1164(2);
                            getSimulator().scheduleEvent(SimEvent1164.createNewSimEvent(signalAckLeft1, time, nextAckL1, getPortAckLeft1()));
                        }
                        break;
                }
            }
        }
    }
}
