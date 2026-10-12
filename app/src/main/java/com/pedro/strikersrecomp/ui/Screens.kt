package com.pedro.strikersrecomp.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.strikersrecomp.DolphinLauncher
import com.pedro.strikersrecomp.R
import com.pedro.strikersrecomp.Store

private fun displayName(ctx: Context, uri: Uri): String {
    ctx.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        if (c.moveToFirst()) return c.getString(0)
    }
    return uri.lastPathSegment ?: "jogo.iso"
}

private fun isSupportedGame(name: String): Boolean {
    val n = name.lowercase()
    return n.endsWith(".iso") || n.endsWith(".wbfs")
}

@Composable
fun Title() {
    val slide = remember { Animatable(-420f) }
    LaunchedEffect(Unit) {
        slide.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
    }
    Column(Modifier.graphicsLayer { translationX = slide.value }) {
        Text(
            text = "STRIKERS",
            fontSize = 60.sp,
            lineHeight = 58.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-2).sp,
            color = Turf.Chalk
        )
        Row(
            Modifier
                .drawBehind {
                    val skew = 14.dp.toPx()
                    val band = Path().apply {
                        moveTo(skew, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width - skew, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(band, Turf.Orange)
                }
                .padding(horizontal = 22.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Bolt(Modifier.size(22.dp), Turf.Ink)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "CHARGED",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Turf.Ink
            )
        }
    }
}

@Composable
fun PlayScreen(store: Store) {
    val ctx = LocalContext.current
    var isoUri by remember { mutableStateOf(store.isoUri) }
    var status by remember { mutableStateOf<String?>(null) }
    var needInstall by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null && !isSupportedGame(displayName(ctx, uri))) {
            status = "Formato não suportado. Escolha um arquivo .iso ou .wbfs."
        } else if (uri != null) {
            try {
                ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                // Alguns provedores não permitem permissão persistente; o app pede de novo depois.
            }
            store.isoUri = uri.toString()
            isoUri = uri.toString()
            status = null
        }
    }

    val isoName: String? = isoUri?.let {
        try {
            displayName(ctx, Uri.parse(it))
        } catch (e: Exception) {
            null
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Title()
        Image(
            painter = painterResource(R.drawable.mario_kick),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Column {
            Surface(
                color = Turf.Ink.copy(alpha = 0.78f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = if (isoName != null) "Jogo pronto" else "Nenhum jogo escolhido",
                        fontWeight = FontWeight.Bold,
                        color = if (isoName != null) Turf.Whistle else Turf.Chalk
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = isoName ?: "Escolha o seu .iso ou .wbfs do Mario Strikers Charged (R4QE01).",
                        color = Turf.Chalk.copy(alpha = 0.8f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { picker.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isoName != null) "Trocar jogo" else "Escolher .iso / .wbfs", color = Turf.Chalk)
            }
            Spacer(Modifier.height(12.dp))
            Button(
                enabled = isoUri != null,
                onClick = {
                    val result = DolphinLauncher.launch(ctx, Uri.parse(isoUri), store.flag("send_path"))
                    when (result) {
                        null -> {
                            status = null
                            needInstall = false
                        }
                        DolphinLauncher.NOT_INSTALLED -> {
                            status = "O Dolphin não está instalado neste celular."
                            needInstall = true
                        }
                        else -> {
                            status = result
                            needInstall = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Turf.Orange,
                    contentColor = Turf.Ink,
                    disabledContainerColor = Turf.Chalk.copy(alpha = 0.12f),
                    disabledContentColor = Turf.Chalk.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Pontapé inicial", fontSize = 20.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
            }
            if (status != null) {
                Spacer(Modifier.height(10.dp))
                Text(status!!, color = Turf.Whistle)
            }
            if (needInstall) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DolphinLauncher.PLAY_STORE_URL)))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Instalar o Dolphin", color = Turf.Chalk)
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(label: String, hint: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, color = Turf.Chalk, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(hint, color = Turf.Chalk.copy(alpha = 0.7f), fontSize = 13.sp)
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Turf.Ink,
                checkedTrackColor = Turf.Orange,
                uncheckedThumbColor = Turf.Chalk,
                uncheckedTrackColor = Turf.Light
            )
        )
    }
}

@Composable
fun ConfigScreen(store: Store) {
    val ctx = LocalContext.current
    var sendPath by remember { mutableStateOf(store.flag("send_path")) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text("Config", fontSize = 32.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = Turf.Chalk)
        Spacer(Modifier.height(4.dp))
        Text("Como o jogo é enviado ao Dolphin.", color = Turf.Chalk.copy(alpha = 0.75f))
        Spacer(Modifier.height(16.dp))
        Surface(color = Turf.Ink.copy(alpha = 0.82f), shape = RoundedCornerShape(14.dp)) {
            Column {
                Column(Modifier.padding(16.dp)) {
                    Text("Dolphin", color = Turf.Chalk, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (DolphinLauncher.isInstalled(ctx)) "Instalado" else "Não encontrado neste celular",
                        color = if (DolphinLauncher.isInstalled(ctx)) Turf.Whistle else Turf.Orange
                    )
                }
                SwitchRow(
                    label = "Enviar caminho do arquivo",
                    hint = "Use só se o jogo não abrir com o padrão. Funciona com arquivos na memória interna ou no cartão SD.",
                    checked = sendPath
                ) {
                    sendPath = it
                    store.setFlag("send_path", it)
                }
            }
        }
    }
}
