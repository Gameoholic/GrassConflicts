package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector

object EntityShootBowListener : Listener {

    @EventHandler
    fun onEntityShootBowEvent(e: EntityShootBowEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player
        val item = player.inventory.itemInMainHand
        var itemName = PlainTextComponentSerializer.plainText().serialize(item.displayName())
        itemName = itemName.substring(1, itemName.length - 1)

        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        } ?: return
        var gcPlayer: GCPlayer? = null
        if (gameManager != null)
            gcPlayer = gameManager.gamePlayers.firstOrNull { gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        if (gcPlayer != null) {
            if (gcPlayer.isDead) {
                e.isCancelled = true
                return
            }
            gcPlayer.lastShotBowName = item.displayName()
        }

        if (gcPlayer != null)
            gcPlayer.arrowsShot++

        if (itemName != "Gun" && itemName != "Stefan Noxite Panić" && itemName != "Bigger Gun") return


        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            updateMainHandLore(e.entity as Player, itemName)
        }, 1L)

    }

    private fun updateMainHandLore(player: Player, itemName: String) {
        val meta = (player.inventory.itemInMainHand.itemMeta ?: return)
        val dataContainer = meta.persistentDataContainer
        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        val durability: Short = dataContainer.get(key, PersistentDataType.SHORT) ?: return
        dataContainer.set(key, PersistentDataType.SHORT, (durability - 1).toShort())
        var newLore = meta.lore()!!
        if (itemName == "Stefan Noxite Panić")
            newLore[meta.lore()!!.size - 1] = Component.text("Uses left: ${durability-1}")
                .color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true)
        else
            newLore[meta.lore()!!.size - 1] = Component.text("Arrows left: ${durability-1}")
                .color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true)
        meta.lore(newLore)
        if (meta is CrossbowMeta) {
            if (itemName == "Bigger Gun") {
                meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
                meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
                meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
            }
            else
                meta.setChargedProjectiles(mutableListOf(ItemStack(Material.ARROW, 1)))

        }
        player.inventory.itemInMainHand.itemMeta = meta
        if (durability <= 1)
            player.inventory.remove(player.inventory.itemInMainHand)
    }
}