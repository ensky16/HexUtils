/*
*@author: hongen
*@version v1.0
*@brief 
*
*
*/
 

import java.io.*;
import java.util.*;


public class HexUtils{
    
    /**offset of one line hex data length*/
    public static final short HEX_LINE_LENGTH_OFS=0;
    /**offset of one line hex data address offset*/
    public static final short HEX_LINE_ADDRESS_OFS=1;
    /**offset of one line hex data type*/
    public static final short HEX_LINE_TYPE_OFS=3;
    /**offset of one line hex data start*/
    public static final short HEX_LINE_DATA_OFS=4;

    /**hex data type:data */
    public static final byte HEX_TYPE_DATA=0;
    /**hex data type: end file*/
    public static final byte HEX_TYPE_END_FILE=1;
    /**hex data type: extend linear address*/
    public static final byte HEX_TYPE_EX_LINEAR_ADD=4;

    /**for debug purpose*/
    public static final boolean DEBUG_FLAG=false;

    /**list variable for hex data*/
    public static List<Object> listObjHex=null; 

    /**
    *@brief char to byte
    */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
    *@brief input string, output hex bytes
    */
    public static byte[] hexStringToBytes(String hexString) 
    {
        if (hexString == null || hexString.equals("")) 
        {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
 
    /**
    * read the hex file, save all the data to List
    *
    * @param strFileDir: this is input file name
    */
    public static List<Object> readHexDataToList(String strFileDir)
    {
        List<Object> listObj = new ArrayList<>();;
        File mfile = new File(strFileDir);
        InputStream inputStreamObj = null;
        BufferedReader bufferedReaderObj = null;
        FileOutputStream fileOutputStreamObj = null;

        byte localOneBlockData[]=new byte[65535+1111];
        byte localDataAddress[]=new byte[4];
        int oneLineHexLen=0;

        short oneLineHexLength=0;

        try 
        {
            if (mfile == null) 
            {
                System.out.println("file is empty");
            }
            
            String str = null;
            inputStreamObj = new FileInputStream(mfile);
            bufferedReaderObj = new BufferedReader(new InputStreamReader(inputStreamObj));
            boolean blockStart=false;
            boolean nextBlockIsComing=false;
            boolean firstTime=true;
            int oneBlockCounter=0;
            byte blockStartAddress[]=new byte[4];
            boolean pureHexData=false;

            while((str = bufferedReaderObj.readLine()) != null)
            {
                //remove first char ":"
                String pureHexStr=str.substring(1);
                byte [] hexByteBuf=hexStringToBytes(pureHexStr);
                oneLineHexLen=hexByteBuf[HEX_LINE_LENGTH_OFS];
                // short sOffset=hexByteBuf[01];
                // sOffset=(short)(sOffset<<8);
                // sOffset+=hexByteBuf[02];

                short i=0;
                byte oneLineHexDataType=hexByteBuf[HEX_LINE_TYPE_OFS];

                if((oneLineHexDataType==HEX_TYPE_EX_LINEAR_ADD)|(oneLineHexDataType==HEX_TYPE_END_FILE))
                {
                    //next block is coming, save the last block data, first time do not have last block data, do not save for first time
                    if(firstTime==false)
                    {
                        HexData hexDataObj=new HexData(oneBlockCounter);
                        hexDataObj.setDataAddress(blockStartAddress, (short)4);
                        hexDataObj.setBlockLen(oneBlockCounter);
                        hexDataObj.saveData(localOneBlockData, oneBlockCounter);
                        listObj.add(hexDataObj);
                        oneBlockCounter=0;
                    }
                }

                if((oneLineHexDataType==HEX_TYPE_EX_LINEAR_ADD))
                {
                    blockStartAddress[0]=hexByteBuf[HEX_LINE_DATA_OFS];
                    blockStartAddress[1]=hexByteBuf[(short)(HEX_LINE_DATA_OFS+1)];
                     
                    blockStart=true;
                    nextBlockIsComing=true;
                    firstTime=false; 
                    oneBlockCounter=0;
                }

                //set full block start address
                if(blockStart)
                {
                     
                    blockStartAddress[2]=hexByteBuf[HEX_LINE_ADDRESS_OFS];
                    blockStartAddress[3]=hexByteBuf[HEX_LINE_ADDRESS_OFS+1];
                    //after fill the address, set the flag as false
                    blockStart=false;
                }

                if(oneLineHexDataType==HEX_TYPE_DATA)
                { 
                    for(i=0; i<oneLineHexLen; i++)
                    {
                        localOneBlockData[oneBlockCounter++]=hexByteBuf[HEX_LINE_DATA_OFS+i];
                    }
                }
            }//end while
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return listObj;
    }

    /**
    *@brief read all hex pure data to a byte buffer, it depends on the List 
    *       take care: if the hex file is not continuous address, this function do not padding ZERO
    *                  this function only combine the block data in hex file
    *@paream inputHexFile, input hex file name
    *
    */
    public static byte [] readHexFileToByteBuf(String inputHexFile)
    {
        listObjHex =readHexDataToList(inputHexFile);

        //read all hex data to a big buffer, then write byte buffer to bin
        int listObjSize=listObjHex.size();
        int allDataLen=0;
        int i=0, j=0, k=0;
        HexData hexObj=null;
        //get total length
        for(i=0; i<listObjSize; i++)
        {
            hexObj=(HexData)listObjHex.get(i);
            allDataLen+=hexObj.getDataBlockLength();
        }

        byte pureDataBuf[]=new byte[allDataLen];
        int counter=0;
        //get all the pure hex data
        for(i=0; i<listObjSize; i++)
        {
            hexObj=(HexData)listObjHex.get(i);
            for(j=0; j<hexObj.getDataBlockLength(); j++)
            {
                pureDataBuf[counter++]=hexObj.getHexData()[j];
                if(counter>allDataLen)
                    break;
            }
        }
        return pureDataBuf;
    }

    /**
    *@brief read a hex file, output a bin file without any padding
    *
    */
    public static boolean hexTobinWithoutPadding(String inputHexFile, String outputBinFile)
    {
        byte pureHex[]=readHexFileToByteBuf(inputHexFile);
        WriteByteToBin(pureHex, pureHex.length, outputBinFile);
        return true;
    }

    /**
    *@brief read a hex file, output a bin file with padding
    *
    */
    public static boolean hexToBinWithPadding(String outputBinFile, byte prePadding[], int prePaddingLen, byte hexBuf[], int hexBufLen, byte endPadding[], int endPaddingLen)
    {
        int totalLen=prePaddingLen+hexBufLen+endPaddingLen;
        byte mainBuf[]=new byte[totalLen];
         
        System.arraycopy(prePadding, 0, mainBuf, 0, prePaddingLen);  
        System.arraycopy(hexBuf, 0, mainBuf, prePaddingLen, hexBufLen);  
        System.arraycopy(endPadding, 0, mainBuf, prePaddingLen+hexBufLen, endPaddingLen);  

        //all data finish, save byte buf data to bin file
        WriteByteToBin(mainBuf, totalLen, outputBinFile);

        return true;
    }


    /**
    * pre padding before hex start address, for example hex start is 0x0004000, then padding 0x4000 bytes ZERO
    * 
    */
    public static boolean hexToBinPrePaddingZero(String inputHexFileDir, String outputBinFile)
    { 
        listObjHex =readHexDataToList(inputHexFileDir);

        //read all hex data to a big buffer, then write byte buffer to bin
        int listObjSize=listObjHex.size();
        int allHexDataLen=0;
        int i=0, j=0, k=0;
        HexData hexObj=null;
        for(i=0; i<listObjSize; i++)
        {
            hexObj=(HexData)listObjHex.get(i);
            allHexDataLen+=hexObj.getDataBlockLength();
        }

        byte pureDataBuf[]=new byte[allHexDataLen];
        int counter=0;
        for(i=0; i<listObjSize; i++)
        {
            hexObj=(HexData)listObjHex.get(i);
            for(j=0; j<hexObj.getDataBlockLength(); j++)
            {
                pureDataBuf[counter++]=hexObj.getHexData()[j];
                if(counter>allHexDataLen)
                    break;
            }
        }
    
        //get hex start address
        hexObj=(HexData)listObjHex.get(0);
        byte startAddress[]=hexObj.getAddress();    //this address should be 4 bytes
        int udAddress=0;
 
        int v0 = (startAddress[0] & 0xff) << 24; 
        int v1 = (startAddress[1] & 0xff) << 16;
        int v2 = (startAddress[2] & 0xff) << 8;
        int v3 = (startAddress[3] & 0xff) ;
        udAddress=(int)(v0 + v1 + v2 + v3); 

        if(udAddress<0)
            return false;

        byte prePadding[]=new byte[udAddress];

        for(i=0; i<udAddress; i++)
        {
            prePadding[i]=0;
        }
        int totalLen=udAddress+allHexDataLen;
        byte mainBuf[]=new byte[totalLen];

        //copy hex data
        System.arraycopy(pureDataBuf, 0, mainBuf, udAddress, allHexDataLen);  

        //all data finish, save byte buf data to bin file
        WriteByteToBin(mainBuf, totalLen, outputBinFile);
        return true;
    }
 

    /**
    * write byte buffer data to a bin file
    *
    */
    public static boolean WriteByteToBin(byte [] inputDataBuf, int dataLen, String binFileDir)
    {
        int testCounter=0;
         File binFileObj = new File(binFileDir);
        if (binFileObj.exists() && binFileObj.isFile()){
            boolean flag = binFileObj.delete();
        }
        try {
            if (binFileObj.createNewFile())
            {
                DataOutputStream out = new DataOutputStream(new FileOutputStream(binFileDir, true));
                out.write(inputDataBuf, 0, dataLen);
                out.close();                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    } 
}


/**
* we use this class to save the hex block data, save 3 informations
* 01: block start address
* 02: block data length
* 03: block pure hex data
*/
class HexData{

    private byte dataAddress[]=new byte[4];
    private int dataBlockLen=0;
    private byte data[];
    private int initDataLen=0;
    public HexData(int size)
    {
        this.initDataLen=size;
        data=new byte[size];
    }

    public byte[] getHexData()
    {
        return this.data;
    }
    public byte [] getAddress()
    {
        return this.dataAddress;
    }
    public int getDataBlockLength()
    {
        return this.dataBlockLen;
    }

    public boolean saveData(byte inputBuf[], int dataLen)
    {
        int i=0;
        if(dataLen>this.initDataLen)
            return false;

        for(i=0; i<dataLen; i++)
            this.data[i]=inputBuf[i];

        return true;
    }

    public boolean setBlockLen(int dataLen)
    {
        this.dataBlockLen=dataLen;
        return true;
    }

    public boolean setDataAddress(byte inputBuf[], short dataLen)
    {
        if(dataLen>4)
            return false;
        byte i=0; 
        for(i=0; i<4; i++)
        {
            dataAddress[i]=inputBuf[i];
        }
        return true;
    }
}


