package add.dataflow.sync;

/**
 * SyncAndI component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for the logical operation "AND" between the
 * input and an immediate.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncAndI extends SyncGenericI {

    /**
     * Object Constructor.
     */
    public SyncAndI() {
        super();
        setCompName("ANDI");
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
        return data & immediate;
    }

}
