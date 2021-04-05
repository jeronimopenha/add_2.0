package add.dataflow.async;

/**
 * AsyncAccAdd component for the UFV asynchronous data flow simulator.<br>
 * The component implements an adder accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncAccAdd extends AsyncGenericAcc {

    /**
     * Object Constructor.
     */
    public AsyncAccAdd() {
        super();
        setCompName("ACC_ADD");
    }

    /**
     * Method responsible for actions required when "Reset" occurs.
     *
     */
    @Override
    public void userReset(double time) {
        setAcc(0);
        setCounter(getImmediate());
        setString();
    }

    /**
     * Method that accumulates the input value with the stored. In this case, it
     * add the value stored by the input and stores it.
     *
     */
    @Override
    protected void accumulate(int data) {
        setAcc(getAcc() + data);
        setString();
    }
}
