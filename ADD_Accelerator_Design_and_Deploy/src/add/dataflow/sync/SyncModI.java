package add.dataflow.sync;

/**
 * SyncModI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for calculating the rest of the integer division
 * of the input by a id (immediate).<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncModI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncModI() {
        super();
        setCompName("MODI");
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
        if (immediate == 0) {
            setString();
            return 0;
        } else {
            setString();
            return data % immediate;
        }
    }
}
