package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDrop
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

object InventoryCloseListener : Listener {

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val airDrop = AirDrop.airDrops.firstOrNull { airDrop ->  airDrop.chest?.location == e.inventory.location && e.inventory.isEmpty}
        if (airDrop != null){
            airDrop.chest?.type = Material.AIR
            airDrop.particleIdleTask?.cancel()
            AirDrop.airDrops.remove(airDrop)
        }
    }
}