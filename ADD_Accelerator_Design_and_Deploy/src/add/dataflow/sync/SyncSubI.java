package add.dataflow.sync;

/**
 * SyncSubI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for subtracting the input by an immediate.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncSubI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncSubI() {
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
    public long compute(long data, long immediate) {
        setString();
        return data - immediate;
    }
}
