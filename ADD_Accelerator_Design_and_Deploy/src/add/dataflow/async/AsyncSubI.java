package add.dataflow.async;

/**
 * AsyncSubI component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for subtracting the input by a id
 * (immediate).<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncSubI extends AsyncGenericI {

    /**
     * Object Constructor.
     */
    public AsyncSubI() {
        super();
        setCompName("SUBI");
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
        return data - getImmediate();
    }
}
