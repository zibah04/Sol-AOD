package com.sol.aod

import android.app.Service
import android.service.dreams.DreamService
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.view.View
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

/**
 * Android's supported screensaver API. Android/MIUI decides when to launch it;
 * on most Android 11 phones this means while charging or docked, not every
 * time the display is locked.
 */
class SolDreamService : DreamService() {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = false
        setContentView(SolDreamFace(this))
    }
}

private class SolDreamFace(context: Context) : View(context) {
    private val handler = Handler(Looper.getMainLooper())
    private val paper = Color.rgb(244, 241, 234)
    private val gold = Color.rgb(198, 161, 91)
    private val muted = Color.rgb(115, 112, 105)
    private val ring = Color.rgb(48, 46, 42)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var tick = 0
    private val refresh = object : Runnable {
        override fun run() {
            tick++
            invalidate()
            handler.postDelayed(this, 30_000L)
        }
    }

    init {
        setBackgroundColor(Color.BLACK)
        handler.post(refresh)
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(refresh)
        super.onDetachedFromWindow()
    }

    private fun text(canvas: Canvas, value: String, x: Float, y: Float, size: Float,
                     color: Int, typeface: Typeface = Typeface.create("sans-serif-light", Typeface.NORMAL),
                     spacing: Float = 0f) {
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.typeface = typeface
        paint.textSize = size
        paint.letterSpacing = spacing
        canvas.drawText(value, x, y, paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // Tiny periodic offset helps reduce static-pixel retention.
        val shiftX = ((tick % 3) - 1) * 5f * resources.displayMetrics.density
        val shiftY = (((tick / 3) % 3) - 1) * 5f * resources.displayMetrics.density
        canvas.save()
        canvas.translate(shiftX, shiftY)

        val now = LocalDateTime.now()
        val density = resources.displayMetrics.density
        val left = w * 0.085f
        val baseY = h * 0.47f
        val hour = now.format(DateTimeFormatter.ofPattern("HH"))
        val minute = now.format(DateTimeFormatter.ofPattern("mm"))
        val date = now.format(DateTimeFormatter.ofPattern("dd  MMM  yyyy")).uppercase()
        val day = now.format(DateTimeFormatter.ofPattern("EEEE")).uppercase()

        val hourSize = (w * 0.205f).coerceIn(68f, 150f)
        text(canvas, hour, left, baseY, hourSize, paper)
        val hourWidth = paint.measureText(hour)
        val capsuleX = left + hourWidth + 13f * density
        val capsuleY = baseY - hourSize * 0.73f
        val capsuleW = 64f * density
        val capsuleH = 38f * density
        paint.color = muted
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f * density
        canvas.drawRoundRect(capsuleX, capsuleY, capsuleX + capsuleW, capsuleY + capsuleH, 22f * density, 22f * density, paint)
        text(canvas, minute, capsuleX + 11f * density, capsuleY + 27f * density, 22f * density, paper)

        text(canvas, date, left + 2f * density, baseY + 42f * density, 10f * density, muted, Typeface.create("sans-serif", Typeface.NORMAL), 0.18f)
        text(canvas, day, left + 2f * density, baseY + 68f * density, 14f * density, paper, Typeface.create("sans-serif", Typeface.BOLD), 0.22f)

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
        val batteryY = baseY + 108f * density
        paint.color = gold
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f * density
        canvas.drawRoundRect(left + 2f * density, batteryY - 9f * density, left + 20f * density, batteryY + 1f * density, 2f * density, 2f * density, paint)
        canvas.drawRect(left + 20f * density, batteryY - 6f * density, left + 22f * density, batteryY - 2f * density, paint)
        text(canvas, "$level%", left + 31f * density, batteryY, 11f * density, paper)
        text(canvas, "SOL", w - 48f * density, h - 34f * density, 9f * density, gold, Typeface.create("sans-serif", Typeface.NORMAL), 0.25f)

        // Partial mechanical dial on the right-hand side.
        val cx = w * 0.91f
        val cy = h * 0.49f
        val radius = (w * 0.36f).coerceAtMost(h * 0.25f)
        paint.color = ring
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.2f * density
        canvas.drawCircle(cx, cy, radius, paint)
        canvas.drawCircle(cx, cy, radius * 0.88f, paint)
        for (i in 0 until 60) {
            val angle = Math.toRadians(i * 6.0 - 90.0)
            val major = i % 5 == 0
            val r1 = radius * if (major) 0.82f else 0.88f
            val r2 = radius * 0.97f
            paint.color = if (major) gold else muted
            paint.strokeWidth = if (major) 1.6f * density else 0.7f * density
            canvas.drawLine(
                cx + cos(angle).toFloat() * r1, cy + sin(angle).toFloat() * r1,
                cx + cos(angle).toFloat() * r2, cy + sin(angle).toFloat() * r2, paint
            )
        }
        paint.color = gold
        paint.strokeWidth = 1f * density
        canvas.drawLine(cx - radius * 0.22f, cy, cx + radius * 0.52f, cy, paint)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, 2.5f * density, paint)
        canvas.restore()
    }
}
