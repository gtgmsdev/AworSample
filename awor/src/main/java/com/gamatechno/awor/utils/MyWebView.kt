package com.gamatechno.awor.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MyWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (clampedX || clampedY) {
            //Content is not scrolling
            //Enable SwipeRefreshLayout
            val parent = this.parent
            if (parent is SwipeRefreshLayout) {
                parent.isEnabled = true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (event!!.actionMasked == MotionEvent.ACTION_DOWN) {
            //Disable SwipeRefreshLayout
            val parent = this.parent
            if (parent is SwipeRefreshLayout) {
                parent.isEnabled = false
            }
        }
        return true
    }
}