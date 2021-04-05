package add.dataflow.async;

/**
 * AsyncAccMul component for the UFV synchronous data flow simulator.<br>
 * The component implements a multiplication accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.
 * @version 2.0
 */
public class AsyncAccMul extends AsyncGenericAcc {

    /**
     * Object Constructor.
     */
    public AsyncAccMul() {
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
    protected void accumulate(int data) {
        setAcc(getAcc() * data);
        setString();
    }
}
