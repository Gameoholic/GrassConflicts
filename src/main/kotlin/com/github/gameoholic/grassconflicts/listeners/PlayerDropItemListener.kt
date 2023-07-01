package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.enums.Phase
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object PlayerDropItemListener : Listener {

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        val gameManager = GrassConflicts.gameManagers.firstOrNull {
            gameManager -> gameManager.queuedPlayers.any {queuedPlayer -> queuedPlayer.player.uniqueId == e.player.uniqueId} ||
            gameManager.gamePlayers.any {gamePlayer -> gamePlayer.player.uniqueId == e.player.uniqueId}
        } ?: return
        if (gameManager.phase != Phase.TEAM_SELECTION) return

        val itemType = e.itemDrop.itemStack.type
        var itemName = PlainTextComponentSerializer.plainText().serialize(e.itemDrop.itemStack.displayName())
        itemName = itemName.substring(1, itemName.length - 1)
        if ((itemType == Material.GRASS_BLOCK && itemName.startsWith("Grass - ")) ||
            (itemType == Material.MYCELIUM && itemName.startsWith("Mycelium - "))) {
            e.isCancelled = true
        }


    }


}