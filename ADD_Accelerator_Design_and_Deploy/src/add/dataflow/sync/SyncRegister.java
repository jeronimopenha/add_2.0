package add.dataflow.sync;

/**
 * SyncRegister component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for pass the input to the output when a clock
 * pulse occurs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncRegister extends SyncGenericUn {

    /**
     * Object Constructor.
     */
    public SyncRegister() {
        super();
        setCompName("REG");
    }
}
