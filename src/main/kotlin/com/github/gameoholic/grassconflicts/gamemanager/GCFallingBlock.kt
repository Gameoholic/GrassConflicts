package com.github.gameoholic.grassconflicts.gamemanager

import com.github.gameoholic.grassconflicts.GrassConflicts
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.FallingBlock
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

class GCFallingBlock(val gcPlayer: GCPlayer, world: World) {
    private var fallingBlock: FallingBlock
    private var task: BukkitTask? = null

    init {
        val player = gcPlayer.player
        var location = player.location
        location.y += 2
        fallingBlock = world.spawnFallingBlock(location, gcPlayer.team.material.createBlockData())
        fallingBlock.dropItem = false

        val velocity = fallingBlock.velocity
        velocity.y = 0.3
        velocity.x = Random.nextDouble(-0.3, 0.3)
        velocity.z = Random.nextDouble(-0.3, 0.3)
        fallingBlock.velocity = velocity

    }
}