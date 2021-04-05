package sync.examples;

import add.Add;
import add.dataflow.base.Clock;
import add.dataflow.sync.SyncBeqI;
import add.dataflow.sync.SyncIn;
import add.dataflow.sync.SyncMux;
import add.dataflow.sync.SyncOut;
import add.dataflow.sync.SyncRegister;
import add.dataflow.sync.SyncSltI;
import add.dataflow.sync.SyncSub;
import add.dataflow.sync.SyncSubI;
import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import add.util.Util;
import java.util.Arrays;
import hades.gui.Editor;

public class Sync1SimSearchFilterConstruct {

    public static void main(String argv[]) {

        AfuManagerSimul afuManager = new AfuManagerSimul(createDesign(5), true);
        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 1024;
        int qtdeDataOut = 1024;
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

    private static Editor createDesign(int q_r) {
        Add add = new Add(false);
        Editor editor = add.getMainEditor();

        SyncIn LOAD_load0 = new SyncIn();
        LOAD_load0.setName("load0");
        LOAD_load0.setAfuId(0);
        Util.createComponent(LOAD_load0);

        SyncSltI SLT_slt0 = new SyncSltI();
        SLT_slt0.setName("slt0");
        SLT_slt0.setAfuId(0);
        SLT_slt0.setImmediate(q_r);
        SLT_slt0.setId(2);
        Util.createComponent(SLT_slt0);

        SyncBeqI BEQ_beq0 = new SyncBeqI();
        BEQ_beq0.setName("beq0");
        BEQ_beq0.setAfuId(0);
        BEQ_beq0.setImmediate(1);
        Util.createComponent(BEQ_beq0);

        SyncMux MUX_mux0 = new SyncMux();
        MUX_mux0.setName("mux0");
        MUX_mux0.setAfuId(0);
        Util.createComponent(MUX_mux0);

        SyncOut STORE_store0 = new SyncOut();
        STORE_store0.setName("store0");
        STORE_store0.setAfuId(0);
        Util.createComponent(STORE_store0);

        SyncRegister REG_register0 = new SyncRegister();
        REG_register0.setName("register0");
        REG_register0.setAfuId(0);
        Util.createComponent(REG_register0);

        SyncRegister REG_register1 = new SyncRegister();
        REG_register1.setName("register1");
        REG_register1.setAfuId(0);
        Util.createComponent(REG_register1);

        SyncSub SUB_sub0 = new SyncSub();
        SUB_sub0.setName("sub0");
        SUB_sub0.setAfuId(0);
        Util.createComponent(SUB_sub0);

        SyncSubI SUB_sub1 = new SyncSubI();
        SUB_sub1.setName("sub1");
        SUB_sub1.setAfuId(0);
        SUB_sub1.setImmediate(1);
        Util.createComponent(SUB_sub1);

        LOAD_load0.connectTo(SLT_slt0, "din0");
        LOAD_load0.connectTo(REG_register0, "din0");
        LOAD_load0.connectTo(SUB_sub0, "din0");
        LOAD_load0.connectTo(SUB_sub0, "din1");
        SLT_slt0.connectTo(BEQ_beq0, "din0");
        BEQ_beq0.connectTo(MUX_mux0, "bin0");
        MUX_mux0.connectTo(STORE_store0, "din0");
        REG_register0.connectTo(REG_register1, "din0");
        REG_register1.connectTo(MUX_mux0, "din0");
        SUB_sub0.connectTo(SUB_sub1, "din0");
        SUB_sub1.connectTo(MUX_mux0, "din1");
        Clock clk = Util.checkClkComponent();
        clk.setPeriod(2.0);

        Util.connectClkWire();
        Util.editorRedraw();
        
        return editor;
    }
}
