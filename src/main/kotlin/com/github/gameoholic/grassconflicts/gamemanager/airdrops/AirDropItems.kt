package com.github.gameoholic.grassconflicts.gamemanager.airdrops

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.Team
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.apache.commons.lang3.Conversion.byteToHex
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.persistence.PersistentDataType
import java.nio.charset.Charset
import java.security.InvalidParameterException
import java.util.*


object AirDropItems {
    val commonItems = arrayOf(
        AirDropItem(AirDropItemType.BUILDING_BLOCK, 10, 45),
        AirDropItem(AirDropItemType.ARROW, 5, 30)
    )

    val rareItems = arrayOf(
        AirDropItem(AirDropItemType.STEFAN, 1, 1),
        AirDropItem(AirDropItemType.STEFAN_BOW, 1, 1),
        AirDropItem(AirDropItemType.CHEESIER_PASTA, 1, 1),
        AirDropItem(AirDropItemType.BAGUETTE, 1, 1),
        AirDropItem(AirDropItemType.GUN, 1, 1),
        AirDropItem(AirDropItemType.BIGGER_GUN, 1, 1),
        AirDropItem(AirDropItemType.INSTA_WALL, 2, 4)
    )

    val powerupItems = arrayOf(
        AirDropItem(AirDropItemType.ONESHOT_POWERUP, 1, 1),
        AirDropItem(AirDropItemType.SPLEEF_POWERUP, 1, 1),
        AirDropItem(AirDropItemType.USA_POWERUP, 1, 1)
    )

    fun generateItemStackFromSelection(selection: Array<AirDropItem>, team: Team, rnd: Random): ItemStack {
        val item = selection[rnd.nextInt(selection.size)]
        val quantity = rnd.nextInt(item.maxCount) + item.minCount
        when (item.airDropItemType) {
            AirDropItemType.ARROW ->
                return getArrowItemStack(team, quantity)
            AirDropItemType.BUILDING_BLOCK ->
                return genBuildingBlockItemStack(team, quantity)
            AirDropItemType.STEFAN ->
                return genStefanItemStack(quantity, rnd)
            AirDropItemType.CHEESIER_PASTA ->
                return genCheesierPasta(quantity)
            AirDropItemType.BAGUETTE ->
                return genBaguette(quantity, rnd)
            AirDropItemType.STEFAN_BOW ->
                return genStefanBowItemStack(quantity, rnd)
            AirDropItemType.GUN ->
                return genGunItemStack(quantity, rnd)
            AirDropItemType.BIGGER_GUN ->
                return genBiggerGunItemStack(quantity, rnd)
            AirDropItemType.INSTA_WALL ->
                return genInstaWallItemStack(quantity)
            else ->
                throw InvalidParameterException("Unknown air drop item type " + item.airDropItemType)
        }
    }

    private fun getArrowItemStack(team: Team, quantity: Int): ItemStack {
        val material = Material.ARROW
        val itemStack = ItemStack(material, quantity)
        return itemStack
    }

    private fun genBuildingBlockItemStack(team: Team, quantity: Int): ItemStack {
        val material = team.material
        val itemStack = ItemStack(material, quantity)
        return itemStack
    }
    private fun genStefanItemStack(quantity: Int, rnd: Random): ItemStack {
        val material = Material.SLIME_BALL
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta
        meta.displayName(Component.text("Stefan Noxite Panić").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
        meta.lore(mutableListOf(
            Component.text("Knockback V").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
            Component.text("Sharpness X").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
            Component.text("Uses Left: 15").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true))
        )

        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        meta.persistentDataContainer.set(key, PersistentDataType.SHORT, 15)
        meta.addEnchant(Enchantment.KNOCKBACK, 5, true)
        meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        val unstackableKey = NamespacedKey(GrassConflicts.plugin, "random") //make unstackable
        meta.persistentDataContainer.set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        itemStack.itemMeta = meta

        return itemStack
    }
    private fun genStefanBowItemStack(quantity: Int, rnd: Random): ItemStack {
        val material = Material.BOW
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta
        meta.isUnbreakable = true
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        meta.displayName(Component.text("Stefan Noxite Panić").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
        meta.lore(mutableListOf(
            Component.text("Punch V").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
            Component.text("Power I").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
            Component.text("Uses Left: 15").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true))
        )
        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        meta.persistentDataContainer.set(key, PersistentDataType.SHORT, 15)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 5, true)
        val unstackableKey = NamespacedKey(GrassConflicts.plugin, "random") //make unstackable
        meta.persistentDataContainer.set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        itemStack.itemMeta = meta

        return itemStack
    }
    private fun genCheesierPasta(quantity: Int): ItemStack {
        val material = Material.END_STONE
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta
        meta.displayName(Component.text("Cheesier Pasta").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.lore(mutableListOf(
            Component.text("Click to summon ").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Thor").color(NamedTextColor.RED)
                    .append(Component.text(", the God of Thunder.").color(NamedTextColor.YELLOW))
                ).decoration(TextDecoration.ITALIC, false),
            Component.text("One time use.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true)
        ))

        itemStack.itemMeta = meta

        return itemStack
    }
    private fun genBaguette(quantity: Int, rnd: Random): ItemStack {
        val material = Material.BREAD
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta
        meta.displayName(Component.text("Baguette").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.addEnchant(Enchantment.DAMAGE_ALL, 20, true)
        meta.lore(mutableListOf(
            Component.text("Sharpness XX").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("Uses Left: 10").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true))
        )
        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        meta.persistentDataContainer.set(key, PersistentDataType.SHORT, 10)
        val unstackableKey = NamespacedKey(GrassConflicts.plugin, "random") //make unstackable
        meta.persistentDataContainer.set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        itemStack.itemMeta = meta

        return itemStack
    }
    private fun genGunItemStack(quantity: Int, rnd: Random): ItemStack {
        val material = Material.CROSSBOW
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta as CrossbowMeta
        meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
        meta.isUnbreakable = true
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.displayName(Component.text("Gun").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
        meta.lore(mutableListOf(
            Component.text("The greatest nation in the world!").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
            Component.text("Arrows Left: 15").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true))
        )
        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        meta.persistentDataContainer.set(key, PersistentDataType.SHORT, 15)
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true)
        val unstackableKey = NamespacedKey(GrassConflicts.plugin, "random") //make unstackable
        meta.persistentDataContainer.set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        itemStack.itemMeta = meta

        return itemStack
    }
    private fun genBiggerGunItemStack(quantity: Int, rnd: Random): ItemStack {
        val material = Material.CROSSBOW
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta as CrossbowMeta
        meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
        meta.isUnbreakable = true
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.displayName(Component.text("Bigger Gun").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
        meta.lore(mutableListOf(
            Component.text("U-S-A! U-S-A!").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
            Component.text("Arrows Left: 30").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true))
        )
        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        meta.persistentDataContainer.set(key, PersistentDataType.SHORT, 30)
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true)
        val unstackableKey = NamespacedKey(GrassConflicts.plugin, "random") //make unstackable
        meta.persistentDataContainer.set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        itemStack.itemMeta = meta

        return itemStack
    }
    private fun genInstaWallItemStack(quantity: Int): ItemStack {
        val material = Material.SNOW_BLOCK
        val itemStack = ItemStack(material, quantity)

        val meta = itemStack.itemMeta
        meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.displayName(Component.text("Insta Wall").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
        meta.lore(mutableListOf(
            Component.text("Literally fortnite").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
            Component.text("One time use.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true))
        )
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true)
        itemStack.itemMeta = meta

        return itemStack
    }
}