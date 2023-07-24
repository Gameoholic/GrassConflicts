package com.github.gameoholic.grassconflicts.util

import com.github.gameoholic.grassconflicts.datatypes.GameParameters
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.lang.Integer.min

object ItemsUtil {
    fun setTeamSelectionItems(player: Player, grassPlayersSize: Int, myceliumPlayersSize: Int) {
        val grass = ItemStack(Material.GRASS_BLOCK, 1)
        val grassMeta = grass.itemMeta
        grassMeta.displayName(
            Component.text("Grass - $grassPlayersSize").color(Team.GRASS.textColor).decoration(TextDecoration.ITALIC, false))
        grass.itemMeta = grassMeta
        player.inventory.setItem(3, grass)

        val mycelium = ItemStack(Material.MYCELIUM, 1)
        val myceliumMeta = mycelium.itemMeta
        myceliumMeta.displayName(
            Component.text("Mycelium - $myceliumPlayersSize").color(Team.MYCELIUM.textColor).decoration(TextDecoration.ITALIC, false))
        mycelium.itemMeta = myceliumMeta
        player.inventory.setItem(5, mycelium)
    }

    fun setGameItems(player: Player, team: Team, gameParameters: GameParameters, gcPlayer: GCPlayer? = null) {
        val boots = ItemStack(Material.LEATHER_BOOTS)
        val bootsMeta = boots.itemMeta as LeatherArmorMeta
        bootsMeta.setColor(team.color)
        bootsMeta.isUnbreakable = true
        bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        bootsMeta.addItemFlags(ItemFlag.HIDE_DYE)
        boots.itemMeta = bootsMeta
        player.inventory.setItem(EquipmentSlot.FEET, boots)

        val leggings = ItemStack(Material.LEATHER_LEGGINGS)
        val leggingsMeta = leggings.itemMeta as LeatherArmorMeta
        leggingsMeta.setColor(team.color)
        leggingsMeta.isUnbreakable = true
        leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        leggingsMeta.addItemFlags(ItemFlag.HIDE_DYE)
        leggings.itemMeta = leggingsMeta
        player.inventory.setItem(EquipmentSlot.LEGS, leggings)

        val chestplate = ItemStack(Material.LEATHER_CHESTPLATE)
        val chestplateMeta = chestplate.itemMeta as LeatherArmorMeta
        chestplateMeta.setColor(team.color)
        chestplateMeta.isUnbreakable = true
        chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        chestplateMeta.addItemFlags(ItemFlag.HIDE_DYE)
        chestplate.itemMeta = chestplateMeta
        player.inventory.setItem(EquipmentSlot.CHEST, chestplate)

        val helmet = ItemStack(Material.LEATHER_HELMET)
        val helmetMeta = helmet.itemMeta as LeatherArmorMeta
        helmetMeta.setColor(team.color)
        helmetMeta.isUnbreakable = true
        helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        helmetMeta.addItemFlags(ItemFlag.HIDE_DYE)
        helmet.itemMeta = helmetMeta
        player.inventory.setItem(EquipmentSlot.HEAD, helmet)

        val sword = ItemStack(Material.IRON_SWORD, 1)
        val swordMeta = sword.itemMeta
        swordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        swordMeta.isUnbreakable = true
        sword.itemMeta = swordMeta
        player.inventory.setItem(0, sword)

        val bow = ItemStack(Material.BOW, 1)
        val bowMeta = bow.itemMeta
        bowMeta.isUnbreakable = true
        bowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        bow.itemMeta = bowMeta
        player.inventory.setItem(1, bow)

        player.inventory.setItem(2, ItemStack(Material.IRON_SHOVEL, 1))
        var blocksGivenOut = 0
        var arrowsGivenOut = 0
        if (gcPlayer == null) {
            player.inventory.setItem(3, ItemStack(team.material, gameParameters.startingBlocks))
            player.inventory.setItem(8, ItemStack(Material.ARROW, gameParameters.startingArrows))
        }
        else {
            player.inventory.setItem(3, ItemStack(team.material, min(gcPlayer.blocksSinceDeath, 64)))
            player.inventory.setItem(8, ItemStack(Material.ARROW, min(gcPlayer.arrowsSinceDeath, 64)))
            blocksGivenOut += min(gcPlayer.blocksSinceDeath, 64)
            arrowsGivenOut += min(gcPlayer.arrowsSinceDeath, 64)
            if (gcPlayer.blocksSinceDeath > blocksGivenOut)
                player.inventory.addItem(ItemStack(team.material, gcPlayer.blocksSinceDeath - blocksGivenOut))
            if (gcPlayer.arrowsSinceDeath > arrowsGivenOut)
                player.inventory.addItem(ItemStack(Material.ARROW, gcPlayer.arrowsSinceDeath - arrowsGivenOut))
        }


    }

    fun setInvulnerabilityItems(player: Player) {
        val boots = ItemStack(Material.DIAMOND_BOOTS)
        val bootsMeta = boots.itemMeta
        bootsMeta.isUnbreakable = true
        bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        boots.itemMeta = bootsMeta
        player.inventory.setItem(EquipmentSlot.FEET, boots)

        val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        val leggingsMeta = leggings.itemMeta
        leggingsMeta.isUnbreakable = true
        leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        leggings.itemMeta = leggingsMeta
        player.inventory.setItem(EquipmentSlot.LEGS, leggings)

        val chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
        val chestplateMeta = chestplate.itemMeta
        chestplateMeta.isUnbreakable = true
        chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        chestplate.itemMeta = chestplateMeta
        player.inventory.setItem(EquipmentSlot.CHEST, chestplate)

        val helmet = ItemStack(Material.DIAMOND_HELMET)
        val helmetMeta = helmet.itemMeta
        helmetMeta.isUnbreakable = true
        helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        helmet.itemMeta = helmetMeta
        player.inventory.setItem(EquipmentSlot.HEAD, helmet)
    }

    fun removeInvulnerabilityItems(player: Player, team: Team) {
        val boots = ItemStack(Material.LEATHER_BOOTS)
        val bootsMeta = boots.itemMeta as LeatherArmorMeta
        bootsMeta.setColor(team.color)
        bootsMeta.isUnbreakable = true
        bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        boots.itemMeta = bootsMeta
        player.inventory.setItem(EquipmentSlot.FEET, boots)

        val leggings = ItemStack(Material.LEATHER_LEGGINGS)
        val leggingsMeta = leggings.itemMeta as LeatherArmorMeta
        leggingsMeta.setColor(team.color)
        leggingsMeta.isUnbreakable = true
        leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        leggings.itemMeta = leggingsMeta
        player.inventory.setItem(EquipmentSlot.LEGS, leggings)

        val chestplate = ItemStack(Material.LEATHER_CHESTPLATE)
        val chestplateMeta = chestplate.itemMeta as LeatherArmorMeta
        chestplateMeta.setColor(team.color)
        chestplateMeta.isUnbreakable = true
        chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        chestplate.itemMeta = chestplateMeta
        player.inventory.setItem(EquipmentSlot.CHEST, chestplate)

        val helmet = ItemStack(Material.LEATHER_HELMET)
        val helmetMeta = helmet.itemMeta as LeatherArmorMeta
        helmetMeta.setColor(team.color)
        helmetMeta.isUnbreakable = true
        helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        helmet.itemMeta = helmetMeta
        player.inventory.setItem(EquipmentSlot.HEAD, helmet)
    }

}