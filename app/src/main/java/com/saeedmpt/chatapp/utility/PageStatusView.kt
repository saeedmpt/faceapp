package com.saeedmpt.chatapp.utility

import android.view.View

object PageStatusView {
    @JvmStatic
    fun pageStatusView(visibleView: Array<out View?>, goneViews: Array<View>) {
        for (i in goneViews.indices) {
            goneViews[i].visibility = View.GONE
        }
        for (i in visibleView.indices) {
            visibleView[i]?.visibility = View.VISIBLE
        }
    }

}