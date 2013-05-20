package io.jvm.zip.bytearray.tools;

import java.util.zip.CRC32;

public class ByteArrayTool {

  public static byte[] ConcatArrays(byte[]...arrays) {
      // Determine the length of the result array
      int totalLength = 0;
      for (int i = 0; i < arrays.length; i++)
      {
          totalLength += arrays[i].length;
      }

      // create the result array
      byte[] result = new byte[totalLength];

      // copy the source arrays into the result array
      int currentIndex = 0;
      for (int i = 0; i < arrays.length; i++)
      {
          System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
          currentIndex += arrays[i].length;
      }

      return result;
  }

  public static long readLongLittleEndian(byte[] array,int pos){
    long temp = 0;
    temp |= array[pos+7]&0xff;
    temp <<=8;
    temp |= array[pos+6]&0xff;
    temp <<=8;
    temp |= array[pos+5]&0xff;
    temp <<=8;
    temp |= array[pos+4]&0xff;
    temp <<=8;
    temp |= array[pos+3]&0xff;
    temp <<=8;
    temp |= array[pos+2]&0xff;
    temp <<=8;
    temp |= array[pos+1]&0xff;
    temp <<=8;
    temp |= array[pos]&0xff;
    return temp;
  }

  public static int readShortLittleEndian(byte[] b, int off){
      return (b[off] & 0xff) | (b[off+1] & 0xff) << 8;
  }

  public static final short readShortBigEndian(byte[] array, int pos) {
    short temp = 0;
    temp |= array[pos] & 0xff;
    temp <<= 8;
    temp |= array[pos + 1] & 0xff;
    return temp;
  }

  public static int readIntLittleEndian(byte[] b, int off){
      return ((b[off] & 0xff) | (b[off+1] & 0xff) << 8) | ((b[off+2] & 0xff) | (b[off+3] & 0xff) << 8) << 16;
  }

  public static byte[] toByteArray(int in,int outSize) {
    byte[] out = new byte[outSize];
    byte[] intArray = toByteArray(in);
    for( int i=0; i<intArray.length && i<outSize; i++ ) {
      out[i] = intArray[i];
    }
    return out;
  }

  public static byte[] toByteArray(int in) {
    byte[] out = new byte[4];

    out[0] = (byte)in;
    out[1] = (byte)(in >> 8);
    out[2] = (byte)(in >> 16);
    out[3] = (byte)(in >> 24);

    return out;
  }

  public static final void writeShortLittleEndian(byte[] array, int pos, short value) {
    array[pos +1] = (byte) (value >>> 8);
    array[pos ] = (byte) (value & 0xFF);
  }

  public static final void writeIntLittleEndian(byte[] array, int pos,int value) {
    array[pos+3] = (byte) (value >>>24);
    array[pos+2] = (byte) (value >>>16);
    array[pos+1] = (byte) (value >>>8);
    array[pos] = (byte) (value &0xFF);
  }

  public static void writeLongLittleEndian(byte[] array, int pos, long value){
    array[pos+7] = (byte) (value >>>56);
    array[pos+6] = (byte) (value >>>48);
    array[pos+5] = (byte) (value >>>40);
    array[pos+4] = (byte) (value >>>32);
    array[pos+3] = (byte) (value >>>24);
    array[pos+2] = (byte) (value >>>16);
    array[pos+1] = (byte) (value >>>8);
    array[pos] = (byte) (value &0xFF);
  }

  public static int computeCRC32(final byte[] bytes, int offset, int length) {
    CRC32 crc = new CRC32();
    crc.update(bytes, offset, length);
    return (int)crc.getValue();
  }

}
