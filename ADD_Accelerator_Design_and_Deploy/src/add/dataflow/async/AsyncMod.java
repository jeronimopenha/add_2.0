package add.dataflow.async;

/**
 * AsyncMod component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for calculating the rest of the integer division
 * of the first input by the second one.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncMod extends AsyncGenericBin {

    /**
     * Object Constructor.
     */
    public AsyncMod() {
        super();
        setCompName("MOD");
    }

    /**
     * Method responsible for the component computation: in this case, it
     * returns the rest of the division between the parameters.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case, it returns
     * the rest of the division between the parameters.
     */
    @Override
    public int compute(int data1, int data2) {
        if (data2 == 0) {
            setString(Integer.toString(0));
            return 0;
        } else {
            setString(Integer.toString(data1 % data2));
            return data1 % data2;
        }
    }
}
