package com.github.gameoholic.grassconflicts.gamemanager.airdrops

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.GameParameters
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.util.VectorInt
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.block.Chest
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class AirDrop(location: Location, team: Team) {

    val blockDisplay: BlockDisplay
    val armorStand: ArmorStand
    private val chickens = mutableListOf<Chicken>()
    private val team: Team
    var particleIdleTask: ParticleIdleTask? = null
    var chest: Block? = null
        private set

    init {
        airDrops.add(this)
        this.team = team
        var armorStandLocation = location.toCenterLocation().clone()
        armorStandLocation.x += 0.05
        armorStandLocation.z += 0.05
        armorStand = armorStandLocation.world.spawnEntity(armorStandLocation, EntityType.ARMOR_STAND) as ArmorStand
        armorStand.setGravity(false)
        armorStand.isInvulnerable = true
        armorStand.isMarker = true
        armorStand.isInvisible = true

        var blockLocation = location.toCenterLocation().clone()
        blockLocation.x -= 0.5
        blockLocation.z -= 0.5
        blockDisplay = location.world.spawnEntity(blockLocation, EntityType.BLOCK_DISPLAY) as BlockDisplay
        blockDisplay.setGravity(false)
        blockDisplay.block = Material.CHEST.createBlockData()

        val chickenLocations: MutableList<Location> = ArrayList()
        val rnd = Random()
        val radius = rnd.nextFloat(5f) + 1.5
        for (i in 0..2) {
            val angle = 2 * Math.PI * i / 3
            val circleX = radius / 2 * cos(angle)
            val circleZ = radius / 2 * sin(angle)
            val newLocation: Location = location.clone()
            newLocation.x = newLocation.x + circleX
            newLocation.z = newLocation.z + circleZ
            chickenLocations.add(newLocation)
        }
        for (i in 0..2) {
            var chickenLoc = chickenLocations[i]
            chickenLoc.y = chickenLoc.y + 8
            val chicken = chickenLoc.world.spawnEntity(chickenLoc, EntityType.CHICKEN) as Chicken
            chicken.isInvulnerable = true
            chicken.setLeashHolder(armorStand)
            chickens.add(chicken)
        }

        AirDropTask(this)
    }

    fun onAirDropLand(animationPercentage: Double) {
        val chestLoc = blockDisplay.location
        armorStand.remove()
        blockDisplay.remove()
        for (chicken in chickens) chicken.remove()

        val blockData = Material.CHEST.createBlockData() as Directional
        blockData.facing = BlockFace.SOUTH
        GrassConflicts.world.getBlockAt(chestLoc).blockData = blockData
        chest = chestLoc.block
        generateChestLoot((chest!!.state as Chest))

        particleIdleTask = ParticleIdleTask(animationPercentage, this)
    }

    private fun generateChestLoot(chest: Chest) {
        val rnd = Random()
        val chestItems = mutableListOf<ItemStack>()

        val commonItemAmount = rnd.nextInt(5) + 0 //0-4 items
        for (i in 1..commonItemAmount)
            chestItems.add(AirDropItems.generateItemStackFromSelection(AirDropItems.commonItems, team, rnd))

        val rareItemAmount = rnd.nextInt(3) + 1 //1-3 items
        for (i in 1..rareItemAmount) {
            var itemStack = AirDropItems.generateItemStackFromSelection(AirDropItems.rareItems, team, rnd)
            for (j in (1..rnd.nextInt(2) + 1)) { //Make rare items of the same type appear multiple times (1-2 times)
                val unstackableKey = NamespacedKey(GrassConflicts.plugin, "random") //make unstackable
                var newItemStack = itemStack.clone()
                if (newItemStack.itemMeta.persistentDataContainer.get(unstackableKey, PersistentDataType.STRING) != null) {
                    var newMeta = newItemStack.itemMeta
                    newMeta.persistentDataContainer.set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString())
                    newItemStack.itemMeta = newMeta
                    itemStack = newItemStack
                }
                chestItems.add(itemStack)
            }
        }


        //Generate random chest item indexes:
        val chestIndexes: MutableSet<Int> = HashSet() //set guarantees they're unique

        while (chestIndexes.size != chestItems.size) {
            chestIndexes.add(rnd.nextInt(27))
        }

        chest.customName(Component.text("Air Drop").color(NamedTextColor.GOLD))
        chest.update()

        //Put items in chest:
        for ((index, itemStack) in chestItems.withIndex()) {
            chest.blockInventory.setItem(chestIndexes.toTypedArray()[index], itemStack)
        }
    }

    companion object {
        var airDrops = mutableListOf<AirDrop>()
        fun spawnOnBothSides(gameParameters: GameParameters, borderZCoordinate: Int) {
            val rnd = Random()
            var myceliumCorner1 = //pos
                VectorInt(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, gameParameters.corner1Coords.blockZ)
            var myceliumCorner2 = //neg
                VectorInt(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, borderZCoordinate + 1)
            var grassCorner1 = //neg
                VectorInt(gameParameters.corner2Coords.blockX, gameParameters.corner2Coords.blockY, gameParameters.corner2Coords.blockZ)
            var grassCorner2 = //pos
                VectorInt(gameParameters.corner1Coords.blockX, gameParameters.corner1Coords.blockY, borderZCoordinate - 1)

            try {
                AirDrop(Location(GrassConflicts.world,
                    (rnd.nextInt(myceliumCorner1.x - myceliumCorner2.x) + myceliumCorner2.x).toDouble(),
                    (gameParameters.buildHeightLimit + 2).toDouble(),
                    (rnd.nextInt(myceliumCorner1.z - myceliumCorner2.z) + myceliumCorner2.z).toDouble()
                ), Team.MYCELIUM)
                AirDrop(Location(GrassConflicts.world,
                    (rnd.nextInt(grassCorner2.x - grassCorner1.x) + grassCorner1.x).toDouble(),
                    (gameParameters.buildHeightLimit + 2).toDouble(),
                    (rnd.nextInt(grassCorner2.z - grassCorner1.z) + grassCorner1.z).toDouble()
                ), Team.GRASS)
            }
            catch (e: Exception) {
                //for soem reason this gets run even after game ends, so fuck it just catching it for now
            }

        }
    }
}