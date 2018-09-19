package com.tencent.mobileqq.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.tencent.common.app.BaseApplicationImpl;
import com.tencent.mobileqq.statistics.StatisticCollector;
import com.tencent.qphone.base.BaseConstants;
import com.tencent.qphone.base.util.BaseApplication;
import com.tencent.qphone.base.util.QLog;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SoLoadUtil
{
  private static SimpleDateFormat jdField_a_of_type_JavaTextSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
  private static Set jdField_a_of_type_JavaUtilSet = new HashSet();
  
  static
  {
    jdField_a_of_type_JavaUtilSet.add("libamrnb.so");
    jdField_a_of_type_JavaUtilSet.add("libcodecsilk.so");
    jdField_a_of_type_JavaUtilSet.add("libSync_mq.so");
  }
  
  private static native int Decode(AssetManager paramAssetManager, String paramString1, String paramString2, String paramString3, String paramString4);
  
  private static int a(int paramInt1, int paramInt2)
  {
    return paramInt1 | paramInt2;
  }
  
  public static int a(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    int i = 0;
    Object localObject = paramContext;
    if (paramContext == null) {}
    for (;;)
    {
      String str;
      try
      {
        localObject = BaseApplicationImpl.getContext();
        paramContext = new File(paramString1);
        if (!paramContext.exists()) {
          paramContext.mkdir();
        }
        str = paramString1 + "tmp_" + BaseApplicationImpl.sProcessId + "_" + Thread.currentThread() + "/";
        paramContext = new File(str);
        paramContext.mkdirs();
        AssetManager localAssetManager = ((Context)localObject).getAssets();
        try
        {
          a((Context)localObject, "DecodeSo", 0, false, false);
          int j = 0;
          if (j <= 1)
          {
            i = Decode(localAssetManager, paramString2, str, "armeabi", paramString3);
            if (i != 0) {}
          }
          else
          {
            if (i == 0) {
              continue;
            }
            if (QLog.isColorLevel()) {
              QLog.d("SoLoadUtil", 2, "decode so fail error: " + i);
            }
            paramContext.delete();
            return i;
          }
          j += 1;
          continue;
          QLog.d("SoLoadUtil", 2, "Unsatisfied Link error: " + paramString2.toString() + "abi:" + a());
        }
        catch (UnsatisfiedLinkError paramString2)
        {
          if (!QLog.isColorLevel()) {
            break label335;
          }
        }
      }
      finally {}
      paramString2 = new File(str + paramString3);
      paramString1 = new File(paramString1 + paramString3);
      if (!paramString1.exists())
      {
        if (!paramString2.renameTo(paramString1)) {
          i = 256;
        }
      }
      else
      {
        paramContext.delete();
        continue;
        label335:
        i = 2;
      }
    }
  }
  
  /* Error */
  private static String a()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 146	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   6: astore_0
    //   7: aload_0
    //   8: ifnull +20 -> 28
    //   11: aload_0
    //   12: ldc -108
    //   14: invokevirtual 154	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   17: ifeq +11 -> 28
    //   20: ldc -108
    //   22: astore_0
    //   23: ldc 2
    //   25: monitorexit
    //   26: aload_0
    //   27: areturn
    //   28: aload_0
    //   29: ifnull +18 -> 47
    //   32: aload_0
    //   33: ldc -100
    //   35: invokevirtual 154	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   38: ifeq +9 -> 47
    //   41: ldc -98
    //   43: astore_0
    //   44: goto -21 -> 23
    //   47: invokestatic 161	com/tencent/mobileqq/utils/SoLoadUtil:b	()Z
    //   50: ifeq +9 -> 59
    //   53: ldc -108
    //   55: astore_0
    //   56: goto -33 -> 23
    //   59: ldc 110
    //   61: astore_0
    //   62: goto -39 -> 23
    //   65: astore_0
    //   66: ldc 2
    //   68: monitorexit
    //   69: aload_0
    //   70: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   6	56	0	str	String
    //   65	5	0	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	7	65	finally
    //   11	20	65	finally
    //   32	41	65	finally
    //   47	53	65	finally
  }
  
  private static String a(String paramString)
  {
    return "lib" + paramString + ".so";
  }
  
  private static String a(boolean paramBoolean)
  {
    if (paramBoolean) {
      return "lib/armeabi/";
    }
    return "lib/" + a() + "/";
  }
  
  private static final HashMap a(String paramString)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put(BaseConstants.RDM_NoChangeFailCode, "");
    localHashMap.put("osVersion", Build.VERSION.RELEASE);
    localHashMap.put("deviceName", Build.MANUFACTURER + "_" + Build.MODEL);
    localHashMap.put("time", jdField_a_of_type_JavaTextSimpleDateFormat.format(new Date(System.currentTimeMillis())));
    localHashMap.put("libName", paramString);
    return localHashMap;
  }
  
  private static final void a(HashMap paramHashMap)
  {
    StatisticCollector.a(BaseApplication.getContext()).a("", "loadSo", false, 0L, 0L, paramHashMap, "");
  }
  
  private static final void a(HashMap paramHashMap, int paramInt, String paramString, long paramLong)
  {
    paramHashMap.put("message", paramString);
    paramHashMap.put("costTime", paramLong + "");
    StatisticCollector.a(paramHashMap, paramInt);
    a(paramHashMap);
  }
  
  public static boolean a()
  {
    return ((!TextUtils.isEmpty(Build.CPU_ABI)) && (Build.CPU_ABI.contains("x86"))) || ((!TextUtils.isEmpty(Build.CPU_ABI2)) && (Build.CPU_ABI2.contains("x86"))) || (b());
  }
  
  public static boolean a(Context paramContext, String paramString, int paramInt, boolean paramBoolean)
  {
    return a(paramContext, paramString, paramInt, paramBoolean, true);
  }
  
  /* Error */
  public static boolean a(Context paramContext, String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 265	android/os/Build$VERSION:SDK_INT	I
    //   6: bipush 18
    //   8: if_icmpge +42 -> 50
    //   11: aload_1
    //   12: ldc_w 267
    //   15: invokevirtual 270	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   18: ifeq +32 -> 50
    //   21: aload_0
    //   22: ldc_w 272
    //   25: iconst_0
    //   26: iconst_0
    //   27: iconst_1
    //   28: invokestatic 108	com/tencent/mobileqq/utils/SoLoadUtil:a	(Landroid/content/Context;Ljava/lang/String;IZZ)Z
    //   31: ifne +19 -> 50
    //   34: ldc 119
    //   36: iconst_1
    //   37: ldc_w 274
    //   40: invokestatic 277	com/tencent/qphone/base/util/QLog:e	(Ljava/lang/String;ILjava/lang/String;)V
    //   43: iconst_0
    //   44: istore_3
    //   45: ldc 2
    //   47: monitorexit
    //   48: iload_3
    //   49: ireturn
    //   50: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   53: ifeq +29 -> 82
    //   56: ldc 119
    //   58: iconst_2
    //   59: new 63	java/lang/StringBuilder
    //   62: dup
    //   63: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   66: ldc_w 279
    //   69: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: aload_1
    //   73: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   79: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   82: invokestatic 284	android/os/SystemClock:uptimeMillis	()J
    //   85: lstore 12
    //   87: aload_1
    //   88: invokestatic 286	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/util/HashMap;
    //   91: astore 23
    //   93: iconst_0
    //   94: istore 7
    //   96: aload_0
    //   97: astore 18
    //   99: aload_0
    //   100: ifnonnull +8 -> 108
    //   103: invokestatic 51	com/tencent/common/app/BaseApplicationImpl:getContext	()Lcom/tencent/qphone/base/util/BaseApplication;
    //   106: astore 18
    //   108: new 63	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   115: aload 18
    //   117: invokevirtual 290	android/content/Context:getFilesDir	()Ljava/io/File;
    //   120: invokevirtual 293	java/io/File:getParent	()Ljava/lang/String;
    //   123: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: ldc_w 295
    //   129: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: astore 27
    //   137: new 63	java/lang/StringBuilder
    //   140: dup
    //   141: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   144: aload 18
    //   146: invokevirtual 290	android/content/Context:getFilesDir	()Ljava/io/File;
    //   149: invokevirtual 293	java/io/File:getParent	()Ljava/lang/String;
    //   152: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: ldc_w 297
    //   158: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: astore_0
    //   165: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   168: ifeq +12 -> 180
    //   171: ldc 119
    //   173: iconst_2
    //   174: ldc_w 299
    //   177: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   180: iload_3
    //   181: ifeq +1695 -> 1876
    //   184: new 53	java/io/File
    //   187: dup
    //   188: new 63	java/lang/StringBuilder
    //   191: dup
    //   192: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   195: aload_0
    //   196: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   199: aload_1
    //   200: invokestatic 301	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/lang/String;
    //   203: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   209: invokespecial 54	java/io/File:<init>	(Ljava/lang/String;)V
    //   212: astore_0
    //   213: aload_0
    //   214: invokevirtual 58	java/io/File:exists	()Z
    //   217: istore_3
    //   218: iload_3
    //   219: ifeq +606 -> 825
    //   222: aload_0
    //   223: invokevirtual 304	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   226: invokestatic 307	java/lang/System:load	(Ljava/lang/String;)V
    //   229: iconst_1
    //   230: istore_3
    //   231: aload 23
    //   233: iconst_0
    //   234: ldc -74
    //   236: invokestatic 284	android/os/SystemClock:uptimeMillis	()J
    //   239: lload 12
    //   241: lsub
    //   242: invokestatic 309	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/util/HashMap;ILjava/lang/String;J)V
    //   245: goto -200 -> 45
    //   248: astore_0
    //   249: iconst_1
    //   250: istore_3
    //   251: new 63	java/lang/StringBuilder
    //   254: dup
    //   255: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   258: ldc -74
    //   260: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: ldc_w 311
    //   266: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: aload_0
    //   270: invokestatic 315	com/tencent/qphone/base/util/QLog:getStackTraceString	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   273: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   276: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   279: astore 14
    //   281: iconst_0
    //   282: iconst_2
    //   283: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   286: istore_2
    //   287: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   290: ifeq +1597 -> 1887
    //   293: ldc 119
    //   295: iconst_2
    //   296: aload 14
    //   298: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   301: goto +1586 -> 1887
    //   304: new 53	java/io/File
    //   307: dup
    //   308: new 63	java/lang/StringBuilder
    //   311: dup
    //   312: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   315: aload 27
    //   317: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   320: aload_1
    //   321: invokestatic 301	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/lang/String;
    //   324: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   327: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   330: invokespecial 54	java/io/File:<init>	(Ljava/lang/String;)V
    //   333: astore 24
    //   335: ldc_w 319
    //   338: invokestatic 325	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   341: invokevirtual 329	java/lang/Integer:intValue	()I
    //   344: istore 5
    //   346: new 63	java/lang/StringBuilder
    //   349: dup
    //   350: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   353: ldc_w 331
    //   356: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   359: aload_1
    //   360: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   366: astore_0
    //   367: new 63	java/lang/StringBuilder
    //   370: dup
    //   371: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   374: ldc_w 333
    //   377: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   380: aload_1
    //   381: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   387: astore 25
    //   389: aload 18
    //   391: ldc_w 335
    //   394: iconst_0
    //   395: invokevirtual 339	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   398: astore 26
    //   400: aload 26
    //   402: aload_0
    //   403: iconst_m1
    //   404: invokeinterface 345 3 0
    //   409: istore 6
    //   411: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   414: ifeq +51 -> 465
    //   417: ldc 119
    //   419: iconst_2
    //   420: new 63	java/lang/StringBuilder
    //   423: dup
    //   424: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   427: ldc_w 347
    //   430: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: aload_1
    //   434: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   437: ldc_w 349
    //   440: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   443: iload 6
    //   445: invokevirtual 77	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   448: ldc_w 351
    //   451: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   454: iload 5
    //   456: invokevirtual 77	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   459: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   462: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   465: iload 6
    //   467: iload 5
    //   469: if_icmpeq +42 -> 511
    //   472: aload 24
    //   474: invokevirtual 58	java/io/File:exists	()Z
    //   477: ifeq +9 -> 486
    //   480: aload 24
    //   482: invokevirtual 129	java/io/File:delete	()Z
    //   485: pop
    //   486: iload 5
    //   488: ifne +383 -> 871
    //   491: aload 26
    //   493: invokeinterface 355 1 0
    //   498: aload_0
    //   499: iconst_m1
    //   500: invokeinterface 361 3 0
    //   505: invokeinterface 364 1 0
    //   510: pop
    //   511: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   514: ifeq +30 -> 544
    //   517: ldc 119
    //   519: iconst_2
    //   520: new 63	java/lang/StringBuilder
    //   523: dup
    //   524: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   527: ldc_w 366
    //   530: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   533: iload 4
    //   535: invokevirtual 369	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   538: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   541: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   544: iload_2
    //   545: istore 5
    //   547: aload 14
    //   549: astore_0
    //   550: aload 24
    //   552: invokevirtual 58	java/io/File:exists	()Z
    //   555: ifne +1353 -> 1908
    //   558: iload 4
    //   560: ifeq +367 -> 927
    //   563: aload 18
    //   565: aload 27
    //   567: ldc_w 371
    //   570: aload_1
    //   571: invokestatic 301	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/lang/String;
    //   574: invokestatic 373	com/tencent/mobileqq/utils/SoLoadUtil:a	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    //   577: istore 5
    //   579: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   582: ifeq +30 -> 612
    //   585: ldc 119
    //   587: iconst_2
    //   588: new 63	java/lang/StringBuilder
    //   591: dup
    //   592: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   595: ldc_w 375
    //   598: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   601: iload 5
    //   603: invokevirtual 77	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   606: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   609: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   612: iload 5
    //   614: ifeq +281 -> 895
    //   617: aload 24
    //   619: invokevirtual 129	java/io/File:delete	()Z
    //   622: pop
    //   623: new 63	java/lang/StringBuilder
    //   626: dup
    //   627: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   630: aload 14
    //   632: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   635: ldc_w 377
    //   638: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   641: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   644: astore_0
    //   645: iload_2
    //   646: bipush 8
    //   648: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   651: istore 5
    //   653: iload 5
    //   655: istore_2
    //   656: aload_0
    //   657: astore 14
    //   659: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   662: ifeq +1228 -> 1890
    //   665: ldc 119
    //   667: iconst_2
    //   668: aload_0
    //   669: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   672: iload 5
    //   674: istore_2
    //   675: aload_0
    //   676: astore 14
    //   678: goto +1212 -> 1890
    //   681: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   684: ifeq +12 -> 696
    //   687: ldc 119
    //   689: iconst_2
    //   690: ldc_w 379
    //   693: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   696: aload 24
    //   698: invokevirtual 58	java/io/File:exists	()Z
    //   701: istore 4
    //   703: iload 4
    //   705: ifeq +1143 -> 1848
    //   708: aload 24
    //   710: invokevirtual 304	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   713: invokestatic 307	java/lang/System:load	(Ljava/lang/String;)V
    //   716: iconst_1
    //   717: istore_3
    //   718: iload_2
    //   719: istore 5
    //   721: aload_0
    //   722: astore 14
    //   724: iload_3
    //   725: ifne +1057 -> 1782
    //   728: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   731: ifeq +12 -> 743
    //   734: ldc 119
    //   736: iconst_2
    //   737: ldc_w 381
    //   740: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   743: aload_1
    //   744: invokestatic 384	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   747: iconst_1
    //   748: istore 4
    //   750: invokestatic 284	android/os/SystemClock:uptimeMillis	()J
    //   753: lload 12
    //   755: lsub
    //   756: lstore 8
    //   758: aload 23
    //   760: iload_2
    //   761: aload_0
    //   762: lload 8
    //   764: invokestatic 309	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/util/HashMap;ILjava/lang/String;J)V
    //   767: iload 4
    //   769: istore_3
    //   770: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   773: ifeq -728 -> 45
    //   776: ldc 119
    //   778: iconst_2
    //   779: new 63	java/lang/StringBuilder
    //   782: dup
    //   783: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   786: ldc_w 386
    //   789: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   792: aload_1
    //   793: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   796: ldc_w 388
    //   799: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   802: lload 8
    //   804: invokevirtual 242	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   807: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   810: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   813: iload 4
    //   815: istore_3
    //   816: goto -771 -> 45
    //   819: astore_0
    //   820: ldc 2
    //   822: monitorexit
    //   823: aload_0
    //   824: athrow
    //   825: new 63	java/lang/StringBuilder
    //   828: dup
    //   829: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   832: ldc -74
    //   834: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   837: ldc_w 390
    //   840: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   843: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   846: astore 14
    //   848: iconst_0
    //   849: iconst_4
    //   850: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   853: istore_2
    //   854: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   857: ifeq +1039 -> 1896
    //   860: ldc 119
    //   862: iconst_2
    //   863: aload 14
    //   865: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   868: goto +1028 -> 1896
    //   871: aload 26
    //   873: invokeinterface 355 1 0
    //   878: aload_0
    //   879: iload 5
    //   881: invokeinterface 361 3 0
    //   886: invokeinterface 364 1 0
    //   891: pop
    //   892: goto -381 -> 511
    //   895: aload 24
    //   897: invokestatic 396	com/tencent/mobileqq/pluginsdk/IOUtil:getCRC32Value	(Ljava/io/File;)J
    //   900: lstore 8
    //   902: aload 26
    //   904: invokeinterface 355 1 0
    //   909: aload 25
    //   911: lload 8
    //   913: invokeinterface 400 4 0
    //   918: invokeinterface 364 1 0
    //   923: pop
    //   924: goto +966 -> 1890
    //   927: aconst_null
    //   928: astore 20
    //   930: aconst_null
    //   931: astore 19
    //   933: aconst_null
    //   934: astore 21
    //   936: aconst_null
    //   937: astore_0
    //   938: aload_0
    //   939: astore 17
    //   941: aload 21
    //   943: astore 15
    //   945: aload 20
    //   947: astore 16
    //   949: new 53	java/io/File
    //   952: dup
    //   953: aload 27
    //   955: invokespecial 54	java/io/File:<init>	(Ljava/lang/String;)V
    //   958: astore 22
    //   960: aload_0
    //   961: astore 17
    //   963: aload 21
    //   965: astore 15
    //   967: aload 20
    //   969: astore 16
    //   971: aload 22
    //   973: invokevirtual 58	java/io/File:exists	()Z
    //   976: ifne +26 -> 1002
    //   979: aload_0
    //   980: astore 17
    //   982: aload 21
    //   984: astore 15
    //   986: aload 20
    //   988: astore 16
    //   990: aload 22
    //   992: invokevirtual 61	java/io/File:mkdir	()Z
    //   995: istore 4
    //   997: iload 4
    //   999: ifeq +864 -> 1863
    //   1002: aload_0
    //   1003: astore 17
    //   1005: aload 21
    //   1007: astore 15
    //   1009: aload 20
    //   1011: astore 16
    //   1013: aload 18
    //   1015: invokevirtual 103	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   1018: new 63	java/lang/StringBuilder
    //   1021: dup
    //   1022: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1025: iconst_0
    //   1026: invokestatic 402	com/tencent/mobileqq/utils/SoLoadUtil:a	(Z)Ljava/lang/String;
    //   1029: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1032: aload_1
    //   1033: invokestatic 301	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/lang/String;
    //   1036: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1039: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1042: invokevirtual 408	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   1045: astore 22
    //   1047: aload 22
    //   1049: astore_0
    //   1050: aload_0
    //   1051: astore 17
    //   1053: aload_0
    //   1054: astore 15
    //   1056: aload 20
    //   1058: astore 16
    //   1060: new 410	java/io/FileOutputStream
    //   1063: dup
    //   1064: new 63	java/lang/StringBuilder
    //   1067: dup
    //   1068: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1071: aload 27
    //   1073: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1076: aload_1
    //   1077: invokestatic 301	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/lang/String;
    //   1080: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1083: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1086: invokespecial 411	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   1089: astore 18
    //   1091: aload_0
    //   1092: aload 18
    //   1094: invokestatic 417	com/tencent/mobileqq/app/InjectUtils:copy	(Ljava/io/InputStream;Ljava/io/OutputStream;)J
    //   1097: lstore 10
    //   1099: aload 18
    //   1101: ifnull +8 -> 1109
    //   1104: aload 18
    //   1106: invokevirtual 422	java/io/OutputStream:close	()V
    //   1109: iload_2
    //   1110: istore 6
    //   1112: aload 14
    //   1114: astore 15
    //   1116: lload 10
    //   1118: lstore 8
    //   1120: aload_0
    //   1121: ifnull +18 -> 1139
    //   1124: aload_0
    //   1125: invokevirtual 425	java/io/InputStream:close	()V
    //   1128: lload 10
    //   1130: lstore 8
    //   1132: aload 14
    //   1134: astore 15
    //   1136: iload_2
    //   1137: istore 6
    //   1139: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   1142: ifeq +12 -> 1154
    //   1145: ldc 119
    //   1147: iconst_2
    //   1148: ldc_w 427
    //   1151: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   1154: iload 6
    //   1156: istore 5
    //   1158: aload 15
    //   1160: astore_0
    //   1161: aload 24
    //   1163: invokevirtual 58	java/io/File:exists	()Z
    //   1166: ifeq +742 -> 1908
    //   1169: lload 8
    //   1171: aload 24
    //   1173: invokevirtual 430	java/io/File:length	()J
    //   1176: lcmp
    //   1177: ifeq +276 -> 1453
    //   1180: aload 24
    //   1182: invokevirtual 129	java/io/File:delete	()Z
    //   1185: pop
    //   1186: new 63	java/lang/StringBuilder
    //   1189: dup
    //   1190: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1193: aload 15
    //   1195: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1198: ldc_w 377
    //   1201: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1204: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1207: astore 14
    //   1209: iload 6
    //   1211: bipush 8
    //   1213: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   1216: istore_2
    //   1217: iload_2
    //   1218: istore 5
    //   1220: aload 14
    //   1222: astore_0
    //   1223: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   1226: ifeq +682 -> 1908
    //   1229: ldc 119
    //   1231: iconst_2
    //   1232: aload 14
    //   1234: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   1237: aload 14
    //   1239: astore_0
    //   1240: goto -559 -> 681
    //   1243: astore 15
    //   1245: aload_0
    //   1246: astore 17
    //   1248: aload 21
    //   1250: astore 15
    //   1252: aload 20
    //   1254: astore 16
    //   1256: aload 18
    //   1258: invokevirtual 103	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   1261: new 63	java/lang/StringBuilder
    //   1264: dup
    //   1265: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1268: iconst_1
    //   1269: invokestatic 402	com/tencent/mobileqq/utils/SoLoadUtil:a	(Z)Ljava/lang/String;
    //   1272: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1275: aload_1
    //   1276: invokestatic 301	com/tencent/mobileqq/utils/SoLoadUtil:a	(Ljava/lang/String;)Ljava/lang/String;
    //   1279: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1282: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1285: invokevirtual 408	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   1288: astore_0
    //   1289: goto -239 -> 1050
    //   1292: astore 15
    //   1294: aload 19
    //   1296: astore 18
    //   1298: aload 17
    //   1300: astore_0
    //   1301: aload 15
    //   1303: astore 17
    //   1305: aload_0
    //   1306: astore 15
    //   1308: aload 18
    //   1310: astore 16
    //   1312: new 63	java/lang/StringBuilder
    //   1315: dup
    //   1316: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1319: aload 14
    //   1321: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1324: ldc_w 432
    //   1327: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1330: aload 17
    //   1332: invokestatic 315	com/tencent/qphone/base/util/QLog:getStackTraceString	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   1335: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1338: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1341: astore 14
    //   1343: aload_0
    //   1344: astore 15
    //   1346: aload 18
    //   1348: astore 16
    //   1350: iload_2
    //   1351: bipush 16
    //   1353: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   1356: istore 6
    //   1358: aload_0
    //   1359: astore 15
    //   1361: aload 18
    //   1363: astore 16
    //   1365: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   1368: ifeq +18 -> 1386
    //   1371: aload_0
    //   1372: astore 15
    //   1374: aload 18
    //   1376: astore 16
    //   1378: ldc 119
    //   1380: iconst_2
    //   1381: aload 14
    //   1383: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   1386: aload 18
    //   1388: ifnull +8 -> 1396
    //   1391: aload 18
    //   1393: invokevirtual 422	java/io/OutputStream:close	()V
    //   1396: aload_0
    //   1397: ifnull +454 -> 1851
    //   1400: aload_0
    //   1401: invokevirtual 425	java/io/InputStream:close	()V
    //   1404: ldc2_w 433
    //   1407: lstore 8
    //   1409: aload 14
    //   1411: astore 15
    //   1413: goto -274 -> 1139
    //   1416: astore_0
    //   1417: ldc2_w 433
    //   1420: lstore 8
    //   1422: aload 14
    //   1424: astore 15
    //   1426: goto -287 -> 1139
    //   1429: astore_1
    //   1430: aload 15
    //   1432: astore_0
    //   1433: aload 16
    //   1435: ifnull +8 -> 1443
    //   1438: aload 16
    //   1440: invokevirtual 422	java/io/OutputStream:close	()V
    //   1443: aload_0
    //   1444: ifnull +7 -> 1451
    //   1447: aload_0
    //   1448: invokevirtual 425	java/io/InputStream:close	()V
    //   1451: aload_1
    //   1452: athrow
    //   1453: aload 24
    //   1455: invokestatic 396	com/tencent/mobileqq/pluginsdk/IOUtil:getCRC32Value	(Ljava/io/File;)J
    //   1458: lstore 8
    //   1460: aload 26
    //   1462: invokeinterface 355 1 0
    //   1467: aload 25
    //   1469: lload 8
    //   1471: invokeinterface 400 4 0
    //   1476: invokeinterface 364 1 0
    //   1481: pop
    //   1482: iload 6
    //   1484: istore 5
    //   1486: aload 15
    //   1488: astore_0
    //   1489: goto +419 -> 1908
    //   1492: astore 14
    //   1494: aload 24
    //   1496: invokestatic 396	com/tencent/mobileqq/pluginsdk/IOUtil:getCRC32Value	(Ljava/io/File;)J
    //   1499: lstore 8
    //   1501: aload 26
    //   1503: aload 25
    //   1505: ldc2_w 433
    //   1508: invokeinterface 438 4 0
    //   1513: lstore 10
    //   1515: lload 8
    //   1517: ldc2_w 433
    //   1520: lcmp
    //   1521: ifeq +120 -> 1641
    //   1524: lload 8
    //   1526: lload 10
    //   1528: lcmp
    //   1529: ifeq +112 -> 1641
    //   1532: aload 24
    //   1534: invokevirtual 129	java/io/File:delete	()Z
    //   1537: pop
    //   1538: new 63	java/lang/StringBuilder
    //   1541: dup
    //   1542: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1545: aload_0
    //   1546: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1549: ldc_w 440
    //   1552: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1555: lload 8
    //   1557: invokevirtual 242	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1560: ldc_w 442
    //   1563: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1566: lload 10
    //   1568: invokevirtual 242	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1571: ldc_w 444
    //   1574: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1577: aload 24
    //   1579: invokevirtual 88	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1582: ldc_w 446
    //   1585: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1588: aload 14
    //   1590: invokestatic 315	com/tencent/qphone/base/util/QLog:getStackTraceString	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   1593: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1596: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1599: astore 14
    //   1601: iload_2
    //   1602: sipush 128
    //   1605: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   1608: istore 5
    //   1610: iload 5
    //   1612: istore_2
    //   1613: aload 14
    //   1615: astore_0
    //   1616: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   1619: ifeq +295 -> 1914
    //   1622: ldc 119
    //   1624: iconst_2
    //   1625: aload 14
    //   1627: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   1630: iconst_0
    //   1631: istore_3
    //   1632: aload 14
    //   1634: astore_0
    //   1635: iload 5
    //   1637: istore_2
    //   1638: goto -920 -> 718
    //   1641: new 63	java/lang/StringBuilder
    //   1644: dup
    //   1645: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1648: aload_0
    //   1649: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1652: ldc_w 448
    //   1655: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1658: aload 24
    //   1660: invokevirtual 88	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1663: ldc_w 446
    //   1666: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1669: aload 14
    //   1671: invokestatic 315	com/tencent/qphone/base/util/QLog:getStackTraceString	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   1674: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1677: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1680: astore 14
    //   1682: iload_2
    //   1683: bipush 32
    //   1685: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   1688: istore 5
    //   1690: iload 5
    //   1692: istore_2
    //   1693: aload 14
    //   1695: astore_0
    //   1696: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   1699: ifeq +215 -> 1914
    //   1702: ldc 119
    //   1704: iconst_2
    //   1705: aload 14
    //   1707: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   1710: iload 5
    //   1712: istore_2
    //   1713: aload 14
    //   1715: astore_0
    //   1716: goto +198 -> 1914
    //   1719: astore 14
    //   1721: new 63	java/lang/StringBuilder
    //   1724: dup
    //   1725: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   1728: aload_0
    //   1729: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1732: ldc_w 450
    //   1735: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1738: aload 14
    //   1740: invokestatic 315	com/tencent/qphone/base/util/QLog:getStackTraceString	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   1743: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1746: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1749: astore_0
    //   1750: iload_2
    //   1751: bipush 64
    //   1753: invokestatic 317	com/tencent/mobileqq/utils/SoLoadUtil:a	(II)I
    //   1756: istore_2
    //   1757: iload_2
    //   1758: istore 5
    //   1760: aload_0
    //   1761: astore 14
    //   1763: invokestatic 117	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   1766: ifeq +16 -> 1782
    //   1769: ldc 119
    //   1771: iconst_2
    //   1772: aload_0
    //   1773: invokestatic 126	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   1776: aload_0
    //   1777: astore 14
    //   1779: iload_2
    //   1780: istore 5
    //   1782: iload 5
    //   1784: istore_2
    //   1785: iload_3
    //   1786: istore 4
    //   1788: aload 14
    //   1790: astore_0
    //   1791: goto -1041 -> 750
    //   1794: astore 15
    //   1796: goto -687 -> 1109
    //   1799: astore_0
    //   1800: iload_2
    //   1801: istore 6
    //   1803: aload 14
    //   1805: astore 15
    //   1807: lload 10
    //   1809: lstore 8
    //   1811: goto -672 -> 1139
    //   1814: astore 15
    //   1816: goto -420 -> 1396
    //   1819: astore 14
    //   1821: goto -378 -> 1443
    //   1824: astore_0
    //   1825: goto -374 -> 1451
    //   1828: astore_1
    //   1829: aload 18
    //   1831: astore 16
    //   1833: goto -400 -> 1433
    //   1836: astore 17
    //   1838: goto -533 -> 1305
    //   1841: astore_0
    //   1842: iload 7
    //   1844: istore_3
    //   1845: goto -1594 -> 251
    //   1848: goto -1130 -> 718
    //   1851: ldc2_w 433
    //   1854: lstore 8
    //   1856: aload 14
    //   1858: astore 15
    //   1860: goto -721 -> 1139
    //   1863: aconst_null
    //   1864: astore 18
    //   1866: aconst_null
    //   1867: astore_0
    //   1868: ldc2_w 433
    //   1871: lstore 10
    //   1873: goto -774 -> 1099
    //   1876: iconst_0
    //   1877: istore_2
    //   1878: iconst_0
    //   1879: istore_3
    //   1880: ldc -74
    //   1882: astore 14
    //   1884: goto -1580 -> 304
    //   1887: goto -1583 -> 304
    //   1890: aload 14
    //   1892: astore_0
    //   1893: goto -1212 -> 681
    //   1896: iconst_0
    //   1897: istore_3
    //   1898: goto -1594 -> 304
    //   1901: astore_0
    //   1902: iconst_0
    //   1903: istore 5
    //   1905: goto -1559 -> 346
    //   1908: iload 5
    //   1910: istore_2
    //   1911: goto -1230 -> 681
    //   1914: iconst_0
    //   1915: istore_3
    //   1916: goto -1198 -> 718
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1919	0	paramContext	Context
    //   0	1919	1	paramString	String
    //   0	1919	2	paramInt	int
    //   0	1919	3	paramBoolean1	boolean
    //   0	1919	4	paramBoolean2	boolean
    //   344	1565	5	i	int
    //   409	1393	6	j	int
    //   94	1749	7	bool	boolean
    //   756	1099	8	l1	long
    //   1097	775	10	l2	long
    //   85	669	12	l3	long
    //   279	1144	14	localObject1	Object
    //   1492	97	14	localUnsatisfiedLinkError1	UnsatisfiedLinkError
    //   1599	115	14	str1	String
    //   1719	20	14	localUnsatisfiedLinkError2	UnsatisfiedLinkError
    //   1761	43	14	localContext1	Context
    //   1819	38	14	localIOException1	java.io.IOException
    //   1882	9	14	str2	String
    //   943	251	15	localObject2	Object
    //   1243	1	15	localException	Exception
    //   1250	1	15	localObject3	Object
    //   1292	10	15	localIOException2	java.io.IOException
    //   1306	181	15	localObject4	Object
    //   1794	1	15	localIOException3	java.io.IOException
    //   1805	1	15	localContext2	Context
    //   1814	1	15	localIOException4	java.io.IOException
    //   1858	1	15	localIOException5	java.io.IOException
    //   947	885	16	localObject5	Object
    //   939	392	17	localObject6	Object
    //   1836	1	17	localIOException6	java.io.IOException
    //   97	1768	18	localObject7	Object
    //   931	364	19	localObject8	Object
    //   928	325	20	localObject9	Object
    //   934	315	21	localObject10	Object
    //   958	90	22	localObject11	Object
    //   91	668	23	localHashMap	HashMap
    //   333	1326	24	localFile	File
    //   387	1117	25	str3	String
    //   398	1104	26	localSharedPreferences	android.content.SharedPreferences
    //   135	937	27	str4	String
    // Exception table:
    //   from	to	target	type
    //   231	245	248	java/lang/UnsatisfiedLinkError
    //   3	43	819	finally
    //   50	82	819	finally
    //   82	93	819	finally
    //   103	108	819	finally
    //   108	180	819	finally
    //   184	218	819	finally
    //   222	229	819	finally
    //   231	245	819	finally
    //   251	301	819	finally
    //   304	335	819	finally
    //   335	346	819	finally
    //   346	465	819	finally
    //   472	486	819	finally
    //   491	511	819	finally
    //   511	544	819	finally
    //   550	558	819	finally
    //   563	612	819	finally
    //   617	653	819	finally
    //   659	672	819	finally
    //   681	696	819	finally
    //   696	703	819	finally
    //   708	716	819	finally
    //   728	743	819	finally
    //   743	747	819	finally
    //   750	767	819	finally
    //   770	813	819	finally
    //   825	868	819	finally
    //   871	892	819	finally
    //   895	924	819	finally
    //   1104	1109	819	finally
    //   1124	1128	819	finally
    //   1139	1154	819	finally
    //   1161	1217	819	finally
    //   1223	1237	819	finally
    //   1391	1396	819	finally
    //   1400	1404	819	finally
    //   1438	1443	819	finally
    //   1447	1451	819	finally
    //   1451	1453	819	finally
    //   1453	1482	819	finally
    //   1494	1515	819	finally
    //   1532	1610	819	finally
    //   1616	1630	819	finally
    //   1641	1690	819	finally
    //   1696	1710	819	finally
    //   1721	1757	819	finally
    //   1763	1776	819	finally
    //   1013	1047	1243	java/lang/Exception
    //   949	960	1292	java/io/IOException
    //   971	979	1292	java/io/IOException
    //   990	997	1292	java/io/IOException
    //   1013	1047	1292	java/io/IOException
    //   1060	1091	1292	java/io/IOException
    //   1256	1289	1292	java/io/IOException
    //   1400	1404	1416	java/io/IOException
    //   949	960	1429	finally
    //   971	979	1429	finally
    //   990	997	1429	finally
    //   1013	1047	1429	finally
    //   1060	1091	1429	finally
    //   1256	1289	1429	finally
    //   1312	1343	1429	finally
    //   1350	1358	1429	finally
    //   1365	1371	1429	finally
    //   1378	1386	1429	finally
    //   708	716	1492	java/lang/UnsatisfiedLinkError
    //   743	747	1719	java/lang/UnsatisfiedLinkError
    //   1104	1109	1794	java/io/IOException
    //   1124	1128	1799	java/io/IOException
    //   1391	1396	1814	java/io/IOException
    //   1438	1443	1819	java/io/IOException
    //   1447	1451	1824	java/io/IOException
    //   1091	1099	1828	finally
    //   1091	1099	1836	java/io/IOException
    //   222	229	1841	java/lang/UnsatisfiedLinkError
    //   335	346	1901	java/lang/NumberFormatException
  }
  
  public static boolean b()
  {
    String str = System.getProperty("os.arch");
    return (Build.BRAND.equals("asus")) && ((Build.CPU_ABI2.toLowerCase(Locale.US).contains("x86")) || (str.toLowerCase(Locale.US).contains("x86")) || (str.toLowerCase(Locale.US).contains("i386")) || (str.toLowerCase(Locale.US).contains("i686")) || (str.toLowerCase(Locale.US).contains("i586")) || (str.toLowerCase(Locale.US).contains("i486")));
  }
}


/* Location:              D:\1\!\utils\SoLoadUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */