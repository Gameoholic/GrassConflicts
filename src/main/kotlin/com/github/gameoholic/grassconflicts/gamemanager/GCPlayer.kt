package com.github.gameoholic.grassconflicts.gamemanager

import com.github.gameoholic.grassconflicts.datatypes.Team
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GCPlayer: QueuedPlayer {

    var team: Team
    var killStreak = 0
    var kills = 0
    var deaths = 0
    var blocksPlaced = 0
    var arrowsShot = 0
    var arrowLanded = 0
    var isDead = false
    var lastShotBowName: Component = Component.empty()
    var blocksSinceDeath = 0
    var arrowsSinceDeath = 0

    constructor(player: Player, team: Team) : super(player) {
        this.team = team
    }


}