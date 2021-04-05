package add.dataflow.sync;

/**
 * SyncAnd component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for the logical operation "SyncAnd" between the
 * input<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncAnd extends SyncGenericBin {

    /**
     * Object Constructor.
     */
    public SyncAnd() {
        super();
        setCompName("AND");
    }

    /**
     * Method responsible for the component computation: in this case it
     * performs the logical operation "SyncAnd" between the parameters.
     *
     * @param data1 Value to be used for the computation related to input 1.
     * @param data2 Value to be used for the computation related to input 2.
     * @return Returns the result of the computation. In this case the result of
     * the logical operation "SyncAnd" between the parameters.
     */
    @Override
    public long compute(long data1, long data2) {
        setString(Integer.toString((int) (data1 & data2)));
        return data1 & data2;
    }
}
