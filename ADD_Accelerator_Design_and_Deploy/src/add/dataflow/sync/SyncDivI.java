package add.dataflow.sync;

/**
 * SyncDivI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for dividing the input by an immediate.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncDivI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncDivI() {
        super();
        setCompName("DIVI");
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
            return data / immediate;
        }
    }
}
