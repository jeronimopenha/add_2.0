
package sync.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;

public class SyncShiftTest {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul("/sync/design/shiftTest.hds");
        AFUSimul afu0 = afuManager.getAFU(0);

        //int qtdeConfImmediate = 1;//um campo para a qtde de confs e outro com a conf
        int qtdeDataIn = 1;
        int qtdeDataOut = 1;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[] dataIn;
        for (int j = 0; j < qtdeIn; j++) {
            dataIn = new int[qtdeDataIn];
            for (int i = 0; i < qtdeDataIn; i++) {
                dataIn[i] = 0x2885842e;
            }
            afu0.createInputBufferSW(j, qtdeDataIn, dataIn);
            System.out.println(Integer.toHexString(dataIn[0]));
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        afu0.start();
        afu0.waitDone(40000000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Integer.toHexString(afu0.getOutputBuffer(j)[0]));
            //System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }
        afuManager.getMainEditor().doClose();
    }
}
