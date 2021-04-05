package add.dataflow.sync;

/**
 * SyncBeq component for the synchronous data flow simulator of the UFV. <br>
 * The component is responsible for comparing equality between the input.
 * Depending on the result of the comparison, the "IF" output or the "ELSE"
 * output will receive the value "1" while the other will receive the value "0".
 * <br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncBeq extends SyncGenericBranch {

    /**
     * Object Constructor.
     */
    public SyncBeq() {
        super();
        setCompName("BEQ");
    }

    /**
     * Method responsible for component computing: in this case performs a
     * comparison of equality between the input. Depending on the result of the
     * comparison, the "IF" output or the "ELSE" output will receive the value
     * "1" while the other will receive the value "0".
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case "1" if the
     * parameters are equal or "0" if they are different.
     */
    @Override
    public long compute(long data1, long data2) {
        setString(Integer.toString((data1 == data2) ? 1 : 0));
        return (data1 == data2) ? branchCtrl.IF.getValue() : branchCtrl.ELSE.getValue();
    }
}
