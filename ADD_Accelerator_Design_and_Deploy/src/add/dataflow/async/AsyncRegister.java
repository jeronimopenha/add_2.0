package add.dataflow.async;

/**
 * AsyncRegister component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for pass the input to the output when a clock
 * pulse occurs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncRegister extends AsyncGenericUn {

    /**
     * Object Constructor.
     */
    public AsyncRegister() {
        super();
        setCompName("REG");
    }
}
