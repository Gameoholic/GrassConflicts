package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.gamemanager.GCFallingBlock
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object PlayerDamageListener : Listener {

    @EventHandler
    fun onEntityDamage(e: EntityDamageEvent) {
        if (e.entity !is Player) return
        val player: Player = e.entity as Player

        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        } ?: return

        var gcPlayer: GCPlayer = gameManager.gamePlayers.firstOrNull {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}!!

        var damagerPlayer: Player? = null
        var killedByThor = false
        var killedByArrow = false

        when (e.cause) {
            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> {
                if (e is EntityDamageByEntityEvent) {
                    when (val damager = e.damager) {
                        is Player -> {
                            damagerPlayer = damager
                        }
                    }
                }
            } // curly bracket mess, I know, this plguin was made in less than a week give me a break ;p

            EntityDamageEvent.DamageCause.PROJECTILE -> {
                if (e is EntityDamageByEntityEvent) {
                    when (val damager = e.damager) {
                        is Arrow -> {
                            val shooter = damager.shooter
                            if (shooter is Player) {
                                damagerPlayer = shooter
                                killedByArrow = true
                            }
                            damager.remove()
                        }
                    }
                }
            }

            EntityDamageEvent.DamageCause.FALL -> {
                e.isCancelled = true
                return
            }

            EntityDamageEvent.DamageCause.LIGHTNING -> {
                damagerPlayer = gameManager.specialItemManager.lastPlayerThorUsage?.player
                killedByThor = true
            }

            EntityDamageEvent.DamageCause.FIRE -> {
                damagerPlayer = gameManager.specialItemManager.lastPlayerThorUsage?.player
                killedByThor = true
            }

            else -> {
                //To get player's death cause here can use  e.entity.lastDamageCause
            }
        }



        if (damagerPlayer != null) {
            var gcDamagerPlayer: GCPlayer? = gameManager.gamePlayers
                .firstOrNull {gcPlayer -> gcPlayer.player.uniqueId == damagerPlayer.uniqueId}
            if (gcDamagerPlayer != null && (gcDamagerPlayer.isDead || gcDamagerPlayer.team == gcPlayer.team)) {
                e.isCancelled = true
                return
            }
        }

        if (gameManager.phase == Phase.GRASS || gcPlayer.isDead)
        {
            e.isCancelled = true
            return
        }

        if (player.health - e.damage > 0) //Fix bug where kill message shows up as Air cause item is removed early
            if (damagerPlayer != null)  {
                updateMainHandLore(damagerPlayer) //Update special item durability
                var damagerGC: GCPlayer? = gameManager.gamePlayers.firstOrNull {gcPlayer -> gcPlayer.player.uniqueId == damagerPlayer?.uniqueId}
                if (damagerGC != null)
                    damagerGC.arrowLanded++
            }

        //We return this late because we want to cancel the damage event if the damager is dead
        if (player.health - e.damage > 0) return

        var killer: GCPlayer? = gameManager.gamePlayers.firstOrNull {gcPlayer -> gcPlayer.player.uniqueId == damagerPlayer?.uniqueId}

        for (i in 1..gcPlayer.killStreak)
            GCFallingBlock(gcPlayer, GrassConflicts.world)

        //Give items to killer if needed:
        if (killer != null) {
            var arrows = 0
            for (itemStack in killer.player.inventory.contents) {
                if (itemStack != null && itemStack.type == Material.ARROW)
                    arrows += itemStack.amount
            }
            var blocks = 0
            for (itemStack in killer.player.inventory.contents) {
                if (itemStack != null && itemStack.type == killer.team.material)
                    blocks += itemStack.amount
            }
            if (arrows < gameManager.gameParameters.maxArrows)
                killer.player.inventory.addItem(
                    ItemStack(Material.ARROW,
                        Integer.min(gameManager.gameParameters.maxArrows - arrows, gameManager.gameParameters.killArrows))
                )

            if (blocks < gameManager.gameParameters.maxBlocks)
                killer.player.inventory.addItem(
                    ItemStack(killer.team.material,
                        Integer.min(gameManager.gameParameters.maxBlocks - blocks, gameManager.gameParameters.killBlocks))
                )
        }

        gcPlayer.deaths += 1
        if (gameManager.gameParameters.respawnTime > 0) //fix bug where for resapwnTime = 0 the player would remain hidden
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.uniqueId != gcPlayer.player.uniqueId)
                    onlinePlayer.hidePlayer(GrassConflicts.plugin, gcPlayer.player)
            }
        gcPlayer.isDead = true
        gcPlayer.player.allowFlight = true
        gcPlayer.player.isFlying = true
        gcPlayer.player.isInvulnerable = true
        var invBlocks = 0
        var invArrows = 0
        for (item in gcPlayer.player.inventory) {
            if (item == null) continue
            if (item.type == Material.MYCELIUM || item.type == Material.GRASS_BLOCK) {
                invBlocks += item.amount
            }
            if (item.type == Material.ARROW)
                invArrows += item.amount
        }
        gcPlayer.arrowsSinceDeath = invArrows
        gcPlayer.blocksSinceDeath = invBlocks
        gcPlayer.player.inventory.clear()
        gcPlayer.player.health = gcPlayer.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.defaultValue
        gcPlayer.player.playSound(
            Sound.sound(Key.key("minecraft:entity.player.death"), Sound.Source.MASTER, 1.0f, 1.0f))


        if (killer != null) {
            gcPlayer.player.sendActionBar(Component.text("Killed by ").color(NamedTextColor.RED)
                .append(gcPlayer.player.name().color(killer.team.textColor)))
            var itemName = PlainTextComponentSerializer.plainText().serialize(killer.player.inventory.itemInMainHand.displayName())
            var itemColor = killer.player.inventory.itemInMainHand.displayName().color()
            if (killedByArrow) {
                itemName = PlainTextComponentSerializer.plainText().serialize(killer.lastShotBowName)
                itemColor = killer.lastShotBowName.color()
            }
            itemName = itemName.substring(1, itemName.length - 1)


            var msgComponent = Component.text("${player.name}").color(gcPlayer.team.textColor)
                .append(Component.text(" was killed by ").color(NamedTextColor.YELLOW))
                .append(Component.text("${killer.player.name}").color(killer.team.textColor))
                .append(Component.text(" using ").color(NamedTextColor.YELLOW))
                .append(Component.text(itemName).color(itemColor))
            if (killedByThor)
                msgComponent = Component.text("${player.name}").color(gcPlayer.team.textColor)
                    .append(Component.text(" was killed by Thor, with the help of ").color(NamedTextColor.YELLOW))
                    .append(Component.text("${killer.player.name}").color(killer.team.textColor))

            if (gcPlayer.killStreak > 4)
                msgComponent = msgComponent.append(Component.text(". They had a kill streak of ").color(NamedTextColor.YELLOW)
                    .append(Component.text(gcPlayer.killStreak).color(NamedTextColor.RED))
                    .append(Component.text("!").color(NamedTextColor.YELLOW)))

            Bukkit.broadcast(msgComponent)

            killer.player.playSound(
                Sound.sound(Key.key("minecraft:block.note_block.guitar"), Sound.Source.MASTER, 1.0f, 2.0f))
            killer.kills += 1
            killer.killStreak += 1
            killer.player.health = gcPlayer.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.defaultValue
            killer.player.sendActionBar(Component.text("Killed ").color(NamedTextColor.RED)
                .append(gcPlayer.player.name().color(gcPlayer.team.textColor)))
            if (killer.killStreak > 4) {
                Bukkit.broadcast(Component.text("${killer.player.name}").color(gcPlayer.team.textColor)
                    .append(Component.text(" is on a killstreak of ").color(NamedTextColor.YELLOW))
                    .append(Component.text(killer.killStreak).color(NamedTextColor.RED))
                    .append(Component.text("!").color(NamedTextColor.YELLOW)))
            }
        }
        else
            Bukkit.broadcast(Component.text("${player.name}").color(gcPlayer.team.textColor)
                .append(Component.text(" died..?").color(NamedTextColor.YELLOW)))

        gcPlayer.killStreak = 0
        gameManager.onPlayerDeath(gcPlayer)

        if (damagerPlayer != null) updateMainHandLore(damagerPlayer) //Update special item durability.

        e.isCancelled = true
    }

    private fun updateMainHandLore(damager: Player) {
        val meta = damager.inventory.itemInMainHand.itemMeta ?: return
        var itemName = PlainTextComponentSerializer.plainText().serialize(damager.inventory.itemInMainHand.displayName())
        itemName = itemName.substring(1, itemName.length - 1)
        if (itemName == "Gun" || itemName == "Stefan Noxite Panić" || itemName == "Bigger Gun") return
        val dataContainer = meta.persistentDataContainer
        val key = NamespacedKey(GrassConflicts.plugin, "durability")
        val durability: Short = dataContainer.get(key, PersistentDataType.SHORT) ?: return
        dataContainer.set(key, PersistentDataType.SHORT, (durability - 1).toShort())
        var newLore = meta.lore()!!
        newLore[meta.lore()!!.size - 1] = Component.text("Uses left: ${durability-1}")
            .color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true)
        meta.lore(newLore)
        damager.inventory.itemInMainHand.itemMeta = meta
        if (durability <= 1)
            damager.inventory.remove(damager.inventory.itemInMainHand)
    }


}