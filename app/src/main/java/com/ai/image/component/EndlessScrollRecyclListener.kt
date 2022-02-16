package com.ai.chatapp.component

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

abstract class EndlessScrollRecyclListener : RecyclerView.OnScrollListener() {
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    private var loading = true
    private val visibleThreshold = 5
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private val startingPageIndex = 0
    private var currentPage = 0
    override fun onScrolled(mRecyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(mRecyclerView, dx, dy)
        val mLayoutManager = mRecyclerView
            .layoutManager as LinearLayoutManager?
        visibleItemCount = mRecyclerView.childCount
        totalItemCount = mLayoutManager!!.itemCount
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
        onScroll(firstVisibleItem, visibleItemCount, totalItemCount)
    }

    fun onScroll(firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem +
            visibleThreshold
        ) {
            onLoadMore(currentPage + 1, totalItemCount)
            loading = true
        }
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int)
}