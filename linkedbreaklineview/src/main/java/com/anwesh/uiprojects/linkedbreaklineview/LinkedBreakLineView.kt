package com.anwesh.uiprojects.linkedbreaklineview

/**
 * Created by anweshmishra on 25/06/18.
 */

import android.view.MotionEvent
import android.view.View
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Context
import android.graphics.Color

val BL_NODES : Int = 5

class LinkedBreakLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class BLState(var j : Int = 0, var prevScale : Float = 0f, var dir : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                dir = 0f
                prevScale = scales[j]
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class BLAnimator (var view : View, var animated : Boolean  = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BLNode(var i : Int, val state : BLState = BLState()) {

        var next : BLNode? = null

        var prev : BLNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < BL_NODES - 1) {
                next = BLNode(i + 1)
                next?.prev = this
            }
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = h / BL_NODES
            val size : Float = Math.min(w, h) /10
            paint.strokeWidth = Math.min(w, h) / 50
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#283593")
            val index : Int = this.i % 2
            val scale : Float = index + (1 - 2 * index) * state.scales[1]
            val x : Float = (h/2 - size) * state.scales[1]
            canvas.save()
            canvas.translate(w/2, h - gap * i - gap * state.scales[0])
            for (i in 0..1) {
                canvas.save()
                canvas.scale(1f - 2 * i, 1f)
                canvas.save()
                canvas.translate(x, 0f)
                canvas.drawLine(0f, 0f, size, 0f, paint)
                canvas.restore()
                canvas.restore()
            }
            canvas.restore()
        }

        fun getNext(dir : Int, cb : () -> Unit) : BLNode {
            var curr : BLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}