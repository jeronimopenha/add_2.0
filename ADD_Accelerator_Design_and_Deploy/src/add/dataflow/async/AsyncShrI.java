package add.dataflow.async;

/**
 * AsyncShrI component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for moving all the bits of the input to the
 * right N times, where N is equal to the value of a (immediate) id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncShrI extends AsyncGenericI {

    /**
     * Object Constructor.
     */
    public AsyncShrI() {
        super();
        setCompName("SHRI");
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
        return data >> getImmediate();
    }

}
