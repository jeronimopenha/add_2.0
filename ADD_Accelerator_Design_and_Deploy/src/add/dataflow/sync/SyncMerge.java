package add.dataflow.sync;

import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;

/**
 * SyncMerge component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for choosing which of the inputs to pass to the
 * output depending on the value of R_IN1 and R_IN2.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncMerge extends SyncGenericBin {

    /**
     * Object Constructor.
     */
    public SyncMerge() {
        super();
        setCompName("MERGE");
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

        Signal signalDin0, signalDin1, signalDout0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin1().getSignal() == null) {
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
                signalDin1 = getPortDin1().getSignal();
                signalDout0 = getPortDout0().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();
                StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();

                dataCtrl ctrl;
                if (getCtrl(dIn0.getValue()) == dataCtrl.VALID || getCtrl(dIn1.getValue()) == dataCtrl.VALID) {
                    ctrl = dataCtrl.VALID;
                } else if (getCtrl(dIn0.getValue()) == dataCtrl.DONE || getCtrl(dIn1.getValue()) == dataCtrl.DONE) {
                    ctrl = dataCtrl.DONE;
                } else {
                    ctrl = dataCtrl.STOP;
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
                        if (getCtrl(dIn0.getValue()) == dataCtrl.VALID) {
                            dOut0.setValue(dIn0.getValue());
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        } else if (getCtrl(dIn1.getValue()) == dataCtrl.VALID) {
                            dOut0.setValue(dIn1.getValue());
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        }
                        break;
                }
            }
        }
    }
}
