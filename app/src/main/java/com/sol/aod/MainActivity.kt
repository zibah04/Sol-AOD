package com.sol.aod

import android.content.Intent
import android.provider.Settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

private val Ink = Color(0xFF000000)
private val Paper = Color(0xFFF4F1EA)
private val Gold = Color(0xFFC6A15B)
private val Muted = Color(0xFF88847D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SolApp() }
    }
}

data class SolTheme(
    val id: String,
    val name: String,
    val description: String,
    val accent: Color,
    val isBuiltIn: Boolean = true
)

private val themes = listOf(
    SolTheme("mechanical", "Mechanical Edition", "The original luxury mechanical-inspired layout.", Gold),
    SolTheme("minimal", "Minimal Edition", "A clean, quiet digital clock concept.", Color(0xFFB7D7E8)),
    SolTheme("cyber", "Cyber Edition", "A future theme slot for a sharper technical look.", Color(0xFF63E6D4)),
    SolTheme("type", "Typography Edition", "A future theme slot focused on expressive type.", Color(0xFFD6B4E8))
)

@Composable
fun SolApp() {
    var selected by remember { mutableStateOf(themes.first()) }
    var page by remember { mutableStateOf("home") }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Ink, surface = Color(0xFF111111), primary = Gold,
        onBackground = Paper, onSurface = Paper
    )) {
        Surface(Modifier.fillMaxSize(), color = Ink) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("SOL AOD", color = Paper, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text("YOUR SCREEN. YOUR STYLE.", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                    }
                    Text("0.1", color = Gold, fontSize = 12.sp)
                }
                when (page) {
                    "gallery" -> GalleryScreen(selected) { selected = it; page = "preview" }
                    "preview" -> PreviewScreen(selected) { page = "gallery" }
                    "settings" -> SettingsScreen()
                    else -> HomeScreen(selected, onGallery = { page = "gallery" }, onPreview = { page = "preview" }, onApply = { startActivity(Intent(Settings.ACTION_DREAM_SETTINGS)) })
                }
                Spacer(Modifier.weight(1f))
                NavigationBar(containerColor = Color(0xFF090909), contentColor = Paper) {
                    listOf("home" to "Home", "gallery" to "Themes", "settings" to "Settings").forEach { (key, label) ->
                        NavigationBarItem(
                            selected = page == key || (page == "preview" && key == "gallery"),
                            onClick = { page = key },
                            icon = { Text(when(key) { "home" -> "⌂"; "gallery" -> "◈"; else -> "⚙" }, fontSize = 19.sp) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Gold, selectedTextColor = Gold,
                                indicatorColor = Color(0xFF292319), unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(theme: SolTheme, onGallery: () -> Unit, onPreview: () -> Unit, onApply: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
        Text("CURRENT DISPLAY", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(390.dp).background(Color(0xFF050505), RoundedCornerShape(22.dp)).clickable { onPreview() }) {
            if (theme.id == "mechanical") MechanicalFace(Modifier.fillMaxSize())
            else GenericFace(theme, Modifier.fillMaxSize())
            Text("TAP TO PREVIEW", Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp), color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text(theme.name, color = Paper, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(theme.description, color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(22.dp))
        Button(onClick = onGallery, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink), shape = RoundedCornerShape(14.dp)) {
            Text("EXPLORE THEME LIBRARY", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onApply, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)) {
            Text("ACTIVATE CHARGING AOD", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text("Android screensaver mode • Starts while charging or docked", color = Muted, fontSize = 10.sp)
    }
}

@Composable
fun GalleryScreen(selected: SolTheme, onSelect: (SolTheme) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("THEME LIBRARY", color = Paper, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Choose a design to preview.", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(themes) { theme ->
                Column(Modifier.clickable { onSelect(theme) }) {
                    Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF080808), RoundedCornerShape(16.dp)).border(1.dp, if (theme.id == selected.id) Gold else Color(0xFF252525), RoundedCornerShape(16.dp))) {
                        if (theme.id == "mechanical") MechanicalFace(Modifier.fillMaxSize(), compact = true)
                        else GenericFace(theme, Modifier.fillMaxSize())
                        if (theme.id != "mechanical") Text("COMING SOON", Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp), color = Muted, fontSize = 8.sp, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(theme.name, color = Paper, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (theme.id == "mechanical") "Available" else "Design slot", color = Muted, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), enabled = false, shape = RoundedCornerShape(12.dp)) {
            Text("IMPORT THEME PACKAGE — COMING IN PHASE 3")
        }
    }
}

@Composable
fun PreviewScreen(theme: SolTheme, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("LIVE PREVIEW", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF050505), RoundedCornerShape(20.dp))) {
            if (theme.id == "mechanical") MechanicalFace(Modifier.fillMaxSize())
            else GenericFace(theme, Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(14.dp))
        Text(theme.name, color = Paper, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("This is an in-app preview. Screen-off AOD activation is a later device-compatibility milestone.", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text("BACK TO THEMES") }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(22.dp)) {
        Text("SETTINGS", color = Paper, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        SettingRow("Display engine", "In-app preview foundation")
        SettingRow("Theme packages", "ZIP-based format planned")
        SettingRow("User theme creator", "Planned for a later phase")
        SettingRow("Device target", "Redmi Note 10 • Android 11")
        SettingRow("Root access", "Not required for the app foundation")
        Spacer(Modifier.height(20.dp))
        Text("Sol AOD does not yet replace the phone's native always-on display. Screen-off behaviour will be implemented and tested separately.", color = Muted, fontSize = 12.sp)
    }
}

@Composable
fun SettingRow(title: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 11.dp)) {
        Text(title, color = Paper, fontSize = 14.sp)
        Text(value, color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFF292929))
    }
}

@Composable
fun MechanicalFace(modifier: Modifier = Modifier, compact: Boolean = false) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) { now = LocalDateTime.now(); delay(1000) }
    }
    val hour = now.format(DateTimeFormatter.ofPattern("HH"))
    val minute = now.format(DateTimeFormatter.ofPattern("mm"))
    val day = now.format(DateTimeFormatter.ofPattern("EEEE")).uppercase()
    val date = now.format(DateTimeFormatter.ofPattern("dd  MMM  yyyy")).uppercase()
    BoxWithConstraints(modifier.background(Ink)) {
        val big = if (compact) 46.sp else 76.sp
        val small = if (compact) 17.sp else 25.sp
        Canvas(Modifier.fillMaxSize()) {
            val radius = size.width * if (compact) 0.37f else 0.43f
            val center = Offset(size.width * 0.88f, size.height * 0.48f)
            drawCircle(Color(0xFF242321), radius, center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2.dp.toPx()))
            drawCircle(Color(0xFF161616), radius * 0.88f, center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()))
            for (i in 0 until 60) {
                val a = Math.toRadians(i * 6.0 - 90.0)
                val major = i % 5 == 0
                val r1 = radius * if (major) 0.82f else 0.87f
                val r2 = radius * 0.96f
                val p1 = Offset(center.x + cos(a).toFloat() * r1, center.y + sin(a).toFloat() * r1)
                val p2 = Offset(center.x + cos(a).toFloat() * r2, center.y + sin(a).toFloat() * r2)
                drawLine(if (major) Gold.copy(alpha = 0.8f) else Color(0xFF77736A), p1, p2, strokeWidth = if (major) 1.8.dp.toPx() else 0.8.dp.toPx(), cap = StrokeCap.Round)
            }
            drawLine(Gold.copy(alpha = 0.7f), Offset(center.x - radius * .2f, center.y), Offset(center.x + radius * .52f, center.y), strokeWidth = 1.dp.toPx())
            drawCircle(Gold, radius * 0.025f, center)
        }
        Column(Modifier.align(Alignment.CenterStart).padding(start = if (compact) 12.dp else 24.dp).offset(y = (-8).dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(hour, color = Paper, fontSize = big, fontWeight = FontWeight.Light, fontFamily = FontFamily.SansSerif, letterSpacing = (-3).sp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.border(1.dp, Color(0xFF77736A), RoundedCornerShape(50)).padding(horizontal = 7.dp, vertical = 4.dp)) {
                    Text(minute, color = Paper, fontSize = small, fontWeight = FontWeight.Light)
                }
            }
            Spacer(Modifier.height(if (compact) 8.dp else 15.dp))
            Text(date, color = Muted, fontSize = if (compact) 7.sp else 9.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(5.dp))
            Text(day, color = Paper, fontSize = if (compact) 9.sp else 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
            Spacer(Modifier.height(if (compact) 10.dp else 18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("▰", color = Gold, fontSize = 10.sp)
                Spacer(Modifier.width(6.dp))
                Text("BATTERY", color = Muted, fontSize = 8.sp, letterSpacing = 1.sp)
                Spacer(Modifier.width(5.dp))
                Text("—%", color = Paper, fontSize = 10.sp)
            }
        }
        Text("SOL", Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 22.dp), color = Gold.copy(alpha = 0.75f), fontSize = 8.sp, letterSpacing = 3.sp)
    }
}

@Composable
fun GenericFace(theme: SolTheme, modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) { while (true) { now = LocalDateTime.now(); delay(1000) } }
    Column(modifier.background(Ink).padding(18.dp), verticalArrangement = Arrangement.Center) {
        Text(now.format(DateTimeFormatter.ofPattern("HH:mm")), color = Paper, fontSize = 36.sp, fontWeight = FontWeight.Light)
        Text(now.format(DateTimeFormatter.ofPattern("EEE, dd MMM")).uppercase(), color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        Text(theme.name.uppercase(), color = theme.accent, fontSize = 8.sp, letterSpacing = 2.sp)
    }
}
