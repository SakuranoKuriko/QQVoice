package com.tencent.mobileqq.utils;

public class SendMessageHandler$SendMessageRunnable
  implements Runnable
{
  public String a;
  public String[] a;
  public int b = -1;
  public long c;
  public boolean c;
  public long d = Long.MAX_VALUE;
  public long e = Long.MAX_VALUE;
  public long f = -1L;
  public long g = -1L;
  public long h = -1L;
  
  public SendMessageHandler$SendMessageRunnable()
  {
    this.jdField_c_of_type_Long = -1L;
    this.jdField_a_of_type_JavaLangString = "";
    this.jdField_a_of_type_ArrayOfJavaLangString = new String[0];
  }
  
  public void run() {}
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append("index:");
    localStringBuilder.append(String.valueOf(this.b));
    localStringBuilder.append(",reason:");
    localStringBuilder.append(this.jdField_a_of_type_JavaLangString);
    localStringBuilder.append(",startTime:");
    localStringBuilder.append(String.valueOf(this.g - this.f));
    localStringBuilder.append(",timeOut:");
    localStringBuilder.append(String.valueOf(this.g - this.f + this.jdField_c_of_type_Long));
    if (this.jdField_c_of_type_Boolean)
    {
      localStringBuilder.append(",duration:");
      localStringBuilder.append(String.valueOf(this.h - this.g));
      localStringBuilder.append(",error:");
      localStringBuilder.append(String.valueOf(this.d));
      if (this.e != Long.MAX_VALUE)
      {
        localStringBuilder.append(",serverReply:");
        localStringBuilder.append(String.valueOf(this.e));
      }
    }
    for (;;)
    {
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localStringBuilder.append(",status:RUNNING");
    }
  }
}


/* Location:              D:\1\!\utils\SendMessageHandler$SendMessageRunnable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */