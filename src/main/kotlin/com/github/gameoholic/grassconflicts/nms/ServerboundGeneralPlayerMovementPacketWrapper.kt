package com.github.gameoholic.grassconflicts.nms

import net.minecraft.network.protocol.Packet
import org.bukkit.entity.Player

abstract class ServerboundGeneralPlayerMovementPacketWrapper {
    //Returns: Whether should block packet or not
    abstract fun handle(packet: Any, player: Player): Boolean

}