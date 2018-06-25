package com.anwesh.uiprojects.linkedbreaklineview

/**
 * Created by anweshmishra on 25/06/18.
 */

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Context
import android.graphics.Color

val BL_NODES : Int = 5

class LinkedBreakLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : BLRenderer = BLRenderer(this)

    var linkedBreakLineListener : LinkedBreakLineListener? = null

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    fun addLinkedBreakLineListener(onComplete : (Int) -> Unit) {
        linkedBreakLineListener = LinkedBreakLineListener(onComplete)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
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

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
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
            val x : Float = (w/2 - size) * scale
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

    data class LinkedBreakLine(var i : Int) {

        private var curr : BLNode = BLNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {j, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(j, scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class BLRenderer(var view : LinkedBreakLineView) {

        private val bl : LinkedBreakLine = LinkedBreakLine(0)

        private val animator : BLAnimator = BLAnimator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            bl.draw(canvas, paint)
            animator.animate {
                bl.update {j, scale ->
                    animator.stop()
                    when (scale) {
                        1f -> view.linkedBreakLineListener?.onComplete?.invoke(j)
                    }
                }
            }
        }

        fun handleTap() {
            bl.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : LinkedBreakLineView {
            val view : LinkedBreakLineView = LinkedBreakLineView(activity)
            activity.setContentView(view)
            return view
        }
    }

    data class LinkedBreakLineListener(var onComplete : (Int) -> Unit)
}