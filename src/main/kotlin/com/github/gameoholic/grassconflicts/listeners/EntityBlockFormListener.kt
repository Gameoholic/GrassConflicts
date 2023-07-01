package com.github.gameoholic.grassconflicts.listeners

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.EntityBlockFormEvent
import org.bukkit.event.entity.EntityChangeBlockEvent

object EntityBlockFormListener : Listener {

    @EventHandler
    fun onEntityBlockFormEvent(e: EntityChangeBlockEvent) {
        if (e.entity.type == EntityType.FALLING_BLOCK) {
            e.isCancelled = true
            e.entity.remove()
        }
    }
}

