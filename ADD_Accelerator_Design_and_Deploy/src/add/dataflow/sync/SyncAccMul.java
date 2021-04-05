package add.dataflow.sync;

/**
 * SyncAccMul component for the UFV synchronous data flow simulator.<br>
 * The component implements a multiplication accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncAccMul extends SyncGenericAcc {

    /**
     * Object Constructor.
     */
    public SyncAccMul() {
        super();
        setAcc(1);
        setCompName("ACC_MUL");
    }

    /**
     * Method responsible for actions required when "Reset" occurs.
     *
     */
    @Override
    public void userReset(double time) {
        setAcc(1);
        setCounter(getImmediate());
        setString();
    }

    /**
     * Method that accumulates the input value with the stored. In this case, it
     * multiplies the value stored by the input and stores it.
     *
     */
    @Override
    protected void accumulate(long data) {
        setAcc(getAcc() * data);
        setString();
    }
}
