package com.github.gameoholic.grassconflicts.nms.v1_19_R3

import com.github.gameoholic.grassconflicts.nms.PacketManager
import com.github.gameoholic.grassconflicts.nms.ClientboundTeleportEntityPacketWrapper
import com.github.gameoholic.grassconflicts.nms.ServerboundGeneralPlayerMovementPacketWrapper
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket
import org.bukkit.Bukkit

class PacketManager: PacketManager() {

    override var clientboundTeleportEntityPacketWrapper: ClientboundTeleportEntityPacketWrapper =
        com.github.gameoholic.grassconflicts.nms.v1_19_R3.ClientboundTeleportEntityPacketWrapper()

    override var serverboundGeneralPlayerMovementPacketWrapper: ServerboundGeneralPlayerMovementPacketWrapper =
        com.github.gameoholic.grassconflicts.nms.v1_19_R3.ServerboundGeneralPlayerMovementPacketWrapper()


    override fun injectPlayer(player: Player) {
        val channelDuplexHandler: ChannelDuplexHandler = object : ChannelDuplexHandler() {
            //Serverbound:
            @Throws(Exception::class)
            override fun channelRead(channelHandlerContext: ChannelHandlerContext, packet: Any) {
//                if (packet is ServerboundMovePlayerPacket || packet is ServerboundPlayerInputPacket ||
//                    packet is ServerboundPlayerAbilitiesPacket) {
//                    if (serverboundGeneralPlayerMovementPacketWrapper.handle(packet, player))
//                        return
//                }
                super.channelRead(channelHandlerContext, packet)
            }

            //Clientbound:
            @Throws(Exception::class)
            override fun write(
                channelHandlerContext: ChannelHandlerContext,
                packet: Any,
                channelPromise: ChannelPromise
            )
            {
                super.write(channelHandlerContext, packet, channelPromise)
            }
        }
        val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler)
    }

    override fun removePlayer(player: Player) {
        val channel = (player as CraftPlayer).handle.connection.connection.channel
        channel.eventLoop().submit<Any?> {
            channel.pipeline().remove(player.getName())
            null
        }
    }


}