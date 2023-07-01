package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.gamemanager.QueuedPlayer
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDropItemType
import com.github.gameoholic.grassconflicts.util.VectorInt
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent

object PlayerInteractListener : Listener {

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player

        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager ->
            (gameManager.queuedPlayers + gameManager.gamePlayers)
                .any {queuedPlayer -> queuedPlayer.player.uniqueId == player.uniqueId}
                } ?: return

        val item = player.inventory.itemInMainHand
        var itemName = PlainTextComponentSerializer.plainText().serialize(item.displayName())
        itemName = itemName.substring(1, itemName.length - 1)
        val itemType = item.type

        if (gameManager.phase != Phase.TEAM_SELECTION) {
            if (itemType == Material.END_STONE && itemName == "Cheesier Pasta") {
                val gcPlayer = gameManager.gamePlayers.first {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
                gameManager.onSpecialItemUsage(AirDropItemType.CHEESIER_PASTA, gcPlayer)
                item.amount -= 1
                e.isCancelled = true
            }
        }

        val block = e.clickedBlock
        if (block != null && block.type == Material.CHEST && gameManager.phase == Phase.GRASS) {

            var gcPlayer: GCPlayer = gameManager.gamePlayers.firstOrNull { gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId} ?: return
            if (gcPlayer.isDead) {
                e.isCancelled = true
                return
            }

            var myceliumCorner1 = //pos
                VectorInt(gameManager.gameParameters.corner1Coords.blockX,
                    gameManager.gameParameters.corner1Coords.blockY,
                    gameManager.gameParameters.corner1Coords.blockZ)
            var myceliumCorner2 = //neg
                VectorInt(gameManager.gameParameters.corner2Coords.blockX,
                    gameManager.gameParameters.corner2Coords.blockY,
                    gameManager.borderZCoordinate + 1)
            var grassCorner1 = //neg
                VectorInt(gameManager.gameParameters.corner2Coords.blockX,
                    gameManager.gameParameters.corner2Coords.blockY,
                    gameManager.gameParameters.corner2Coords.blockZ)
            var grassCorner2 = //pos
                VectorInt(gameManager.gameParameters.corner1Coords.blockX,
                    gameManager.gameParameters.corner1Coords.blockY,
                    gameManager.borderZCoordinate - 1)

            var blockInGrassRegion = block.x >= grassCorner1.x && block.z >= grassCorner1.z &&
                block.x <= grassCorner2.x && block.z <= grassCorner2.z
            var blockInMyceliumRegion = block.x <= myceliumCorner1.x && block.z <= myceliumCorner1.z &&
                block.x >= myceliumCorner2.x && block.z >= myceliumCorner2.z
            if ((gcPlayer.team == Team.GRASS && blockInMyceliumRegion) || (gcPlayer.team == Team.MYCELIUM && blockInGrassRegion))
                e.isCancelled = true
            return
        }

        if (gameManager.phase != Phase.TEAM_SELECTION) return

        if (itemType == Material.GRASS_BLOCK && itemName.startsWith("Grass - ")) {
            gameManager.addPlayerToTeam(player, Team.GRASS)
        }
        else if (itemType == Material.MYCELIUM && itemName.startsWith("Mycelium - ")) {
            gameManager.addPlayerToTeam(player, Team.MYCELIUM)
        }
        else
            return
        e.isCancelled = true

    }


}