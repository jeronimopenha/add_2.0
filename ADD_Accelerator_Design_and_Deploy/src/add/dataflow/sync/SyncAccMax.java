package add.dataflow.sync;

/**
 * SyncAccMax component for the UFV synchronous data flow simulator.<br>
 * The component implements a store for the highest input value.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncAccMax extends SyncGenericAcc {

    private boolean firstScan;

    /**
     * Object Constructor.
     */
    public SyncAccMax() {
        super();
        setCompName("ACC_MAX");
        this.firstScan = true;
    }

    /**
     * Method responsible for actions required when "Reset" occurs.
     *
     */
    @Override
    public void userReset(double time) {
        setAcc(0);
        setCounter(getImmediate());
        setFirstScan(true);
        setString();
    }

    /**
     * Method that compares the parameter to the stored value. If the parameter
     * is larger, it will override the stored value.
     *
     * @param data - Value to be used for computing.
     */
    @Override
    protected void accumulate(long data) {
        if (isFirstScan()) {
            setAcc(data);
            setFirstScan(false);
        } else if (data > getAcc()) {
            setAcc(data);
        }
        setString();
    }

    /**
     * @return the firstScan
     */
    public boolean isFirstScan() {
        return firstScan;
    }

    /**
     * @param firstScan the firstScan to set
     */
    public void setFirstScan(boolean firstScan) {
        this.firstScan = firstScan;
    }
}
