package sync.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import java.util.Arrays;

public class Sync1SimSearchFilter {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul("/sync/design/simSearchFilter.hds");
        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 30;
        int qtdeDataOut = 30;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[] dataIn;
        for (int j = 0; j < qtdeIn; j++) {
            dataIn = new int[qtdeDataIn];
            for (int i = 0; i < qtdeDataIn; i++) {
                dataIn[i] = (int) (Math.random() * 10);//random Data
            }
            afu0.createInputBufferSW(j, qtdeDataIn, dataIn);
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        afu0.start();
        afu0.waitDone(40000000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }

        afuManager.getMainEditor().doClose();
    }
}
