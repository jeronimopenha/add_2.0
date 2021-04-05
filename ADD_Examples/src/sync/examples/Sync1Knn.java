package sync.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import java.util.Arrays;

public class Sync1Knn {

    public static void main(String argv[]) {
        long tempoInicio = System.currentTimeMillis();
        //AfuManagerSimul afuManager = new AfuManagerSimul("/home/jeronimo/Área de Trabalho/teste/teste2.hds");//"/sync/design/knn.hds");
        AfuManagerSimul afuManager = new AfuManagerSimul("/home/jeronimo/Área de Trabalho/teste/knn.hds", false);

        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 128 * 32;
        int qtdeDataOut = 3;

        int[][] dataIn = new int[afu0.getNumInputBuffer()][qtdeDataIn];

        int idx = 1;

        for (int i = 0; i < afu0.getNumInputBuffer(); i++) {
            for (int j = 0; j < qtdeDataIn; j++) {
                dataIn[i][j] = idx;
            }
            idx++;
            afu0.createInputBufferSW(i, qtdeDataIn, dataIn[i]);
        }

        for (int j = 0; j < afu0.getNumOutputBuffer(); j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        afu0.start();
        afu0.waitDone(400000);

        for (int j = 0; j < afu0.getNumOutputBuffer(); j++) {
            System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }

        /*try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
        }*/
        System.out.println("Tempo Total: " + (System.currentTimeMillis() - tempoInicio));

        afuManager.getMainEditor().doClose();
    }
}
