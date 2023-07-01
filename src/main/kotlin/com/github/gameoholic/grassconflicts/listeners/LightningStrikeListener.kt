package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.util.VectorInt
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.LightningStrikeEvent

object LightningStrikeListener : Listener {

    @EventHandler
    fun onLightningStrike(e: LightningStrikeEvent) {
        var loc = e.lightning.location
        if (GrassConflicts.gameManagers.size == 0) return

        var gameManager = GrassConflicts.gameManagers[0]

        var myceliumCorner1 = //pos
            VectorInt(gameManager.gameParameters.corner1Coords.blockX,
                gameManager.gameParameters.corner1Coords.blockY,
                gameManager.gameParameters.corner1Coords.blockZ)
        var grassCorner1 = //neg
            VectorInt(gameManager.gameParameters.corner2Coords.blockX,
                gameManager.gameParameters.corner2Coords.blockY,
                gameManager.gameParameters.corner2Coords.blockZ)
        

        var blockInPlayArea = loc.x >= grassCorner1.x && loc.z >= grassCorner1.z &&
            loc.x <= myceliumCorner1.x && loc.z <= myceliumCorner1.z
        if (!blockInPlayArea) {
            e.isCancelled = true
            return
        }


    }


}
