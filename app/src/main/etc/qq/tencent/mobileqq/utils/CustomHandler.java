package com.tencent.mobileqq.utils;

import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
import mqq.os.MqqHandler;

public class CustomHandler
  extends MqqHandler
{
  private final WeakReference a;
  
  public CustomHandler(Handler.Callback paramCallback)
  {
    this.a = new WeakReference(paramCallback);
  }
  
  public CustomHandler(Looper paramLooper, Handler.Callback paramCallback)
  {
    super(paramLooper);
    this.a = new WeakReference(paramCallback);
  }
  
  public void handleMessage(Message paramMessage)
  {
    Handler.Callback localCallback = (Handler.Callback)this.a.get();
    if (localCallback != null) {
      localCallback.handleMessage(paramMessage);
    }
  }
  
  public String toString()
  {
    Handler.Callback localCallback = (Handler.Callback)this.a.get();
    return super.toString() + " " + localCallback;
  }
}


/* Location:              D:\1\!\utils\CustomHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */