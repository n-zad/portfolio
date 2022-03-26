package com.nick.pong

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import kotlin.math.abs

class Ball(var image: Bitmap) {
    var x: Int = 0
    var y: Int = 0
    var w: Int = 0
    var h: Int = 0

    private var xVelocity = 20
    private var yVelocity = 20

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    init {
        w = image.width
        h = image.height
        x = screenWidth/2
        y = screenHeight/2
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    fun update() {
        // val randomNum = ThreadLocalRandom.current().nextInt(1,5)

        if (x > screenWidth - image.width || x < image.width) {
            xVelocity *= -1
        }
        if (y > screenHeight - image.height || y < image.height) {
            yVelocity *= -1
        }
        if (x > screenWidth - image.width) {
            x = screenWidth/2
            y = screenHeight/2
        }

        x += xVelocity
        y += yVelocity
    }

    fun check_collision(posY: Int): Boolean {
        if (x > screenWidth - 100 - image.width && abs(y - posY) < 96/2) {
            xVelocity *= -1
            return true
        }
        return false
    }
}