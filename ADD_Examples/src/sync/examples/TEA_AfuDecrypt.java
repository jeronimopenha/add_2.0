/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync.examples;

import add.Add;
import add.dataflow.base.Clock;
import add.dataflow.sync.SyncAddI;
import add.dataflow.sync.SyncIn;
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
import java.util.Arrays;
import java.util.function.LongToIntFunction;

/**
 *
 * @author kristtopher
 */
public class TEA_AfuDecrypt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //AfuManagerSimul afuManager = new AfuManagerSimul(createDesign(), true);
        AfuManagerSimul afuManager = new AfuManagerSimul("/home/jeronimo/Área de Trabalho/teste/kaio_dec.hds", true);
        afuManager.getMainEditor().doZoomFit();

        /*
        0xAF52F9C9 0xAF52F9C9 0xAF52F9C9 0xAF52F9C9 0xAF52F9C9 0xAF52F9C9 0xAF52F9C9 
        0x29FFBC55 0x29FFBC55 0x29FFBC55 0x29FFBC55 0x29FFBC55 0x29FFBC55 0x29FFBC55 
        */
        
        /*
        0x9474B8E8 0x9474B8E8 0x9474B8E8 0x9474B8E8 0x9474B8E8 0x9474B8E8 0x9474B8E8 
        0xC73BCA7D 0xC73BCA7D 0xC73BCA7D 0xC73BCA7D 0xC73BCA7D 0xC73BCA7D 0xC73BCA7D
        */
        
        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 7;//64
        int qtdeDataOut = 64;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        int[][] dataIn = {{0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e}, {0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b}};
        //int[][] dataIn = {{0x2885842e},{0xa3e6999b}};
        for (int j = 0; j < qtdeIn; j++) {
            afu0.createInputBufferSW(j, dataIn[j].length, dataIn[j]);
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        Clock clk = (Clock) afuManager.getMainEditor().getDesign().getComponent("CLOCK");
        //clk.setPeriod(0.001);

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
        }

        afu0.start();
        afu0.waitDone(400000000);
        for (int j = 0; j < qtdeOut; j++) {
            int[] vetor = afu0.getOutputBuffer(j);
            if (vetor != null) {
                for (int i = 0; i < vetor.length; i++) {
                    System.out.print(String.format("0x%08X", vetor[i]) + " ");
                }
                System.out.println();
            }
            //System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
            //System.out.println((afu0.getOutputBuffer(j)));
        }
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    private static Editor createDesign() {
        Add add = new Add(false);
        Editor editor = add.getMainEditor();
        int N = 32;
        int xi = 6000;
        int d = 2400;
        int y0 = 0,  y1 = 3000, y2 = 6000, y3 = 9000, y4=12000;
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

        SyncRegister[] reg = new SyncRegister[704];
        for (int i = 0; i < 704; i++) {
            reg[i] = new SyncRegister();
            reg[i].setName("reg" + i);
            reg[i].setWidth(32);
        }

        SyncSub[] sub = new SyncSub[64];
        for (int i = 0; i < 64; i++) {
            sub[i] = new SyncSub();
            sub[i].setName("sub" + i);
            sub[i].setWidth(32);
        }

        SyncAddI[] addi = new SyncAddI[192];
        for (int i = 0; i < 192; i++) {
            addi[i] = new SyncAddI();
            addi[i].setName("addi" + i);
            addi[i].setWidth(32);
        }

        SyncXor[] xor = new SyncXor[128];
        for (int i = 0; i < 128; i++) {
            xor[i] = new SyncXor();
            xor[i].setName("xor" + i);
            xor[i].setWidth(32);
        }

        SyncShlI[] shli = new SyncShlI[64];
        for (int i = 0; i < 64; i++) {
            shli[i] = new SyncShlI();
            shli[i].setName("shli" + i);
            shli[i].setWidth(32);
        }

        SyncShrI[] shri = new SyncShrI[64];
        for (int i = 0; i < 64; i++) {
            shri[i] = new SyncShrI();
            shri[i].setName("shri" + i);
            shri[i].setWidth(32);
        }

        for (int i = 0; i < N; i++) {
            Util.createComponent(reg[i * 22 + 0], xi + d, y0);
            Util.createComponent(reg[i * 22 + 1], xi + d, y4);
            Util.createComponent(reg[i * 22 + 2], xi + d, y2);
            Util.createComponent(reg[i * 22 + 3], xi + 2 * d, y4);
            Util.createComponent(reg[i * 22 + 4], xi + 2 * d, y0);
            Util.createComponent(reg[i * 22 + 5], xi + 3 * d, y0);
            Util.createComponent(reg[i * 22 + 6], xi + 3 * d, y4);
            Util.createComponent(reg[i * 22 + 7], xi + 4 * d, y0);
            Util.createComponent(reg[i * 22 + 8], xi + 4 * d, y4);
            Util.createComponent(reg[i * 22 + 9], xi + 5 * d, y0);
            Util.createComponent(reg[i * 22 + 10], xi + 6 * d, y0);
            Util.createComponent(reg[i * 22 + 11], xi + 6 * d, y4);
            Util.createComponent(reg[i * 22 + 12], xi + 6 * d, y2);
            Util.createComponent(reg[i * 22 + 13], xi + 7 * d, y0);
            Util.createComponent(reg[i * 22 + 14], xi + 7 * d, y4);
            Util.createComponent(reg[i * 22 + 15], xi + 8 * d, y0);
            Util.createComponent(reg[i * 22 + 16], xi + 8 * d, y4);
            Util.createComponent(reg[i * 22 + 17], xi + 9 * d, y0);
            Util.createComponent(reg[i * 22 + 18], xi + 9 * d, y4);
            Util.createComponent(reg[i * 22 + 19], xi + 10 * d, y4);
            Util.createComponent(reg[i * 22 + 20], xi + 3 * d, y3);
            Util.createComponent(reg[i * 22 + 21], xi + 8 * d, y3);

            Util.createComponent(sub[i * 2 + 0], xi + 10 * d, y0);
            Util.createComponent(sub[i * 2 + 1], xi + 5 * d, y3);

            Util.createComponent(addi[i * 6 + 0], xi + 2 * d, y1);
            addi[i * 6 + 0].setImmediate(k2);
            Util.createComponent(addi[i * 6 + 1], xi + 2 * d, y2);
            addi[i * 6 + 1].setImmediate(sum);
            Util.createComponent(addi[i * 6 + 2], xi + 2 * d, y3);
            addi[i * 6 + 2].setImmediate(k3);
            Util.createComponent(addi[i * 6 + 3], xi + 7 * d, y1);
            addi[i * 6 + 3].setImmediate(k0);
            Util.createComponent(addi[i * 6 + 4], xi + 7 * d, y2);
            addi[i * 6 + 4].setImmediate(sum);
            Util.createComponent(addi[i * 6 + 5], xi + 7 * d, y3);
            addi[i * 6 + 5].setImmediate(k1);

            Util.createComponent(xor[i * 4 + 0], xi + 3 * d, y2);
            Util.createComponent(xor[i * 4 + 1], xi + 4 * d, y3);
            Util.createComponent(xor[i * 4 + 2], xi + 8 * d, y2);
            Util.createComponent(xor[i * 4 + 3], xi + 9 * d, y3);

            Util.createComponent(shri[i * 2 + 0], xi + d, y3);
            shri[i * 2 + 0].setImmediate(5);
            Util.createComponent(shri[i * 2 + 1], xi + 6 * d, y3);
            shri[i * 2 + 1].setImmediate(5);

            Util.createComponent(shli[i * 2 + 0], xi + d, y1);
            shli[i * 2 + 0].setImmediate(4);
            Util.createComponent(shli[i * 2 + 1], xi + 6 * d, y1);
            shli[i * 2 + 1].setImmediate(4);

            reg[i * 22 + 0].connectTo(reg[i * 22 + 4], "din0");
            reg[i * 22 + 4].connectTo(reg[i * 22 + 5], "din0");
            reg[i * 22 + 5].connectTo(reg[i * 22 + 7], "din0");
            reg[i * 22 + 7].connectTo(reg[i * 22 + 9], "din0");
            reg[i * 22 + 9].connectTo(reg[i * 22 + 10], "din0");
            reg[i * 22 + 10].connectTo(reg[i * 22 + 13], "din0");
            reg[i * 22 + 13].connectTo(reg[i * 22 + 15], "din0");
            reg[i * 22 + 15].connectTo(reg[i * 22 + 17], "din0");
            reg[i * 22 + 1].connectTo(reg[i * 22 + 3], "din0");
            reg[i * 22 + 3].connectTo(reg[i * 22 + 6], "din0");
            reg[i * 22 + 6].connectTo(reg[i * 22 + 8], "din0");
            reg[i * 22 + 8].connectTo(sub[i * 2 + 1], "din0");
            reg[i * 22 + 17].connectTo(sub[i * 2 + 0], "din0");
            shli[i * 2 + 0].connectTo(addi[i * 6 + 0], "din0");
            reg[i * 22 + 2].connectTo(addi[i * 6 + 1], "din0");
            shri[i * 2 + 0].connectTo(addi[i * 6 + 2], "din0");
            addi[i * 6 + 2].connectTo(reg[i * 22 + 20], "din0");
            addi[i * 6 + 1].connectTo(xor[i * 4 + 0], "din1");
            addi[i * 6 + 0].connectTo(xor[i * 4 + 0], "din0");
            reg[i * 22 + 20].connectTo(xor[i * 4 + 1], "din1");
            xor[i * 4 + 0].connectTo(xor[i * 4 + 1], "din0");
            xor[i * 4 + 1].connectTo(sub[i * 2 + 1], "din1");
            sub[i * 2 + 1].connectTo(reg[i * 22 + 12], "din0");
            sub[i * 2 + 1].connectTo(shli[i * 2 + 1], "din0");
            sub[i * 2 + 1].connectTo(shri[i * 2 + 1], "din0");
            sub[i * 2 + 1].connectTo(reg[i * 22 + 11], "din0");
            reg[i * 22 + 11].connectTo(reg[i * 22 + 14], "din0");
            reg[i * 22 + 14].connectTo(reg[i * 22 + 16], "din0");
            reg[i * 22 + 16].connectTo(reg[i * 22 + 18], "din0");
            reg[i * 22 + 18].connectTo(reg[i * 22 + 19], "din0");
            shli[i * 2 + 1].connectTo(addi[i * 6 + 3], "din0");
            shri[i * 2 + 1].connectTo(addi[i * 6 + 5], "din0");
            reg[i * 22 + 12].connectTo(addi[i * 6 + 4], "din0");
            addi[i * 6 + 5].connectTo(reg[i * 22 + 21], "din0");
            addi[i * 6 + 3].connectTo(xor[i * 4 + 2], "din0");
            addi[i * 6 + 4].connectTo(xor[i * 4 + 2], "din1");
            xor[i * 4 + 2].connectTo(xor[i * 4 + 3], "din0");
            reg[i * 22 + 21].connectTo(xor[i * 4 + 3], "din1");
            xor[i * 4 + 3].connectTo(sub[i * 2 + 0], "din1");

            if (i > 0){
            sub[(i-1) * 2 + 0].connectTo(reg[(i) * 22 + 0], "din0");
            sub[(i-1) * 2 + 0].connectTo(reg[i * 22 + 2], "din0");
            sub[(i-1) * 2 + 0].connectTo(shli[i * 2 + 0], "din0");
            sub[(i-1) * 2 + 0].connectTo(shri[i * 2 + 0], "din0");
            reg[(i-1) * 22 + 19].connectTo(reg[i * 22 + 1], "din0");
            }
            sum = sum - delta;
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

        //conectar os componentes
        syncIn[0].connectTo(reg[0], "din0");
        syncIn[1].connectTo(reg[1], "din0");
        syncIn[0].connectTo(reg[2], "din0");
        syncIn[0].connectTo(shli[0], "din0");
        syncIn[0].connectTo(shri[0], "din0");
        sub[(N - 1) * 2].connectTo(syncOut[0], "din0");
        reg[(N - 1) * 22 + 19].connectTo(syncOut[1], "din0");

        Clock clk = Util.checkClkComponent();
        clk.setPeriod(0.1);

        Util.connectClkWire();
        Util.editorRedraw();

        return editor;
    }
}