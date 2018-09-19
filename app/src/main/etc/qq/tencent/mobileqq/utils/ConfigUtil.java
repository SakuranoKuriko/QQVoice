package com.tencent.mobileqq.utils;

import android.content.Context;
import java.util.Locale;

public class ConfigUtil
{
  public static String a(Context paramContext, String paramString1, String paramString2)
  {
    paramContext = FileUtils.a(String.format(Locale.getDefault(), "%s_%s_config_content", new Object[] { paramString1, paramString2 }));
    if (paramContext == null) {
      return null;
    }
    return (String)paramContext;
  }
  
  public static void a(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    FileUtils.a(String.format(Locale.getDefault(), "%s_%s_config_content", new Object[] { paramString1, paramString2 }), paramString3);
  }
}


/* Location:              D:\1\!\utils\ConfigUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */