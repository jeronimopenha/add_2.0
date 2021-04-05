package add.dataflow.async;

/**
 * AsyncAccMin component for the UFV asynchronous data flow simulator.<br>
 * The component implements a store for the lowest input value.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncAccMin extends AsyncGenericAcc {

    private boolean firstScan;

    /**
     * Object Constructor.
     */
    public AsyncAccMin() {
        super();
        setCompName("ACC_MIN");
        firstScan = true;
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
     * is smaller, it will replace the stored value.
     *
     * @param data - Value to be used for computing.
     */
    @Override
    protected void accumulate(int data) {
        if (isFirstScan()) {
            setAcc(data);
            setFirstScan(false);
        } else if (data < getAcc()) {
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
