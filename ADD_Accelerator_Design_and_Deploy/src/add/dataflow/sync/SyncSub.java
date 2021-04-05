package add.dataflow.sync;

/**
 * SyncSub component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for subtracting the inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncSub extends SyncGenericBin {

    /**
     * Object Constructor.
     */
    public SyncSub() {
        super();
        setCompName("SUB");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * subtraction of the parameters.
     *
     * @param data1 Value to be used for the computation related to input 1.
     * @param data2 Value to be used for the computation related to input 2.
     * @return Returns the result of the computation. In this case the value
     * of the subtraction of the parameters.
     */
    @Override
    public long compute(long data1, long data2) {
        setString(Integer.toString((int) (data1 - data2)));
        return data1 - data2;
    }

}
