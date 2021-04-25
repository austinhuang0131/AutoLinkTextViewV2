package io.github.armcha.autolink

import android.os.Handler
import android.os.Looper
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.TextView

internal class LinkTouchMovementMethod : LinkMovementMethod() {
    private var longClickHandler: Handler? = null
    private var isLongPressed = false
    private var pressedSpan: TouchableSpan? = null

    override fun onTouchEvent(textView: TextView, spannable: Spannable, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_CANCEL) {
            longClickHandler?.removeCallbacksAndMessages(null)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isLongPressed = false
                pressedSpan = getPressedSpan(textView, spannable, event)
                if (pressedSpan != null) {
                    pressedSpan?.isPressed = true
                    Selection.setSelection(spannable, spannable.getSpanStart(pressedSpan),
                            spannable.getSpanEnd(pressedSpan))
                    longClickHandler?.postDelayed({
                        pressedSpan?.isPressed = false
                        pressedSpan?.onLongClick(textView)
                        isLongPressed = true
                        pressedSpan = null
                        Selection.removeSelection(spannable)
                    }, ViewConfiguration.getLongPressTimeout().toLong())
                }
            }
            MotionEvent.ACTION_UP -> {
                longClickHandler?.removeCallbacksAndMessages(null)
                if (!isLongPressed) {
                    pressedSpan?.onClick(textView)
                }
                pressedSpan?.isPressed = false
                isLongPressed = false
                pressedSpan = null
                Selection.removeSelection(spannable)
            }
            MotionEvent.ACTION_MOVE -> {
                val touchedSpan = getPressedSpan(textView, spannable, event)
                if (pressedSpan != null && touchedSpan != pressedSpan) {
                    pressedSpan?.isPressed = false
                    pressedSpan = null
                    Selection.removeSelection(spannable)
                }
            }
            else -> {
                if (pressedSpan != null) {
                    pressedSpan?.isPressed = false
                    super.onTouchEvent(textView, spannable, event)
                }
                pressedSpan = null
                Selection.removeSelection(spannable)
            }
        }
        return true
    }

    private fun getPressedSpan(textView: TextView, spannable: Spannable, event: MotionEvent): TouchableSpan? {

        var x = event.x.toInt()
        var y = event.y.toInt()

        x -= textView.totalPaddingLeft
        y -= textView.totalPaddingTop

        x += textView.scrollX
        y += textView.scrollY

        val layout = textView.layout
        val verticalLine = layout.getLineForVertical(y)
        val horizontalOffset = layout.getOffsetForHorizontal(verticalLine, x.toFloat())

        val link = spannable.getSpans(horizontalOffset, horizontalOffset, TouchableSpan::class.java)
        return link.getOrNull(0)
    }

    companion object {
        val instance: MovementMethod?
            get() {
                if (sInstance == null) {
                    sInstance = LinkTouchMovementMethod()
                    sInstance!!.longClickHandler = Handler(Looper.getMainLooper())
                }
                return sInstance
            }

        private var sInstance: LinkTouchMovementMethod? = null
    }
}
