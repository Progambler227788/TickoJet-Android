package com.talhaatif.tickojet.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearHorizontalSpacingDecoration(
    private val spacing: Int,
    private val edgeSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        outRect.left = if (position == 0) edgeSpacing else spacing / 2
        outRect.right = if (position == state.itemCount - 1) edgeSpacing else spacing / 2
    }
}
