package add.exec;

import add.dataflow.async.AsyncGenericI;
import add.dataflow.async.AsyncIn;
import add.dataflow.async.AsyncOut;
import add.dataflow.base.AddGenericRtlibObject;
import add.dataflow.base.AddSimObject;
import add.dataflow.base.ClkConnector;
import add.dataflow.sync.SyncGenericI;
import add.dataflow.sync.SyncIn;
import add.dataflow.sync.SyncOut;
import add.util.Util;
import hades.gui.Editor;
import hades.models.Design;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import jfig.utils.SetupManager;

public class AfuManagerSimul {

    private final int AFU_INF_SIZE = 96;

    private Editor mainEditor;

    private int numInputBuffers;
    private int numOutputBuffers;
    private int numAFUs;

    private boolean commitedWorkspace;
    private boolean flagDoneAll;

    private Map<Integer, AFUSimul> AFUs = new TreeMap<>();
    //Collection of input and output objects.
    private ArrayList<AddSimObject> inList = new ArrayList<>();
    private ArrayList<AddSimObject> outList = new ArrayList<>();

    private ArrayList<Integer> listAfuId = new ArrayList<>();

    private int[] afuInfo = new int[AFU_INF_SIZE];

    public AfuManagerSimul(String designPath) {

        initSetupManager();

        this.mainEditor = new Editor(true);
        this.mainEditor.getFrame().setBounds(0, 0, 1024, 768);
        this.mainEditor.doOpenDesign(designPath, true);
        Util.setEditor(mainEditor);

        commitedWorkspace = false;

        readInfoHwAfu();
        createWorkspace();
        createAFUs();
        mainEditor.getSimulator().runForever();
    }

    public AfuManagerSimul(String designPath, boolean showEditor) {
        initSetupManager();
        this.mainEditor = new Editor(showEditor);
        this.mainEditor.getFrame().setBounds(0, 0, 1024, 768);
        this.mainEditor.doOpenDesign(designPath, true);
        Util.setEditor(mainEditor);

        commitedWorkspace = false;

        readInfoHwAfu();
        createWorkspace();
        createAFUs();
        mainEditor.getSimulator().runForever();
    }

    public AfuManagerSimul(Editor editor, boolean showEditor) {
        initSetupManager();
        this.mainEditor = editor;
        this.mainEditor.getFrame().setBounds(0, 0, 1024, 768);
        Util.setEditor(mainEditor);

        if (showEditor) {
            mainEditor.getFrame().setVisible(true);
        }

        commitedWorkspace = false;

        readInfoHwAfu();
        createWorkspace();
        createAFUs();
        mainEditor.getSimulator().runForever();
    }

    private void initSetupManager() {
        SetupManager.loadGlobalProperties("hades/.hadesrc");
        SetupManager.setProperty("Hades.Editor.AutoStartSimulation", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceBorder", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayClassLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortSymbols", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayBusPortSymbols", "true");
        SetupManager.setProperty("Hades.LayerTable.RtlibAnimation", "true");
        SetupManager.setProperty("Hades.Editor.EnableToolTips", "true");
        SetupManager.setProperty("Hades.Editor.PopupMenuResource", "/add/dataflow/base/PopupMenu.txt");
    }

    private void readInfoHwAfu() {
        getInList().clear();
        getOutList().clear();
        Design design = getMainEditor().getDesign();

        try {
            for (Enumeration<AddSimObject> e = design.getComponents(); e.hasMoreElements();) {
                AddSimObject obj = e.nextElement();

                //Num input/output queues
                if (obj instanceof SyncIn || obj instanceof AsyncIn) {
                    getInList().add(obj);
                    setNumInputBuffers(getNumInputBuffers() + 1);
                    if (obj instanceof SyncIn) {
                        SyncIn syncIn = (SyncIn) obj;
                        afuInfo[obj.getAfuId() * 3] = syncIn.getWidth();
                    }else{
                        AsyncIn asyncIn = (AsyncIn) obj;
                        afuInfo[obj.getAfuId() * 3] = asyncIn.getWidth();
                    }
                    afuInfo[obj.getAfuId() * 3 + 1]++;
                } else if (obj instanceof SyncOut || obj instanceof AsyncOut) {
                    getOutList().add(obj);
                    setNumOutputBuffers(getNumOutputBuffers() + 1);
                    afuInfo[(obj.getAfuId() * 3) + 2]++;
                }
                //Num of AFUs
                if (!getListAfuId().contains(obj.getAfuId())) {
                    getListAfuId().add(obj.getAfuId());
                }
            }
            setNumAFUs(getListAfuId().size());
        } catch (Exception e) {

        }
    }

    public AFUSimul getAFU(int id) {
        if (id < getNumAFUs()) {
            return AFUs.get(id);
        } else {
            JOptionPane.showMessageDialog(null, "AFU ID invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void simFreeBuffer(int numBytes) {
        setCommitedWorkspace(false);
    }

    public void createAFUs() {
        getAFUs().clear();
        for (int id : getListAfuId()) {
            int afuNumBits = afuInfo[id * 3];
            int afuNumInputBuffers = afuInfo[id * 3 + 1];
            int afuNumOutputBuffers = afuInfo[(id * 3) + 2];

            getAFUs().put(id, new AFUSimul(this, id, afuNumBits ,afuNumInputBuffers, afuNumOutputBuffers));
        }
    }

    public void clear() {

    }

    public void commitWorkspace() {
        getInList().sort(new Comparator<AddSimObject>() {
            @Override
            public int compare(AddSimObject o1, AddSimObject o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Set<Integer> keys = getAFUs().keySet();
        for (Integer key : keys) {
            AFUSimul afu = this.AFUs.get(key);
            int idx = 0;
            for (AddSimObject obj : this.inList) {
                if (obj.getAfuId() == afu.getId()) {
                    if (obj instanceof SyncIn) {
                        SyncIn syncObj = (SyncIn) obj;
                        syncObj.setVectorIn(afu.getInputBuffer(idx));
                        idx++;
                    } else {
                        AsyncIn asyncObj = (AsyncIn) obj;
                        asyncObj.setVectorIn(afu.getInputBuffer(idx));
                        idx++;
                    }
                }
            }

            Design design = getMainEditor().getDesign();

            try {
                for (Enumeration<AddSimObject> e = design.getComponents(); e.hasMoreElements();) {
                    AddSimObject obj = e.nextElement();
                    if (obj.getAfuId() == afu.getId()) {
                        if (obj instanceof SyncGenericI) {
                            SyncGenericI syncObj = (SyncGenericI) obj;
                            for (int i = 0; i < afu.getConfigurations().length; i++) {
                                if (syncObj.getId() == (afu.getConfigurations()[i] & 0xff)) {
                                    syncObj.setImmediate(afu.getConfigurations()[i] >> 8);
                                }
                            }
                        } else if (obj instanceof AsyncGenericI) {
                            AsyncGenericI asyncObj = (AsyncGenericI) obj;
                            for (int i = 0; i < afu.getConfigurations().length; i++) {
                                if (asyncObj.getId() == (afu.getConfigurations()[i] & 0xff)) {
                                    asyncObj.setImmediate(afu.getConfigurations()[i] >> 8);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public void startAFUs(long startAfus) {
        if (!workspaceIsCommited()) {
            commitWorkspace();
        }
        for (int id : getListAfuId()) {
            if (((startAfus >> id) & 1) == 1) {
                Design design = getMainEditor().getDesign();

                try {
                    for (Enumeration<AddSimObject> e = design.getComponents(); e.hasMoreElements();) {
                        AddSimObject obj = e.nextElement();
                        if (obj instanceof ClkConnector) {
                            continue;
                        }
                        if (obj instanceof AddGenericRtlibObject) {
                            AddGenericRtlibObject rtlibObj = (AddGenericRtlibObject) obj;
                            if (rtlibObj.getAfuId() == id) {
                                rtlibObj.reset(rtlibObj.getSimulator().getSimTime());
                                rtlibObj.setStart(true);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void stopAFUs(long stopAfus) {
        for (int id : getListAfuId()) {
            if (((stopAfus >> id) & 1) == 1) {
                Design design = getMainEditor().getDesign();

                try {
                    for (Enumeration<AddSimObject> e = design.getComponents(); e.hasMoreElements();) {
                        AddSimObject obj = e.nextElement();

                        if (obj.getAfuId() == id) {
                            obj.setStart(false);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void waitAllDone(long timeWaitMax) {
        boolean flagDone = false;

        while (!flagDone && timeWaitMax > 0) {
            flagDone = true;
            for (AddSimObject obj : getOutList()) {
                if (obj instanceof SyncOut) {
                    SyncOut syncOut = (SyncOut) obj;
                    if (!syncOut.isDone()) {
                        flagDone = false;
                        break;
                    }
                } else if (obj instanceof AsyncOut) {
                    AsyncOut asyncOut = (AsyncOut) obj;
                    if (!asyncOut.isDone()) {
                        flagDone = false;
                        break;
                    }
                }
            }
            try {
                timeWaitMax -= 30;
                Thread.currentThread().sleep(30);
            } catch (InterruptedException e) {
            }
        }
        stopAFUs(0);
        setDoneAll(flagDone);
    }

    public boolean isDoneAll() {
        return this.flagDoneAll;
    }

    public void setDoneAll(boolean doneAll) {
        boolean[] afuDoneInfo = new boolean[AFU_INF_SIZE];

        this.flagDoneAll = doneAll;

        for (int i = 0; i < afuDoneInfo.length; i++) {
            afuDoneInfo[i] = true;
        }

        for (AddSimObject obj : getOutList()) {
            if (obj instanceof SyncOut) {
                SyncOut syncOut = (SyncOut) obj;
                if (!syncOut.isDone()) {
                    afuDoneInfo[syncOut.getAfuId()] = false;
                }
            } else if (obj instanceof AsyncOut) {
                AsyncOut asyncOut = (AsyncOut) obj;
                if (!asyncOut.isDone()) {
                    afuDoneInfo[asyncOut.getAfuId()] = false;
                }
            }
        }

        Set<Integer> keys = AFUs.keySet();
        for (Integer key : keys) {
            AFUSimul afu = AFUs.get(key);
            afu.setDone(afuDoneInfo[afu.getId()]);
        }

    }

    /**
     * @return the commitedWorkspace
     */
    public boolean workspaceIsCommited() {
        return commitedWorkspace;
    }

    public boolean workspaceIscommited() {
        return false;

    }

    void printWorkspace() {

    }

    void printDSM() {

    }

    void printStatics() {

    }

    void printInfoAFUManager() {

    }

    public int getNumClConf() {
        //nothing to do here in simulator
        return 0;

    }

    public int getNumClDSM() {
        //nothing to do here in simulator
        return 0;

    }

    public boolean AFUIsSimulated() {
        //nothing to do here in simulator
        return true;
    }

    public int/*uint32_t*/ readCSR(int regID) {
        //nothing to do here in simulator
        return 0;
    }

    public void createWorkspace() {
        //nothing to do here in simulator
    }

    public void updateWorkspace() {
        //nothing to do here in simulator
    }

    public void writeCSR(/*uint32_t*/int regID, /*uint32_t*/ int val) {
        //nothing to do here in simulator
    }

    /**
     * @return the mainEditor
     */
    public Editor getMainEditor() {
        return mainEditor;
    }

    /**
     * @param mainEditor the mainEditor to set
     */
    public void setMainEditor(Editor mainEditor) {
        this.mainEditor = mainEditor;
    }

    /**
     * @return the numInputBuffers
     */
    public int getNumInputBuffers() {
        return numInputBuffers;
    }

    /**
     * @param numInputBuffers the numInputBuffers to set
     */
    public void setNumInputBuffers(int numInputBuffers) {
        this.numInputBuffers = numInputBuffers;
    }

    /**
     * @return the numOutputBuffers
     */
    public int getNumOutputBuffers() {
        return numOutputBuffers;
    }

    /**
     * @param numOutputBuffers the numOutputBuffers to set
     */
    public void setNumOutputBuffers(int numOutputBuffers) {
        this.numOutputBuffers = numOutputBuffers;
    }

    /**
     * @return the numAFUs
     */
    public int getNumAFUs() {
        return numAFUs;
    }

    /**
     * @param numAFUs the numAFUs to set
     */
    public void setNumAFUs(int numAFUs) {
        this.numAFUs = numAFUs;
    }

    /**
     * @param commitedWorkspace the commitedWorkspace to set
     */
    public void setCommitedWorkspace(boolean commitedWorkspace) {
        this.commitedWorkspace = commitedWorkspace;
    }

    /**
     * @return the AFUs
     */
    public Map<Integer, AFUSimul> getAFUs() {
        return AFUs;
    }

    /**
     * @param AFUs the AFUs to set
     */
    public void setAFUs(Map<Integer, AFUSimul> AFUs) {
        this.AFUs = AFUs;
    }

    /**
     * @return the listAfuId
     */
    private ArrayList<Integer> getListAfuId() {
        return listAfuId;
    }

    /**
     * @return the inList
     */
    private ArrayList<AddSimObject> getInList() {
        return inList;
    }

    /**
     * @param inList the inList to set
     */
    private void setInList(ArrayList<AddSimObject> inList) {
        this.inList = inList;
    }

    /**
     * @return the outList
     */
    public ArrayList<AddSimObject> getOutList() {
        return outList;
    }

    /**
     * @param outList the outList to set
     */
    private void setOutList(ArrayList<AddSimObject> outList) {
        this.outList = outList;
    }
}
