package com.saeedmpt.chatapp.model


class EventModel {
    interface VARIABLE {
        companion object {
            const val ACTION_GALLERY = "ACTION_GALLERY"
            const val ACTION_CAPTURE = "ACTION_CAPTURE"
            const val ACTION_FACEING = "ACTION_FACEING"
            const val ACTION_FILTER = "ACTION_FILTER"
            const val ACTION_BACK = "ACTION_BACK"
        }
    }

    lateinit var action: String
    lateinit var model: Any
    var value: String? = ""
}