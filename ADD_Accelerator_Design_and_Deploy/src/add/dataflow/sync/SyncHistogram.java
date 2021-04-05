package add.dataflow.sync;

import add.dataflow.base.AddSimObject;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.utils.StringTokenizer;

/**
 * SyncHistogram component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for computing the amount of times a given value
 * is delivered at its input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncHistogram extends SyncGenericI {

    private int[] histogram;
    private int counter;
    private int decr;
    private int widthCounter;
    private int searchInit;
    private int searchEnd;

    /**
     * copy(): This function is used to create a clone of this RTLIB object,
     * including the values for width (n_bits), current value (vector),
     * propagation delay, and version ID.
     * <p>
     */
    @Override
    public AddSimObject copy() {
        SyncHistogram tmp = null;
        try {
            tmp = this.getClass().newInstance();
            tmp.setEditor(this.getEditor());
            tmp.setVisible(this.isVisible());
            tmp.setName(this.getName());
            tmp.setClassLoader(this.getClassLoader());
            tmp.setWidth(this.n_bits - 2);
            tmp.setDelay(this.getDelay());
            tmp.setVersionId(this.getVersionId());
            tmp.setHistogram(this.getHistogram());
            tmp.setCounter(this.getCounter());
            tmp.setDecr(this.getDecr());
            tmp.setWidthCounter(this.getWidthCounter());
            tmp.setSearchInit(this.getSearchInit());
            tmp.setSearchEnd(this.getSearchEnd());
            tmp.setStrLblId(this.getStrLblId());
            tmp.setStrLblImmediate(this.getStrLblImmediate());
            tmp.setLblName(this.getLblName());
            tmp.setComponentType(this.getComponentType());
            tmp.setAfuId(this.getAfuId());
            tmp.setStart(this.isStart());
            return (AddSimObject) tmp;
        } catch (IllegalAccessException | InstantiationException e) {
            message("-E- Internal error in SyncHistogram.copy(): " + e);
            jfig.utils.ExceptionTracer.trace(e);
            return null;
        }
    }

    /**
     * Object Constructor.
     */
    public SyncHistogram() {
        super();
        this.counter = 0;
        this.decr = 0;
        this.widthCounter = 4;
        this.searchInit = 0;
        this.searchEnd = 255;
        setCompName("HIST");
    }

    /**
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data Value to be used for the computation.
     * @param immediate Immediate.
     * @return - Return of computation
     */
    @Override
    public long compute(long data, long immediate) {
        setString();
        try {
            return getHistogram()[(int) (data - getSearchInit())]++;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     */
    @Override
    public void reset(double time) {
        Signal signalDout0;
        //para portDout
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getRealWidth(), 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }
        setCounter(0);
        setDecr(0);
        setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
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

        Signal signalDin0, signalDout0;

        boolean hasDisconnectedPorts = false;

        if ((getPortClk().getSignal()) == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDin0().getSignal() == null) {
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
                signalDout0 = getPortDout0().getSignal();
                StdLogicVector dIn0 = (StdLogicVector) signalDin0.getValue();

                switch (getCtrl(dIn0.getValue())) {
                    case DONE:
                        if (getCounter() > 0) {
                            if (getDecr() < getHistogram().length) {
                                dOut0.setValue(createDataOut(getHistogram()[getDecr()], dataCtrl.VALID));
                                getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                                setDecr(getDecr() + 1);
                            } else {
                                dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                                getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                                setCounter(0);
                                setDecr(0);
                                setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                            }
                        } else {
                            dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        }
                        setString();
                        break;
                    case STOP:
                        dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

                        setString();
                        break;
                    case VALID:
                        if (getCounter() < getImmediate()) {
                            compute(getData(dIn0.getValue()), getImmediate());
                            setCounter(getCounter() + 1);
                        } else {
                            if (getDecr() < getHistogram().length) {
                                dOut0.setValue(createDataOut(getHistogram()[getDecr()], dataCtrl.VALID));
                                getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                                setDecr(getDecr() + 1);
                            } else {
                                setCounter(0);
                                setDecr(0);
                                setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                            }
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
                + " " + getWidthCounter()
                + " " + getSearchInit()
                + " " + getSearchEnd()
                + " " + getAfuId());
    }

    /**
     * Method responsible for reading the component settings in the file saved
     * by the simulator.
     *
     * @param s - Settings for the component read from the file saved by the
     * simulator.
     * @return - Returns true if the settings are read successfully.
     */
    @Override
    public boolean initialize(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int n_tokens = st.countTokens();
        try {
            switch (n_tokens) {
                case 0:
                    setVersionId(1001);
                    setRealWidth(18);
                    setId(1);
                    setImmediate(1);
                    setWidthCounter(4);
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(18);
                    setId(1);
                    setImmediate(1);
                    setWidthCounter(4);
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setId(1);
                    setImmediate(1);
                    setWidthCounter(4);
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(1);
                    setImmediate(1);
                    setWidthCounter(4);
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(1);
                    setWidthCounter(4);
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setWidthCounter(4);
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 6:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setWidthCounter(Integer.parseInt(st.nextToken()));
                    setSearchInit(0);
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 7:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setWidthCounter(Integer.parseInt(st.nextToken()));
                    setSearchInit(Integer.parseInt(st.nextToken()));
                    setSearchEnd(255);
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 8:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setWidthCounter(Integer.parseInt(st.nextToken()));
                    setSearchInit(Integer.parseInt(st.nextToken()));
                    setSearchEnd(Integer.parseInt(st.nextToken()));
                    setAfuId(0);
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 9:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setWidthCounter(Integer.parseInt(st.nextToken()));
                    setSearchInit(Integer.parseInt(st.nextToken()));
                    setSearchEnd(Integer.parseInt(st.nextToken()));
                    setAfuId(st.nextToken());
                    setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
                    constructStandardValues();
                    constructPorts();
                    break;
                default:
                    throw new Exception("invalid number of arguments");
            }
        } catch (Exception e) {
            message("-E- " + toString() + ".initialize(): " + e + " " + s);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Create and display a 'ConfigDialog' to set the parameters for th object.
     */
    @Override
    public void configure() {
        String[] fields = {"Component Name:", "name",
            "Number of bits [1 .. 32]:", "width",
            "Output Delay [sec]:", "delay",
            "Component ID (Except 0):", "id",
            "Component's immediate", "immediate",
            "Counter Width (bits):", "widthCounter",
            "Search Init Value:", "searchInit",
            "Search End Value:", "searchEnd",
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name, bus width, \nthe delay and the component's ID\n");
        propertySheet.setVisible(true);
    }

    /**
     * @return the histogram
     */
    public int[] getHistogram() {
        return histogram;
    }

    /**
     * @param histogram the histogram to set
     */
    public void setHistogram(int[] histogram) {
        this.histogram = histogram;
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

    /**
     * @return the decr
     */
    public int getDecr() {
        return decr;
    }

    /**
     * @param decr the decr to set
     */
    public void setDecr(int decr) {
        this.decr = decr;
    }

    /**
     * @return the widthCounter
     */
    public int getWidthCounter() {
        return widthCounter;
    }

    /**
     * @param widthCounter the widthCounter to set
     */
    public void setWidthCounter(int widthCounter) {
        this.widthCounter = widthCounter;
    }

    /**
     * @param widthCounter the widthCounter to set
     */
    public void setWidthCounter(String widthCounter) {
        if (widthCounter.equals("0")) {
            widthCounter = "1";
        }
        try {
            this.widthCounter = Integer.parseInt(widthCounter);
        } catch (NumberFormatException e) {
            this.widthCounter = 4;
        }
    }

    /**
     * @return the searchInit
     */
    public int getSearchInit() {
        return searchInit;
    }

    /**
     * @param searchInit the searchInit to set
     */
    public void setSearchInit(int searchInit) {
        this.searchInit = searchInit;
        setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
    }

    /**
     * @param searchInit the searchInit to set
     */
    public void setSearchInit(String searchInit) {
        try {
            this.searchInit = Integer.parseInt(searchInit);
            if (this.searchEnd < this.searchInit) {
                this.searchEnd = this.searchInit;
            }
        } catch (NumberFormatException e) {
            this.searchInit = 0;
        }
    }

    /**
     * @return the searchEnd
     */
    public int getSearchEnd() {
        return searchEnd;
    }

    /**
     * @param searchEnd the searchEnd to set
     */
    public void setSearchEnd(int searchEnd) {
        this.searchEnd = searchEnd;
        setHistogram(new int[getSearchEnd() - getSearchInit() + 1]);
    }

    /**
     * @param searchEnd the searchEnd to set
     */
    public void setSearchEnd(String searchEnd) {
        try {
            this.searchEnd = Integer.parseInt(searchEnd);
            if (this.searchEnd < this.searchInit) {
                this.searchEnd = this.searchInit;
            }
        } catch (NumberFormatException e) {
            this.searchEnd = 255;
        }
    }
}
