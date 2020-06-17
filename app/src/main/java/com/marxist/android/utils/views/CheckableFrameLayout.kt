package com.marxist.android.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout

class CheckableFrameLayout : FrameLayout, Checkable {
    private var mChecked = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context!!, attrs, defStyle) {
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (mChecked) {
            View.mergeDrawableStates(
                drawableState,
                CHECKED_STATE_SET
            )
        }
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            invalidate()
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
            android.R.attr.state_activated,
            android.R.attr.state_checked
        )
    }
}