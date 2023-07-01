package com.github.gameoholic.grassconflicts.nms.v1_19_R3

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.gamemanager.GCPlayer
import com.github.gameoholic.grassconflicts.nms.ClientboundTeleportEntityPacketWrapper
import com.github.gameoholic.grassconflicts.nms.ServerboundGeneralPlayerMovementPacketWrapper
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class ClientboundTeleportEntityPacketWrapper: ClientboundTeleportEntityPacketWrapper(){

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


//override fun send(targetPlayers: MutableList<Player>, player: Player, location: Location) {
//    val entityId = player.entityId
//    val x = location.x
//    val y = location.y
//    val z = location.z
//    val yRot = location.yaw.toInt().toByte()
//    val xRot = location.pitch.toInt().toByte()
//    val onGround = true
//
//    val byteBuf = FriendlyByteBuf(Unpooled.buffer())
//    byteBuf.writeVarInt(entityId)
//    byteBuf.writeDouble(x)
//    byteBuf.writeDouble(y)
//    byteBuf.writeDouble(z)
//    byteBuf.writeByte(yRot.toInt())
//    byteBuf.writeByte(xRot.toInt())
//    byteBuf.writeBoolean(onGround)
//
//    var teleportEntityPacket = ClientboundTeleportEntityPacket(byteBuf)
//
//    for (targetPlayer in targetPlayers) {
//        var craftPlayer: CraftPlayer = targetPlayer as CraftPlayer
//        val serverPlayer = craftPlayer.handle
//        val listener = serverPlayer.connection
//        listener.send(teleportEntityPacket)
//    }
//}