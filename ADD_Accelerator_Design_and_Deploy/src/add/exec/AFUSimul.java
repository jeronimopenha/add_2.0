package add.exec;

import add.dataflow.async.AsyncOut;
import add.dataflow.base.AddSimObject;
import add.dataflow.sync.SyncOut;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class AFUSimul {

    private AfuManagerSimul afuManager;

    private int id;
    private int numBits;
    private int numInputBuffer;
    private int numOutputBuffer;
    private int[] sizeOfInputBuffers;
    private int[] sizeOfOutputBuffers;
    private int[] configurations;

    private boolean done;

    private Map<Integer, int[]> inputBuffers = new TreeMap<>();
    private Map<Integer, int[]> outputBuffers = new TreeMap<>();

    public AFUSimul(AfuManagerSimul afuManager, int id, int numBits, int numInputBuffers, int numOutputBuffers) {
        this.afuManager = afuManager;
        this.numBits = numBits;
        this.numInputBuffer = numInputBuffers;
        this.numOutputBuffer = numOutputBuffers;
        this.id = id;
        this.sizeOfInputBuffers = new int[numInputBuffers];
        this.sizeOfOutputBuffers = new int[numOutputBuffers];
        this.configurations = new int[1];
    }

    public void start() {
        getAfuManager().startAFUs((long) 1 << id);
    }

    public void stop() {
        getAfuManager().stopAFUs((long) 1 << id);
    }

    /**
     * @return the done
     */
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void waitDone(long timeWaitMax) {
        boolean flagDone = false;

        while (!flagDone && timeWaitMax > 0) {
            flagDone = true;
            for (AddSimObject obj : getAfuManager().getOutList()) {
                if (obj.getAfuId() != getId()) {
                    continue;
                }
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
        stop();
        setOutputBuffers();
        setDone(flagDone);
    }

    private void setOutputBuffers() {
        ArrayList<AddSimObject> out = afuManager.getOutList();
        int[] data;
        out.sort(new Comparator<AddSimObject>() {
            @Override
            public int compare(AddSimObject o1, AddSimObject o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        int idx = 0;
        try {
            for (AddSimObject obj : out) {
                if (obj.getAfuId() != getId()) {
                    continue;
                }
                if (outputBuffers.containsKey(idx)) {
                    outputBuffers.remove(idx);
                    if (obj instanceof SyncOut) {
                        SyncOut syncObj = (SyncOut) obj;
                        data = syncObj.getVectorOut();
                    } else {
                        AsyncOut asyncObj = (AsyncOut) obj;
                        data = asyncObj.getVectorOut();
                    }
                    outputBuffers.put(idx, data);
                    idx++;
                }
            }
        } catch (Exception e) {

        }
    }

    public boolean createInputBufferSW(int bufferID, int nElements, int[] dataToCopy) {
        return createInputBufferSW(bufferID, dataToCopy);
    }

    public boolean createInputBufferSW(int bufferID, int[] dataToCopy) {
        int[] data = new int[dataToCopy.length];

        System.arraycopy(dataToCopy, 0, data, 0, data.length);

        this.inputBuffers.put(bufferID, dataToCopy);
        this.sizeOfInputBuffers[bufferID] = dataToCopy.length;
        return true;
    }

    public boolean createOutputBufferSW(int bufferID, int nElements) {
        int[] data = new int[nElements];
        this.outputBuffers.put(bufferID, data);
        this.sizeOfOutputBuffers[bufferID] = nElements;
        return true;
    }

    public void clear() {
        this.afuManager = null;
        this.setNumInputBuffer(0);
        this.setNumOutputBuffer(0);
        this.sizeOfInputBuffers = null;
        this.sizeOfOutputBuffers = null;
        this.inputBuffers.clear();
        this.outputBuffers.clear();
    }

    public int[] getInputBuffer(int BufferID) {
        if (BufferID >= 0 && BufferID < getNumInputBuffer()) {
            return this.inputBuffers.get(BufferID);
        }
        return null;
    }

    public int[] getOutputBuffer(int BufferID) {
        if (BufferID >= 0 && BufferID < getNumOutputBuffer()) {
            return this.outputBuffers.get(BufferID);
        }
        return null;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the afuManager
     */
    public AfuManagerSimul getAfuManager() {
        return afuManager;
    }

    /**
     * @param afuManager the afuManager to set
     */
    public void setAfuManager(AfuManagerSimul afuManager) {
        this.afuManager = afuManager;
    }

    /**
     * @return the numInputBuffer
     */
    public int getNumInputBuffer() {
        return numInputBuffer;
    }

    /**
     * @param numInputBuffer the numInputBuffer to set
     */
    private void setNumInputBuffer(int numInputBuffer) {
        this.numInputBuffer = numInputBuffer;
    }

    /**
     * @return the numOutputBuffer
     */
    public int getNumOutputBuffer() {
        return numOutputBuffer;
    }

    /**
     * @return the sizeOfInputBuffers
     */
    public int[] getSizeOfInputBuffers() {
        return sizeOfInputBuffers;
    }

    /**
     * @return the sizeOfOutputBuffers
     */
    public int[] getSizeOfOutputBuffers() {
        return sizeOfOutputBuffers;
    }

    /**
     * @param numOutputBuffer the numOutputBuffer to set
     */
    public void setNumOutputBuffer(int numOutputBuffer) {
        this.numOutputBuffer = numOutputBuffer;
    }

    
    public int[] getConfigurations() {
        return configurations;
    }

    
    public void setConfigurations(int[] configurations) {
        this.configurations = configurations;
    }

    /**
     * @return the numBits
     */
    public int getNumBits() {
        return numBits;
    }

    /**
     * @param numBits the numBits to set
     */
    public void setNumBits(int numBits) {
        this.numBits = numBits;
    }
}
