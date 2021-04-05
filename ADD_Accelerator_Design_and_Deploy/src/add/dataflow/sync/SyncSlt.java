package add.dataflow.sync;

/**
 * SyncSlt component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for returning the value 1 if the first input is
 * less than the second one.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncSlt extends SyncGenericBin {

    /**
     * Object Constructor.
     */
    public SyncSlt() {
        super();
        setCompName("SLT");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * comparison if the first parameter is less than the other one.
     *
     * @param data1 Value to be used for the computation related to input 1.
     * @param data2 Value to be used for the computation related to input 2.
     * @return Returns the result of the computation. In this case 1 or 0
     * depending on the comparison between the parameters.
     */
    @Override
    public long compute(long data1, long data2) {
        setString(Integer.toString((data1 < data2) ? 1 : 0));
        return (data1 < data2) ? 1 : 0;
    }
}
