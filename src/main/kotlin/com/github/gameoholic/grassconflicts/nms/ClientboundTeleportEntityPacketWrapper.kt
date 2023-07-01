package com.github.gameoholic.grassconflicts.nms

import org.bukkit.Location
import org.bukkit.entity.Player

abstract class ClientboundTeleportEntityPacketWrapper {

    //Returns: Whether should block packet or not.
    //Will block packet if player is dead. Used because we teleport the player far away on death, but on their pov they need to stay where they are.
    abstract fun handle(packet: Any, player: Player): Boolean

}