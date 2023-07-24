package com.github.gameoholic.grassconflicts.commands

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDrop
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*


object TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player)
            if (!sender.hasPermission("grassconflicts.test"))
                return true

        val location = (sender as Player).location
        location.y += 10
        AirDrop(location.toCenterLocation(), Team.MYCELIUM)


        val material = Material.STICK
        val itemStack = ItemStack(material, 1)

        val meta = itemStack.itemMeta
        meta.displayName(Component.text("מקל הקסמים").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
        meta.lore(mutableListOf(
            Component.text("קולקציית הכישופים").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("#נדיר").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
            Component.text("#בדיקה").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("פריט זה מומן על ידי ").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(sender.name).color(NamedTextColor.DARK_RED)),
            Component.text("3/7/2023").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            )
        )

        itemStack.itemMeta = meta

        sender.inventory.addItem(itemStack)

        return true
    }




}