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

public class SyncKnnQueue extends SyncGenericUn {

    private PortStdLogicVector portDin1;

    private long knnValue;

    private boolean lastScan;

    public SyncKnnQueue() {
        super();
        setCompName("KNN");
        setWidth(16);
        lastScan = false;
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDin0(new PortStdLogicVector(this, "din0", Port.IN, null, SyncKnnCtrl.CTRL_WIDTH));
        setPortDin1(new PortStdLogicVector(this, "din1", Port.IN, null, getRealWidth()));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getRealWidth()));

        ports = new Port[4];
        ports[0] = getPortClk();
        ports[1] = getPortDin0();
        ports[2] = getPortDin1();
        ports[3] = getPortDout0();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    public void userReset(double time) {
        lastScan = false;
    }

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

                switch (getCtrl(dIn1.getValue())) {
                    case OPT:
                        if (dIn0.getValue() == 0) {
                            dOut0.setValue(dIn1.getValue());
                        } else if (dIn0.getValue() == 1) {
                            dOut0.setValue(createDataOut(getKnnValue(), dataCtrl.OPT));
                            setKnnValue(getData(dIn1.getValue()));
                        }
                        setString(Integer.toString((int) getKnnValue()));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        break;
                    case VALID:
                        if (dIn0.getValue() == 0) {
                            dOut0.setValue(createDataOut(getData(dIn1.getValue()), dataCtrl.OPT));
                        } else if (dIn0.getValue() == 1) {
                            dOut0.setValue(createDataOut(getKnnValue(), dataCtrl.OPT));
                            setKnnValue(getData(dIn1.getValue()));
                        } else {
                            dOut0.setValue(createDataOut(getKnnValue(), dataCtrl.VALID));
                            setKnnValue(getData(dIn1.getValue()));
                        }
                        setString(Integer.toString((int) getKnnValue()));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        break;
                    case DONE:
                        if (!isLastScan()) {
                            dOut0.setValue(createDataOut(getKnnValue(), dataCtrl.VALID));
                            setLastScan(true);
                        } else {
                            dOut0.setValue(createDataOut(0, dataCtrl.DONE));
                        }
                        setString("DONE");
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        break;
                    case STOP:
                        dOut0.setValue(createDataOut(0, dataCtrl.STOP));
                        getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
                        setString("STOP");
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
        busportsymbol.initialize("0 0 " + getPortDin0().getName());
        this.symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 600 " + getPortDin1().getName());
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
     * @return the portDin1
     */
    private PortStdLogicVector getPortDin1() {
        return portDin1;
    }

    /**
     * @param portDin1 the portDin1 to set
     */
    private void setPortDin1(PortStdLogicVector portDin1) {
        this.portDin1 = portDin1;
    }

    /**
     * @return the knnValue
     */
    private long getKnnValue() {
        return knnValue;
    }

    /**
     * @param knnValue the knnValue to set
     */
    private void setKnnValue(long knnValue) {
        this.knnValue = knnValue;
    }

    /**
     * @return the lastScan
     */
    private boolean isLastScan() {
        return lastScan;
    }

    /**
     * @param lastScan the lastScan to set
     */
    private void setLastScan(boolean lastScan) {
        this.lastScan = lastScan;
    }
}
