package add.dataflow.async;

/**
 * AsyncOrI component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for the logical operation "OR" between the input
 * and a id (immediate)<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncOrI extends AsyncGenericI {

    /**
     * Object Constructor.
     */
    public AsyncOrI() {
        super();
        setCompName("ORI");
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
        return data | getImmediate();
    }

}
