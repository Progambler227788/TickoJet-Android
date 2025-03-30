package com.talhaatif.tickojet.custom

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class OptimizedItemAnimator : DefaultItemAnimator() {
    init {
        // Enable change animations only when necessary
        supportsChangeAnimations = false
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        fromX: Int, fromY: Int,
        toX: Int, toY: Int
    ): Boolean {
        // Disable change animations for better performance
        dispatchChangeFinished(oldHolder, true)
        dispatchChangeFinished(newHolder, false)
        return false
    }
}