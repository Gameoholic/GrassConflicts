package com.github.gameoholic.grassconflicts.gamemanager.airdrops

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.GrassConflictsPlugin
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class ParticleIdleTask(animationPercentage: Double, airDrop: AirDrop) : BukkitRunnable() {

    private val airDrop: AirDrop
    private var animationPercentage: Double
    private val animationSpeed = 2.5
    private var ticksPassedSinceStart = 0

    init {
        this.airDrop = airDrop
        this.animationPercentage = animationPercentage
        runTaskTimer(GrassConflicts.plugin, 0L, 1L)
    }

    override fun run() {
        airDrop.chest ?: return

        spawnParticles()
        animationPercentage += animationSpeed
        if (animationPercentage >= 100)
            animationPercentage = 0.0
        ticksPassedSinceStart++
        if (ticksPassedSinceStart > 1000)
            cancel()
    }

    private fun spawnParticles() {
        val angle1 = 2 * Math.PI * (animationPercentage / 100.0)
        spawnParticle(angle1)
//        val angle2 = 2 * Math.PI * ((animationPercentage + 50) / 100.0)
//        spawnParticle(angle2)
    }

    private fun spawnParticle(angle: Double) {
        val radius = 1.5
        val dustOptions = Particle.DustOptions(Color.fromRGB(209, 137, 33), 1.0f)
        val circleX1 = radius / 2 * cos(angle)
        val circleZ1 = radius / 2 * sin(angle)
        var location: Location = airDrop.chest!!.location
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