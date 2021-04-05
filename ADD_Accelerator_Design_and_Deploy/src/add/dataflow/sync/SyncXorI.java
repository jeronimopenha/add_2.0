package add.dataflow.sync;

/**
 * SyncAddI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for for execute logic function xor in the input
 * by an immediate.<br>
 *
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncXorI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncXorI() {
        super();
        setCompName("XORI");
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
        return data ^ immediate;
    }
}
