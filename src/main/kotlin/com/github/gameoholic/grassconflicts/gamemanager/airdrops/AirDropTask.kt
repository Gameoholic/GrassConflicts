package com.github.gameoholic.grassconflicts.gamemanager.airdrops

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.GrassConflictsPlugin
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class AirDropTask(airDrop: AirDrop) : BukkitRunnable() {

    private val airDrop: AirDrop
    private var animationPercentage = 0.0
    private val animationSpeed = 2.5
    private val chestSpeed = 0.1

    init {
        this.airDrop = airDrop
        runTaskTimer(GrassConflicts.plugin, 0L, 1L)
    }

    override fun run() {
        val blockBelowLoc = airDrop.blockDisplay.location
        if (GrassConflicts.world.getBlockAt(
                blockBelowLoc.blockX, (blockBelowLoc.y - 0.25).toInt(), blockBelowLoc.blockZ).type != Material.AIR) {
            airDrop.onAirDropLand(animationPercentage)
            cancel()
        }
        var armorStandLocation = airDrop.armorStand.location
        armorStandLocation.y -= chestSpeed
        airDrop.armorStand.teleport(armorStandLocation)
        var blockDisplayLocation = airDrop.blockDisplay.location
        blockDisplayLocation.y -= chestSpeed
        airDrop.blockDisplay.teleport(blockDisplayLocation)

        spawnParticles()
        animationPercentage += animationSpeed
        if (animationPercentage >= 100)
            animationPercentage = 0.0


    }



    private fun spawnParticles() {
        val angle1 = 2 * Math.PI * (animationPercentage / 100.0)
        spawnParticle(angle1)
        val angle2 = 2 * Math.PI * ((animationPercentage + 50) / 100.0)
        spawnParticle(angle2)
    }

    private fun spawnParticle(angle: Double) {
        val radius = 1.5
        val dustOptions = DustOptions(Color.fromRGB(209, 137, 33), 1.0f)
        val circleX1 = radius / 2 * cos(angle)
        val circleZ1 = radius / 2 * sin(angle)
        var location: Location = airDrop.blockDisplay.location
        val particleVert = Vector(0.5, -1.2, 0.5)

        location.world.spawnParticle(
            Particle.REDSTONE,
            Location(
                location.world,
                location.x + circleX1 + particleVert.x,
                location.y + 1.6 + particleVert.y,
                location.z + circleZ1 + particleVert.z
            ),
            10,
            dustOptions
        )
    }


}
