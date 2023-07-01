package com.github.gameoholic.grassconflicts.gamemanager

import com.github.gameoholic.grassconflicts.datatypes.GameParameters
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDropItemType
import org.bukkit.block.Block
import org.bukkit.entity.Player

abstract class AbstractGameManager {

    var phase: Phase = Phase.TEAM_SELECTION
    var queuedPlayers: MutableList<QueuedPlayer> //players without a team yet
    val gameParameters: GameParameters

    val gamePlayers = mutableListOf<GCPlayer>()
    var borderZCoordinate: Int

    abstract val specialItemManager: SpecialItemManager
    var gameEnded = false


    constructor(players: MutableList<QueuedPlayer>, parameters: GameParameters) {
        queuedPlayers = players
        gameParameters = parameters
        borderZCoordinate = (parameters.corner2Coords.blockZ - parameters.corner1Coords.blockZ) / 2 + parameters.corner1Coords.blockZ
    }

    abstract fun startTeamSelection()

    abstract fun addPlayerToTeam(player: Player, team: Team)

    abstract fun onPlayerDeath(gcPlayer: GCPlayer)
    abstract fun onSpecialItemUsage(itemType: AirDropItemType, gcPlayer: GCPlayer, block: Block? = null)

}