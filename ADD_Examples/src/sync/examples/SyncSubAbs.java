package sync.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import java.util.Arrays;

public class SyncSubAbs {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul("/sync/design/SubAbs.hds");
        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 10;
        int qtdeDataOut = 10;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[][] dataIn = new int[qtdeIn][qtdeDataIn];
       

        for (int j = 0; j < qtdeIn; j++) {
            for (int i = 0; i < qtdeDataIn; i++) {
                dataIn[j][i] = (int) (Math.random() * 100);//random Data
            }
            System.out.println(Arrays.toString(dataIn[j]));
            afu0.createInputBufferSW(j, qtdeDataIn, dataIn[j]);
        }
        
        
        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        afu0.start();
        afu0.waitDone(40000);//tempo em milissegundos

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }

        afuManager.getMainEditor().doClose();
    }
}
