package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.util.VectorInt
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

object BlockDropItemListener : Listener {

    @EventHandler
    fun onBlockDropItem(e: BlockDropItemEvent) {
        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == e.player.uniqueId}
        } ?: return

        var gcPlayer: GCPlayer = gameManager.gamePlayers.firstOrNull {gcPlayer -> gcPlayer.player.uniqueId == e.player.uniqueId}!!

        if (e.items.size == 0) return
        if (e.items[0].itemStack.type == Material.DIRT) {
            e.isCancelled = true
            e.block.location.world.dropItemNaturally(e.block.location, ItemStack(gcPlayer.team.material, 1))
        }
    }


}