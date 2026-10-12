// Ponto de entrada nativo. Por enquanto é um stub: recebe o ISO e devolve false
// ("motor ainda não integrado"). Veja docs/PORTING.md para os próximos passos.
#include <jni.h>
#include <android/log.h>
#include <unistd.h>

#define TAG "StrikersRecomp"

extern "C" JNIEXPORT jboolean JNICALL
Java_com_pedro_strikersrecomp_NativeBridge_nativeStart(JNIEnv *, jobject,
                                                       jint isoFd,
                                                       jlong cheatMask,
                                                       jfloat resScale) {
    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "nativeStart fd=%d cheats=0x%llx res=%.2f (stub)",
                        isoFd, (unsigned long long) cheatMask, (double) resScale);
    close(isoFd);
    return JNI_FALSE;
}
