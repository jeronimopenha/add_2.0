package sync.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import java.util.Arrays;

public class Sync1AfuSimulTest {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul("/sync/design/sumTest.hds");
        AFUSimul afu0 = afuManager.getAFU(0);
        AFUSimul afu1 = afuManager.getAFU(1);
        AFUSimul afu2 = afuManager.getAFU(2);
        AFUSimul afu3 = afuManager.getAFU(3);

        int qtdeDataIn = 32;
        int qtdeDataOut = 32;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[] dataIn;
        for (int j = 0; j < qtdeIn; j++) {
            dataIn = new int[qtdeDataIn];
            for (int i = 0; i < qtdeDataIn; i++) {
                dataIn[i] = (int) (Math.random() * 10);//random Data
            }
            afu0.createInputBufferSW(j, qtdeDataIn, dataIn);
            afu1.createInputBufferSW(j, qtdeDataIn, dataIn);
            afu2.createInputBufferSW(j, qtdeDataIn, dataIn);
            afu3.createInputBufferSW(j, qtdeDataIn, dataIn);
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
            afu1.createOutputBufferSW(j, qtdeDataOut);
            afu2.createOutputBufferSW(j, qtdeDataOut);
            afu3.createOutputBufferSW(j, qtdeDataOut);
        }

        afu0.start();
        afu0.waitDone(40000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
        }

        afu1.start();
        afu1.waitDone(40000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Arrays.toString(afu1.getOutputBuffer(j)));
        }

        afu2.start();
        afu2.waitDone(40000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Arrays.toString(afu2.getOutputBuffer(j)));
        }

        afu3.start();
        afu3.waitDone(40000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.println(Arrays.toString(afu3.getOutputBuffer(j)));
        }
        afuManager.getMainEditor().doClose();
    }
}
