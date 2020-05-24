# HexUtils
起因：有个项目需要使用蓝牙下载Bin或Hex到MCU的外部Flash，所以写了这么一个模块。

## 功能
使用java语言读取Hex数据到Byte数组，Hex文件转Bin文件

目前版本还不完善，会有Bug存在，请注意。目前只会返回Hex文件的纯数据，中间有空地址的话，不会处理。

## 思路
将Hex中的数据分为三部分，基址，每个block长度，每个block的数据。
Hex一行中的Offset是两个字节，所以最大长度是0xFFFF。但有的时候地址又是不连续的，所以无法将所有Hex数据放到一起。
可以使用一个Class，将所有数据分为三部分“基地，长度，数据”，每次处理一个Block，这样的话，有空地址可以对其填充零等。

如果地址都是连续的，可以将其读取到一个大的Byte数组中。

## 使用方法

可以参考HexUtilsUnitTest.java

1. 读取Hex数据到一个Byte数组
byte pureHexData[]=HexUtils.readHexFileToByteBuf(".\\HexFileName.hex");

2. 将hex文件转换为Bin文件，v1.0版本只支持连续长度的Hex，中间有空地址没有padding 0x00
 
示例：
HexUtils.hexTobinWithoutPadding(".\\HexFileName.hex", "hexToBin_noPadding.bin");

2. 将hex文件转换为Bin文件，前面Padding 0x00, v1.0版本只支持连续长度的Hex，中间有空地址没有padding 0x00
 
示例：
HexUtils.hexToBinPrePaddingZero(".\\HexFileName.hex", "hexToBin_paddingZero.bin");

## UnitTest
我使用自己的hex文件测试过，转成Bin文件以后没有丢失数据。比如文件为 bin001.bin。
我本身就有一个HexToBin.exe工具，可以将Hex转换为Bin文件，使用这个工具转一个文件为bin002.bin。
其中bin001.bin和bin002.bin完全一致。










