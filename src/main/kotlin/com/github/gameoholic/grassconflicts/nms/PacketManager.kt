package com.github.gameoholic.grassconflicts.nms
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

abstract class PacketManager {
    abstract var clientboundTeleportEntityPacketWrapper: ClientboundTeleportEntityPacketWrapper
    abstract var serverboundGeneralPlayerMovementPacketWrapper: ServerboundGeneralPlayerMovementPacketWrapper
    abstract fun injectPlayer(player: Player)
    abstract fun removePlayer(player: Player)
        fun test(targetPlayers: MutableList<Player>, player: Player, location: Location) {
        val entityId = player.entityId
        val x = location.x
        val y = location.y
        val z = location.z
        val yRot = location.yaw.toInt().toByte()
        val xRot = location.pitch.toInt().toByte()
        val onGround = true

        val byteBuf = FriendlyByteBuf(Unpooled.buffer())
        byteBuf.writeVarInt(entityId)
        byteBuf.writeDouble(x)
        byteBuf.writeDouble(y)
        byteBuf.writeDouble(z)
        byteBuf.writeByte(yRot.toInt())
        byteBuf.writeByte(xRot.toInt())
        byteBuf.writeBoolean(onGround)

        var teleportEntityPacket = ClientboundTeleportEntityPacket(byteBuf)

        for (targetPlayer in targetPlayers) {
            var craftPlayer: CraftPlayer = targetPlayer as CraftPlayer
            val serverPlayer = craftPlayer.handle
            val listener = serverPlayer.connection
            listener.send(teleportEntityPacket)
    }
}
}