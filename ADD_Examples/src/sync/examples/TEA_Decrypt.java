package sync.examples;

import add.dataflow.base.Clock;
import add.exec.AFUSimul;
import add.exec.AfuManagerSimul;

/**
 *
 * @author kristtopher
 */
public class TEA_Decrypt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //AfuManager afuManager = new AfuManager("/home/kristtopher/Desktop/soma_sub.hds");
        AfuManagerSimul afuManager = new AfuManagerSimul("/sync/design/TEA_DECRYPT.hds");
        AFUSimul afu0 = afuManager.getAFU(0);

        int qtdeConfImmediate = 1;//um campo para a qtde de confs e outro com a conf
        int qtdeDataIn = 7;//64
        int qtdeDataOut = 64;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();
         
        int[][] dataIn = {{0x2885842e,0x2885842e,0x2885842e,0x2885842e,0x2885842e,0x2885842e,0x2885842e},{0xa3e6999b,0xa3e6999b,0xa3e6999b,0xa3e6999b,0xa3e6999b,0xa3e6999b,0xa3e6999b}};
        //int[][] dataIn = {{0x2885842e},{0xa3e6999b}};
        for (int j = 0; j < qtdeIn; j++) {
            afu0.createInputBufferSW(j, dataIn[j].length, dataIn[j]);
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }
        
        //Clock clk = (Clock) afuManager.getMainEditor().getDesign().getComponent("CLOCK");
        //clk.setPeriod(0.001);
        
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {}
        
        
        
        afu0.start();
        afu0.waitDone(400000000);
        for (int j = 0; j < qtdeOut; j++) {
            for(Integer i:afu0.getOutputBuffer(j)){
               System.out.print(String.format("0x%08X",i) + " "); 
            }
            System.out.println();
            //System.out.println(Arrays.toString(afu0.getOutputBuffer(j)));
            //System.out.println((afu0.getOutputBuffer(j)));
        }
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {}

        //afuManager.getMainEditor().doClose();
        
    }
    
}
