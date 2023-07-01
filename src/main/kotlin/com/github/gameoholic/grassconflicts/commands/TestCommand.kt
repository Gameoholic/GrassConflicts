package com.github.gameoholic.grassconflicts.commands

import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDrop
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        val location = (sender as Player).location
        location.y += 10
        AirDrop(location.toCenterLocation(), Team.MYCELIUM)



        return true
    }




}