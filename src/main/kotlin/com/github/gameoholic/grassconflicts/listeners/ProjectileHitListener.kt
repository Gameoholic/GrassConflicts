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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

object ProjectileHitListener : Listener {

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        if (e.entity.shooter !is Player || e.hitBlock == null) return
        val player = e.entity.shooter as Player
        val block = e.hitBlock!!

        e.entity.remove()

        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        } ?: return

        var gcPlayer: GCPlayer = gameManager.gamePlayers.firstOrNull { gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId} ?: return

        if (block.location.blockY <= gameManager.gameParameters.corner1Coords.blockY
            || block.location.blockY > gameManager.gameParameters.buildHeightLimit || block.type == Material.CHEST ||
            block.x > gameManager.gameParameters.corner1Coords.blockX || block.z > gameManager.gameParameters.corner1Coords.blockZ
            || block.x < gameManager.gameParameters.corner2Coords.blockX || block.z < gameManager.gameParameters.corner2Coords.blockZ)
            return



        if (gameManager.phase == Phase.GRASS) {

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

            if (gcPlayer.team == Team.GRASS && blockInMyceliumRegion) return
            if (gcPlayer.team == Team.MYCELIUM && blockInGrassRegion) return
        }

        GrassConflicts.world.spawnParticle(
            Particle.BLOCK_CRACK, block.location.x + 0.5,
            block.location.y + 0.5,
            block.location.z + 0.5,
            20, 0.0, 0.0, 0.0, 0.1, block.type.createBlockData())

        GrassConflicts.world.playSound(
            Sound.sound(
            Key.key("minecraft:block.grass.break"), Sound.Source.MASTER, 0.5f, 1.0f),
            block.x.toDouble(), block.y.toDouble(), block.z.toDouble())

        block.type = Material.AIR
    }


}