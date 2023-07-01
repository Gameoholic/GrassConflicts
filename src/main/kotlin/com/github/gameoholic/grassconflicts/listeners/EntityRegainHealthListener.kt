package com.github.gameoholic.grassconflicts.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent

object EntityRegainHealthListener : Listener {

    @EventHandler
    fun onEntityRegainHealthEvent(e: EntityRegainHealthEvent) {
        e.amount = e.amount * 0.5;
    }
}