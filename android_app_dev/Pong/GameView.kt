package com.nick.pong

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    private val thread: GameThread
    private var ball: Ball? = null
    private var player: Player? = null

    private var touched: Boolean = false
    private var touched_x: Int = 0
    private var touched_y: Int = 0
    private var collision: Boolean = false

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        ball = Ball(BitmapFactory.decodeResource(resources, R.drawable.ball))
        player = Player(BitmapFactory.decodeResource(resources, R.drawable.wall))

        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            retry = false
        }
    }

    fun update() {
        collision = ball!!.check_collision(player!!.getY())
        ball!!.update()
        if(touched) {
          player!!.update(touched_x, touched_y)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        ball!!.draw(canvas)
        player!!.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touched_x = event.x.toInt()
        touched_y = event.y.toInt()

        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> touched = true
            MotionEvent.ACTION_MOVE -> touched = true
            MotionEvent.ACTION_UP -> touched = false
            MotionEvent.ACTION_CANCEL -> touched = false
            MotionEvent.ACTION_OUTSIDE -> touched = false
        }

        return true
    }
}