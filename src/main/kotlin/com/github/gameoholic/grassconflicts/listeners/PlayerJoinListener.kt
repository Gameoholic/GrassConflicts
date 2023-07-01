package com.github.gameoholic.grassconflicts.listeners

import com.github.gameoholic.grassconflicts.GrassConflicts
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Duration

object PlayerJoinListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
//        GrassConflicts.packetManager.injectPlayer(e.player)
        if (GrassConflicts.gameManagers.size == 0) return

        e.player.sendTitlePart(
            TitlePart.TIMES, Title.Times.times
            (Duration.ofMillis(200), Duration.ofMillis(4000), Duration.ofMillis(200)))
        e.player.sendTitlePart(
            TitlePart.TITLE,
            Component.empty())
        e.player.sendTitlePart(
            TitlePart.SUBTITLE,
            Component.text("You're spectating the game.").color(NamedTextColor.YELLOW))

        e.player.sendMessage(Component.text("[!] A game is currently in progress.").color(NamedTextColor.RED)
            .append(Component.text("\nYou'll be able to join when it ends.").color(NamedTextColor.GREEN)))
        e.player.gameMode = GameMode.SPECTATOR
        e.player.teleport(GrassConflicts.gameManagers[0].gamePlayers[0].player.location)
    }


}