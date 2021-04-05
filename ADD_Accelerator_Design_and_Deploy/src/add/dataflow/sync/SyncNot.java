package add.dataflow.sync;

/**
 * SyncNot component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for the bitwise inversion of the input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncNot extends SyncGenericUn {

    /**
     * Object Constructor.
     */
    public SyncNot() {
        super();
        setCompName("NOT");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * bitwise inversion of the parameter.
     *
     * @param data Value to be used for computing.
     * @return Returns the result of the computation. In this case the value of
     * the bitwise inversion of the parameter.
     */
    @Override
    public long compute(long data) {
        setString(Integer.toString((int) ~data));
        return ~data;
    }
}
