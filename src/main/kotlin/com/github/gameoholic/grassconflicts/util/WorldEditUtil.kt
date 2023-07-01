package com.github.gameoholic.grassconflicts.util

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.GameParameters
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import java.lang.Math.abs

object WorldEditUtil {

    fun fillCuboid(cuboid: CuboidRegion, material: Material) {
        val region: Region = cuboid.boundingBox
        for (block: BlockVector3 in region) {
            val loc = Location(GrassConflicts.world, block.blockX.toDouble(), block.blockY.toDouble(), block.blockZ.toDouble())
            loc.block.type = material
        }
        return
    }

    fun clearAndPrepareMap(gameParameters: GameParameters, borderZCoordinate: Int) {
        var region = CuboidRegion(BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, gameParameters.corner1Coords.blockZ),
            BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.buildHeightLimit, gameParameters.corner2Coords.blockZ))
        fillCuboid(region, Material.AIR)

        var borderRegion = CuboidRegion(BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, borderZCoordinate),
            BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, borderZCoordinate))
        fillCuboid(borderRegion, Material.CRIMSON_NYLIUM)

        var myceliumRegion = CuboidRegion(BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, gameParameters.corner1Coords.blockZ),
            BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, borderZCoordinate + 1))
        fillCuboid(myceliumRegion, Material.MYCELIUM)

        var grassRegion = CuboidRegion(BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, gameParameters.corner2Coords.blockZ),
            BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, borderZCoordinate - 1))
        fillCuboid(grassRegion, Material.GRASS_BLOCK)
    }

    fun moveBorder(material: Material, borderZCoordinate: Int, borderZChangeParameter: Int, gameParameters: GameParameters) {
        var borderZChange = borderZChangeParameter

        //Fix border going out of map bounds for lane changes higher than 1:
        if (borderZCoordinate + borderZChange > gameParameters.corner1Coords.z)
            borderZChange -= borderZCoordinate + borderZChange - gameParameters.corner1Coords.z.toInt()
        else if (borderZCoordinate + borderZChange < gameParameters.corner2Coords.z)
            borderZChange += gameParameters.corner2Coords.z.toInt() - borderZCoordinate - borderZChange

        var region = CuboidRegion(BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, borderZCoordinate),
            BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, borderZCoordinate + borderZChange - 1 * (borderZChange/kotlin.math.abs(borderZChange))))
        fillCuboid(region, material)

        var airRegion = CuboidRegion(BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY + 1, borderZCoordinate + 1 * (borderZChange/kotlin.math.abs(borderZChange))),
            BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.buildHeightLimit, borderZCoordinate + borderZChange))
        fillCuboid(airRegion, Material.AIR)

        var borderRegion = CuboidRegion(BlockVector3.at(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, borderZCoordinate + borderZChange),
            BlockVector3.at(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, borderZCoordinate + borderZChange))
        fillCuboid(borderRegion, Material.CRIMSON_NYLIUM)
    }
}