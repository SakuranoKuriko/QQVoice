#include <jni.h>
#ifndef _Included_com_tencent_mobileqq_utils_SilkCodecWrapper
#define _Included_com_tencent_mobileqq_utils_SilkCodecWrapper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_tencent_mobileqq_utils_SilkCodecWrapper
 * Method:    SilkDecoderNew
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_tencent_mobileqq_utils_SilkCodecWrapper_SilkDecoderNew
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_tencent_mobileqq_utils_SilkCodecWrapper
 * Method:    SilkEncoderNew
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_tencent_mobileqq_utils_SilkCodecWrapper_SilkEncoderNew
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_tencent_mobileqq_utils_SilkCodecWrapper
 * Method:    decode
 * Signature: (I[B[BII)I
 */
JNIEXPORT jint JNICALL Java_com_tencent_mobileqq_utils_SilkCodecWrapper_decode
  (JNIEnv *, jobject, jint, jbyteArray, jbyteArray, jint, jint);

/*
 * Class:     com_tencent_mobileqq_utils_SilkCodecWrapper
 * Method:    deleteCodec
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_tencent_mobileqq_utils_SilkCodecWrapper_deleteCodec
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_tencent_mobileqq_utils_SilkCodecWrapper
 * Method:    encode
 * Signature: (I[B[BI)I
 */
JNIEXPORT jint JNICALL Java_com_tencent_mobileqq_utils_SilkCodecWrapper_encode
  (JNIEnv *, jobject, jint, jbyteArray, jbyteArray, jint);

#ifdef __cplusplus
}
#endif
#endif
