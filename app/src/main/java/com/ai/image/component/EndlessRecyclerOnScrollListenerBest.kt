package com.ai.chatapp.component

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerOnScrollListenerBest(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        /////
        if (dy < 0) //check for scroll down
        {
            val visibleItemCount = mLinearLayoutManager.childCount
            val totalItemCount = mLinearLayoutManager.itemCount
            val pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition()
            if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                onLoadMore()
            }
        }
    }

    abstract fun onLoadMore()

    companion object {
        var TAG = EndlessRecyclerOnScrollListenerBest::class.java.simpleName
    }
}