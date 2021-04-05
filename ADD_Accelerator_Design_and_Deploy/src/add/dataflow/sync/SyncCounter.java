/**
 * SyncMemory component for the UFV synchronous data flow simulator.<br>
 * The component is intended to implement a counter with the possibility of
 * restarting counting.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
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
import hades.utils.StringTokenizer;

public class SyncCounter extends add.dataflow.sync.SyncGenericI {

    protected final int SET_VECTOR_WIDTH = 1;

    private long counterIni, counterEnd, counterStep, counterInc;
    private int idxConf;

    public SyncCounter() {
        super();
        setCompName("COUNTER");
        counterIni = 0;
        counterEnd = 256;
        counterStep = 1;
        counterInc = 0;
        idxConf = 0;
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "set0", Port.IN, null, SET_VECTOR_WIDTH));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));

        ports = new Port[3];
        ports[0] = getPortClk();
        ports[1] = getPortDin0();
        ports[2] = getPortDout0();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     *
     * @param time
     */
    @Override
    public void userReset(double time) {
        setIdxConf(0);
        setCounterInc(0);
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

        if (getPortClk().getSignal() == null) {
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
                StdLogicVector dIn = (StdLogicVector) signalDin0.getValue();

                if ((int) dIn.getValue() == 1) {
                    counterInc = 0;
                } else {
                    if (counterInc < counterEnd) {
                        dOut0.setValue(createDataOut(counterIni + counterInc, dataCtrl.VALID));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        counterInc += counterStep;
                    } else {
                        dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                    }
                }
            }
        }
    }

    /**
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        symbol = new Symbol();
        symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 -1300 1800 1200");
        symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1200");
        symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 1200 " + getPortClk().getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin0().getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 600 " + getPortDout0().getName());
        symbol.addMember(busportsymbol);

        setLblName(new Label());
        getLblName().initialize("0 -1000 " + getName());
        symbol.addMember(getLblName());

        Label lblComponentType = new Label();
        lblComponentType.initialize("900 600 2 " + getComponentType());
        symbol.addMember(lblComponentType);

        setStrLblId(new Label());
        getStrLblId().initialize("0 -600 ID=" + Integer.toString(getId()));
        symbol.addMember(getStrLblId());

        setStrLblImmediate(new Label());
        //getStrLblImmediate().initialize("0 -200 IM=" + Integer.toString((int) getImmediate()));
        //symbol.addMember(getStrLblImmediate());
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
            "Counter Init Value:", "counterIni",
            "Counter End Value:", "counterEnd",
            "Counter Step Value:", "counterStep",
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name, bus width, \nthe delay and the component's ID\n");
        propertySheet.setVisible(true);
    }

    /**
     * @return the counterIni
     */
    public long getCounterIni() {
        return counterIni;
    }

    /**
     * @param counterIni the counterIni to set
     */
    public void setCounterIni(long counterIni) {
        this.counterIni = counterIni;
    }

    /**
     * @param counterIni
     */
    public void setCounterIni(String counterIni) {
        try {
            this.counterIni = Long.parseLong(counterIni);
        } catch (NumberFormatException e) {
            this.counterIni = 0;
        }
    }

    /**
     * @return the counterEnd
     */
    public long getCounterEnd() {
        return counterEnd;
    }

    /**
     * @param counterEnd the counterEnd to set
     */
    public void setCounterEnd(long counterEnd) {
        this.counterEnd = counterEnd;
    }

    /**
     * @param counterEnd the counterEnd to set
     */
    public void setCounterEnd(String counterEnd) {
        try {
            this.counterEnd = Long.parseLong(counterEnd);
        } catch (NumberFormatException e) {
            this.counterEnd = 256;
        }
    }

    /**
     * @return the counterStep
     */
    public long getCounterStep() {
        return counterStep;
    }

    /**
     * @param counterStep the counterStep to set
     */
    public void setCounterStep(long counterStep) {
        this.counterStep = counterStep;
    }

    /**
     * @param counterStep the counterStep to set
     */
    public void setCounterStep(String counterStep) {
        try {
            this.counterStep = Long.parseLong(counterStep);
        } catch (NumberFormatException e) {
            this.counterStep = 1;
        }
    }

    /**
     * @param immediate the immediate to set
     */
    public void setImmediate(long immediate) {
        switch (getIdxConf()) {
            case 0:
                setCounterIni(immediate);
                setIdxConf(getIdxConf() + 1);
                break;
            case 1:
                setCounterEnd(immediate);
                setIdxConf(getIdxConf() + 1);
                break;
            case 2:
                setCounterStep(immediate);
                setIdxConf(getIdxConf() + 1);
                break;
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
                //+ " " + getImmediate()
                + " " + getCounterIni()
                + " " + getCounterEnd()
                + " " + getCounterStep()
                + " " + getAfuId()
        );
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
                    //setImmediate(1);
                    setCounterIni(0);
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(18);
                    setId(1);
                    //setImmediate(1);
                    setCounterIni(0);
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setId(1);
                    //setImmediate(1);
                    setCounterIni(0);
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(1);
                    //setImmediate(1);
                    setCounterIni(0);
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    //setImmediate(1);
                    setCounterIni(0);
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                /*case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setCounterIni(0);
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;*/
                case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    //setImmediate(Long.parseLong(st.nextToken()));
                    setCounterIni(Long.parseLong(st.nextToken()));
                    setCounterEnd(256);
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 6:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    //setImmediate(Long.parseLong(st.nextToken()));
                    setCounterIni(Long.parseLong(st.nextToken()));
                    setCounterEnd(Long.parseLong(st.nextToken()));
                    setCounterStep(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 7:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    //setImmediate(Long.parseLong(st.nextToken()));
                    setCounterIni(Long.parseLong(st.nextToken()));
                    setCounterEnd(Long.parseLong(st.nextToken()));
                    setCounterStep(Long.parseLong(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 8:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    //setImmediate(Long.parseLong(st.nextToken()));
                    setCounterIni(Long.parseLong(st.nextToken()));
                    setCounterEnd(Long.parseLong(st.nextToken()));
                    setCounterStep(Long.parseLong(st.nextToken()));
                    setAfuId(st.nextToken());
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
     * @return the counterInc
     */
    public long getCounterInc() {
        return counterInc;
    }

    /**
     * @param counterInc the counterInc to set
     */
    public void setCounterInc(long counterInc) {
        this.counterInc = counterInc;
    }

    /**
     * @return the idxConf
     */
    public int getIdxConf() {
        return idxConf;
    }

    /**
     * @param idxConf the idxConf to set
     */
    public void setIdxConf(int idxConf) {
        this.idxConf = idxConf;
    }
}
