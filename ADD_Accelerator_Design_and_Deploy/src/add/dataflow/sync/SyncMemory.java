package add.dataflow.sync;

import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * SyncMemory component for the UFV synchronous data flow simulator.<br>
 * The component is intended to deliver preconfigured and stored values to the
 * output according to the address given in din0.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncMemory extends add.dataflow.sync.SyncGenericI {

    private long[] mem;
    private int idxConfMem = 0;

    /**
     * Object Constructor.
     */
    public SyncMemory() {
        super();
        setCompName("MEMORY");
        idxConfMem = 0;
        mem = new long[256];
    }

    /**
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data Value to be used for the computation.
     * @param immediate Immediate.
     * @return - Return of computation
     */
    public long compute(long data, long immediate) {
        setString();
        return mem[((int) data) & 0xff];
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
        idxConfMem = 0;
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
     * Method responsible for opening the settings window for the component.
     *
     */
    @Override
    public void configure() {
        String[] fields = {"Instance name:", "name",
            "Number of bits [1, 2, 4, 8, 16 or 32]:", "width",
            "Component's index (except 0):", "id",
            "AFU Id:", "afuId"};

        propertySheet = hades.gui.PropertySheet.getPropertySheet(this, fields);
        propertySheet.setHelpText("Specify instance name, bus width, component's id and immediate.");
        propertySheet.setVisible(true);
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
        int a;
        int n_tokens = st.countTokens();
        try {
            switch (n_tokens) {
                case 0:
                    setVersionId(1001);
                    setRealWidth(18);
                    setId(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 1:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(18);
                    setId(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 2:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setId(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 3:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(1);
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 4:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 5:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    a = Integer.parseInt(st.nextToken());
                    setAfuId(0);
                    constructStandardValues();
                    constructPorts();
                    break;
                case 6:
                    setVersionId(Integer.parseInt(st.nextToken()));
                    setRealWidth(Integer.parseInt(st.nextToken()));
                    setDelay(st.nextToken());
                    setId(Integer.parseInt(st.nextToken()));
                    a = Integer.parseInt(st.nextToken());
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

    /**
     * @param immediate the immediate to set
     */
    public void setImmediate(long immediate) {
        mem[idxConfMem & 0xff] = immediate;
        idxConfMem++;
    }

}
