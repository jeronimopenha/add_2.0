package add.dataflow.async;

/**
 * AsyncSltI component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for returning the value 1 if the input is less
 * than the (immediate) id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncSltI extends AsyncGenericI {

    /**
     * Object Constructor.
     */
    public AsyncSltI() {
        super();
        setCompName("SLTI");
    }

    /**
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data Value to be used for the computation.
     * @param immediate Immediate.
     * @return - Return of computation
     */
    @Override
    public int compute(int data, int immediate) {
        setString();
        return (data < getImmediate()) ? 1 : 0;
    }
}
