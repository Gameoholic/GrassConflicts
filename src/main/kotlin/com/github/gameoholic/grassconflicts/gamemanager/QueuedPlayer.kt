package com.github.gameoholic.grassconflicts.gamemanager

import com.github.gameoholic.grassconflicts.GrassConflicts
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.logging.Level

open class QueuedPlayer {
    var player: Player

    constructor(player: Player) {
        this.player = player
    }

}