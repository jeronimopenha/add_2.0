package add.exec;

import java.util.Map;
import java.util.TreeMap;

public class AFUFpga {

    private AfuManagerFpga afuManager;

    private int id;
    private int numBits;
    private int numInputBuffer;
    private int numOutputBuffer;
    private int[] sizeOfInputBuffers;
    private int[] sizeOfOutputBuffers;
    private int[] configurations;

    public int [] idxBuffersIn;
    public int [] idxBuffersOut;
    
    public boolean afuDone;
    public boolean[] rdDone, wrDone;

    public Map<Integer, int[]> inputBuffers = new TreeMap<>();
    public Map<Integer, int[]> outputBuffers = new TreeMap<>();

    public AFUFpga(AfuManagerFpga afuManager, int id, int numBits, int numInputBuffers, int numOutputBuffers) {
        this.afuManager = afuManager;
        this.numBits = numBits;
        this.numInputBuffer = numInputBuffers;
        this.numOutputBuffer = numOutputBuffers;
        this.id = id;
        this.sizeOfInputBuffers = new int[numInputBuffers];
        this.sizeOfOutputBuffers = new int[numOutputBuffers];
        this.configurations = new int[1];
        this.rdDone = new boolean[numInputBuffers];
        this.wrDone = new boolean[numOutputBuffers];
    }

    public void start() {
        for (int i = 0; i < wrDone.length; i++) {
            wrDone[i] = false;
        }

        for (int i = 0; i < rdDone.length; i++) {
            rdDone[i] = false;
        }
        afuDone = false;
        getAfuManager().startAFUs(1 << id);
    }

    public void stop() {
        getAfuManager().stopAFUs(1 << id);
    }

    /**
     * @return the afuDone
     */
    public boolean isDone() {
        return afuDone;
    }

    public void setDone(boolean afuDone) {
        this.afuDone = afuDone;
    }

    public void waitDone(long timeWaitMax) {
        boolean flagDone = false;

        while (!flagDone && timeWaitMax > 0) {
            flagDone = true;
            for (int i = 0; i < wrDone.length; i++) {
                if (!wrDone[i]) {
                    flagDone = false;
                }
            }
            for (int i = 0; i < rdDone.length; i++) {
                if (!rdDone[i]) {
                    flagDone = false;
                }
            }
            if (!afuDone) {
                flagDone = false;
            } 
            try {
                timeWaitMax -= 10;
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("DONE");
        stop();
    }

    public boolean createInputBufferSW(int bufferID, int nElements, int[] dataToCopy) {
        return createInputBufferSW(bufferID, dataToCopy);
    }

    public boolean createInputBufferSW(int bufferID, int[] dataToCopy) {
        int[] data;
        if (dataToCopy.length % 4 == 0) {
            data = new int[dataToCopy.length];
        } else {
            data = new int[dataToCopy.length + (4 - dataToCopy.length % 4)];
        }

        System.arraycopy(dataToCopy, 0, data, 0, dataToCopy.length);

        this.inputBuffers.put(bufferID, data);
        this.sizeOfInputBuffers[bufferID] = data.length;
        return true;
    }

    public boolean createOutputBufferSW(int bufferID, int nElements) {
        int[] data;
        if (nElements % 4 == 0) {
            data = new int[nElements];
        } else {
            data = new int[nElements + (4 - nElements % 4)];
        }
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
    public AfuManagerFpga getAfuManager() {
        return afuManager;
    }

    /**
     * @param afuManager the afuManager to set
     */
    public void setAfuManager(AfuManagerFpga afuManager) {
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
