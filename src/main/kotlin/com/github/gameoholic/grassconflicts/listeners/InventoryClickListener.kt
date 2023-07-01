package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDrop
import com.github.gameoholic.grassconflicts.util.VectorInt
import com.github.gameoholic.grassconflicts.util.WorldEditUtil
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

object InventoryClickListener : Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == e.whoClicked.uniqueId}
        } ?: return

        if (e.slotType == InventoryType.SlotType.ARMOR)
            e.isCancelled = true


    }


}