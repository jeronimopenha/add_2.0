package add.dataflow.sync;

/**
 * SyncDiv component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for dividing the inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncDiv extends SyncGenericBin {

    /**
     * Object Constructor.
     */
    public SyncDiv() {
        super();
        setCompName("DIV");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * division of the parameters.
     *
     * @param data1 Value to be used for the computation related to input 1.
     * @param data2 Value to be used for the computation related to input 2.
     * @return Returns the result of the computation. In this case the value of
     * the division of the parameters.
     */
    @Override
    public long compute(long data1, long data2) {
        if (data2 == 0) {
            setString(Integer.toString(0));
            return 0;
        } else {
            setString(Integer.toString((int) (data1 / data2)));
            return data1 / data2;
        }
    }
}
