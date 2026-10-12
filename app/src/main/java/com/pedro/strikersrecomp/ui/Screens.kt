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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.strikersrecomp.NativeBridge
import com.pedro.strikersrecomp.R
import com.pedro.strikersrecomp.Store

// Os cheats ficam na ordem: o índice vira um bit na máscara enviada ao motor nativo.
val CHEATS = listOf(
    "megastrike_full" to "Megastrike sempre carregado",
    "unlimited_items" to "Itens ilimitados",
    "freeze_clock" to "Cronômetro congelado",
    "double_goals" to "Gols valem em dobro"
)

private const val NOT_READY = "Ainda sem efeito: o motor nativo precisa ser integrado (veja docs/PORTING.md)."

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
                    val uri = Uri.parse(isoUri)
                    val pfd = try {
                        ctx.contentResolver.openFileDescriptor(uri, "r")
                    } catch (e: Exception) {
                        null
                    }
                    if (pfd == null) {
                        status = "Não consegui abrir o arquivo. Escolha o ISO de novo."
                    } else if (!NativeBridge.available) {
                        pfd.close()
                        status = "Este build não inclui o motor nativo ainda. Veja docs/PORTING.md."
                    } else {
                        var mask = 0L
                        CHEATS.forEachIndexed { i, c -> if (store.flag("cheat_" + c.first)) mask = mask or (1L shl i) }
                        val ok = NativeBridge.start(pfd.detachFd(), mask, store.number("res_scale", 1f))
                        if (!ok) status = "O motor nativo ainda não está integrado."
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
        }
    }
}

@Composable
private fun Panel(title: String, subtitle: String, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(title, fontSize = 32.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = Turf.Chalk)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = Turf.Chalk.copy(alpha = 0.75f))
        Spacer(Modifier.height(16.dp))
        Surface(color = Turf.Ink.copy(alpha = 0.82f), shape = RoundedCornerShape(14.dp)) {
            content()
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, Modifier.weight(1f), color = Turf.Chalk)
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
fun ToggleScreen(title: String, subtitle: String, items: List<Pair<String, String>>, store: Store, prefix: String) {
    val state = remember {
        mutableStateMapOf<String, Boolean>().apply {
            items.forEach { put(it.first, store.flag(prefix + it.first)) }
        }
    }
    Panel(title, subtitle) {
        LazyColumn {
            items(items) { item ->
                SwitchRow(item.second, state[item.first] == true) { v ->
                    state[item.first] = v
                    store.setFlag(prefix + item.first, v)
                }
            }
        }
    }
}

@Composable
fun ConfigScreen(store: Store) {
    var res by remember { mutableStateOf(store.number("res_scale", 1f)) }
    var opacity by remember { mutableStateOf(store.number("touch_opacity", 0.6f)) }
    var fps by remember { mutableStateOf(store.flag("show_fps")) }
    var haptics by remember { mutableStateOf(store.flag("haptics", true)) }

    Panel("Config", NOT_READY) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text("Resolução interna: " + String.format("%.1fx", res), color = Turf.Chalk)
                Slider(
                    value = res,
                    onValueChange = { res = it; store.setNumber("res_scale", it) },
                    valueRange = 0.5f..2f,
                    steps = 5,
                    colors = SliderDefaults.colors(thumbColor = Turf.Orange, activeTrackColor = Turf.Orange)
                )
            }
            Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text("Opacidade dos botões na tela: " + (opacity * 100).toInt() + "%", color = Turf.Chalk)
                Slider(
                    value = opacity,
                    onValueChange = { opacity = it; store.setNumber("touch_opacity", it) },
                    valueRange = 0.1f..1f,
                    colors = SliderDefaults.colors(thumbColor = Turf.Orange, activeTrackColor = Turf.Orange)
                )
            }
            SwitchRow("Mostrar FPS", fps) { fps = it; store.setFlag("show_fps", it) }
            SwitchRow("Vibração nos botões", haptics) { haptics = it; store.setFlag("haptics", it) }
        }
    }
}
