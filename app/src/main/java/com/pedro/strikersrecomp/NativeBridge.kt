package com.pedro.strikersrecomp

/**
 * Ponte para o motor nativo (C++). Sem o build nativo (-PenableNative),
 * [available] fica false e o app só mostra o menu.
 */
object NativeBridge {
    val available: Boolean = try {
        System.loadLibrary("strikersrecomp")
        true
    } catch (e: UnsatisfiedLinkError) {
        false
    }

    private external fun nativeStart(isoFd: Int, cheatMask: Long, resScale: Float): Boolean

    fun start(isoFd: Int, cheatMask: Long, resScale: Float): Boolean =
        available && nativeStart(isoFd, cheatMask, resScale)
}
