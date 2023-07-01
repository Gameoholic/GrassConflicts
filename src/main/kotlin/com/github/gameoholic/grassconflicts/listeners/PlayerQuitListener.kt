package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener : Listener {

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
//        GrassConflicts.packetManager.removePlayer(e.player)
    }


}