package add.dataflow.sync;

/**
 * SyncShrI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for moving all the bits of the input to the
 * right N times, where N is equal to the value of an immediate.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncShrI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncShrI() {
        super();
        setCompName("SHRI");
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
        return (data & getMaskData()) >> immediate;
    }

}
