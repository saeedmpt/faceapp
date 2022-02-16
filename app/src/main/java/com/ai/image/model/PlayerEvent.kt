package com.ai.image.model


class PlayerEvent {
    interface VARIABLE {
        companion object {
            const val type_player_status_icon = "player_status_icon"
            const val type_player_info = "player_info"
            const val type_player_prepare = "player_prepare"
            const val type_player_stoped = "player_stoped"
            const val icon_play = "icon_play"
            const val icon_pause = "icon_pause"
        }
    }

    var type: String? = null
    var icon: String? = null
    var isPrepareLoading = false
    var currentTime: Long = 0
    var totalTime: Long = 0
    var userName: String? = null
}