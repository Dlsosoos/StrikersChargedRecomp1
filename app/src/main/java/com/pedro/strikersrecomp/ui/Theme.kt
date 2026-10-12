package com.pedro.strikersrecomp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Paleta "noite de estádio": gramado listrado, giz de campo e laranja do Megastrike. */
object Turf {
    val Ink = Color(0xFF07120D)
    val Dark = Color(0xFF0E2A1F)
    val Light = Color(0xFF123626)
    val Chalk = Color(0xFFEEF1E6)
    val Orange = Color(0xFFFF6A13)
    val Whistle = Color(0xFFFFD321)
}

@Composable
fun StrikersTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Turf.Orange,
            onPrimary = Turf.Ink,
            secondary = Turf.Whistle,
            onSecondary = Turf.Ink,
            background = Turf.Ink,
            onBackground = Turf.Chalk,
            surface = Turf.Dark,
            onSurface = Turf.Chalk,
            surfaceVariant = Turf.Light,
            onSurfaceVariant = Turf.Chalk
        ),
        content = content
    )
}
