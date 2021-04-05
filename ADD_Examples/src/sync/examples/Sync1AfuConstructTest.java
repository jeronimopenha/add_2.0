package sync.examples;

import add.Add;
import add.dataflow.base.Clock;
import add.dataflow.sync.SyncIn;
import add.dataflow.sync.SyncOut;
import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import add.util.Util;
import hades.gui.Editor;
import java.util.Arrays;

public class Sync1AfuConstructTest {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul(createDesign(), true);
        afuManager.getMainEditor().doZoomFit();

        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 32;
        int qtdeDataOut = 32;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[][] dataIn = new int[qtdeIn][qtdeDataIn];
        for (int j = 0; j < qtdeIn; j++) {
            for (int i = 0; i < qtdeDataIn; i++) {
                dataIn[j][i] = (j + 1) * i;//(int) (Math.random() * 10);//random Data
            }
            afu0.createInputBufferSW(j, qtdeDataIn, dataIn[j]);
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        afu0.start();
        afu0.waitDone(40000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.printf("Output " + j + ": ");
            System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
        }

        afu0.start();
        afu0.waitDone(40000);

        for (int j = 0; j < qtdeOut; j++) {
            System.out.printf("Output " + j + ": ");
            System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
        }
        afuManager.getMainEditor().doClose();
    }

    private static Editor createDesign() {
        Add add = new Add(false);
        Editor editor = add.getMainEditor();

        //construção dos componentes
        SyncIn syncIn = new SyncIn();
        syncIn.setName("in0");
        syncIn.setAfuId(0);

        SyncOut syncOut0 = new SyncOut();
        syncOut0.setName("out0");
        syncOut0.setAfuId(0);

        SyncOut syncOut1 = new SyncOut();
        syncOut1.setName("out1");
        syncOut1.setAfuId(0);

        //primeiro criar os componentes no editor
        //Util.createComponent(syncIn, 2400, 6000);
        //Util.createComponent(syncOut0, 5400, 6000);
        //Util.createComponent(syncOut1, 5400, 9000);
        Util.createComponent(syncIn);
        Util.createComponent(syncOut0);
        Util.createComponent(syncOut1);

        //conectar os componentes
        //pego o componente IN e mando conectar no out na porta din0
        syncIn.connectTo(syncOut0, "din0");
        syncIn.connectTo(syncOut1, "din0");

        //Aqui passa os dois componentes e as portas a serem ligadas.
        //Util.connectComponents(syncIn, syncOut, "dout0", "din0");
        Clock clk = Util.checkClkComponent();
        clk.setPeriod(0.001);

        Util.connectClkWire();
        Util.editorRedraw();

        return editor;
    }
}
