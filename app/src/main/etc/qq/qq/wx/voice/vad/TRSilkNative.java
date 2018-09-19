package com.qq.wx.voice.vad;

class TRSilkNative
{
  native int nativeTRSilkDecode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  
  native int nativeTRSilkDecodeInit(int paramInt1, int paramInt2);
  
  native int nativeTRSilkDecodeRelease();
  
  native int nativeTRSilkEncode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  
  native int nativeTRSilkInit(int paramInt1, int paramInt2);
  
  native int nativeTRSilkRelease();
}


/* Location:           D:\Share\vad\
 * Qualified Name:     com.qq.wx.voice.vad.TRSilkNative
 * JD-Core Version:    0.7.1
 */