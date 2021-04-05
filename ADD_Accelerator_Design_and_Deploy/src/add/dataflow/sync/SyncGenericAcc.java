package add.dataflow.sync;

import add.dataflow.base.AddSimObject;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;

/**
 * SyncGenericAcc component for the UFV synchronous data flow simulator.<br>
 * The component implements a generic accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncGenericAcc extends SyncGenericI {

    private long acc;
    private long counter;

    /**
     * Object Constructor.
     */
    public SyncGenericAcc() {
        super();
        this.acc = 0;
        this.counter = getImmediate();
        setCompName("Generic_ACC");
    }

    /**
     * copy(): This function is used to create a clone of this RTLIB object,
     * including the values for width (n_bits), current value (vector),
     * propagation delay, and version ID.
     * <p>
     */
    @Override
    public AddSimObject copy() {
        SyncGenericAcc tmp = null;
        try {
            tmp = this.getClass().newInstance();
            tmp.setEditor(this.getEditor());
            tmp.setVisible(this.isVisible());
            tmp.setName(this.getName());
            tmp.setClassLoader(this.getClassLoader());
            tmp.setWidth(this.n_bits - 2);
            tmp.setDelay(this.getDelay());
            tmp.setVersionId(this.getVersionId());
            tmp.setAcc(this.getAcc());
            tmp.setCounter(this.getCounter());
            tmp.setStrLblId(this.getStrLblId());
            tmp.setStrLblImmediate(this.getStrLblImmediate());
            tmp.setLblName(this.getLblName());
            tmp.setComponentType(this.getComponentType());
            tmp.setAfuId(this.getAfuId());
            tmp.setStart(this.isStart());
            return (AddSimObject) tmp;
        } catch (IllegalAccessException | InstantiationException e) {
            message("-E- Internal error in SyncGenericUn.copy(): " + e);
            jfig.utils.ExceptionTracer.trace(e);
            return null;
        }
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    @Override
    public void reset(double time) {
        Signal signalDout0;
        //para portDout
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }
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
    protected void accumulate(long data) {
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

        Signal signalDin0, signalDout0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector dOut = new StdLogicVector(getRealWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                signalDin0 = getPortDin0().getSignal();
                signalDout0 = getPortDout0().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();

                switch (getCtrl(dIn0.getValue())) {
                    case DONE:
                        dOut.setValue(createDataOut(0, dataCtrl.DONE));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut, getPortDout0()));

                        setString();
                        break;
                    case STOP:
                        dOut.setValue(createDataOut(0, dataCtrl.STOP));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut, getPortDout0()));

                        setString();
                        break;
                    case VALID:
                        if (getCounter() > 1) {
                            accumulate(getData(dIn0.getValue()));
                            setCounter(getCounter() - 1);

                            dOut.setValue(createDataOut(0, dataCtrl.STOP));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut, getPortDout0()));
                        } else {
                            accumulate(getData(dIn0.getValue()));
                            dOut.setValue(createDataOut(getAcc(), dataCtrl.VALID));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut, getPortDout0()));
                            setCounter(getImmediate());
                            setAcc(0);
                        }
                        break;
                }
            }
        }
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

    /**
     * @return the acc
     */
    public long getAcc() {
        return acc;
    }

    /**
     * @param acc the acc to set
     */
    public void setAcc(long acc) {
        this.acc = acc;
    }

    /**
     * @return the counter
     */
    public long getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(long counter) {
        this.counter = counter;
    }
}
