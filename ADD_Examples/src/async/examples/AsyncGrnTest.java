package async.examples;

import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;

public class AsyncGrnTest {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul("/async/design/grnTest.hds");
        AFUSimul afu0 = afuManager.getAFU(0);

        //int qtdeConfImmediate = 1;//um campo para a qtde de confs e outro com a conf
        int qtdeDataIn = 3 * (1 << 6);
        int qtdeDataOut = (1 << 6);
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[] dataIn = new int[qtdeDataIn];
        int valor = 0;
        for (int i = 0; i < dataIn.length; i++) {
            if (i % 3 == 0) {
                valor = i / 3;
            }
            for (int j = 0; j < 32; j++) {
                int complemento;
                complemento = ((valor & 1) != 0) ? 1 : 0;
                dataIn[i] = (complemento << j) | dataIn[i];
                valor >>= 1;
            }
        }

        afu0.createInputBufferSW(0, qtdeDataIn, dataIn);
        //System.out.println(Arrays.toString(dataIn));

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        add.dataflow.base.Clock clk = (add.dataflow.base.Clock) afuManager.getMainEditor().getDesign().getComponent("CLOCK");
        clk.setPeriod(1.0);
        
        afu0.start();
        afu0.waitDone(40000000);

        for (int i = 0; i < qtdeOut; i++) {
            int[] outputVector = afu0.getOutputBuffer(i);
            for (int j = 0; j < outputVector.length; j++) {
                System.out.println(j + " " + Integer.toString(outputVector[j] & 0xffff) + " " + Integer.toString(outputVector[j] >> 16 & 0xffff));
            }
        }

        /*try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
            }*/
        afuManager.getMainEditor().doClose();
    }
}
