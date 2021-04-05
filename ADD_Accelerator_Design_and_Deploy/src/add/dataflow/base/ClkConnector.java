package add.dataflow.base;

import add.util.Util;
import hades.models.PortStdLogic1164;
import hades.simulator.Port;
import hades.symbols.BboxRectangle;
import hades.symbols.Label;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

public class ClkConnector extends add.dataflow.sync.SyncGenericUn {

    private boolean connectClkWires;
    //private Timer clkSupervisor;

    public ClkConnector() {
        super();
        //connectClkWires = false;
        //clkSupervisor = new Timer();
        //clkSupervisor.schedule(new ConnectorTimerTaskHanler(), 1000, 1000);
    }

    /*class ConnectorTimerTaskHanler extends TimerTask {

        @Override
        public void run() {
            if (getEditor() != null) {
                if (isConnectClkWires()) {
                    Util.connectClkWire();
                } else {
                    Util.disconnectClkWire();
                }
            }
        }
    }*/
    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));

        ports = new Port[1];
        ports[0] = getPortClk();
    }

    @Override
    public void evaluate(Object arg) {
    }

    /**
     * Method responsible for opening the settings window for the component.
     *
     */
    @Override
    public void configure() {
        String[] fields = {"Instance name:", "name"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("To use the component, click on it.");
        propertySheet.setVisible(true);
    }

    /**
     * Method responsible for indicating to the simulator that the component's
     * symbol will be constructed dynamically by the constructDynamicSymbol()
     * method, or will be read from a file of the same name as the ".sym"
     * extension.
     *
     * @return TRUE means that the symbol will be built dynamically.
     */
    @Override
    public boolean needsDynamicSymbol() {
        return true;
    }

    /**
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        this.symbol = new Symbol();
        this.symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 0 1800 1200");
        this.symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1200");
        this.symbol.addMember(rec);

        Label label = new Label();
        label.initialize("900 600 2 ClkCon");
        this.symbol.addMember(label);
    }

    /**
     * Method responsible for writing component settings to the file saved by
     * the simulator.
     *
     * @param ps -Simulator writing object.
     */
    @Override
    public void write(java.io.PrintWriter ps) {
        int conected = (isConnectClkWires() ? 1 : 0);
        ps.print(" " + getVersionId()
                + " " + conected);
    }

    /**
     * Method responsible for reading the component settings in the file saved
     * by the simulator.
     *
     * @param s Settings for the component read from the file saved by the
     * simulator.
     * @return Returns true if the settings are read successfully.
     */
    @Override
    public boolean initialize(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int n_tokens = st.countTokens();
        try {
            switch (n_tokens) {
                case 0:
                    setVersionId(1001);
                    setConnectClkWires(false);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setConnectClkWires(false);
                    constructStandardValues();
                    constructPorts();
                    initMasks();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setConnectClkWires((Integer.parseInt(st.nextToken()) == 1));
                    constructStandardValues();
                    constructPorts();
                    initMasks();
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

    @Override
    public void mousePressed(java.awt.event.MouseEvent me) {
        FrmClkRstConnector frmClkRstConnector = new FrmClkRstConnector(this);
        frmClkRstConnector.show();
    }

    /**
     * @return the connectClkWires
     */
    public boolean isConnectClkWires() {
        return connectClkWires;
    }

    /**
     * @param connectClkWires the connectClkWires to set
     */
    public void setConnectClkWires(boolean connectClkWires) {
        this.connectClkWires = connectClkWires;
    }

    public void connectWires() {
        if (isConnectClkWires()) {
            Util.connectClkWire();
        } else {
            Util.disconnectClkWire();
        }
    }

}
