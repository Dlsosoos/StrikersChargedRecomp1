package com.pedro.strikersrecomp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract

/** Abre o Dolphin já iniciando o jogo escolhido (extras AutoStartFile/AutoStartFiles). */
object DolphinLauncher {
    const val NOT_INSTALLED = "NOT_INSTALLED"
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.dolphinemu.dolphinemu"

    // Dolphin oficial e o fork MMJR2.
    private val PACKAGES = listOf("org.dolphinemu.dolphinemu", "org.dolphinemu.mmjr")

    fun isInstalled(ctx: Context): Boolean = findLaunchIntent(ctx) != null

    private fun findLaunchIntent(ctx: Context): Intent? {
        for (pkg in PACKAGES) {
            val i = ctx.packageManager.getLaunchIntentForPackage(pkg)
            if (i != null) return i
        }
        return null
    }

    /** Converte content://...externalstorage... em /storage/emulated/0/... quando possível. */
    private fun realPath(uri: Uri): String? {
        if (uri.authority != "com.android.externalstorage.documents") return null
        val id = try {
            DocumentsContract.getDocumentId(uri)
        } catch (e: Exception) {
            return null
        }
        val parts = id.split(":", limit = 2)
        if (parts.size != 2) return null
        val root = if (parts[0] == "primary") "/storage/emulated/0" else "/storage/" + parts[0]
        return root + "/" + parts[1]
    }

    /** Devolve null se abriu, [NOT_INSTALLED] se não há Dolphin, ou uma mensagem de erro. */
    fun launch(ctx: Context, gameUri: Uri, sendPath: Boolean): String? {
        val intent = findLaunchIntent(ctx) ?: return NOT_INSTALLED

        val path = if (sendPath) realPath(gameUri) else null
        if (sendPath && path == null) {
            return "Não consegui descobrir o caminho do arquivo. Desligue \"Enviar caminho\" na aba Config."
        }

        val game = path ?: gameUri.toString()
        intent.putExtra("AutoStartFile", game)
        intent.putExtra("AutoStartFiles", arrayOf(game))
        if (path == null) {
            // Dá ao Dolphin permissão de leitura para este arquivo.
            intent.data = gameUri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        return try {
            ctx.startActivity(intent)
            null
        } catch (e: Exception) {
            "Não consegui abrir o Dolphin: " + (e.message ?: "erro desconhecido")
        }
    }
}
