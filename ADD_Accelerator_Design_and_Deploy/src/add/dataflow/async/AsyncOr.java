package add.dataflow.async;

/**
 * AsyncOr component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for the logical operation "AsyncOr" between the
 * input<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncOr extends AsyncGenericBin {

    /**
     * Object Constructor.
     */
    public AsyncOr() {
        super();
        setCompName("OR");
    }

    /**
     * Method responsible for the component computation: in this case it
     * performs the logical operation "AsyncOr" between the parameters.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case the result
     * of the logical operation "AsyncOr" between the parameters.
     */
    @Override
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1 | data2));
        return data1 | data2;
    }
}
