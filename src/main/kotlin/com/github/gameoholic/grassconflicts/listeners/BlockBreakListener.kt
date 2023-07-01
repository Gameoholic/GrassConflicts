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

object BlockBreakListener : Listener {

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        } ?: return

        var gcPlayer: GCPlayer = gameManager.gamePlayers.firstOrNull { gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId} ?: return
        if (gcPlayer.isDead)  {
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

        var blockInYRange = e.block.y > grassCorner1.y && e.block.y <= gameManager.gameParameters.buildHeightLimit
        if (!blockInYRange) {
            e.isCancelled = true
            return
        }

        var blockInGrassRegion = e.block.x >= grassCorner1.x && e.block.z >= grassCorner1.z &&
            e.block.x <= grassCorner2.x && e.block.z <= grassCorner2.z
        var blockInMyceliumRegion = e.block.x <= myceliumCorner1.x && e.block.z <= myceliumCorner1.z &&
            e.block.x >= myceliumCorner2.x && e.block.z >= myceliumCorner2.z
        if (!blockInGrassRegion && !blockInMyceliumRegion) {
            e.isCancelled = true
            return
        }
        if (gameManager.phase == Phase.GRASS &&
            ((gcPlayer.team == Team.GRASS && !blockInGrassRegion) || gcPlayer.team == Team.MYCELIUM && !blockInMyceliumRegion)) {
            e.isCancelled = true
            return
        }


    }


}