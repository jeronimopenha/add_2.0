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
import java.awt.HeadlessException;
import javax.swing.JOptionPane;

public class SyncConst extends add.dataflow.sync.SyncGenericI {

    public SyncConst() {
        super();
        setCompName("CONST");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortDout0(new PortStdLogicVector(this, "dout0", Port.OUT, null, getWidth()));

        ports = new Port[2];
        ports[0] = getPortClk();
        ports[1] = getPortDout0();
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     */
    public void reset(double time) {
        Signal signalDout0;
        //para portDout0
        if ((signalDout0 = getPortDout0().getSignal()) != null) {
            StdLogicVector dOut0 = new StdLogicVector(getWidth(), getImmediate() & getMaskData());
            getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));
        }
        setString();
        userReset(time);
    }

    @Override
    public void evaluate(Object arg) {

        double time = getSimulator().getSimTime() + getDelay();

        Signal signalDout0;

        boolean hasDisconnectedPorts = false;

        if (getPortClk().getSignal() == null) {
            hasDisconnectedPorts = true;
        } else if (getPortDout0().getSignal() == null) {
            hasDisconnectedPorts = true;
        }

        StdLogicVector dOut0 = new StdLogicVector(getWidth());

        if (hasDisconnectedPorts) {
            reset(time);
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge() && isStart()) {

                signalDout0 = getPortDout0().getSignal();

                dOut0.setValue(getImmediate() & getMaskData());
                getSimulator().scheduleEvent(new SimEvent(signalDout0, time, dOut0, getPortDout0()));

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
        getStrLblImmediate().initialize("0 -200 IM=" + Integer.toString((int) getImmediate()));
        symbol.addMember(getStrLblImmediate());
    }

    /**
     * Method responsible for changing bus size according to user settings.
     *
     * @param s String that brings the size of the bus chosen by the user.
     */
    @Override
    public void setWidth(String s) {
        int n;
        try {
            n = Integer.parseInt(s);
            if (isConnected() && n != getWidth()) {
                JOptionPane.showMessageDialog(null, "GenericI.setWidth: The component bus size can not be changed "
                        + "\nwhile it is connected to another component.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "GenericI.setWidth: illegal argument" + e,
                    "Warning", JOptionPane.ERROR_MESSAGE);
            n = this.n_bits; // default width
        }
        setWidth(n);
    }

    /**
     * Method responsible for changing bus size according to user settings.
     *
     * @param _n Integer that brings the size of the bus chosen by the user
     */
    @Override
    public void setWidth(int _n) {
        if (isConnected() && _n != getWidth()) {
            JOptionPane.showMessageDialog(null, "Cannot change the width of an connected ADD-Tool object!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if ((getWidth() < 1) || (getWidth() > 32)) {
            JOptionPane.showMessageDialog(null, "Bus width out of range [1..32], using 16 instead!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.n_bits = 16;
        } else {
            this.n_bits = _n;
        }
        constructStandardValues();
        constructPorts();
        updateSymbol();
        initMasks();
    }

    /**
     * Returns the size of the data bus.
     *
     * @return Size of the data bus.
     */
    @Override
    public int getWidth() {
        return this.n_bits;
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
                + " " + getWidth()
                + " " + getDelay()
                + " " + getId()
                + " " + getImmediate()
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
                    setWidth(18);
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setWidth(18);
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setWidth(Integer.parseInt(st.nextToken()));
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(1);
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 6:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setImmediate(Long.parseLong(st.nextToken()));
                    setAfuId(st.nextToken());
                    constructStandardValues();
                    constructPorts();
                    break;
                default:
                    throw new Exception("invalid number of arguments");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, toString() + ".initialize(): " + e + " " + s,
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return true;
    }
}
