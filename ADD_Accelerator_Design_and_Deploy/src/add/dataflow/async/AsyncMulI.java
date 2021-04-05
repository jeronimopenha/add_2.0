package add.dataflow.async;

/**
 * AsyncMulI component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for multiplying the input by a (immediate)
 * id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncMulI extends AsyncGenericI {

    /**
     * Object Constructor.
     */
    public AsyncMulI() {
        super();
        setCompName("MULI");
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
        return data * getImmediate();
    }
}
