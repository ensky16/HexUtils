import java.io.*;
import java.util.*;

public class HexUtilsUnitTest
{
    public static void main(String [] args){

        String hexFileDir=".\\Test.hex";
        String binFileDir=".\\Test.bin";
       // String binFileDir=".\\Test001.bin";
        List<Object> listObj =HexUtils.readHexDataToList(hexFileDir);
 
        HexData hexObj=null;
         
        byte TestData[]=new byte[]{0x31, 0x32, 0x03, 0x09, 0x33, 0x12, 0x09};
        HexUtils.WriteByteToBin(TestData, TestData.length, "test001.bin");
         
        byte pureHexData[]=HexUtils.readHexFileToByteBuf(hexFileDir);
        HexUtils.hexTobinWithoutPadding(hexFileDir, "hexToBin_noPadding.bin");
        HexUtils.hexToBinPrePaddingZero(hexFileDir, "hexToBin_paddingZero.bin");
        byte readBinBuf[]=  HexUtils.readBinFile(binFileDir);
        HexUtils.writeBinFile(readBinBuf, "target.txt");       
        
		
		 
        /***************test read bin file start*********************/
        int j=0;
        int counter2=0;
        System.out.printf("bin file length is: %02X", readBinBuf.length);
        for(j=0; j<readBinBuf.length; j++)
        {
            System.out.printf("%02X", readBinBuf[j]);
            counter2++;
                
            if(counter2==16)
            {
                System.out.printf("   ");
                System.out.printf("No: %04x ",j);
                counter2=0;
                System.out.println();
            }
        }
        /***************test read bin file end*********************/

        /***************test read hex file start*********************/
        /*
        int i=0;
        int counter=0;
        for(i=0; i<pureHexData.length; i++)
        {
            System.out.printf("%02X", pureHexData[i]);
            counter++;			
	        if(counter==16)
            {
		       System.out.printf("   ");
				System.out.printf("No: %04x ",i);
                counter=0;
                System.out.println();
            }			 
		}
        */
        /***************test read hex file end*********************/
    }    
}