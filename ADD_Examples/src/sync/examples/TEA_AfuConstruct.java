package sync.examples;

import add.Add;
import add.dataflow.base.Clock;
import add.dataflow.sync.SyncAdd;
import add.dataflow.sync.SyncAddI;
import add.dataflow.sync.SyncIn;
import add.dataflow.sync.SyncMulI;
import add.dataflow.sync.SyncOut;
import add.dataflow.sync.SyncRegister;
import add.dataflow.sync.SyncShlI;
import add.dataflow.sync.SyncShrI;
import add.dataflow.sync.SyncSub;
import add.dataflow.sync.SyncXor;
import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;
import add.util.Util;
import hades.gui.Editor;

/**
 *
 * @author kristtopher
 */
public class TEA_AfuConstruct {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        AfuManagerSimul afuManager = new AfuManagerSimul(createDesign(), true);
        //AfuManagerSimul afuManager = new AfuManagerSimul("/home/jeronimo/Área de Trabalho/teste/kaio.hds", false);
        afuManager.getMainEditor().doZoomFit();

        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 128 * 16;
        int qtdeDataOut = 3;

        int[][] dataIn = new int[afu0.getNumInputBuffer()][qtdeDataIn];

        for (int i = 0; i < afu0.getNumInputBuffer(); i++) {
            for (int j = 0; j < qtdeDataIn; j++) {
                if (i % 2 == 0) {
                    dataIn[i][j] = 0x2885842e;
                } else {
                    dataIn[i][j] = 0xa3e6999b;
                }
            }
            afu0.createInputBufferSW(i, qtdeDataIn, dataIn[i]);
        }

        for (int j = 0; j < afu0.getNumOutputBuffer(); j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        Clock clk = (Clock) afuManager.getMainEditor().getDesign().getComponent("CLOCK");
        clk.setPeriod(0.001);

        afu0.start();
        afu0.waitDone(400000000);
        /*for (int j = 0; j < afu0.getNumOutputBuffer(); j++) {
            int[] vetor = afu0.getOutputBuffer(j);
            if (vetor != null) {
                for (int i = 0; i < vetor.length; i++) {
                    System.out.println(Integer.toHexString(vetor[i]));
                }
                System.out.println();
            }
        }*/
        System.out.println("Tempo Total: " + (System.currentTimeMillis() - tempoInicio));

        afuManager.getMainEditor().doClose();
    }

    private static Editor createDesign() {
        Add add = new Add(false);
        Editor editor = add.getMainEditor();
        int N = 32;
        int xi = 6000;
        int d = 2400;
        int y0 = 0, y1 = 3000, y2 = 6000, y3 = 9000, y4 = 12000;
        long k0 = 0x9474B8E8;
        long k1 = 0xC73BCA7D;
        long k2 = 0x53239142;
        long k3 = 0xf3c3121a;
        long delta = 0x9e3779b9;
        long sum = 0xC6EF3720;

        //construção dos componentes
        //entrada
        SyncIn[] syncIn = new SyncIn[2];
        for (int i = 0; i < 2; i++) {
            syncIn[i] = new SyncIn();
            syncIn[i].setName("in" + i);
            syncIn[i].setAfuId(0);
            syncIn[i].setWidth(32);
        }
        Util.createComponent(syncIn[0], xi, y0);
        Util.createComponent(syncIn[1], xi, y4);

        SyncRegister[] reg_D = new SyncRegister[704];
        for (int i = 0; i < 704; i++) {
            reg_D[i] = new SyncRegister();
            reg_D[i].setName("reg_D" + i);
            reg_D[i].setWidth(32);
        }

        SyncSub[] sub_D = new SyncSub[64];
        for (int i = 0; i < 64; i++) {
            sub_D[i] = new SyncSub();
            sub_D[i].setName("sub_D" + i);
            sub_D[i].setWidth(32);
        }

        SyncAddI[] addi_D = new SyncAddI[192];
        for (int i = 0; i < 192; i++) {
            addi_D[i] = new SyncAddI();
            addi_D[i].setName("addi_D" + i);
            addi_D[i].setWidth(32);
        }

        SyncXor[] xor_D = new SyncXor[128];
        for (int i = 0; i < 128; i++) {
            xor_D[i] = new SyncXor();
            xor_D[i].setName("xor_D" + i);
            xor_D[i].setWidth(32);
        }

        SyncShlI[] shli_D = new SyncShlI[64];
        for (int i = 0; i < 64; i++) {
            shli_D[i] = new SyncShlI();
            shli_D[i].setName("shli_D" + i);
            shli_D[i].setWidth(32);
        }

        SyncShrI[] shri_D = new SyncShrI[64];
        for (int i = 0; i < 64; i++) {
            shri_D[i] = new SyncShrI();
            shri_D[i].setName("shri_D" + i);
            shri_D[i].setWidth(32);
        }

        SyncRegister[] reg_fir = new SyncRegister[2];
        for (int i = 0; i < 2; i++) {
            reg_fir[i] = new SyncRegister();
            reg_fir[i].setName("reg_fir" + i);
            reg_fir[i].setWidth(32);
        }

        SyncAdd[] add_fir = new SyncAdd[6];
        for (int i = 0; i < 6; i++) {
            add_fir[i] = new SyncAdd();
            add_fir[i].setName("add_fir" + i);
            add_fir[i].setWidth(32);
        }

        SyncMulI[] mult_fir = new SyncMulI[8];
        for (int i = 0; i < 8; i++) {
            mult_fir[i] = new SyncMulI();
            mult_fir[i].setName("mult_fir" + i);
            mult_fir[i].setWidth(32);
        }

        SyncRegister[] reg_C = new SyncRegister[704];
        for (int i = 0; i < 704; i++) {
            reg_C[i] = new SyncRegister();
            reg_C[i].setName("reg_C" + i);
            reg_C[i].setWidth(32);
        }

        SyncAdd[] add_C = new SyncAdd[64];
        for (int i = 0; i < 64; i++) {
            add_C[i] = new SyncAdd();
            add_C[i].setName("add_C" + i);
            add_C[i].setWidth(32);
        }

        SyncAddI[] addi_C = new SyncAddI[192];
        for (int i = 0; i < 192; i++) {
            addi_C[i] = new SyncAddI();
            addi_C[i].setName("addi_C" + i);
            addi_C[i].setWidth(32);
        }

        SyncXor[] xor_C = new SyncXor[128];
        for (int i = 0; i < 128; i++) {
            xor_C[i] = new SyncXor();
            xor_C[i].setName("xor_C" + i);
            xor_C[i].setWidth(32);
        }

        SyncShlI[] shli_C = new SyncShlI[64];
        for (int i = 0; i < 64; i++) {
            shli_C[i] = new SyncShlI();
            shli_C[i].setName("shli_C" + i);
            shli_C[i].setWidth(32);
        }

        SyncShrI[] shri_C = new SyncShrI[64];
        for (int i = 0; i < 64; i++) {
            shri_C[i] = new SyncShrI();
            shri_C[i].setName("shri_C" + i);
            shri_C[i].setWidth(32);
        }

        //Descriptografar
        for (int i = 0; i < N; i++) {
            Util.createComponent(reg_D[i * 22 + 0], xi + d, y0);
            Util.createComponent(reg_D[i * 22 + 1], xi + d, y4);
            Util.createComponent(reg_D[i * 22 + 2], xi + d, y2);
            Util.createComponent(reg_D[i * 22 + 3], xi + 2 * d, y4);
            Util.createComponent(reg_D[i * 22 + 4], xi + 2 * d, y0);
            Util.createComponent(reg_D[i * 22 + 5], xi + 3 * d, y0);
            Util.createComponent(reg_D[i * 22 + 6], xi + 3 * d, y4);
            Util.createComponent(reg_D[i * 22 + 7], xi + 4 * d, y0);
            Util.createComponent(reg_D[i * 22 + 8], xi + 4 * d, y4);
            Util.createComponent(reg_D[i * 22 + 9], xi + 5 * d, y0);
            Util.createComponent(reg_D[i * 22 + 10], xi + 6 * d, y0);
            Util.createComponent(reg_D[i * 22 + 11], xi + 6 * d, y4);
            Util.createComponent(reg_D[i * 22 + 12], xi + 6 * d, y2);
            Util.createComponent(reg_D[i * 22 + 13], xi + 7 * d, y0);
            Util.createComponent(reg_D[i * 22 + 14], xi + 7 * d, y4);
            Util.createComponent(reg_D[i * 22 + 15], xi + 8 * d, y0);
            Util.createComponent(reg_D[i * 22 + 16], xi + 8 * d, y4);
            Util.createComponent(reg_D[i * 22 + 17], xi + 9 * d, y0);
            Util.createComponent(reg_D[i * 22 + 18], xi + 9 * d, y4);
            Util.createComponent(reg_D[i * 22 + 19], xi + 10 * d, y4);
            Util.createComponent(reg_D[i * 22 + 20], xi + 3 * d, y3);
            Util.createComponent(reg_D[i * 22 + 21], xi + 8 * d, y3);

            Util.createComponent(sub_D[i * 2 + 0], xi + 10 * d, y0);
            Util.createComponent(sub_D[i * 2 + 1], xi + 5 * d, y3);

            Util.createComponent(addi_D[i * 6 + 0], xi + 2 * d, y1);
            addi_D[i * 6 + 0].setImmediate(k2);
            Util.createComponent(addi_D[i * 6 + 1], xi + 2 * d, y2);
            addi_D[i * 6 + 1].setImmediate(sum);
            Util.createComponent(addi_D[i * 6 + 2], xi + 2 * d, y3);
            addi_D[i * 6 + 2].setImmediate(k3);
            Util.createComponent(addi_D[i * 6 + 3], xi + 7 * d, y1);
            addi_D[i * 6 + 3].setImmediate(k0);
            Util.createComponent(addi_D[i * 6 + 4], xi + 7 * d, y2);
            addi_D[i * 6 + 4].setImmediate(sum);
            Util.createComponent(addi_D[i * 6 + 5], xi + 7 * d, y3);
            addi_D[i * 6 + 5].setImmediate(k1);

            Util.createComponent(xor_D[i * 4 + 0], xi + 3 * d, y2);
            Util.createComponent(xor_D[i * 4 + 1], xi + 4 * d, y3);
            Util.createComponent(xor_D[i * 4 + 2], xi + 8 * d, y2);
            Util.createComponent(xor_D[i * 4 + 3], xi + 9 * d, y3);

            Util.createComponent(shri_D[i * 2 + 0], xi + d, y3);
            shri_D[i * 2 + 0].setImmediate(5);
            Util.createComponent(shri_D[i * 2 + 1], xi + 6 * d, y3);
            shri_D[i * 2 + 1].setImmediate(5);

            Util.createComponent(shli_D[i * 2 + 0], xi + d, y1);
            shli_D[i * 2 + 0].setImmediate(4);
            Util.createComponent(shli_D[i * 2 + 1], xi + 6 * d, y1);
            shli_D[i * 2 + 1].setImmediate(4);

            reg_D[i * 22 + 0].connectTo(reg_D[i * 22 + 4], "din0");
            reg_D[i * 22 + 4].connectTo(reg_D[i * 22 + 5], "din0");
            reg_D[i * 22 + 5].connectTo(reg_D[i * 22 + 7], "din0");
            reg_D[i * 22 + 7].connectTo(reg_D[i * 22 + 9], "din0");
            reg_D[i * 22 + 9].connectTo(reg_D[i * 22 + 10], "din0");
            reg_D[i * 22 + 10].connectTo(reg_D[i * 22 + 13], "din0");
            reg_D[i * 22 + 13].connectTo(reg_D[i * 22 + 15], "din0");
            reg_D[i * 22 + 15].connectTo(reg_D[i * 22 + 17], "din0");
            reg_D[i * 22 + 1].connectTo(reg_D[i * 22 + 3], "din0");
            reg_D[i * 22 + 3].connectTo(reg_D[i * 22 + 6], "din0");
            reg_D[i * 22 + 6].connectTo(reg_D[i * 22 + 8], "din0");
            reg_D[i * 22 + 8].connectTo(sub_D[i * 2 + 1], "din0");
            reg_D[i * 22 + 17].connectTo(sub_D[i * 2 + 0], "din0");
            shli_D[i * 2 + 0].connectTo(addi_D[i * 6 + 0], "din0");
            reg_D[i * 22 + 2].connectTo(addi_D[i * 6 + 1], "din0");
            shri_D[i * 2 + 0].connectTo(addi_D[i * 6 + 2], "din0");
            addi_D[i * 6 + 2].connectTo(reg_D[i * 22 + 20], "din0");
            addi_D[i * 6 + 1].connectTo(xor_D[i * 4 + 0], "din1");
            addi_D[i * 6 + 0].connectTo(xor_D[i * 4 + 0], "din0");
            reg_D[i * 22 + 20].connectTo(xor_D[i * 4 + 1], "din1");
            xor_D[i * 4 + 0].connectTo(xor_D[i * 4 + 1], "din0");
            xor_D[i * 4 + 1].connectTo(sub_D[i * 2 + 1], "din1");
            sub_D[i * 2 + 1].connectTo(reg_D[i * 22 + 12], "din0");
            sub_D[i * 2 + 1].connectTo(shli_D[i * 2 + 1], "din0");
            sub_D[i * 2 + 1].connectTo(shri_D[i * 2 + 1], "din0");
            sub_D[i * 2 + 1].connectTo(reg_D[i * 22 + 11], "din0");
            reg_D[i * 22 + 11].connectTo(reg_D[i * 22 + 14], "din0");
            reg_D[i * 22 + 14].connectTo(reg_D[i * 22 + 16], "din0");
            reg_D[i * 22 + 16].connectTo(reg_D[i * 22 + 18], "din0");
            reg_D[i * 22 + 18].connectTo(reg_D[i * 22 + 19], "din0");
            shli_D[i * 2 + 1].connectTo(addi_D[i * 6 + 3], "din0");
            shri_D[i * 2 + 1].connectTo(addi_D[i * 6 + 5], "din0");
            reg_D[i * 22 + 12].connectTo(addi_D[i * 6 + 4], "din0");
            addi_D[i * 6 + 5].connectTo(reg_D[i * 22 + 21], "din0");
            addi_D[i * 6 + 3].connectTo(xor_D[i * 4 + 2], "din0");
            addi_D[i * 6 + 4].connectTo(xor_D[i * 4 + 2], "din1");
            xor_D[i * 4 + 2].connectTo(xor_D[i * 4 + 3], "din0");
            reg_D[i * 22 + 21].connectTo(xor_D[i * 4 + 3], "din1");
            xor_D[i * 4 + 3].connectTo(sub_D[i * 2 + 0], "din1");

            if (i > 0) {
                sub_D[(i - 1) * 2 + 0].connectTo(reg_D[(i) * 22 + 0], "din0");
                sub_D[(i - 1) * 2 + 0].connectTo(reg_D[i * 22 + 2], "din0");
                sub_D[(i - 1) * 2 + 0].connectTo(shli_D[i * 2 + 0], "din0");
                sub_D[(i - 1) * 2 + 0].connectTo(shri_D[i * 2 + 0], "din0");
                reg_D[(i - 1) * 22 + 19].connectTo(reg_D[i * 22 + 1], "din0");
            }
            sum = sum - delta;
            xi += 24000;
        }
        // fir 4
        Util.createComponent(mult_fir[0], xi + d, y0);
        mult_fir[0].setImmediate(1);
        Util.createComponent(mult_fir[1], xi + d, y1);
        mult_fir[1].setImmediate(2);
        Util.createComponent(mult_fir[2], xi + d, y2);
        mult_fir[2].setImmediate(3);
        Util.createComponent(mult_fir[3], xi + d, y3);
        mult_fir[3].setImmediate(4);
        Util.createComponent(mult_fir[4], xi + d, y4);
        mult_fir[4].setImmediate(1);
        Util.createComponent(mult_fir[5], xi + d, y4 + 3000);
        mult_fir[5].setImmediate(2);
        Util.createComponent(mult_fir[6], xi + d, y4 + 6000);
        mult_fir[6].setImmediate(3);
        Util.createComponent(mult_fir[7], xi + d, y4 + 9000);
        mult_fir[1].setImmediate(4);
        Util.createComponent(reg_fir[0], xi + 2 * d, y0);
        Util.createComponent(reg_fir[1], xi + 2 * d, y4);
        Util.createComponent(add_fir[0], xi + 3 * d, y0);
        Util.createComponent(add_fir[1], xi + 4 * d, y1);
        Util.createComponent(add_fir[2], xi + 5 * d, y2);
        Util.createComponent(add_fir[3], xi + 3 * d, y4);
        Util.createComponent(add_fir[4], xi + 4 * d, y4 + 3000);
        Util.createComponent(add_fir[5], xi + 5 * d, y4 + 6000);
        xi += 5 * d;
        // conect fir 4
        sub_D[(N - 1) * 2].connectTo(mult_fir[0], "din0");
        sub_D[(N - 1) * 2].connectTo(mult_fir[1], "din0");
        sub_D[(N - 1) * 2].connectTo(mult_fir[2], "din0");
        sub_D[(N - 1) * 2].connectTo(mult_fir[3], "din0");
        reg_D[(N - 1) * 22 + 19].connectTo(mult_fir[4], "din0");
        reg_D[(N - 1) * 22 + 19].connectTo(mult_fir[5], "din0");
        reg_D[(N - 1) * 22 + 19].connectTo(mult_fir[6], "din0");
        reg_D[(N - 1) * 22 + 19].connectTo(mult_fir[7], "din0");
        mult_fir[0].connectTo(reg_fir[0], "din0");
        mult_fir[4].connectTo(reg_fir[1], "din0");
        reg_fir[0].connectTo(add_fir[0], "din0");
        reg_fir[1].connectTo(add_fir[3], "din0");
        add_fir[0].connectTo(add_fir[1], "din0");
        add_fir[1].connectTo(add_fir[2], "din0");
        add_fir[3].connectTo(add_fir[4], "din0");
        add_fir[4].connectTo(add_fir[5], "din0");
        mult_fir[1].connectTo(add_fir[0], "din1");
        mult_fir[2].connectTo(add_fir[1], "din1");
        mult_fir[3].connectTo(add_fir[2], "din1");
        mult_fir[5].connectTo(add_fir[3], "din1");
        mult_fir[6].connectTo(add_fir[4], "din1");
        mult_fir[7].connectTo(add_fir[5], "din1");

        //Criptografar
        sum = 0;
        for (int i = 0; i < N; i++) {
            sum = sum + delta;
            Util.createComponent(reg_C[i * 22 + 0], xi + d, y0);
            Util.createComponent(reg_C[i * 22 + 1], xi + d, y4);
            Util.createComponent(reg_C[i * 22 + 2], xi + d, y2);
            Util.createComponent(reg_C[i * 22 + 3], xi + 2 * d, y4);
            Util.createComponent(reg_C[i * 22 + 4], xi + 2 * d, y0);
            Util.createComponent(reg_C[i * 22 + 5], xi + 3 * d, y0);
            Util.createComponent(reg_C[i * 22 + 6], xi + 3 * d, y4);
            Util.createComponent(reg_C[i * 22 + 7], xi + 4 * d, y0);
            Util.createComponent(reg_C[i * 22 + 8], xi + 4 * d, y4);
            Util.createComponent(reg_C[i * 22 + 9], xi + 5 * d, y0);
            Util.createComponent(reg_C[i * 22 + 10], xi + 6 * d, y0);
            Util.createComponent(reg_C[i * 22 + 11], xi + 6 * d, y4);
            Util.createComponent(reg_C[i * 22 + 12], xi + 6 * d, y2);
            Util.createComponent(reg_C[i * 22 + 13], xi + 7 * d, y0);
            Util.createComponent(reg_C[i * 22 + 14], xi + 7 * d, y4);
            Util.createComponent(reg_C[i * 22 + 15], xi + 8 * d, y0);
            Util.createComponent(reg_C[i * 22 + 16], xi + 8 * d, y4);
            Util.createComponent(reg_C[i * 22 + 17], xi + 9 * d, y0);
            Util.createComponent(reg_C[i * 22 + 18], xi + 9 * d, y4);
            Util.createComponent(reg_C[i * 22 + 19], xi + 10 * d, y4);
            Util.createComponent(reg_C[i * 22 + 20], xi + 3 * d, y3);
            Util.createComponent(reg_C[i * 22 + 21], xi + 8 * d, y3);

            Util.createComponent(add_C[i * 2 + 0], xi + 10 * d, y0);
            Util.createComponent(add_C[i * 2 + 1], xi + 5 * d, y3);

            Util.createComponent(addi_C[i * 6 + 0], xi + 2 * d, y1);
            addi_C[i * 6 + 0].setImmediate(k0);
            Util.createComponent(addi_C[i * 6 + 1], xi + 2 * d, y2);
            addi_C[i * 6 + 1].setImmediate(sum);
            Util.createComponent(addi_C[i * 6 + 2], xi + 2 * d, y3);
            addi_C[i * 6 + 2].setImmediate(k1);
            Util.createComponent(addi_C[i * 6 + 3], xi + 7 * d, y1);
            addi_C[i * 6 + 3].setImmediate(k2);
            Util.createComponent(addi_C[i * 6 + 4], xi + 7 * d, y2);
            addi_C[i * 6 + 4].setImmediate(sum);
            Util.createComponent(addi_C[i * 6 + 5], xi + 7 * d, y3);
            addi_C[i * 6 + 5].setImmediate(k3);

            Util.createComponent(xor_C[i * 4 + 0], xi + 3 * d, y2);
            Util.createComponent(xor_C[i * 4 + 1], xi + 4 * d, y3);
            Util.createComponent(xor_C[i * 4 + 2], xi + 8 * d, y2);
            Util.createComponent(xor_C[i * 4 + 3], xi + 9 * d, y3);

            Util.createComponent(shri_C[i * 2 + 0], xi + d, y3);
            shri_C[i * 2 + 0].setImmediate(5);
            Util.createComponent(shri_C[i * 2 + 1], xi + 6 * d, y3);
            shri_C[i * 2 + 1].setImmediate(5);

            Util.createComponent(shli_C[i * 2 + 0], xi + d, y1);
            shli_C[i * 2 + 0].setImmediate(4);
            Util.createComponent(shli_C[i * 2 + 1], xi + 6 * d, y1);
            shli_C[i * 2 + 1].setImmediate(4);

            reg_C[i * 22 + 0].connectTo(reg_C[i * 22 + 4], "din0");
            reg_C[i * 22 + 4].connectTo(reg_C[i * 22 + 5], "din0");
            reg_C[i * 22 + 5].connectTo(reg_C[i * 22 + 7], "din0");
            reg_C[i * 22 + 7].connectTo(reg_C[i * 22 + 9], "din0");
            reg_C[i * 22 + 9].connectTo(reg_C[i * 22 + 10], "din0");
            reg_C[i * 22 + 10].connectTo(reg_C[i * 22 + 13], "din0");
            reg_C[i * 22 + 13].connectTo(reg_C[i * 22 + 15], "din0");
            reg_C[i * 22 + 15].connectTo(reg_C[i * 22 + 17], "din0");
            reg_C[i * 22 + 1].connectTo(reg_C[i * 22 + 3], "din0");
            reg_C[i * 22 + 3].connectTo(reg_C[i * 22 + 6], "din0");
            reg_C[i * 22 + 6].connectTo(reg_C[i * 22 + 8], "din0");
            reg_C[i * 22 + 8].connectTo(add_C[i * 2 + 1], "din0");
            reg_C[i * 22 + 17].connectTo(add_C[i * 2 + 0], "din0");
            shli_C[i * 2 + 0].connectTo(addi_C[i * 6 + 0], "din0");
            reg_C[i * 22 + 2].connectTo(addi_C[i * 6 + 1], "din0");
            shri_C[i * 2 + 0].connectTo(addi_C[i * 6 + 2], "din0");
            addi_C[i * 6 + 2].connectTo(reg_C[i * 22 + 20], "din0");
            addi_C[i * 6 + 1].connectTo(xor_C[i * 4 + 0], "din1");
            addi_C[i * 6 + 0].connectTo(xor_C[i * 4 + 0], "din0");
            reg_C[i * 22 + 20].connectTo(xor_C[i * 4 + 1], "din1");
            xor_C[i * 4 + 0].connectTo(xor_C[i * 4 + 1], "din0");
            xor_C[i * 4 + 1].connectTo(add_C[i * 2 + 1], "din1");
            add_C[i * 2 + 1].connectTo(reg_C[i * 22 + 12], "din0");
            add_C[i * 2 + 1].connectTo(shli_C[i * 2 + 1], "din0");
            add_C[i * 2 + 1].connectTo(shri_C[i * 2 + 1], "din0");
            add_C[i * 2 + 1].connectTo(reg_C[i * 22 + 11], "din0");
            reg_C[i * 22 + 11].connectTo(reg_C[i * 22 + 14], "din0");
            reg_C[i * 22 + 14].connectTo(reg_C[i * 22 + 16], "din0");
            reg_C[i * 22 + 16].connectTo(reg_C[i * 22 + 18], "din0");
            reg_C[i * 22 + 18].connectTo(reg_C[i * 22 + 19], "din0");
            shli_C[i * 2 + 1].connectTo(addi_C[i * 6 + 3], "din0");
            shri_C[i * 2 + 1].connectTo(addi_C[i * 6 + 5], "din0");
            reg_C[i * 22 + 12].connectTo(addi_C[i * 6 + 4], "din0");
            addi_C[i * 6 + 5].connectTo(reg_C[i * 22 + 21], "din0");
            addi_C[i * 6 + 3].connectTo(xor_C[i * 4 + 2], "din0");
            addi_C[i * 6 + 4].connectTo(xor_C[i * 4 + 2], "din1");
            xor_C[i * 4 + 2].connectTo(xor_C[i * 4 + 3], "din0");
            reg_C[i * 22 + 21].connectTo(xor_C[i * 4 + 3], "din1");
            xor_C[i * 4 + 3].connectTo(add_C[i * 2 + 0], "din1");

            if (i > 0) {
                add_C[(i - 1) * 2 + 0].connectTo(reg_C[(i) * 22 + 0], "din0");
                add_C[(i - 1) * 2 + 0].connectTo(reg_C[i * 22 + 2], "din0");
                add_C[(i - 1) * 2 + 0].connectTo(shli_C[i * 2 + 0], "din0");
                add_C[(i - 1) * 2 + 0].connectTo(shri_C[i * 2 + 0], "din0");
                reg_C[(i - 1) * 22 + 19].connectTo(reg_C[i * 22 + 1], "din0");
            }

            xi += 24000;
        }

//saida
        SyncOut[] syncOut = new SyncOut[2];
        for (int i = 0; i < 2; i++) {
            syncOut[i] = new SyncOut();
            syncOut[i].setName("out" + i);
            syncOut[i].setAfuId(0);
            syncOut[i].setWidth(32);
        }
        Util.createComponent(syncOut[0], xi + d, y0);
        Util.createComponent(syncOut[1], xi + d, y4);

        //conectar os componentes de entrada e saida
        syncIn[0].connectTo(reg_D[0], "din0");
        syncIn[1].connectTo(reg_D[1], "din0");
        syncIn[0].connectTo(reg_D[2], "din0");
        syncIn[0].connectTo(shli_D[0], "din0");
        syncIn[0].connectTo(shri_D[0], "din0");
        add_fir[5].connectTo(reg_C[0], "din0");
        add_fir[2].connectTo(reg_C[1], "din0");
        add_fir[5].connectTo(reg_C[2], "din0");
        add_fir[5].connectTo(shli_C[0], "din0");
        add_fir[5].connectTo(shri_C[0], "din0");
        add_C[(N - 1) * 2].connectTo(syncOut[1], "din0");
        reg_C[(N - 1) * 22 + 19].connectTo(syncOut[0], "din0");

        //Clock clk = Util.checkClkComponent();
        //clk.setPeriod(1);
        Util.connectClkWire();
        Util.editorRedraw();
        return editor;
    }
}
