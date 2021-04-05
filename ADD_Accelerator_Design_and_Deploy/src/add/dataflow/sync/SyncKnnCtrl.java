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

public class SyncKnnCtrl extends SyncGenericUn {

    public static final int CTRL_WIDTH = 2;

    private PortStdLogicVector portDout1;

    private long knnDistance;

    public SyncKnnCtrl() {
        super();
        setCompName("KNNCTRL");
        setWidth(16);
        knnDistance = (1 << getWidth()-1) - 1;
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));
        setPortDout1(new PortStdLogicVector(this, "dout1", Port.OUT, null, CTRL_WIDTH));

        ports = new Port[4];
        ports[0] = getPortClk();
        ports[1] = getPortDin0();
        ports[2] = getPortDout0();
        ports[3] = getPortDout1();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    @Override
    public void userReset(double time) {
        Signal signalDout1;
        //para portDout1
        if ((signalDout1 = getPortDout1().getSignal()) != null) {
            StdLogicVector dOut1 = new StdLogicVector(CTRL_WIDTH, 0);
            getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));
        }
        setKnnDistance((1 << getWidth()-1) - 1);
    }

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
                StdLogicVector dIn = (StdLogicVector) signalDin0.getValue();

                switch (getCtrl(dIn.getValue())) {
                    case DONE:
                        if (signalDout0 != null) {
                            dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        }

                        dOut1.setValue(2);
                        getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                        setString("DONE");
                        break;
                    case STOP:
                        if (signalDout0 != null) {
                            dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        }
                        dOut1.setValue(0);
                        getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                        setString("STOP");
                        break;
                    case VALID:
                        if (getData(dIn.getValue()) < getKnnDistance()) {
                            if (signalDout0 != null) {
                                dOut0.setValue(createDataOut(getKnnDistance(), dataCtrl.VALID));
                                getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                            }
                            setKnnDistance(getData(dIn.getValue()));

                            dOut1.setValue(1);
                            getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));

                        } else {
                            if (signalDout0 != null) {
                                dOut0.setValue(dIn.getValue());
                                getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                            }

                            dOut1.setValue(0);
                            getSimulator().scheduleEvent(new SimEvent(signalDout1, time, dOut1, getPortDout1()));
                        }
                        setString(Integer.toString((int) getKnnDistance()));
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
        bbr.initialize("0 -900 1800 1200");
        this.symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1200");
        this.symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 1200 " + getPortClk().getName());
        this.symbol.addMember(portsymbol);

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
        lblComponentType.initialize("900 600 2 " + getComponentType());
        this.symbol.addMember(lblComponentType);

        setStringLabel(new Label());
        getStringLabel().initialize("0 -200 " + getS());
        this.symbol.addMember(getStringLabel());
    }

    /**
     * @return the portDout1
     */
    private PortStdLogicVector getPortDout1() {
        return portDout1;
    }

    /**
     * @param portDout1 the portDout1 to set
     */
    private void setPortDout1(PortStdLogicVector portDout1) {
        this.portDout1 = portDout1;
    }

    /**
     * @return the knnDistance
     */
    private long getKnnDistance() {
        return knnDistance;
    }

    /**
     * @param knnDistance the knnDistance to set
     */
    private void setKnnDistance(long knnDistance) {
        this.knnDistance = knnDistance;
    }
}
