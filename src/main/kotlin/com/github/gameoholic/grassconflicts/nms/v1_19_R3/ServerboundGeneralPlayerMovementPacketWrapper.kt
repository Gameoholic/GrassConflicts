package com.github.gameoholic.grassconflicts.nms.v1_19_R3

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.nms.ServerboundGeneralPlayerMovementPacketWrapper
import net.minecraft.network.protocol.Packet
import org.bukkit.entity.Player

class ServerboundGeneralPlayerMovementPacketWrapper: ServerboundGeneralPlayerMovementPacketWrapper() {
    override fun handle(packet: Any, player: Player): Boolean {
        var gameManager = GrassConflicts.gameManagers.firstOrNull {
                gameManager -> gameManager.gamePlayers.any {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        } ?: return false

        var gcPlayer: GCPlayer = gameManager.gamePlayers.firstOrNull { gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}!!

        if (gcPlayer.isDead)
            return true

        return false
    }


}