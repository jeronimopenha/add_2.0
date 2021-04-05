package sync.examples;

import add.exec.AFUFpga;
import add.exec.AfuManagerFpga;

public class Sync1AfuFpga {

    public static void main(String argv[]) {

        AfuManagerFpga afuManager = new AfuManagerFpga("/dev/ttyUSB0", false);
        AFUFpga afu0 = afuManager.getAFU(0);

        int qtdeDataIn = 7;//64
        int qtdeDataOut = 20;
        int qtdeIn = afu0.getNumInputBuffer();
        int qtdeOut = afu0.getNumOutputBuffer();

        //int[][] dataIn = {{0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e, 0x2885842e}, {0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b, 0xa3e6999b}};
        //int[][] dataIn = {{0x2885842e},{0xa3e6999b}};
        int[][] dataIn = {
            {16, 10, 9, 8, 7, 6, 1, 5, 4, 3, 2, 15, 3, 4, 5, 6},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}
        };

        for (int j = 0; j < qtdeIn; j++) {
            afu0.createInputBufferSW(j, dataIn[j].length, dataIn[j]);
        }

        for (int j = 0; j < qtdeOut; j++) {
            afu0.createOutputBufferSW(j, qtdeDataOut);
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        afu0.start();

        afu0.waitDone(
                1000000);

        boolean flag = false;

        System.out.println(
                "ReceivedData");

        for (int j = 0; j < qtdeOut; j++) {
            int[] buffer = afu0.getOutputBuffer(j);
            for (int i = 0; i < buffer.length; i++) {
                System.out.print(Integer.toHexString(buffer[i]) + " ");
            }
            System.out.println("");
        }

        afuManager.close();
    }
}
