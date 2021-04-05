package add.dataflow.sync;

/**
 * SyncBeqI component for the synchronous data flow simulator of the UFV. <br>
 * The component is responsible for comparing equality between the input and a
 * constant (immediate). Depending on the result of the comparison, the "IF"
 * output or the "ELSE" output will receive the value "1" while the other will
 * receive the value "0". <br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 2.0
 */
public class SyncBeqI extends SyncGenericBranchI {

    /**
     * Object Constructor.
     */
    public SyncBeqI() {
        super();
        setCompName("BEQI");
    }

    /**
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data Value to be used for the computation.
     * @param immediate Immediate.
     * @return - Return of computation
     */
    @Override
    public long compute(long data, long immediate) {
        setString();
        return (data == getImmediate()) ? branchCtrl.IF.getValue() : branchCtrl.ELSE.getValue();
    }
}
