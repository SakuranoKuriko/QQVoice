package com.tencent.mobileqq.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.provider.MediaStore.Images.Media;
import com.tencent.common.app.BaseApplicationImpl;
import com.tencent.image.SafeBitmapFactory;
import com.tencent.mobileqq.activity.photo.LocalMediaInfo;
import com.tencent.mobileqq.activity.photo.MediaFileFilter;
import com.tencent.mobileqq.activity.photo.MediaScanner;
import com.tencent.mobileqq.activity.photo.MediaScannerInfo;
import com.tencent.mobileqq.activity.photo.MimeHelper;
import com.tencent.mobileqq.filemanager.util.FileUtil;
import com.tencent.mobileqq.widget.QQToast;
import com.tencent.qphone.base.util.QLog;
import common.config.service.QzoneConfig;
import cooperation.qzone.QZoneHelper;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlbumUtil
{
  public static int a;
  public static long a;
  public static String a;
  public static HashMap a;
  private static Map jdField_a_of_type_JavaUtilMap = new HashMap();
  public static final int[] a;
  static String[] jdField_a_of_type_ArrayOfJavaLangString;
  private static long jdField_b_of_type_Long;
  public static String b;
  public static HashMap b;
  private static String[] jdField_b_of_type_ArrayOfJavaLangString = { "_id", "_data", "duration", "date_added", "date_modified", "mime_type", "_size" };
  private static long c;
  public static String c;
  public static HashMap c;
  public static final HashMap d;
  
  static
  {
    jdField_a_of_type_Int = 10485760;
    jdField_a_of_type_JavaLangString = "";
    jdField_a_of_type_JavaUtilHashMap = new HashMap();
    jdField_b_of_type_JavaUtilHashMap = new HashMap();
    jdField_c_of_type_JavaUtilHashMap = new HashMap();
    d = new HashMap();
    d.put("image", Integer.valueOf(0));
    d.put("video", Integer.valueOf(1));
    d.put("mobileqq", Integer.valueOf(2));
    jdField_a_of_type_ArrayOfInt = new int[] { 0, 3000, 1 };
    if (Build.VERSION.SDK_INT >= 16) {}
    for (jdField_a_of_type_ArrayOfJavaLangString = new String[] { "_id", "_data", "mime_type", "date_added", "date_modified", "orientation", "latitude", "longitude", "_size", "width", "height" }; Build.VERSION.SDK_INT >= 16; jdField_a_of_type_ArrayOfJavaLangString = new String[] { "_id", "_data", "mime_type", "date_added", "date_modified", "orientation", "latitude", "longitude", "_size" })
    {
      jdField_b_of_type_ArrayOfJavaLangString = new String[] { "_id", "_data", "duration", "date_added", "date_modified", "mime_type", "_size", "width", "height" };
      return;
    }
  }
  
  public static float a()
  {
    return QzoneConfig.getInstance().getConfig("QZoneSetting", "photoGroupListImageCropSpaceRatio", 0.25F);
  }
  
  public static int a()
  {
    return QzoneConfig.getInstance().getConfig("MiniVideo", "MaxSelectVideoNum", 50);
  }
  
  public static int a(LocalMediaInfo paramLocalMediaInfo)
  {
    if (paramLocalMediaInfo == null) {
      return -1;
    }
    String[] arrayOfString = MimeHelper.a(paramLocalMediaInfo.mMimeType);
    if (arrayOfString == null) {
      return 0;
    }
    Integer localInteger = (Integer)d.get(arrayOfString[0]);
    if (localInteger == null)
    {
      QLog.w("AlbumUtil", 1, "getMediaType fail : " + paramLocalMediaInfo.mMimeType + " - " + arrayOfString[0]);
      return -1;
    }
    return localInteger.intValue();
  }
  
  private static Cursor a(Context paramContext, String paramString, String[] paramArrayOfString, int paramInt)
  {
    if (paramInt > 0)
    {
      localObject = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon();
      ((Uri.Builder)localObject).appendQueryParameter("limit", String.valueOf(paramInt));
    }
    for (Object localObject = ((Uri.Builder)localObject).build();; localObject = MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
      return paramContext.getContentResolver().query((Uri)localObject, jdField_a_of_type_ArrayOfJavaLangString, paramString, paramArrayOfString, "_id DESC");
    }
  }
  
  public static String a(Context paramContext)
  {
    return paramContext.getSharedPreferences("album_file", 0).getString("album_key_id", null);
  }
  
  public static String a(List paramList)
  {
    Object localObject1 = new StringBuilder("_data");
    ((StringBuilder)localObject1).append(" IS NOT NULL AND ");
    ((StringBuilder)localObject1).append("_size");
    ((StringBuilder)localObject1).append(" > 0 ");
    Object localObject2 = localObject1;
    if (paramList != null)
    {
      localObject2 = localObject1;
      if (paramList.size() > 0)
      {
        ((StringBuilder)localObject1).append(" AND (");
        localObject2 = paramList.iterator();
        paramList = (List)localObject1;
        int i = 1;
        if (((Iterator)localObject2).hasNext())
        {
          localObject1 = (String)((Iterator)localObject2).next();
          if (i == 0) {
            paramList.append(" OR ");
          }
          for (;;)
          {
            paramList.append(" ( ");
            paramList = a("bucket_id", paramList, (String)localObject1);
            paramList.append(" ) ");
            break;
            i = 0;
          }
        }
        paramList.append(" ) ");
        localObject2 = paramList;
      }
    }
    return ((StringBuilder)localObject2).toString();
  }
  
  private static StringBuilder a(String paramString1, StringBuilder paramStringBuilder, String paramString2)
  {
    paramStringBuilder.append(paramString1);
    paramStringBuilder.append(" = ");
    paramStringBuilder.append(paramString2);
    return paramStringBuilder;
  }
  
  public static URL a(LocalMediaInfo paramLocalMediaInfo)
  {
    return a(paramLocalMediaInfo, null);
  }
  
  public static URL a(LocalMediaInfo paramLocalMediaInfo, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder("albumthumb");
    localStringBuilder.append("://");
    localStringBuilder.append(paramLocalMediaInfo.path);
    if (paramString != null)
    {
      localStringBuilder.append("#");
      localStringBuilder.append(paramString);
    }
    try
    {
      paramLocalMediaInfo = new URL(localStringBuilder.toString());
      return paramLocalMediaInfo;
    }
    catch (MalformedURLException paramLocalMediaInfo)
    {
      if (QLog.isColorLevel()) {
        QLog.d("SelectPhotoTrace", 2, paramLocalMediaInfo.getMessage(), paramLocalMediaInfo);
      }
    }
    return null;
  }
  
  public static URL a(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder("videocover");
    localStringBuilder.append("://");
    localStringBuilder.append(paramString1);
    if (paramString2 != null)
    {
      localStringBuilder.append("#");
      localStringBuilder.append(paramString2);
    }
    try
    {
      paramString1 = new URL(localStringBuilder.toString());
      return paramString1;
    }
    catch (MalformedURLException paramString1)
    {
      if (QLog.isColorLevel()) {
        QLog.d("SelectPhotoTrace", 2, paramString1.getMessage(), paramString1);
      }
    }
    return null;
  }
  
  private static ArrayList a(Context paramContext, Cursor paramCursor, List paramList, int paramInt, MediaFileFilter paramMediaFileFilter, boolean paramBoolean, long paramLong)
  {
    paramContext = new ArrayList();
    paramCursor = (ArrayList)a(paramCursor, paramList, paramInt, paramMediaFileFilter, paramBoolean, paramLong);
    if (paramCursor != null) {
      paramContext.addAll(paramCursor);
    }
    if (QLog.isColorLevel()) {
      QLog.e("AlbumUtil", 2, "getSystemAndAppVideoList listsize=" + paramContext.size());
    }
    paramCursor = MediaScanner.a(BaseApplicationImpl.getContext());
    if (paramCursor != null)
    {
      paramCursor = paramCursor.a(false, -1);
      if (paramCursor != null)
      {
        paramCursor = paramCursor.iterator();
        while (paramCursor.hasNext())
        {
          paramList = (MediaScannerInfo)paramCursor.next();
          paramMediaFileFilter = new LocalMediaInfo();
          paramMediaFileFilter._id = paramList.jdField_a_of_type_Int;
          paramMediaFileFilter.mDuration = paramList.jdField_c_of_type_Long;
          paramMediaFileFilter.fileSize = paramList.jdField_a_of_type_Long;
          paramMediaFileFilter.path = paramList.jdField_c_of_type_JavaLangString;
          paramMediaFileFilter.modifiedDate = paramList.jdField_b_of_type_Long;
          paramMediaFileFilter.mMimeType = paramList.d;
          paramMediaFileFilter.isSystemMeidaStore = false;
          paramContext.add(paramMediaFileFilter);
        }
      }
    }
    Collections.sort(paramContext);
    return paramContext;
  }
  
  public static List a(Context paramContext, int paramInt1, int paramInt2, MediaFileFilter paramMediaFileFilter)
  {
    return a(paramContext, paramInt1, paramInt2, paramMediaFileFilter, false, 0, null, false);
  }
  
  /* Error */
  public static List a(Context paramContext, int paramInt1, int paramInt2, MediaFileFilter paramMediaFileFilter, long paramLong)
  {
    // Byte code:
    //   0: invokestatic 377	android/os/SystemClock:uptimeMillis	()J
    //   3: lstore 6
    //   5: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   8: ifeq +30 -> 38
    //   11: ldc -121
    //   13: iconst_2
    //   14: new 137	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   21: ldc_w 379
    //   24: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   27: lload 6
    //   29: invokevirtual 382	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   32: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   35: invokestatic 384	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   38: aconst_null
    //   39: astore 9
    //   41: iload_2
    //   42: ifgt +14 -> 56
    //   45: new 386	java/lang/IllegalArgumentException
    //   48: dup
    //   49: ldc_w 388
    //   52: invokespecial 389	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   55: athrow
    //   56: new 293	java/util/ArrayList
    //   59: dup
    //   60: invokespecial 294	java/util/ArrayList:<init>	()V
    //   63: astore 11
    //   65: getstatic 392	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   68: invokevirtual 172	android/net/Uri:buildUpon	()Landroid/net/Uri$Builder;
    //   71: astore 10
    //   73: aload 10
    //   75: ldc -82
    //   77: iload_2
    //   78: bipush 6
    //   80: imul
    //   81: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 183	android/net/Uri$Builder:appendQueryParameter	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   87: pop
    //   88: aload 10
    //   90: invokevirtual 187	android/net/Uri$Builder:build	()Landroid/net/Uri;
    //   93: astore 10
    //   95: aload_0
    //   96: invokevirtual 193	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   99: aload 10
    //   101: getstatic 92	com/tencent/mobileqq/utils/AlbumUtil:jdField_b_of_type_ArrayOfJavaLangString	[Ljava/lang/String;
    //   104: ldc_w 394
    //   107: aconst_null
    //   108: ldc -61
    //   110: invokevirtual 201	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   113: astore_0
    //   114: aload_0
    //   115: astore 9
    //   117: aload 9
    //   119: astore_0
    //   120: getstatic 62	android/os/Build$VERSION:SDK_INT	I
    //   123: bipush 16
    //   125: if_icmplt +188 -> 313
    //   128: iconst_1
    //   129: istore 8
    //   131: aload 9
    //   133: astore_0
    //   134: aload 9
    //   136: aload 11
    //   138: iload_2
    //   139: aload_3
    //   140: iload 8
    //   142: lload 4
    //   144: invokestatic 297	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/database/Cursor;Ljava/util/List;ILcom/tencent/mobileqq/activity/photo/MediaFileFilter;ZJ)Ljava/util/List;
    //   147: pop
    //   148: aload 9
    //   150: ifnull +10 -> 160
    //   153: aload 9
    //   155: invokeinterface 399 1 0
    //   160: invokestatic 316	com/tencent/common/app/BaseApplicationImpl:getContext	()Lcom/tencent/qphone/base/util/BaseApplication;
    //   163: invokestatic 321	com/tencent/mobileqq/activity/photo/MediaScanner:a	(Landroid/content/Context;)Lcom/tencent/mobileqq/activity/photo/MediaScanner;
    //   166: astore_0
    //   167: aload_0
    //   168: ifnull +212 -> 380
    //   171: aload_0
    //   172: iconst_1
    //   173: bipush 100
    //   175: invokevirtual 324	com/tencent/mobileqq/activity/photo/MediaScanner:a	(ZI)Ljava/util/ArrayList;
    //   178: astore_0
    //   179: aload_0
    //   180: ifnull +200 -> 380
    //   183: aload_0
    //   184: invokevirtual 325	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   187: astore_0
    //   188: aload_0
    //   189: invokeinterface 241 1 0
    //   194: ifeq +186 -> 380
    //   197: aload_0
    //   198: invokeinterface 245 1 0
    //   203: checkcast 327	com/tencent/mobileqq/activity/photo/MediaScannerInfo
    //   206: astore 9
    //   208: new 121	com/tencent/mobileqq/activity/photo/LocalMediaInfo
    //   211: dup
    //   212: invokespecial 328	com/tencent/mobileqq/activity/photo/LocalMediaInfo:<init>	()V
    //   215: astore 10
    //   217: aload 10
    //   219: aload 9
    //   221: getfield 329	com/tencent/mobileqq/activity/photo/MediaScannerInfo:jdField_a_of_type_Int	I
    //   224: i2l
    //   225: putfield 331	com/tencent/mobileqq/activity/photo/LocalMediaInfo:_id	J
    //   228: aload 10
    //   230: aload 9
    //   232: getfield 333	com/tencent/mobileqq/activity/photo/MediaScannerInfo:jdField_c_of_type_Long	J
    //   235: putfield 336	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mDuration	J
    //   238: aload 10
    //   240: aload 9
    //   242: getfield 338	com/tencent/mobileqq/activity/photo/MediaScannerInfo:jdField_a_of_type_Long	J
    //   245: putfield 341	com/tencent/mobileqq/activity/photo/LocalMediaInfo:fileSize	J
    //   248: aload 10
    //   250: aload 9
    //   252: getfield 343	com/tencent/mobileqq/activity/photo/MediaScannerInfo:jdField_c_of_type_JavaLangString	Ljava/lang/String;
    //   255: putfield 271	com/tencent/mobileqq/activity/photo/LocalMediaInfo:path	Ljava/lang/String;
    //   258: aload 10
    //   260: aload 9
    //   262: getfield 345	com/tencent/mobileqq/activity/photo/MediaScannerInfo:jdField_b_of_type_Long	J
    //   265: putfield 348	com/tencent/mobileqq/activity/photo/LocalMediaInfo:modifiedDate	J
    //   268: aload 10
    //   270: aload 9
    //   272: getfield 350	com/tencent/mobileqq/activity/photo/MediaScannerInfo:d	Ljava/lang/String;
    //   275: putfield 124	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mMimeType	Ljava/lang/String;
    //   278: aload 10
    //   280: iconst_0
    //   281: putfield 354	com/tencent/mobileqq/activity/photo/LocalMediaInfo:isSystemMeidaStore	Z
    //   284: aload_3
    //   285: ifnull +15 -> 300
    //   288: aload_3
    //   289: aload 9
    //   291: getfield 350	com/tencent/mobileqq/activity/photo/MediaScannerInfo:d	Ljava/lang/String;
    //   294: invokevirtual 404	com/tencent/mobileqq/activity/photo/MediaFileFilter:a	(Ljava/lang/String;)Z
    //   297: ifne -109 -> 188
    //   300: aload 11
    //   302: aload 10
    //   304: invokeinterface 405 2 0
    //   309: pop
    //   310: goto -122 -> 188
    //   313: iconst_0
    //   314: istore 8
    //   316: goto -185 -> 131
    //   319: astore 10
    //   321: aconst_null
    //   322: astore 9
    //   324: aload 9
    //   326: astore_0
    //   327: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   330: ifeq +19 -> 349
    //   333: aload 9
    //   335: astore_0
    //   336: ldc -121
    //   338: iconst_2
    //   339: aload 10
    //   341: invokevirtual 406	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   344: aload 10
    //   346: invokestatic 287	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   349: aload 9
    //   351: ifnull -191 -> 160
    //   354: aload 9
    //   356: invokeinterface 399 1 0
    //   361: goto -201 -> 160
    //   364: astore_0
    //   365: aload 9
    //   367: astore_3
    //   368: aload_3
    //   369: ifnull +9 -> 378
    //   372: aload_3
    //   373: invokeinterface 399 1 0
    //   378: aload_0
    //   379: athrow
    //   380: aload 11
    //   382: invokestatic 364	java/util/Collections:sort	(Ljava/util/List;)V
    //   385: new 293	java/util/ArrayList
    //   388: dup
    //   389: invokespecial 294	java/util/ArrayList:<init>	()V
    //   392: astore_0
    //   393: iload_2
    //   394: aload 11
    //   396: invokeinterface 229 1 0
    //   401: invokestatic 412	java/lang/Math:min	(II)I
    //   404: istore_2
    //   405: iconst_0
    //   406: istore_1
    //   407: iload_1
    //   408: iload_2
    //   409: if_icmpge +25 -> 434
    //   412: aload_0
    //   413: aload 11
    //   415: iload_1
    //   416: invokeinterface 415 2 0
    //   421: invokeinterface 405 2 0
    //   426: pop
    //   427: iload_1
    //   428: iconst_1
    //   429: iadd
    //   430: istore_1
    //   431: goto -24 -> 407
    //   434: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   437: ifeq +34 -> 471
    //   440: ldc -121
    //   442: iconst_2
    //   443: new 137	java/lang/StringBuilder
    //   446: dup
    //   447: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   450: ldc_w 417
    //   453: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   456: invokestatic 377	android/os/SystemClock:uptimeMillis	()J
    //   459: lload 6
    //   461: lsub
    //   462: invokevirtual 382	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   465: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   468: invokestatic 384	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   471: aload_0
    //   472: areturn
    //   473: astore 9
    //   475: aload_0
    //   476: astore_3
    //   477: aload 9
    //   479: astore_0
    //   480: goto -112 -> 368
    //   483: astore 10
    //   485: goto -161 -> 324
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	488	0	paramContext	Context
    //   0	488	1	paramInt1	int
    //   0	488	2	paramInt2	int
    //   0	488	3	paramMediaFileFilter	MediaFileFilter
    //   0	488	4	paramLong	long
    //   3	457	6	l	long
    //   129	186	8	bool	boolean
    //   39	327	9	localObject1	Object
    //   473	5	9	localObject2	Object
    //   71	232	10	localObject3	Object
    //   319	26	10	localException1	Exception
    //   483	1	10	localException2	Exception
    //   63	351	11	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   65	114	319	java/lang/Exception
    //   65	114	364	finally
    //   120	128	473	finally
    //   134	148	473	finally
    //   327	333	473	finally
    //   336	349	473	finally
    //   120	128	483	java/lang/Exception
    //   134	148	483	java/lang/Exception
  }
  
  /* Error */
  public static List a(Context paramContext, int paramInt1, int paramInt2, MediaFileFilter paramMediaFileFilter, boolean paramBoolean1, int paramInt3, ArrayList paramArrayList, boolean paramBoolean2)
  {
    // Byte code:
    //   0: invokestatic 377	android/os/SystemClock:uptimeMillis	()J
    //   3: lstore 9
    //   5: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   8: ifeq +30 -> 38
    //   11: ldc -121
    //   13: iconst_2
    //   14: new 137	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   21: ldc_w 419
    //   24: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   27: lload 9
    //   29: invokevirtual 382	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   32: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   35: invokestatic 384	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   38: aconst_null
    //   39: astore 12
    //   41: iload_2
    //   42: ifgt +14 -> 56
    //   45: new 386	java/lang/IllegalArgumentException
    //   48: dup
    //   49: ldc_w 388
    //   52: invokespecial 389	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   55: athrow
    //   56: new 293	java/util/ArrayList
    //   59: dup
    //   60: invokespecial 294	java/util/ArrayList:<init>	()V
    //   63: astore 13
    //   65: getstatic 62	android/os/Build$VERSION:SDK_INT	I
    //   68: bipush 16
    //   70: if_icmplt +299 -> 369
    //   73: iconst_1
    //   74: istore 8
    //   76: iload 8
    //   78: ifeq +120 -> 198
    //   81: iload 4
    //   83: ifeq +107 -> 190
    //   86: ldc_w 421
    //   89: astore 11
    //   91: aload_0
    //   92: aload 11
    //   94: iconst_3
    //   95: anewarray 64	java/lang/String
    //   98: dup
    //   99: iconst_0
    //   100: iload 5
    //   102: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   105: aastore
    //   106: dup
    //   107: iconst_1
    //   108: iload_1
    //   109: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   112: aastore
    //   113: dup
    //   114: iconst_2
    //   115: iload_1
    //   116: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   119: aastore
    //   120: iload_2
    //   121: invokestatic 423	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;I)Landroid/database/Cursor;
    //   124: astore_0
    //   125: aload_0
    //   126: aload 13
    //   128: iload_1
    //   129: iload_2
    //   130: iload 8
    //   132: aload_3
    //   133: aload 6
    //   135: iload 7
    //   137: invokestatic 426	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/database/Cursor;Ljava/util/List;IIZLcom/tencent/mobileqq/activity/photo/MediaFileFilter;Ljava/util/ArrayList;Z)V
    //   140: aload_0
    //   141: ifnull +9 -> 150
    //   144: aload_0
    //   145: invokeinterface 399 1 0
    //   150: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   153: ifeq +34 -> 187
    //   156: ldc -121
    //   158: iconst_2
    //   159: new 137	java/lang/StringBuilder
    //   162: dup
    //   163: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   166: ldc_w 428
    //   169: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: invokestatic 377	android/os/SystemClock:uptimeMillis	()J
    //   175: lload 9
    //   177: lsub
    //   178: invokevirtual 382	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   181: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   184: invokestatic 384	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   187: aload 13
    //   189: areturn
    //   190: ldc_w 430
    //   193: astore 11
    //   195: goto -104 -> 91
    //   198: iload 5
    //   200: ifle +88 -> 288
    //   203: new 137	java/lang/StringBuilder
    //   206: dup
    //   207: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   210: ldc_w 432
    //   213: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   216: iload 5
    //   218: invokevirtual 307	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   221: ldc_w 434
    //   224: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   227: ldc 68
    //   229: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   232: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   235: astore 11
    //   237: getstatic 166	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   240: invokevirtual 172	android/net/Uri:buildUpon	()Landroid/net/Uri$Builder;
    //   243: astore 14
    //   245: aload 14
    //   247: ldc -82
    //   249: iload_2
    //   250: bipush 6
    //   252: imul
    //   253: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   256: invokevirtual 183	android/net/Uri$Builder:appendQueryParameter	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   259: pop
    //   260: aload 14
    //   262: invokevirtual 187	android/net/Uri$Builder:build	()Landroid/net/Uri;
    //   265: astore 14
    //   267: aload_0
    //   268: invokevirtual 193	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   271: aload 14
    //   273: getstatic 88	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_ArrayOfJavaLangString	[Ljava/lang/String;
    //   276: aload 11
    //   278: aconst_null
    //   279: ldc -61
    //   281: invokevirtual 201	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   284: astore_0
    //   285: goto -160 -> 125
    //   288: ldc_w 394
    //   291: astore 11
    //   293: goto -56 -> 237
    //   296: astore_3
    //   297: aconst_null
    //   298: astore_0
    //   299: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   302: ifeq +14 -> 316
    //   305: ldc -121
    //   307: iconst_2
    //   308: aload_3
    //   309: invokevirtual 406	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   312: aload_3
    //   313: invokestatic 287	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   316: aload_0
    //   317: ifnull -167 -> 150
    //   320: aload_0
    //   321: invokeinterface 399 1 0
    //   326: goto -176 -> 150
    //   329: astore_0
    //   330: aload 12
    //   332: astore_3
    //   333: aload_3
    //   334: ifnull +9 -> 343
    //   337: aload_3
    //   338: invokeinterface 399 1 0
    //   343: aload_0
    //   344: athrow
    //   345: astore 6
    //   347: aload_0
    //   348: astore_3
    //   349: aload 6
    //   351: astore_0
    //   352: goto -19 -> 333
    //   355: astore 6
    //   357: aload_0
    //   358: astore_3
    //   359: aload 6
    //   361: astore_0
    //   362: goto -29 -> 333
    //   365: astore_3
    //   366: goto -67 -> 299
    //   369: iconst_0
    //   370: istore 8
    //   372: goto -296 -> 76
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	375	0	paramContext	Context
    //   0	375	1	paramInt1	int
    //   0	375	2	paramInt2	int
    //   0	375	3	paramMediaFileFilter	MediaFileFilter
    //   0	375	4	paramBoolean1	boolean
    //   0	375	5	paramInt3	int
    //   0	375	6	paramArrayList	ArrayList
    //   0	375	7	paramBoolean2	boolean
    //   74	297	8	bool	boolean
    //   3	173	9	l	long
    //   89	203	11	str	String
    //   39	292	12	localObject1	Object
    //   63	125	13	localArrayList	ArrayList
    //   243	29	14	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   91	125	296	java/lang/Exception
    //   203	237	296	java/lang/Exception
    //   237	285	296	java/lang/Exception
    //   91	125	329	finally
    //   203	237	329	finally
    //   237	285	329	finally
    //   125	140	345	finally
    //   299	316	355	finally
    //   125	140	365	java/lang/Exception
  }
  
  /* Error */
  public static List a(Context paramContext, int paramInt, List paramList, MediaFileFilter paramMediaFileFilter)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: new 293	java/util/ArrayList
    //   6: dup
    //   7: invokespecial 294	java/util/ArrayList:<init>	()V
    //   10: astore 5
    //   12: aload_2
    //   13: invokestatic 437	com/tencent/mobileqq/utils/AlbumUtil:a	(Ljava/util/List;)Ljava/lang/String;
    //   16: astore_2
    //   17: getstatic 166	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   20: invokevirtual 172	android/net/Uri:buildUpon	()Landroid/net/Uri$Builder;
    //   23: astore 6
    //   25: aload 6
    //   27: ldc -82
    //   29: iload_1
    //   30: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   33: invokevirtual 183	android/net/Uri$Builder:appendQueryParameter	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   36: pop
    //   37: aload 6
    //   39: invokevirtual 187	android/net/Uri$Builder:build	()Landroid/net/Uri;
    //   42: astore 6
    //   44: aload_0
    //   45: invokevirtual 193	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   48: aload 6
    //   50: getstatic 88	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_ArrayOfJavaLangString	[Ljava/lang/String;
    //   53: aload_2
    //   54: aconst_null
    //   55: ldc -61
    //   57: invokevirtual 201	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   60: astore_0
    //   61: aload_0
    //   62: aload 5
    //   64: iconst_0
    //   65: iload_1
    //   66: iconst_0
    //   67: aload_3
    //   68: aconst_null
    //   69: iconst_0
    //   70: invokestatic 426	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/database/Cursor;Ljava/util/List;IIZLcom/tencent/mobileqq/activity/photo/MediaFileFilter;Ljava/util/ArrayList;Z)V
    //   73: aload_0
    //   74: ifnull +9 -> 83
    //   77: aload_0
    //   78: invokeinterface 399 1 0
    //   83: aload 5
    //   85: areturn
    //   86: astore_2
    //   87: aconst_null
    //   88: astore_0
    //   89: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   92: ifeq +14 -> 106
    //   95: ldc -121
    //   97: iconst_2
    //   98: aload_2
    //   99: invokevirtual 406	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   102: aload_2
    //   103: invokestatic 287	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   106: aload_0
    //   107: ifnull -24 -> 83
    //   110: aload_0
    //   111: invokeinterface 399 1 0
    //   116: aload 5
    //   118: areturn
    //   119: astore_0
    //   120: aload 4
    //   122: astore_2
    //   123: aload_2
    //   124: ifnull +9 -> 133
    //   127: aload_2
    //   128: invokeinterface 399 1 0
    //   133: aload_0
    //   134: athrow
    //   135: astore_3
    //   136: aload_0
    //   137: astore_2
    //   138: aload_3
    //   139: astore_0
    //   140: goto -17 -> 123
    //   143: astore_3
    //   144: aload_0
    //   145: astore_2
    //   146: aload_3
    //   147: astore_0
    //   148: goto -25 -> 123
    //   151: astore_2
    //   152: goto -63 -> 89
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	paramContext	Context
    //   0	155	1	paramInt	int
    //   0	155	2	paramList	List
    //   0	155	3	paramMediaFileFilter	MediaFileFilter
    //   1	120	4	localObject1	Object
    //   10	107	5	localArrayList	ArrayList
    //   23	26	6	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   44	61	86	java/lang/Exception
    //   44	61	119	finally
    //   61	73	135	finally
    //   89	106	143	finally
    //   61	73	151	java/lang/Exception
  }
  
  /* Error */
  private static List a(Context paramContext, String paramString, int paramInt, MediaFileFilter paramMediaFileFilter)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: new 293	java/util/ArrayList
    //   9: dup
    //   10: invokespecial 294	java/util/ArrayList:<init>	()V
    //   13: astore 7
    //   15: aload_0
    //   16: aload_1
    //   17: aconst_null
    //   18: iload_2
    //   19: invokestatic 423	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;I)Landroid/database/Cursor;
    //   22: astore_0
    //   23: getstatic 62	android/os/Build$VERSION:SDK_INT	I
    //   26: bipush 16
    //   28: if_icmplt +30 -> 58
    //   31: iconst_1
    //   32: istore 4
    //   34: aload_0
    //   35: aload 7
    //   37: iconst_m1
    //   38: iload_2
    //   39: iload 4
    //   41: aload_3
    //   42: invokestatic 441	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/database/Cursor;Ljava/util/List;IIZLcom/tencent/mobileqq/activity/photo/MediaFileFilter;)V
    //   45: aload_0
    //   46: ifnull +9 -> 55
    //   49: aload_0
    //   50: invokeinterface 399 1 0
    //   55: aload 7
    //   57: areturn
    //   58: iconst_0
    //   59: istore 4
    //   61: goto -27 -> 34
    //   64: astore_1
    //   65: aload 6
    //   67: astore_0
    //   68: aload_0
    //   69: astore 5
    //   71: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   74: ifeq +17 -> 91
    //   77: aload_0
    //   78: astore 5
    //   80: ldc -121
    //   82: iconst_2
    //   83: aload_1
    //   84: invokevirtual 406	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   87: aload_1
    //   88: invokestatic 287	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   91: aload_0
    //   92: ifnull -37 -> 55
    //   95: aload_0
    //   96: invokeinterface 399 1 0
    //   101: aload 7
    //   103: areturn
    //   104: astore_1
    //   105: aload 5
    //   107: astore_0
    //   108: aload_0
    //   109: ifnull +9 -> 118
    //   112: aload_0
    //   113: invokeinterface 399 1 0
    //   118: aload_1
    //   119: athrow
    //   120: astore_1
    //   121: goto -13 -> 108
    //   124: astore_1
    //   125: goto -57 -> 68
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	128	0	paramContext	Context
    //   0	128	1	paramString	String
    //   0	128	2	paramInt	int
    //   0	128	3	paramMediaFileFilter	MediaFileFilter
    //   32	28	4	bool	boolean
    //   1	105	5	localContext	Context
    //   4	62	6	localObject	Object
    //   13	89	7	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   15	23	64	java/lang/Exception
    //   15	23	104	finally
    //   71	77	104	finally
    //   80	91	104	finally
    //   23	31	120	finally
    //   34	45	120	finally
    //   23	31	124	java/lang/Exception
    //   34	45	124	java/lang/Exception
  }
  
  /* Error */
  public static List a(Context paramContext, String paramString1, String paramString2, int paramInt1, MediaFileFilter paramMediaFileFilter, int paramInt2, int paramInt3, boolean paramBoolean1, ArrayList paramArrayList, boolean paramBoolean2)
  {
    // Byte code:
    //   0: new 293	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 294	java/util/ArrayList:<init>	()V
    //   7: astore 13
    //   9: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   12: ifeq +12 -> 24
    //   15: ldc -121
    //   17: iconst_2
    //   18: ldc_w 444
    //   21: invokestatic 310	com/tencent/qphone/base/util/QLog:e	(Ljava/lang/String;ILjava/lang/String;)V
    //   24: aconst_null
    //   25: astore 12
    //   27: new 137	java/lang/StringBuilder
    //   30: dup
    //   31: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   34: astore 14
    //   36: iconst_0
    //   37: istore 10
    //   39: aload_2
    //   40: ifnull +13 -> 53
    //   43: ldc_w 446
    //   46: aload_1
    //   47: invokevirtual 449	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   50: ifeq +6 -> 56
    //   53: iconst_1
    //   54: istore 10
    //   56: iload 10
    //   58: ifeq +18 -> 76
    //   61: iload_3
    //   62: ifgt +55 -> 117
    //   65: new 386	java/lang/IllegalArgumentException
    //   68: dup
    //   69: ldc_w 451
    //   72: invokespecial 389	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   75: athrow
    //   76: aload 14
    //   78: new 137	java/lang/StringBuilder
    //   81: dup
    //   82: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   85: ldc_w 453
    //   88: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload_1
    //   92: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: ldc_w 455
    //   98: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: pop
    //   108: aload 14
    //   110: ldc_w 457
    //   113: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: pop
    //   117: getstatic 62	android/os/Build$VERSION:SDK_INT	I
    //   120: bipush 16
    //   122: if_icmplt +524 -> 646
    //   125: iconst_1
    //   126: istore 11
    //   128: iload 11
    //   130: ifeq +323 -> 453
    //   133: iload 7
    //   135: ifeq +199 -> 334
    //   138: aload 14
    //   140: new 137	java/lang/StringBuilder
    //   143: dup
    //   144: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   147: ldc_w 459
    //   150: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: getstatic 27	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_Int	I
    //   156: invokevirtual 307	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   159: ldc_w 461
    //   162: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: ldc 84
    //   167: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: ldc_w 463
    //   173: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: ldc 84
    //   178: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   181: ldc_w 465
    //   184: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: ldc 86
    //   189: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: ldc_w 463
    //   195: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: ldc 86
    //   200: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: ldc_w 467
    //   206: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: ldc 86
    //   211: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   214: ldc_w 469
    //   217: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: ldc 84
    //   222: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: ldc_w 471
    //   228: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   231: ldc 86
    //   233: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   236: ldc_w 469
    //   239: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   242: ldc 84
    //   244: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: ldc_w 473
    //   250: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: ldc 68
    //   255: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   261: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: pop
    //   265: aload_0
    //   266: aload 14
    //   268: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   271: iconst_3
    //   272: anewarray 64	java/lang/String
    //   275: dup
    //   276: iconst_0
    //   277: iload 5
    //   279: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   282: aastore
    //   283: dup
    //   284: iconst_1
    //   285: iload 6
    //   287: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   290: aastore
    //   291: dup
    //   292: iconst_2
    //   293: iload 6
    //   295: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   298: aastore
    //   299: iload_3
    //   300: invokestatic 423	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;I)Landroid/database/Cursor;
    //   303: astore_0
    //   304: aload_0
    //   305: aload 13
    //   307: iload 6
    //   309: iload_3
    //   310: iload 11
    //   312: aload 4
    //   314: aload 8
    //   316: iload 9
    //   318: invokestatic 426	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/database/Cursor;Ljava/util/List;IIZLcom/tencent/mobileqq/activity/photo/MediaFileFilter;Ljava/util/ArrayList;Z)V
    //   321: aload_0
    //   322: ifnull +9 -> 331
    //   325: aload_0
    //   326: invokeinterface 399 1 0
    //   331: aload 13
    //   333: areturn
    //   334: aload 14
    //   336: new 137	java/lang/StringBuilder
    //   339: dup
    //   340: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   343: ldc_w 459
    //   346: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   349: getstatic 27	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_Int	I
    //   352: invokevirtual 307	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   355: ldc_w 461
    //   358: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   361: ldc 84
    //   363: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   366: ldc_w 463
    //   369: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   372: ldc 84
    //   374: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   377: ldc_w 475
    //   380: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   383: ldc 86
    //   385: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   388: ldc_w 463
    //   391: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   394: ldc 86
    //   396: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   399: ldc_w 477
    //   402: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   405: ldc 68
    //   407: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   410: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   413: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   416: pop
    //   417: goto -152 -> 265
    //   420: astore_1
    //   421: aconst_null
    //   422: astore_0
    //   423: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   426: ifeq +14 -> 440
    //   429: ldc -121
    //   431: iconst_2
    //   432: aload_1
    //   433: invokevirtual 406	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   436: aload_1
    //   437: invokestatic 287	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   440: aload_0
    //   441: ifnull -110 -> 331
    //   444: aload_0
    //   445: invokeinterface 399 1 0
    //   450: aload 13
    //   452: areturn
    //   453: iload 5
    //   455: ifle +113 -> 568
    //   458: aload 14
    //   460: new 137	java/lang/StringBuilder
    //   463: dup
    //   464: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   467: ldc_w 432
    //   470: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   473: iload 5
    //   475: invokevirtual 307	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   478: ldc_w 479
    //   481: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   484: ldc 82
    //   486: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   489: ldc_w 481
    //   492: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   495: getstatic 27	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_Int	I
    //   498: invokevirtual 307	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   501: ldc_w 483
    //   504: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: ldc 68
    //   509: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   512: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   515: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   518: pop
    //   519: getstatic 166	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   522: invokevirtual 172	android/net/Uri:buildUpon	()Landroid/net/Uri$Builder;
    //   525: astore_1
    //   526: aload_1
    //   527: ldc -82
    //   529: iload_3
    //   530: bipush 6
    //   532: imul
    //   533: invokestatic 177	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   536: invokevirtual 183	android/net/Uri$Builder:appendQueryParameter	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   539: pop
    //   540: aload_1
    //   541: invokevirtual 187	android/net/Uri$Builder:build	()Landroid/net/Uri;
    //   544: astore_1
    //   545: aload_0
    //   546: invokevirtual 193	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   549: aload_1
    //   550: getstatic 88	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_ArrayOfJavaLangString	[Ljava/lang/String;
    //   553: aload 14
    //   555: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   558: aconst_null
    //   559: ldc -61
    //   561: invokevirtual 201	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   564: astore_0
    //   565: goto -261 -> 304
    //   568: aload 14
    //   570: new 137	java/lang/StringBuilder
    //   573: dup
    //   574: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   577: ldc_w 485
    //   580: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   583: getstatic 27	com/tencent/mobileqq/utils/AlbumUtil:jdField_a_of_type_Int	I
    //   586: invokevirtual 307	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   589: ldc_w 483
    //   592: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   595: ldc 68
    //   597: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   600: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   603: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   606: pop
    //   607: goto -88 -> 519
    //   610: astore_0
    //   611: aload 12
    //   613: astore_1
    //   614: aload_1
    //   615: ifnull +9 -> 624
    //   618: aload_1
    //   619: invokeinterface 399 1 0
    //   624: aload_0
    //   625: athrow
    //   626: astore_2
    //   627: aload_0
    //   628: astore_1
    //   629: aload_2
    //   630: astore_0
    //   631: goto -17 -> 614
    //   634: astore_2
    //   635: aload_0
    //   636: astore_1
    //   637: aload_2
    //   638: astore_0
    //   639: goto -25 -> 614
    //   642: astore_1
    //   643: goto -220 -> 423
    //   646: iconst_0
    //   647: istore 11
    //   649: goto -521 -> 128
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	652	0	paramContext	Context
    //   0	652	1	paramString1	String
    //   0	652	2	paramString2	String
    //   0	652	3	paramInt1	int
    //   0	652	4	paramMediaFileFilter	MediaFileFilter
    //   0	652	5	paramInt2	int
    //   0	652	6	paramInt3	int
    //   0	652	7	paramBoolean1	boolean
    //   0	652	8	paramArrayList	ArrayList
    //   0	652	9	paramBoolean2	boolean
    //   37	20	10	i	int
    //   126	522	11	bool	boolean
    //   25	587	12	localObject	Object
    //   7	444	13	localArrayList	ArrayList
    //   34	535	14	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   138	265	420	java/lang/Exception
    //   265	304	420	java/lang/Exception
    //   334	417	420	java/lang/Exception
    //   458	519	420	java/lang/Exception
    //   519	565	420	java/lang/Exception
    //   568	607	420	java/lang/Exception
    //   138	265	610	finally
    //   265	304	610	finally
    //   334	417	610	finally
    //   458	519	610	finally
    //   519	565	610	finally
    //   568	607	610	finally
    //   304	321	626	finally
    //   423	440	634	finally
    //   304	321	642	java/lang/Exception
  }
  
  public static List a(Context paramContext, String paramString1, String paramString2, int paramInt1, MediaFileFilter paramMediaFileFilter, int paramInt2, int paramInt3, boolean paramBoolean1, ArrayList paramArrayList, boolean paramBoolean2, long paramLong)
  {
    return a(paramContext, paramString1, paramString2, paramInt1, paramMediaFileFilter, paramInt2, paramInt3, paramBoolean1, paramArrayList, paramBoolean2, paramLong, -1);
  }
  
  /* Error */
  public static List a(Context paramContext, String paramString1, String paramString2, int paramInt1, MediaFileFilter paramMediaFileFilter, int paramInt2, int paramInt3, boolean paramBoolean1, ArrayList paramArrayList, boolean paramBoolean2, long paramLong, int paramInt4)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 19
    //   3: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   6: ifeq +12 -> 18
    //   9: ldc -121
    //   11: iconst_2
    //   12: ldc_w 491
    //   15: invokestatic 310	com/tencent/qphone/base/util/QLog:e	(Ljava/lang/String;ILjava/lang/String;)V
    //   18: ldc_w 493
    //   21: aload_1
    //   22: invokevirtual 449	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   25: ifeq +126 -> 151
    //   28: aload_0
    //   29: invokevirtual 193	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   32: getstatic 392	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   35: getstatic 92	com/tencent/mobileqq/utils/AlbumUtil:jdField_b_of_type_ArrayOfJavaLangString	[Ljava/lang/String;
    //   38: aconst_null
    //   39: aconst_null
    //   40: ldc -61
    //   42: invokevirtual 201	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   45: astore 16
    //   47: aload 16
    //   49: astore 17
    //   51: getstatic 62	android/os/Build$VERSION:SDK_INT	I
    //   54: bipush 16
    //   56: if_icmplt +47 -> 103
    //   59: iconst_1
    //   60: istore 15
    //   62: aload 16
    //   64: astore 17
    //   66: aload_0
    //   67: aload 16
    //   69: aconst_null
    //   70: iload_3
    //   71: aload 4
    //   73: iload 15
    //   75: lload 10
    //   77: invokestatic 495	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;Landroid/database/Cursor;Ljava/util/List;ILcom/tencent/mobileqq/activity/photo/MediaFileFilter;ZJ)Ljava/util/ArrayList;
    //   80: astore 18
    //   82: aload 18
    //   84: astore_0
    //   85: aload_0
    //   86: astore_1
    //   87: aload 16
    //   89: ifnull +12 -> 101
    //   92: aload 16
    //   94: invokeinterface 399 1 0
    //   99: aload_0
    //   100: astore_1
    //   101: aload_1
    //   102: areturn
    //   103: iconst_0
    //   104: istore 15
    //   106: goto -44 -> 62
    //   109: astore 18
    //   111: aconst_null
    //   112: astore 16
    //   114: aload 16
    //   116: astore 17
    //   118: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   121: ifeq +18 -> 139
    //   124: aload 16
    //   126: astore 17
    //   128: ldc -121
    //   130: iconst_2
    //   131: ldc_w 497
    //   134: aload 18
    //   136: invokestatic 499	com/tencent/qphone/base/util/QLog:e	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   139: aload 16
    //   141: ifnull +10 -> 151
    //   144: aload 16
    //   146: invokeinterface 399 1 0
    //   151: aload 19
    //   153: astore 16
    //   155: aload 4
    //   157: ifnull +42 -> 199
    //   160: aload 19
    //   162: astore 16
    //   164: aload 4
    //   166: invokevirtual 501	com/tencent/mobileqq/activity/photo/MediaFileFilter:a	()Z
    //   169: ifeq +30 -> 199
    //   172: iload 12
    //   174: iconst_1
    //   175: if_icmpne +153 -> 328
    //   178: aload_0
    //   179: aload_1
    //   180: aload_2
    //   181: iload_3
    //   182: aload 4
    //   184: iload 5
    //   186: iload 6
    //   188: iload 7
    //   190: aload 8
    //   192: iload 9
    //   194: invokestatic 503	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;ILcom/tencent/mobileqq/activity/photo/MediaFileFilter;IIZLjava/util/ArrayList;Z)Ljava/util/List;
    //   197: astore 16
    //   199: aload 4
    //   201: ifnull +486 -> 687
    //   204: aload 4
    //   206: invokevirtual 505	com/tencent/mobileqq/activity/photo/MediaFileFilter:b	()Z
    //   209: ifeq +478 -> 687
    //   212: aload_0
    //   213: aload_1
    //   214: aload_2
    //   215: iload_3
    //   216: aload 4
    //   218: lload 10
    //   220: invokestatic 508	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;ILcom/tencent/mobileqq/activity/photo/MediaFileFilter;J)Ljava/util/List;
    //   223: astore_0
    //   224: ldc_w 446
    //   227: aload_1
    //   228: invokevirtual 449	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   231: ifeq +63 -> 294
    //   234: invokestatic 377	android/os/SystemClock:uptimeMillis	()J
    //   237: lstore 10
    //   239: aload 16
    //   241: ifnull +8 -> 249
    //   244: aload 16
    //   246: invokestatic 364	java/util/Collections:sort	(Ljava/util/List;)V
    //   249: aload_0
    //   250: ifnull +7 -> 257
    //   253: aload_0
    //   254: invokestatic 364	java/util/Collections:sort	(Ljava/util/List;)V
    //   257: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   260: ifeq +34 -> 294
    //   263: ldc -121
    //   265: iconst_2
    //   266: new 137	java/lang/StringBuilder
    //   269: dup
    //   270: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   273: ldc_w 510
    //   276: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   279: invokestatic 377	android/os/SystemClock:uptimeMillis	()J
    //   282: lload 10
    //   284: lsub
    //   285: invokevirtual 382	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   288: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   291: invokestatic 384	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   294: aload_0
    //   295: ifnull +12 -> 307
    //   298: aload_0
    //   299: invokeinterface 513 1 0
    //   304: ifeq +48 -> 352
    //   307: aload 16
    //   309: areturn
    //   310: astore_0
    //   311: aconst_null
    //   312: astore 17
    //   314: aload 17
    //   316: ifnull +10 -> 326
    //   319: aload 17
    //   321: invokeinterface 399 1 0
    //   326: aload_0
    //   327: athrow
    //   328: aload_0
    //   329: aload_1
    //   330: aload_2
    //   331: iload_3
    //   332: aload 4
    //   334: iload 5
    //   336: iload 6
    //   338: iload 7
    //   340: aload 8
    //   342: iload 9
    //   344: invokestatic 515	com/tencent/mobileqq/utils/AlbumUtil:b	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;ILcom/tencent/mobileqq/activity/photo/MediaFileFilter;IIZLjava/util/ArrayList;Z)Ljava/util/List;
    //   347: astore 16
    //   349: goto -150 -> 199
    //   352: aload_0
    //   353: astore_1
    //   354: aload 16
    //   356: ifnull -255 -> 101
    //   359: aload_0
    //   360: astore_1
    //   361: aload 16
    //   363: invokeinterface 513 1 0
    //   368: ifne -267 -> 101
    //   371: aload 16
    //   373: invokeinterface 229 1 0
    //   378: istore 5
    //   380: iconst_0
    //   381: istore 6
    //   383: iconst_0
    //   384: istore 12
    //   386: iload 12
    //   388: aload_0
    //   389: invokeinterface 229 1 0
    //   394: if_icmpge +266 -> 660
    //   397: aload_0
    //   398: iload 12
    //   400: invokeinterface 415 2 0
    //   405: checkcast 121	com/tencent/mobileqq/activity/photo/LocalMediaInfo
    //   408: astore_1
    //   409: aload 16
    //   411: iload 5
    //   413: iconst_1
    //   414: isub
    //   415: invokeinterface 415 2 0
    //   420: checkcast 121	com/tencent/mobileqq/activity/photo/LocalMediaInfo
    //   423: astore_2
    //   424: aload_1
    //   425: getfield 348	com/tencent/mobileqq/activity/photo/LocalMediaInfo:modifiedDate	J
    //   428: aload_2
    //   429: getfield 348	com/tencent/mobileqq/activity/photo/LocalMediaInfo:modifiedDate	J
    //   432: lcmp
    //   433: ifgt +95 -> 528
    //   436: iload_3
    //   437: iconst_m1
    //   438: if_icmpne +39 -> 477
    //   441: iload 12
    //   443: aload_0
    //   444: invokeinterface 229 1 0
    //   449: if_icmpge +211 -> 660
    //   452: aload 16
    //   454: aload_0
    //   455: iload 12
    //   457: invokeinterface 415 2 0
    //   462: invokeinterface 405 2 0
    //   467: pop
    //   468: iload 12
    //   470: iconst_1
    //   471: iadd
    //   472: istore 12
    //   474: goto -33 -> 441
    //   477: aload_0
    //   478: invokeinterface 229 1 0
    //   483: iload 12
    //   485: isub
    //   486: iload_3
    //   487: iload 5
    //   489: isub
    //   490: invokestatic 412	java/lang/Math:min	(II)I
    //   493: istore 5
    //   495: iconst_0
    //   496: istore_3
    //   497: iload_3
    //   498: iload 5
    //   500: if_icmpge +160 -> 660
    //   503: aload 16
    //   505: aload_0
    //   506: iload_3
    //   507: iload 12
    //   509: iadd
    //   510: invokeinterface 415 2 0
    //   515: invokeinterface 405 2 0
    //   520: pop
    //   521: iload_3
    //   522: iconst_1
    //   523: iadd
    //   524: istore_3
    //   525: goto -28 -> 497
    //   528: iload 6
    //   530: istore 13
    //   532: iload 13
    //   534: iload 5
    //   536: if_icmpge +136 -> 672
    //   539: aload 16
    //   541: iload 13
    //   543: invokeinterface 415 2 0
    //   548: checkcast 121	com/tencent/mobileqq/activity/photo/LocalMediaInfo
    //   551: astore_2
    //   552: aload_1
    //   553: getfield 348	com/tencent/mobileqq/activity/photo/LocalMediaInfo:modifiedDate	J
    //   556: aload_2
    //   557: getfield 348	com/tencent/mobileqq/activity/photo/LocalMediaInfo:modifiedDate	J
    //   560: lcmp
    //   561: ifgt +12 -> 573
    //   564: iload 13
    //   566: iconst_1
    //   567: iadd
    //   568: istore 13
    //   570: goto -38 -> 532
    //   573: aload 16
    //   575: iload 13
    //   577: aload_1
    //   578: invokeinterface 518 3 0
    //   583: iload_3
    //   584: iconst_m1
    //   585: if_icmpeq +24 -> 609
    //   588: aload 16
    //   590: invokeinterface 229 1 0
    //   595: iload_3
    //   596: if_icmple +13 -> 609
    //   599: aload 16
    //   601: iload 5
    //   603: invokeinterface 521 2 0
    //   608: pop
    //   609: aload 16
    //   611: invokeinterface 229 1 0
    //   616: istore 14
    //   618: iload 5
    //   620: istore 6
    //   622: iload 5
    //   624: iload 14
    //   626: if_icmpeq +7 -> 633
    //   629: iload 14
    //   631: istore 6
    //   633: iload 13
    //   635: iconst_1
    //   636: iadd
    //   637: istore 5
    //   639: iload 12
    //   641: iconst_1
    //   642: iadd
    //   643: istore 12
    //   645: iload 6
    //   647: istore 13
    //   649: iload 5
    //   651: istore 6
    //   653: iload 13
    //   655: istore 5
    //   657: goto -271 -> 386
    //   660: aload 16
    //   662: areturn
    //   663: astore_0
    //   664: goto -350 -> 314
    //   667: astore 18
    //   669: goto -555 -> 114
    //   672: iload 5
    //   674: istore 13
    //   676: iload 6
    //   678: istore 5
    //   680: iload 13
    //   682: istore 6
    //   684: goto -45 -> 639
    //   687: aconst_null
    //   688: astore_0
    //   689: goto -465 -> 224
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	692	0	paramContext	Context
    //   0	692	1	paramString1	String
    //   0	692	2	paramString2	String
    //   0	692	3	paramInt1	int
    //   0	692	4	paramMediaFileFilter	MediaFileFilter
    //   0	692	5	paramInt2	int
    //   0	692	6	paramInt3	int
    //   0	692	7	paramBoolean1	boolean
    //   0	692	8	paramArrayList	ArrayList
    //   0	692	9	paramBoolean2	boolean
    //   0	692	10	paramLong	long
    //   0	692	12	paramInt4	int
    //   530	151	13	i	int
    //   616	14	14	j	int
    //   60	45	15	bool	boolean
    //   45	616	16	localObject1	Object
    //   49	271	17	localObject2	Object
    //   80	3	18	localArrayList	ArrayList
    //   109	26	18	localException1	Exception
    //   667	1	18	localException2	Exception
    //   1	160	19	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   28	47	109	java/lang/Exception
    //   28	47	310	finally
    //   51	59	663	finally
    //   66	82	663	finally
    //   118	124	663	finally
    //   128	139	663	finally
    //   51	59	667	java/lang/Exception
    //   66	82	667	java/lang/Exception
  }
  
  /* Error */
  public static List a(Context paramContext, String paramString1, String paramString2, int paramInt, MediaFileFilter paramMediaFileFilter, long paramLong)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnull +13 -> 14
    //   4: ldc_w 446
    //   7: aload_1
    //   8: invokevirtual 449	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   11: ifeq +16 -> 27
    //   14: aload_0
    //   15: sipush 210
    //   18: iload_3
    //   19: aload 4
    //   21: lload 5
    //   23: invokestatic 523	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/content/Context;IILcom/tencent/mobileqq/activity/photo/MediaFileFilter;J)Ljava/util/List;
    //   26: areturn
    //   27: new 293	java/util/ArrayList
    //   30: dup
    //   31: invokespecial 294	java/util/ArrayList:<init>	()V
    //   34: astore 8
    //   36: new 137	java/lang/StringBuilder
    //   39: dup
    //   40: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   43: ldc_w 453
    //   46: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: aload_1
    //   50: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: ldc_w 455
    //   56: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   62: astore_1
    //   63: aload_0
    //   64: invokevirtual 193	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   67: getstatic 392	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   70: getstatic 92	com/tencent/mobileqq/utils/AlbumUtil:jdField_b_of_type_ArrayOfJavaLangString	[Ljava/lang/String;
    //   73: aload_1
    //   74: aconst_null
    //   75: ldc -61
    //   77: invokevirtual 201	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   80: astore_1
    //   81: aload_1
    //   82: astore_0
    //   83: getstatic 62	android/os/Build$VERSION:SDK_INT	I
    //   86: bipush 16
    //   88: if_icmplt +35 -> 123
    //   91: iconst_1
    //   92: istore 7
    //   94: aload_1
    //   95: astore_0
    //   96: aload_1
    //   97: aload 8
    //   99: iload_3
    //   100: aload 4
    //   102: iload 7
    //   104: lload 5
    //   106: invokestatic 297	com/tencent/mobileqq/utils/AlbumUtil:a	(Landroid/database/Cursor;Ljava/util/List;ILcom/tencent/mobileqq/activity/photo/MediaFileFilter;ZJ)Ljava/util/List;
    //   109: pop
    //   110: aload_1
    //   111: ifnull +77 -> 188
    //   114: aload_1
    //   115: invokeinterface 399 1 0
    //   120: aload 8
    //   122: areturn
    //   123: iconst_0
    //   124: istore 7
    //   126: goto -32 -> 94
    //   129: astore_2
    //   130: aconst_null
    //   131: astore_1
    //   132: aload_1
    //   133: astore_0
    //   134: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   137: ifeq +15 -> 152
    //   140: aload_1
    //   141: astore_0
    //   142: ldc -121
    //   144: iconst_2
    //   145: ldc_w 497
    //   148: aload_2
    //   149: invokestatic 499	com/tencent/qphone/base/util/QLog:e	(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Throwable;)V
    //   152: aload_1
    //   153: ifnull +35 -> 188
    //   156: aload_1
    //   157: invokeinterface 399 1 0
    //   162: aload 8
    //   164: areturn
    //   165: astore_1
    //   166: aconst_null
    //   167: astore_0
    //   168: aload_0
    //   169: ifnull +9 -> 178
    //   172: aload_0
    //   173: invokeinterface 399 1 0
    //   178: aload_1
    //   179: athrow
    //   180: astore_1
    //   181: goto -13 -> 168
    //   184: astore_2
    //   185: goto -53 -> 132
    //   188: aload 8
    //   190: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	191	0	paramContext	Context
    //   0	191	1	paramString1	String
    //   0	191	2	paramString2	String
    //   0	191	3	paramInt	int
    //   0	191	4	paramMediaFileFilter	MediaFileFilter
    //   0	191	5	paramLong	long
    //   92	33	7	bool	boolean
    //   34	155	8	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   63	81	129	java/lang/Exception
    //   63	81	165	finally
    //   83	91	180	finally
    //   96	110	180	finally
    //   134	140	180	finally
    //   142	152	180	finally
    //   83	91	184	java/lang/Exception
    //   96	110	184	java/lang/Exception
  }
  
  /* Error */
  @android.annotation.TargetApi(10)
  private static List a(Cursor paramCursor, List paramList, int paramInt, MediaFileFilter paramMediaFileFilter, boolean paramBoolean, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokeinterface 531 1 0
    //   6: ifgt +7 -> 13
    //   9: aconst_null
    //   10: astore_0
    //   11: aload_0
    //   12: areturn
    //   13: aload_1
    //   14: astore 22
    //   16: aload_1
    //   17: ifnonnull +12 -> 29
    //   20: new 293	java/util/ArrayList
    //   23: dup
    //   24: invokespecial 294	java/util/ArrayList:<init>	()V
    //   27: astore 22
    //   29: aload_0
    //   30: ldc 66
    //   32: invokeinterface 535 2 0
    //   37: istore 11
    //   39: aload_0
    //   40: ldc 68
    //   42: invokeinterface 535 2 0
    //   47: istore 12
    //   49: aload_0
    //   50: ldc 72
    //   52: invokeinterface 535 2 0
    //   57: istore 13
    //   59: aload_0
    //   60: ldc 74
    //   62: invokeinterface 535 2 0
    //   67: istore 14
    //   69: aload_0
    //   70: ldc 90
    //   72: invokeinterface 535 2 0
    //   77: istore 15
    //   79: aload_0
    //   80: ldc 70
    //   82: invokeinterface 535 2 0
    //   87: istore 16
    //   89: aload_0
    //   90: ldc 82
    //   92: invokeinterface 535 2 0
    //   97: istore 17
    //   99: iconst_0
    //   100: istore 8
    //   102: iconst_0
    //   103: istore 7
    //   105: iload 4
    //   107: ifeq +23 -> 130
    //   110: aload_0
    //   111: ldc 84
    //   113: invokeinterface 535 2 0
    //   118: istore 8
    //   120: aload_0
    //   121: ldc 86
    //   123: invokeinterface 535 2 0
    //   128: istore 7
    //   130: iconst_0
    //   131: istore 9
    //   133: aconst_null
    //   134: astore 24
    //   136: aload_0
    //   137: invokeinterface 538 1 0
    //   142: ifeq +367 -> 509
    //   145: aload_0
    //   146: iload 12
    //   148: invokeinterface 540 2 0
    //   153: astore 25
    //   155: aload 25
    //   157: invokestatic 545	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   160: ifne -24 -> 136
    //   163: new 547	java/io/File
    //   166: dup
    //   167: aload 25
    //   169: invokespecial 548	java/io/File:<init>	(Ljava/lang/String;)V
    //   172: invokevirtual 551	java/io/File:exists	()Z
    //   175: ifeq -39 -> 136
    //   178: aload_0
    //   179: iload 16
    //   181: invokeinterface 540 2 0
    //   186: astore 23
    //   188: aload_3
    //   189: ifnull +70 -> 259
    //   192: aload_3
    //   193: aload 23
    //   195: invokevirtual 404	com/tencent/mobileqq/activity/photo/MediaFileFilter:a	(Ljava/lang/String;)Z
    //   198: ifeq +61 -> 259
    //   201: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   204: ifeq -68 -> 136
    //   207: aload 24
    //   209: astore_1
    //   210: aload 24
    //   212: ifnonnull +11 -> 223
    //   215: new 137	java/lang/StringBuilder
    //   218: dup
    //   219: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   222: astore_1
    //   223: aload_1
    //   224: ldc_w 553
    //   227: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   230: aload 23
    //   232: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   235: ldc_w 555
    //   238: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   241: aload 25
    //   243: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: ldc_w 557
    //   249: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: pop
    //   253: aload_1
    //   254: astore 24
    //   256: goto -120 -> 136
    //   259: aload_0
    //   260: iload 13
    //   262: invokeinterface 561 2 0
    //   267: lstore 18
    //   269: aload_0
    //   270: iload 14
    //   272: invokeinterface 561 2 0
    //   277: lstore 20
    //   279: new 121	com/tencent/mobileqq/activity/photo/LocalMediaInfo
    //   282: dup
    //   283: invokespecial 328	com/tencent/mobileqq/activity/photo/LocalMediaInfo:<init>	()V
    //   286: astore 26
    //   288: aload 26
    //   290: aload_0
    //   291: iload 11
    //   293: invokeinterface 561 2 0
    //   298: putfield 331	com/tencent/mobileqq/activity/photo/LocalMediaInfo:_id	J
    //   301: aload 26
    //   303: aload 25
    //   305: putfield 271	com/tencent/mobileqq/activity/photo/LocalMediaInfo:path	Ljava/lang/String;
    //   308: aload 26
    //   310: aload 23
    //   312: putfield 124	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mMimeType	Ljava/lang/String;
    //   315: aload 26
    //   317: lload 18
    //   319: putfield 564	com/tencent/mobileqq/activity/photo/LocalMediaInfo:addedDate	J
    //   322: aload 26
    //   324: lload 20
    //   326: putfield 348	com/tencent/mobileqq/activity/photo/LocalMediaInfo:modifiedDate	J
    //   329: aload 26
    //   331: aload_0
    //   332: iload 15
    //   334: invokeinterface 561 2 0
    //   339: putfield 336	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mDuration	J
    //   342: aload 26
    //   344: aload_0
    //   345: iload 17
    //   347: invokeinterface 561 2 0
    //   352: putfield 341	com/tencent/mobileqq/activity/photo/LocalMediaInfo:fileSize	J
    //   355: iload 4
    //   357: ifeq +243 -> 600
    //   360: aload 26
    //   362: aload_0
    //   363: iload 8
    //   365: invokeinterface 568 2 0
    //   370: putfield 571	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mediaWidth	I
    //   373: aload 26
    //   375: aload_0
    //   376: iload 7
    //   378: invokeinterface 568 2 0
    //   383: putfield 574	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mediaHeight	I
    //   386: aload 26
    //   388: getfield 336	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mDuration	J
    //   391: lconst_0
    //   392: lcmp
    //   393: ifne +50 -> 443
    //   396: new 576	android/media/MediaMetadataRetriever
    //   399: dup
    //   400: invokespecial 577	android/media/MediaMetadataRetriever:<init>	()V
    //   403: astore 23
    //   405: aload 23
    //   407: astore_1
    //   408: aload 23
    //   410: aload 25
    //   412: invokevirtual 580	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   415: aload 23
    //   417: astore_1
    //   418: aload 26
    //   420: aload 23
    //   422: bipush 9
    //   424: invokevirtual 583	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   427: invokestatic 589	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   430: putfield 336	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mDuration	J
    //   433: aload 23
    //   435: ifnull +8 -> 443
    //   438: aload 23
    //   440: invokevirtual 592	android/media/MediaMetadataRetriever:release	()V
    //   443: lload 5
    //   445: lconst_0
    //   446: lcmp
    //   447: ifle +14 -> 461
    //   450: aload 26
    //   452: getfield 336	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mDuration	J
    //   455: lload 5
    //   457: lcmp
    //   458: ifgt -322 -> 136
    //   461: aload 22
    //   463: aload 26
    //   465: invokeinterface 405 2 0
    //   470: pop
    //   471: iload 9
    //   473: iconst_1
    //   474: iadd
    //   475: istore 10
    //   477: getstatic 593	com/tencent/mobileqq/utils/AlbumUtil:jdField_b_of_type_Long	J
    //   480: lload 20
    //   482: lcmp
    //   483: ifge +8 -> 491
    //   486: lload 20
    //   488: putstatic 593	com/tencent/mobileqq/utils/AlbumUtil:jdField_b_of_type_Long	J
    //   491: iload 10
    //   493: istore 9
    //   495: iload_2
    //   496: ifle -360 -> 136
    //   499: iload 10
    //   501: istore 9
    //   503: iload 10
    //   505: iload_2
    //   506: if_icmplt -370 -> 136
    //   509: aload 22
    //   511: astore_0
    //   512: aload 24
    //   514: ifnull -503 -> 11
    //   517: aload 22
    //   519: astore_0
    //   520: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   523: ifeq -512 -> 11
    //   526: ldc -121
    //   528: iconst_2
    //   529: aload 24
    //   531: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   534: invokestatic 384	com/tencent/qphone/base/util/QLog:d	(Ljava/lang/String;ILjava/lang/String;)V
    //   537: aload 22
    //   539: areturn
    //   540: astore 25
    //   542: aconst_null
    //   543: astore 23
    //   545: aload 23
    //   547: astore_1
    //   548: aload 25
    //   550: invokevirtual 596	java/lang/Exception:printStackTrace	()V
    //   553: aload 23
    //   555: astore_1
    //   556: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   559: ifeq +15 -> 574
    //   562: aload 23
    //   564: astore_1
    //   565: ldc -121
    //   567: iconst_2
    //   568: ldc_w 598
    //   571: invokestatic 601	com/tencent/qphone/base/util/QLog:i	(Ljava/lang/String;ILjava/lang/String;)V
    //   574: aload 23
    //   576: ifnull -133 -> 443
    //   579: aload 23
    //   581: invokevirtual 592	android/media/MediaMetadataRetriever:release	()V
    //   584: goto -141 -> 443
    //   587: astore_0
    //   588: aconst_null
    //   589: astore_1
    //   590: aload_1
    //   591: ifnull +7 -> 598
    //   594: aload_1
    //   595: invokevirtual 592	android/media/MediaMetadataRetriever:release	()V
    //   598: aload_0
    //   599: athrow
    //   600: invokestatic 605	com/tencent/util/VersionUtils:d	()Z
    //   603: ifeq -160 -> 443
    //   606: new 576	android/media/MediaMetadataRetriever
    //   609: dup
    //   610: invokespecial 577	android/media/MediaMetadataRetriever:<init>	()V
    //   613: astore_1
    //   614: aload_1
    //   615: aload 25
    //   617: invokevirtual 580	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   620: aload_1
    //   621: bipush 18
    //   623: invokevirtual 583	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   626: astore 23
    //   628: aload_1
    //   629: bipush 19
    //   631: invokevirtual 583	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   634: astore 25
    //   636: aload_1
    //   637: invokevirtual 592	android/media/MediaMetadataRetriever:release	()V
    //   640: aload 26
    //   642: aload 23
    //   644: invokestatic 608	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   647: putfield 571	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mediaWidth	I
    //   650: aload 26
    //   652: aload 25
    //   654: invokestatic 608	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   657: putfield 574	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mediaHeight	I
    //   660: goto -217 -> 443
    //   663: astore_1
    //   664: aload 26
    //   666: iconst_0
    //   667: putfield 571	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mediaWidth	I
    //   670: aload 26
    //   672: iconst_0
    //   673: putfield 574	com/tencent/mobileqq/activity/photo/LocalMediaInfo:mediaHeight	I
    //   676: invokestatic 279	com/tencent/qphone/base/util/QLog:isColorLevel	()Z
    //   679: ifeq -236 -> 443
    //   682: ldc -121
    //   684: iconst_2
    //   685: ldc_w 610
    //   688: invokestatic 601	com/tencent/qphone/base/util/QLog:i	(Ljava/lang/String;ILjava/lang/String;)V
    //   691: goto -248 -> 443
    //   694: astore_0
    //   695: goto -105 -> 590
    //   698: astore 25
    //   700: goto -155 -> 545
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	703	0	paramCursor	Cursor
    //   0	703	1	paramList	List
    //   0	703	2	paramInt	int
    //   0	703	3	paramMediaFileFilter	MediaFileFilter
    //   0	703	4	paramBoolean	boolean
    //   0	703	5	paramLong	long
    //   103	274	7	i	int
    //   100	264	8	j	int
    //   131	371	9	k	int
    //   475	32	10	m	int
    //   37	255	11	n	int
    //   47	100	12	i1	int
    //   57	204	13	i2	int
    //   67	204	14	i3	int
    //   77	256	15	i4	int
    //   87	93	16	i5	int
    //   97	249	17	i6	int
    //   267	51	18	l1	long
    //   277	210	20	l2	long
    //   14	524	22	localObject1	Object
    //   186	457	23	localObject2	Object
    //   134	396	24	localList	List
    //   153	258	25	str1	String
    //   540	76	25	localException1	Exception
    //   634	19	25	str2	String
    //   698	1	25	localException2	Exception
    //   286	385	26	localLocalMediaInfo	LocalMediaInfo
    // Exception table:
    //   from	to	target	type
    //   396	405	540	java/lang/Exception
    //   396	405	587	finally
    //   640	660	663	java/lang/NumberFormatException
    //   408	415	694	finally
    //   418	433	694	finally
    //   548	553	694	finally
    //   556	562	694	finally
    //   565	574	694	finally
    //   408	415	698	java/lang/Exception
    //   418	433	698	java/lang/Exception
  }
  
  public static void a() {}
  
  private static void a(Activity paramActivity)
  {
    paramActivity.overridePendingTransition(2131034132, 2131034133);
  }
  
  public static void a(Activity paramActivity, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      if (paramBoolean2)
      {
        a(paramActivity);
        return;
      }
      b(paramActivity);
      return;
    }
    if (paramBoolean2)
    {
      c(paramActivity);
      return;
    }
    d(paramActivity);
  }
  
  public static void a(Context paramContext, String paramString1, String paramString2)
  {
    paramContext = paramContext.getSharedPreferences("album_file", 0).edit();
    paramContext.putString("album_key_id", jdField_b_of_type_JavaLangString);
    paramContext.putString("album_key_name", jdField_c_of_type_JavaLangString);
    paramContext.commit();
  }
  
  public static void a(Intent paramIntent)
  {
    if (paramIntent.getBooleanExtra("PhotoConst.IS_RECODE_LAST_ALBUMPATH", false))
    {
      String str = paramIntent.getStringExtra("ALBUM_ID");
      paramIntent = paramIntent.getStringExtra("PhotoConst.LAST_ALBUMPATH");
      if ((paramIntent != null) && (str != null))
      {
        jdField_a_of_type_JavaLangString = paramIntent;
        jdField_a_of_type_Long = System.currentTimeMillis();
      }
    }
  }
  
  public static void a(Intent paramIntent, String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramIntent.putExtra("PhotoConst.IS_RECODE_LAST_ALBUMPATH", paramBoolean);
      paramIntent.putExtra("ALBUM_ID", paramString1);
      paramIntent.putExtra("PhotoConst.LAST_ALBUMPATH", paramString2);
    }
  }
  
  private static void a(Cursor paramCursor, List paramList, int paramInt1, int paramInt2, boolean paramBoolean, MediaFileFilter paramMediaFileFilter)
  {
    a(paramCursor, paramList, paramInt1, paramInt2, paramBoolean, paramMediaFileFilter, null, false);
  }
  
  private static void a(Cursor paramCursor, List paramList, int paramInt1, int paramInt2, boolean paramBoolean1, MediaFileFilter paramMediaFileFilter, ArrayList paramArrayList, boolean paramBoolean2)
  {
    Object localObject1;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    int i8;
    int i9;
    int k;
    if (paramCursor.getCount() > 0)
    {
      localObject1 = paramList;
      if (paramList == null) {
        localObject1 = new ArrayList();
      }
      i1 = paramCursor.getColumnIndexOrThrow("_id");
      i2 = paramCursor.getColumnIndexOrThrow("_data");
      i3 = paramCursor.getColumnIndexOrThrow("orientation");
      i4 = paramCursor.getColumnIndexOrThrow("date_added");
      i5 = paramCursor.getColumnIndexOrThrow("date_modified");
      i6 = paramCursor.getColumnIndexOrThrow("mime_type");
      i7 = paramCursor.getColumnIndexOrThrow("_size");
      i8 = paramCursor.getColumnIndexOrThrow("latitude");
      i9 = paramCursor.getColumnIndexOrThrow("longitude");
      if (!paramBoolean1) {
        break label1058;
      }
      k = paramCursor.getColumnIndexOrThrow("width");
    }
    for (int j = paramCursor.getColumnIndexOrThrow("height");; j = 0)
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inJustDecodeBounds = true;
      int[] arrayOfInt = new int[2];
      int i = 0;
      label324:
      label636:
      label740:
      label1049:
      label1056:
      for (;;)
      {
        String str;
        if (paramCursor.moveToNext())
        {
          str = paramCursor.getString(i2);
          if (!FileUtil.b(str)) {
            continue;
          }
          if ((paramArrayList != null) && (paramArrayList.size() > 0))
          {
            paramList = paramArrayList.iterator();
            do
            {
              if (!paramList.hasNext()) {
                break;
              }
            } while (!str.contains((String)paramList.next()));
          }
        }
        for (int m = 1;; m = 0)
        {
          if (m != 0) {
            break label1056;
          }
          Object localObject2 = paramCursor.getString(i6);
          paramList = (List)localObject2;
          if (paramMediaFileFilter != null)
          {
            paramList = (List)localObject2;
            if (paramMediaFileFilter.a((String)localObject2))
            {
              if ((!"*/*".equals(localObject2)) && (!"image/*".equals(localObject2))) {
                break label740;
              }
              if ((str.indexOf(".jpg") == -1) && (str.indexOf(".jpeg") == -1)) {
                break label636;
              }
              paramList = "image/jpeg";
            }
          }
          long l1 = paramCursor.getLong(i4);
          long l2 = paramCursor.getLong(i5);
          if (jdField_b_of_type_Long < l2) {
            jdField_b_of_type_Long = l2;
          }
          int n = 0;
          m = n;
          if (paramBoolean1)
          {
            m = n;
            if (paramCursor.getInt(k) == 0) {
              m = 1;
            }
          }
          long l3 = paramCursor.getLong(i1);
          if ((paramInt1 > 0) && ((!paramBoolean1) || (m != 0)))
          {
            a(str, localOptions, arrayOfInt);
            if ((arrayOfInt[0] < paramInt1) && (arrayOfInt[1] < paramInt1)) {
              break label1049;
            }
            localObject2 = new LocalMediaInfo();
            ((LocalMediaInfo)localObject2)._id = l3;
            ((LocalMediaInfo)localObject2).path = str;
            ((LocalMediaInfo)localObject2).addedDate = l1;
            ((LocalMediaInfo)localObject2).modifiedDate = l2;
            ((LocalMediaInfo)localObject2).orientation = paramCursor.getInt(i3);
            ((LocalMediaInfo)localObject2).mMimeType = paramList;
            ((LocalMediaInfo)localObject2).fileSize = paramCursor.getLong(i7);
            ((LocalMediaInfo)localObject2).mediaWidth = arrayOfInt[0];
            ((LocalMediaInfo)localObject2).mediaHeight = arrayOfInt[1];
            ((LocalMediaInfo)localObject2).latitude = ((int)(paramCursor.getDouble(i8) * 1000000.0D));
            ((LocalMediaInfo)localObject2).longitude = ((int)(paramCursor.getDouble(i9) * 1000000.0D));
            if (((((LocalMediaInfo)localObject2).mediaWidth <= 0) || (((LocalMediaInfo)localObject2).mediaHeight <= 0)) && (QLog.isColorLevel())) {
              QLog.i("AlbumUtil", 2, "no size " + str);
            }
            ((List)localObject1).add(localObject2);
            i += 1;
          }
          for (;;)
          {
            if ((paramInt2 > 0) && (i >= paramInt2))
            {
              return;
              if (str.indexOf(".gif") != -1)
              {
                paramList = "image/gif";
                break label324;
              }
              if (str.indexOf(".bmp") != -1)
              {
                paramList = "image/bmp";
                break label324;
              }
              if (str.indexOf(".png") != -1)
              {
                paramList = "image/png";
                break label324;
              }
              if (!QLog.isColorLevel()) {
                break;
              }
              QLog.i("AlbumUtil", 2, "Filter mime type:" + (String)localObject2 + ", path is " + str);
              break;
              if (!QLog.isColorLevel()) {
                break;
              }
              QLog.i("AlbumUtil", 2, "Filter unknown " + (String)localObject2 + ", path is " + str);
              break;
              localObject2 = new LocalMediaInfo();
              ((LocalMediaInfo)localObject2)._id = l3;
              ((LocalMediaInfo)localObject2).path = str;
              ((LocalMediaInfo)localObject2).addedDate = paramCursor.getLong(i4);
              ((LocalMediaInfo)localObject2).modifiedDate = paramCursor.getLong(i5);
              ((LocalMediaInfo)localObject2).orientation = paramCursor.getInt(i3);
              ((LocalMediaInfo)localObject2).mMimeType = paramList;
              ((LocalMediaInfo)localObject2).fileSize = paramCursor.getLong(i7);
              ((LocalMediaInfo)localObject2).latitude = ((int)(paramCursor.getDouble(i8) * 1000000.0D));
              ((LocalMediaInfo)localObject2).longitude = ((int)(paramCursor.getDouble(i9) * 1000000.0D));
              if (paramBoolean1)
              {
                ((LocalMediaInfo)localObject2).mediaWidth = paramCursor.getInt(k);
                ((LocalMediaInfo)localObject2).mediaHeight = paramCursor.getInt(j);
              }
              if ((((LocalMediaInfo)localObject2).mediaWidth <= 0) || (((LocalMediaInfo)localObject2).mediaHeight <= 0))
              {
                a(str, localOptions, arrayOfInt);
                ((LocalMediaInfo)localObject2).mediaWidth = arrayOfInt[0];
                ((LocalMediaInfo)localObject2).mediaHeight = arrayOfInt[1];
              }
              if (((((LocalMediaInfo)localObject2).mediaWidth <= 0) || (((LocalMediaInfo)localObject2).mediaHeight <= 0)) && (QLog.isColorLevel())) {
                QLog.i("AlbumUtil", 2, "no size " + str);
              }
              ((List)localObject1).add(localObject2);
              i += 1;
              continue;
            }
            break;
          }
        }
      }
      label1058:
      k = 0;
    }
  }
  
  public static void a(LocalMediaInfo paramLocalMediaInfo, String paramString)
  {
    try
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(paramString, localOptions);
      paramLocalMediaInfo.mediaHeight = localOptions.outHeight;
      paramLocalMediaInfo.mediaWidth = localOptions.outWidth;
      paramLocalMediaInfo.mMimeType = localOptions.outMimeType;
      paramLocalMediaInfo.addedDate = System.currentTimeMillis();
      paramLocalMediaInfo.modifiedDate = System.currentTimeMillis();
      paramLocalMediaInfo.path = paramString;
      paramLocalMediaInfo.panoramaPhotoType = 3;
      paramLocalMediaInfo.fileSize = new File(paramString).length();
      return;
    }
    catch (Exception paramLocalMediaInfo)
    {
      QLog.e("AlbumUtil", 2, "decode image error", paramLocalMediaInfo);
    }
  }
  
  private static void a(String paramString, BitmapFactory.Options paramOptions, int[] paramArrayOfInt)
  {
    Integer localInteger = (Integer)jdField_a_of_type_JavaUtilMap.get(paramString);
    if (localInteger == null) {}
    for (;;)
    {
      try
      {
        SafeBitmapFactory.decodeFile(paramString, paramOptions);
        k = paramOptions.outWidth;
      }
      catch (OutOfMemoryError paramString)
      {
        i = 0;
        j = 0;
        continue;
      }
      try
      {
        m = paramOptions.outHeight;
        i = m;
        j = k;
        if (k <= 65535)
        {
          i = m;
          j = k;
          if (m > 65535) {}
        }
      }
      catch (OutOfMemoryError paramString)
      {
        i = 0;
        j = k;
        continue;
      }
      try
      {
        i = paramOptions.outWidth;
        j = paramOptions.outHeight;
        jdField_a_of_type_JavaUtilMap.put(paramString, Integer.valueOf(i << 16 & 0xFFFF0000 | j & 0xFFFF));
        j = k;
        i = m;
      }
      catch (OutOfMemoryError paramString)
      {
        i = m;
        j = k;
        continue;
      }
      paramArrayOfInt[0] = j;
      paramArrayOfInt[1] = i;
      return;
      j = localInteger.intValue() >> 16 & 0xFFFF;
      i = localInteger.intValue() & 0xFFFF;
    }
  }
  
  public static boolean a(int paramInt)
  {
    boolean bool2 = false;
    int[] arrayOfInt = jdField_a_of_type_ArrayOfInt;
    int j = arrayOfInt.length;
    int i = 0;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < j)
      {
        if (paramInt == arrayOfInt[i]) {
          bool1 = true;
        }
      }
      else {
        return bool1;
      }
      i += 1;
    }
  }
  
  public static boolean a(Context paramContext, int paramInt, LocalMediaInfo paramLocalMediaInfo, boolean paramBoolean)
  {
    boolean bool = true;
    if ((paramLocalMediaInfo != null) && (a(paramLocalMediaInfo) == 1)) {
      if (!paramBoolean) {
        break label182;
      }
    }
    label182:
    for (int i = b();; i = a())
    {
      paramBoolean = bool;
      if (paramInt >= i)
      {
        if (System.currentTimeMillis() - jdField_c_of_type_Long > 2000L)
        {
          QQToast.a(paramContext, "不能上传超过" + i + "个视频", 0).a();
          jdField_c_of_type_Long = System.currentTimeMillis();
        }
        paramBoolean = false;
      }
      long l = QZoneHelper.b();
      if (paramLocalMediaInfo.fileSize > l)
      {
        QQToast.a(paramContext, "请上传不超过" + (float)l / 1024.0F / 1024.0F / 1024.0F + "G的视频", 0).a();
        paramBoolean = false;
      }
      if (paramLocalMediaInfo.mDuration <= 600000L) {
        break;
      }
      QQToast.a(paramContext, "请上传不超过10分钟的视频", 0).a();
      return false;
    }
    return paramBoolean;
  }
  
  public static int b()
  {
    return QzoneConfig.getInstance().getConfig("MiniVideo", "ShuoShuoMaxSelectVideoNum", 10);
  }
  
  public static String b(Context paramContext)
  {
    return paramContext.getSharedPreferences("album_file", 0).getString("album_key_name", null);
  }
  
  public static List b(Context paramContext, String paramString1, String paramString2, int paramInt1, MediaFileFilter paramMediaFileFilter, int paramInt2, int paramInt3, boolean paramBoolean1, ArrayList paramArrayList, boolean paramBoolean2)
  {
    if (QLog.isColorLevel()) {
      QLog.e("AlbumUtil", 2, "getAlbumPhotos");
    }
    if ((paramString2 == null) || ("$RecentAlbumId".equals(paramString1))) {
      paramContext = a(paramContext, paramInt3, paramInt1, paramMediaFileFilter, paramBoolean1, paramInt2, paramArrayList, paramBoolean2);
    }
    do
    {
      do
      {
        return paramContext;
        paramString1 = a(paramContext, "bucket_id='" + paramString1 + "'", paramInt1, paramMediaFileFilter);
        paramContext = paramString1;
      } while (paramString1 == null);
      paramContext = paramString1;
    } while (!QLog.isColorLevel());
    QLog.d("AlbumUtil", 2, "photo list size is:" + paramString1.size());
    return paramString1;
  }
  
  public static List b(Context paramContext, String paramString1, String paramString2, int paramInt, MediaFileFilter paramMediaFileFilter, long paramLong)
  {
    return a(paramContext, paramString1, paramString2, paramInt, paramMediaFileFilter, 0, -1, false, null, false, paramLong);
  }
  
  public static void b()
  {
    jdField_a_of_type_JavaUtilHashMap.clear();
    jdField_a_of_type_JavaLangString = null;
    jdField_a_of_type_Long = 0L;
    jdField_b_of_type_JavaLangString = null;
    jdField_c_of_type_JavaLangString = null;
  }
  
  private static void b(Activity paramActivity)
  {
    paramActivity.overridePendingTransition(2131034129, 2131034130);
  }
  
  public static void c()
  {
    jdField_c_of_type_JavaUtilHashMap.clear();
    jdField_b_of_type_JavaUtilHashMap.clear();
  }
  
  private static void c(Activity paramActivity)
  {
    paramActivity.overridePendingTransition(2131034123, 2131034124);
  }
  
  private static void d(Activity paramActivity)
  {
    paramActivity.overridePendingTransition(2131034121, 2131034122);
  }
}


/* Location:              D:\1\!\utils\AlbumUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */