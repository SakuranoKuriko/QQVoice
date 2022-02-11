#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedMacroInspection"
#ifndef ___ANDROID_LOG_H___
#define ___ANDROID_LOG_H___

#include <android/log.h>
#define AndroidLogD(tag, ...) __android_log_print(ANDROID_LOG_DEBUG, tag, __VA_ARGS__)
#define AndroidLogI(tag, ...) __android_log_print(ANDROID_LOG_INFO , tag, __VA_ARGS__)
#define AndroidLogW(tag, ...) __android_log_print(ANDROID_LOG_WARN , tag, __VA_ARGS__)
#define AndroidLogE(tag, ...) __android_log_print(ANDROID_LOG_ERROR, tag, __VA_ARGS__)
#define AndroidLogF(tag, ...) __android_log_print(ANDROID_LOG_FATAL, tag, __VA_ARGS__)

#endif
#pragma clang diagnostic pop