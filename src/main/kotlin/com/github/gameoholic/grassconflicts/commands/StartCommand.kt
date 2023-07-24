package com.github.gameoholic.grassconflicts.commands

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.GameParameters
import com.github.gameoholic.grassconflicts.datatypes.LaneChangeParameters
import com.github.gameoholic.grassconflicts.datatypes.PhaseParameters
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.gamemanager.GameManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object StartCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player)
            if (!sender.hasPermission("grassconflicts.start"))
                return true

        var gameStartSkipSeconds = 0
        if (args!!.isNotEmpty())
            gameStartSkipSeconds = args[0].toInt()
        val worldName = GrassConflicts.world.name
        val gameParameters = GameParameters(
            gameStartSkipSeconds,
            Location(Bukkit.getWorld(worldName), -94.0, 134.0, -171.0), //corner1 coords (pos, mycelium)
            Location(Bukkit.getWorld(worldName), -141.0, 134.0, -219.0), //corner2 coords (neg, grass)
            Location(Bukkit.getWorld(worldName), -116.985, 135.0, -222.906, 0f, 0f), //grass teleport loc
            Location(Bukkit.getWorld(worldName), -116.985, 135.0, -168.133, -180f, 0f), //mycelium teleport loc
            mutableListOf(
                PhaseParameters(30, Phase.GRASS, 0, 0, 0),
                PhaseParameters(40, Phase.CONFLICTS, 0, 0, 0),

                PhaseParameters(20, Phase.GRASS, 1, 2, 0),
                PhaseParameters(40, Phase.CONFLICTS, 0, 0, 0),

                PhaseParameters(20, Phase.GRASS, 2, 3, 0),
                PhaseParameters(40, Phase.CONFLICTS, 0, 1, 0),

                PhaseParameters(20, Phase.GRASS, 2, 3, 0),
                PhaseParameters(40, Phase.CONFLICTS, 0, 1, 20),

                PhaseParameters(20, Phase.GRASS, 3, 4, 0),
                PhaseParameters(40, Phase.CONFLICTS, 1, 2, 20),

                PhaseParameters(20, Phase.GRASS, 3, 4, 0),
                PhaseParameters(40, Phase.CONFLICTS, 2, 2, 20),

                PhaseParameters(15, Phase.GRASS, 3, 4, 0),
                PhaseParameters(40, Phase.CONFLICTS, 2, 3, 20),

                PhaseParameters(15, Phase.GRASS, 3, 4, 0),
                PhaseParameters(40, Phase.CONFLICTS, 2, 3, 20),

                PhaseParameters(15, Phase.GRASS, 3, 4, 0),
                PhaseParameters(40, Phase.CONFLICTS, 2, 3, 20),

                PhaseParameters(15, Phase.GRASS, 3, 4, 0),
                PhaseParameters(40, Phase.CONFLICTS, 2, 3, 20),
                ),
            mutableListOf( //77,63,63,63
                LaneChangeParameters(77, 1), LaneChangeParameters(63, 2),
                LaneChangeParameters(63, 3), LaneChangeParameters(63, 5)),
            2, 45, 30, 64, 45, 30, 20,
            2, 5, 146
        )

        val gameManager = GameManager(Bukkit.getOnlinePlayers().toMutableList(), gameParameters).startTeamSelection()
        return true
    }
}