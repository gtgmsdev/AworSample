#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <malloc.h>
#include <string.h>

//base_url
JNIEXPORT jstring JNICALL
Java_com_gamatechno_awor_network_ApiConst_getBaseDev(JNIEnv *env, jobject thiz) {
 return (*env)->  NewStringUTF(env, "https://dev.awor.xyz/api/");
}

JNIEXPORT jstring JNICALL
Java_com_gamatechno_awor_network_ApiConst_getBaseLive(JNIEnv *env, jobject thiz) {
 return (*env)->  NewStringUTF(env, "https://dev.awor.xyz/api/");
}