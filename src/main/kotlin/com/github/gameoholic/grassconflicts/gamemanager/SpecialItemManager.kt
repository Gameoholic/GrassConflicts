package com.github.gameoholic.grassconflicts.gamemanager

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDrop
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.*

class SpecialItemManager(gameManager: GameManager) {
    val gameManager: GameManager
    var lastPlayerThorUsage: GCPlayer? = null

    init {
        this.gameManager = gameManager
    }

    fun onCheesierPastaUsage(gcPlayer: GCPlayer) {
        lastPlayerThorUsage = gcPlayer
        var oppositeTeamGCPlayers: MutableList<GCPlayer> = if (gcPlayer.team == Team.MYCELIUM)
            gameManager.gamePlayers.filter {gcPlayer -> gcPlayer.team == Team.GRASS}.toMutableList()
        else
            gameManager.gamePlayers.filter {gcPlayer -> gcPlayer.team == Team.MYCELIUM}.toMutableList()

        for (gcPlayer in oppositeTeamGCPlayers) {
            gcPlayer.player.sendMessage(Component.text("Suddenly, the smell of cheesier pasta wafts into the air.")
                .color(NamedTextColor.YELLOW))
        }

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            for (gcPlayer in oppositeTeamGCPlayers) {
                gcPlayer.player.velocity = Vector(0.0, 5.0, 0.0)
                gcPlayer.player.location.world.spawnEntity(gcPlayer.player.location, EntityType.LIGHTNING)
                gcPlayer.player.sendMessage(Component.text("Thou hast been mightily smitten by Thor!").color(NamedTextColor.YELLOW))
            }
        }, 40L)
    }

    fun onInstaWallUsage(gcPlayer: GCPlayer, block: Block) {
        val wallMaterial = Material.SNOW_BLOCK // Replace with the desired material for the wall

        val playerDirection = gcPlayer.player.location.direction

        val wallDirection = when {
            playerDirection.x > 0 -> BlockFace.EAST
            playerDirection.x < 0 -> BlockFace.WEST
            playerDirection.z > 0 -> BlockFace.SOUTH
            playerDirection.z < 0 -> BlockFace.NORTH
//            playerDirection.y > 0 -> BlockFace.UP
//            playerDirection.y < 0 -> BlockFace.DOWN
            else -> return // Player is looking straight up or down, so we can't determine the wall direction
        }

        // Calculate the starting position for the wall (above the bottom center block)
        val wallStart = block.getRelative(BlockFace.UP)

        // Calculate the offset for the wall
        val xOffset = if (wallDirection == BlockFace.EAST) 1 else if (wallDirection == BlockFace.WEST) -1 else 0
        val zOffset = if (wallDirection == BlockFace.SOUTH) 1 else if (wallDirection == BlockFace.NORTH) -1 else 0

        // Build a 3x3 wall centered around the starting position
        var frame = 1
        for (i in -1..1) {
            for (j in -1..1) {
                frame += 2
                GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
                    val currentBlock = wallStart.getRelative(xOffset * i, j, zOffset * i)
                    currentBlock.type = wallMaterial
                    GrassConflicts.world.playSound(
                        Sound.sound(
                            Key.key("minecraft:block.snow.place"), Sound.Source.MASTER, 0.5f, 1.0f),
                        block.x.toDouble(), block.y.toDouble(), block.z.toDouble())
                }, frame.toLong())
            }
        }
    }

}