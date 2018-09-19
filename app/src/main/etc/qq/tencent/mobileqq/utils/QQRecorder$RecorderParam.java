package com.tencent.mobileqq.utils;

import almz;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class QQRecorder$RecorderParam
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new almz();
  public int a;
  public Object a;
  public int b;
  public int c;
  public int d;
  public int e;
  
  public QQRecorder$RecorderParam(int paramInt1, int paramInt2, int paramInt3)
  {
    this.a = paramInt1;
    this.b = paramInt2;
    this.c = paramInt3;
  }
  
  public QQRecorder$RecorderParam(Parcel paramParcel)
  {
    this.a = paramParcel.readInt();
    this.b = paramParcel.readInt();
    this.c = paramParcel.readInt();
    this.d = paramParcel.readInt();
    this.e = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.a);
    paramParcel.writeInt(this.b);
    paramParcel.writeInt(this.c);
    paramParcel.writeInt(this.d);
    paramParcel.writeInt(this.e);
  }
}


/* Location:              D:\1\!\utils\QQRecorder$RecorderParam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */