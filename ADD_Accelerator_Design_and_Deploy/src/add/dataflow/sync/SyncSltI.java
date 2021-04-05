package add.dataflow.sync;

/**
 * SyncSltI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for returning the value 1 if the input is less
 * than the immediate.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncSltI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncSltI() {
        super();
        setCompName("SLTI");
    }

    /**
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data Value to be used for the computation.
     * @param immediate Immediate.
     * @return Return of computation
     */
    @Override
    public long compute(long data, long immediate) {
        setString();
        return (data < immediate) ? 1 : 0;
    }
}
