package sync.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import java.util.Arrays;

public class TestMemory {

    public static void main(String argv[]) {
        long tempoInicio = System.currentTimeMillis();
        AfuManagerSimul afuManager = new AfuManagerSimul("/sync/design/testMemory.hds");

        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 32;
        int qtdeDataOut = 32;

        int[][] dataIn = new int[afu0.getNumInputBuffer()][qtdeDataIn];
        int[] conf = new int[256];

        for (int i = 0;i<conf.length;i++){
            conf[i] = (i<< 8)| 1;//((int) (Math.random() * 256) << 8) | 1;
        }
        afu0.setConfigurations(conf);
        
        for (int i = 0; i < afu0.getNumInputBuffer(); i++) {
            for (int j = 0; j < qtdeDataIn; j++) {
                dataIn[i][j] = j;
            }
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

        System.out.println("Tempo Total: " + (System.currentTimeMillis() - tempoInicio));

        afuManager.getMainEditor().doClose();
    }
}
