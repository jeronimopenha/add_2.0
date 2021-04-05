package add.dataflow.async;

/**
 * AsyncMax component for the UFV asynchronous data flow simulator.<br>
 * The component is responsible for passing the output to the largest value
 * input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class AsyncMax extends AsyncGenericBin {

    /**
     * Object Constructor.
     */
    public AsyncMax() {
        super();
        setCompName("MAX");
    }

    /**
     * Method responsible for the computation of components: in this case, it
     * performs a comparison between the parameters and returns the largest
     * between the two.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case, the
     * largest of the parameters.
     */
    @Override
    public int compute(int data1, int data2) {
        if (data1 > data2) {
            setString(Integer.toString(data1));
            return data1;
        } else {
            setString(Integer.toString(data2));
            return data2;
        }
    }
}
