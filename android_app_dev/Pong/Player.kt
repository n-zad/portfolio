package com.nick.pong

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas

class Player(private val image: Bitmap) {
    private var x: Int = 0
    private var y: Int = 0
    private val w: Int
    private val h: Int
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    init {
        w = image.width
        h = image.height
        x = screenWidth - 100
        y = screenHeight/2
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    fun update(touch_x: Int, touch_y: Int) {
        //x = touch_x - w/2
        y = touch_y - h/2
    }

    fun getY(): Int {
        return y
    }
}