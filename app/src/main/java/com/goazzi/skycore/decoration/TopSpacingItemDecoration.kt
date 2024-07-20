package com.goazzi.skycore.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopSpacingItemDecoration(private val paddingTop: Int, private val paddingHorizontal:Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION && position != 0) {
            outRect.top = paddingTop
        }
        outRect.left = paddingHorizontal
        outRect.right = paddingHorizontal
    }
}