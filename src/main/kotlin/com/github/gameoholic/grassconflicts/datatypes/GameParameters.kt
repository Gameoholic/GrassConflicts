package com.github.gameoholic.grassconflicts.datatypes

import org.bukkit.Location

//Corner1coords: positive x,z coords of mycelium block
//Cornerr2coords: negative x z coords of grass block
//gameStartSkipSeconds: How many seconds of the start to skip
//buildheightlimit: cant build ABOVE this limit
data class GameParameters(val gameStartSkipSeconds: Int, val corner1Coords: Location, val corner2Coords: Location,
                          val grassTeleportLocation: Location,
                          val myceliumTeleportLocation: Location, val phases: MutableList<PhaseParameters>,
                          val laneChanges: MutableList<LaneChangeParameters>, val respawnTime: Int,
                            val startingBlocks: Int, val startingArrows: Int, val maxBlocks: Int, val maxArrows: Int,
    val blocksPerGrassPhase: Int, val arrowsPerGrassPhase: Int, val killArrows: Int, val killBlocks: Int, val buildHeightLimit: Int)
