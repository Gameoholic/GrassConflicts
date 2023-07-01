package com.github.gameoholic.grassconflicts.gamemanager

import com.github.gameoholic.grassconflicts.GrassConflicts
import com.github.gameoholic.grassconflicts.datatypes.GameParameters
import com.github.gameoholic.grassconflicts.datatypes.LaneChangeParameters
import com.github.gameoholic.grassconflicts.datatypes.PhaseParameters
import com.github.gameoholic.grassconflicts.enums.Phase
import com.github.gameoholic.grassconflicts.datatypes.Team
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDrop
import com.github.gameoholic.grassconflicts.gamemanager.airdrops.AirDropItemType
import com.github.gameoholic.grassconflicts.util.ItemsUtil
import com.github.gameoholic.grassconflicts.util.WorldEditUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.minecraft.util.Mth.ceil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import java.lang.Integer.min
import java.time.Duration
import java.util.*
import kotlin.math.abs
import kotlin.math.max


class GameManager : AbstractGameManager {

    private val playersAudience = Audience.audience(
    queuedPlayers.map { player ->  player.player} + gamePlayers.map { gcPlayer ->  gcPlayer.player})

    private var preGameTimerTask: BukkitTask? = null
    private var gameTimerTask: BukkitTask? = null //every 20 ticks
    private var gameTickTimerTask: BukkitTask? = null
    private var respawnCooldownTasks = mutableMapOf<UUID, BukkitTask>()

    private var grassBossBar: BossBar? = null
    private var conflictsBossBar: BossBar? = null

    private var currentPhaseSecondsPassed = 0
    private var currentLaneChangeSecondsPassed = 0
    private var phaseNumber = 0
    private var laneChangeNumber = 0

    override val specialItemManager = SpecialItemManager(this)


    constructor(players: MutableList<Player>, parameters: GameParameters) :
        super(players.map { player -> QueuedPlayer(player)}.toMutableList(), parameters) {
        GrassConflicts.gameManagers.add(this)
    }



    override fun startTeamSelection() {
        for (queuedPlayer in queuedPlayers) {
            queuedPlayer.player.inventory.clear()
            queuedPlayer.player.allowFlight = false
            queuedPlayer.player.isInvulnerable = false
            queuedPlayer.player.gameMode = GameMode.SURVIVAL
            grassBossBar?.let { playersAudience.hideBossBar(it) }
            conflictsBossBar?.let { playersAudience.hideBossBar(it) }
            grassBossBar = null
            conflictsBossBar = null
            queuedPlayer.player.playerListName(Component.text(queuedPlayer.player.name).color(NamedTextColor.WHITE))
            for (player in Bukkit.getOnlinePlayers()){
                player.showPlayer(GrassConflicts.plugin, queuedPlayer.player)
            }
        }
        playersAudience.sendTitlePart(TitlePart.TIMES, Title.Times.times
            (Duration.ofMillis(200), Duration.ofMillis(4000), Duration.ofMillis(200)))
        playersAudience.sendTitlePart(TitlePart.TITLE,
            Component.text("Grass").color(NamedTextColor.GREEN))
        playersAudience.playSound(Sound.sound(
            Key.key("minecraft:block.grass.place"), Sound.Source.MASTER, 1.0f, 1.0f))

        var secondsPassed = 1 + gameParameters.gameStartSkipSeconds
        preGameTimerTask = GrassConflicts.plugin.server.scheduler.runTaskTimer(GrassConflicts.plugin, Runnable {
            onPregameTimerSecondPassed(secondsPassed)
            secondsPassed++
        }, 20L, 20L)

    }

    private fun onPregameTimerSecondPassed(secondsPassed: Int) {
        when (secondsPassed) {
            2 -> {
                playersAudience.sendTitlePart(TitlePart.SUBTITLE,
                    Component.text("Conflicts?").color(NamedTextColor.RED))
                playersAudience.playSound(
                    Sound.sound(Key.key("minecraft:entity.player.attack.crit"), Sound.Source.MASTER, 1.0f, 1.0f))
            }
            4 -> {
                playersAudience.clearTitle()
                playersAudience.sendTitlePart(TitlePart.TIMES, Title.Times.times
                    (Duration.ofMillis(200), Duration.ofMillis(12000), Duration.ofMillis(200)))
                playersAudience.sendTitlePart(TitlePart.TITLE,
                    Component.text("Select your team.").color(NamedTextColor.YELLOW))
                playersAudience.playSound(
                    Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0f, 1.0f))
                for (queuedPlayer in queuedPlayers) {
                    ItemsUtil.setTeamSelectionItems(queuedPlayer.player,
                        getTeamPlayers(Team.GRASS).size, getTeamPlayers(Team.MYCELIUM).size)
                }
            }
        }

        val gameStartTimerSecondsPassed = secondsPassed - 6
        var color = NamedTextColor.YELLOW
        var secondsString = "seconds"
        if (secondsPassed >= 6 && gameStartTimerSecondsPassed < 10) {
            if (10 - gameStartTimerSecondsPassed < 4) {
                playersAudience.playSound( //button
                    Sound.sound(Key.key("minecraft:block.note_block.harp"), Sound.Source.MASTER, 1.0f, 1.0f))
                when (10 - gameStartTimerSecondsPassed) {
                    3 -> color = NamedTextColor.GREEN
                    2 -> color = NamedTextColor.GOLD
                    1 -> {color = NamedTextColor.RED; secondsString = "second"}
                }
            }
            playersAudience.sendTitlePart(TitlePart.SUBTITLE,
                Component.text("Game starts in ").color(NamedTextColor.YELLOW)
                    .append(Component.text(10 - gameStartTimerSecondsPassed).color(color))
                    .append(Component.text(" $secondsString").color(NamedTextColor.YELLOW))
            )
        }
        if (gameStartTimerSecondsPassed >= 10) {
            startGame()
            preGameTimerTask!!.cancel()
        }
    }


    override fun addPlayerToTeam(player: Player, team: Team) {
        //Prevent chat spam
        var gcPlayer: GCPlayer? = gamePlayers.firstOrNull {gcPlayer -> gcPlayer.player.uniqueId == player.uniqueId}
        if (gcPlayer != null && gcPlayer.team == team) return

        //Add player
        gamePlayers.remove(gamePlayers.firstOrNull {p -> p.player.uniqueId == player.uniqueId} )
        queuedPlayers.remove(queuedPlayers.firstOrNull {p -> p.player.uniqueId == player.uniqueId} )
        gamePlayers.add(GCPlayer(player, team))
        player.playerListName(Component.text(player.name).color(team.textColor))

        //Announce & update items
        Bukkit.broadcast(Component.text(player.name).color(NamedTextColor.YELLOW)
            .append(Component.text(" joined ").color(NamedTextColor.YELLOW))
            .append(Component.text("Team ${team.name}").color(team.textColor))
            .append(Component.text(".").color(NamedTextColor.YELLOW)))

        for (player in queuedPlayers) {
            ItemsUtil.setTeamSelectionItems(player.player, getTeamPlayers(Team.GRASS).size, getTeamPlayers(Team.MYCELIUM).size)
        }
        for (gcPlayer in gamePlayers) {
            ItemsUtil.setTeamSelectionItems(gcPlayer.player, getTeamPlayers(Team.GRASS).size, getTeamPlayers(Team.MYCELIUM).size)
        }
    }


    private fun startGame() {
        //Assign teams to unassigned players
        for (queuedPlayer in queuedPlayers.toList()) {
            queuedPlayers.remove(queuedPlayer)
            if (getTeamPlayers(Team.MYCELIUM).size > getTeamPlayers(Team.GRASS).size) {
                gamePlayers.add(GCPlayer(queuedPlayer.player, Team.GRASS))
                queuedPlayer.player.playerListName(Component.text(queuedPlayer.player.name).color(Team.GRASS.textColor))
            }
            else {
                gamePlayers.add(GCPlayer(queuedPlayer.player, Team.MYCELIUM))
                queuedPlayer.player.playerListName(Component.text(queuedPlayer.player.name).color(Team.MYCELIUM.textColor))
            }
        }

        for (gcPlayer in gamePlayers) {
            gcPlayer.player.addPotionEffect(
                PotionEffect(PotionEffectType.SATURATION, 999999, 1, false, false))
            gcPlayer.player.inventory.clear()
            ItemsUtil.setGameItems(gcPlayer.player, gcPlayer.team, gameParameters)
        }

        WorldEditUtil.clearAndPrepareMap(gameParameters, borderZCoordinate)

        startGrassPhase(gameParameters.phases[0])
        gameTimerTask = GrassConflicts.plugin.server.scheduler.runTaskTimer(GrassConflicts.plugin, Runnable {
            onGameTimerSecondPassed()
        }, 0L, 20L)

        var giveVelocityCooldown = 6
        gameTimerTask = GrassConflicts.plugin.server.scheduler.runTaskTimer(GrassConflicts.plugin, Runnable {
            onGameTimerTick(giveVelocityCooldown)
            if (giveVelocityCooldown == 0)
                giveVelocityCooldown = 7
            giveVelocityCooldown--
        }, 0L, 1L)
    }

    private fun onGameTimerTick(giveVelocityCooldown: Int) {
        //Remove air drop chest tasks for chests that do not exist:
        val airDrop = AirDrop.airDrops.firstOrNull { airDrop -> airDrop.chest != null && airDrop.chest!!.location != null &&
            GrassConflicts.world.getBlockAt(airDrop.chest!!.location).type != Material.CHEST}
        if (airDrop != null) {
            airDrop.chest?.type = Material.AIR
            airDrop.particleIdleTask?.cancel()
            AirDrop.airDrops.remove(airDrop)
        }

        if (giveVelocityCooldown > 0) return
        if (phase != Phase.GRASS) return
        for (gcPlayer in gamePlayers) {
            if (gcPlayer.player.location.z >= borderZCoordinate - 0.9 && gcPlayer.team == Team.GRASS) {
                val velocity = gcPlayer.player.velocity
                velocity.z = max(abs(velocity.z), 1.0) * -1.5
                if (velocity.y < 0.6)
                    velocity.y = 0.6
                gcPlayer.player.velocity = velocity
            }
            else if (gcPlayer.player.location.z <= borderZCoordinate + 0.9 && gcPlayer.team == Team.MYCELIUM) {
                val velocity = gcPlayer.player.velocity
                velocity.z = max(abs(velocity.z), 1.0) * 1.5
                if (velocity.y < 0.6)
                    velocity.y = 0.6
                gcPlayer.player.velocity = velocity
            }
        }
    }

    private fun startGrassPhase(phaseParameters: PhaseParameters) {
        if (phaseNumber == 0) {
            for (gcPlayer in gamePlayers) {
                if (gcPlayer.team == Team.GRASS)
                    gcPlayer.player.teleport(gameParameters.grassTeleportLocation)
                else
                    gcPlayer.player.teleport(gameParameters.myceliumTeleportLocation)
            }
        }
        phase = Phase.GRASS
        playersAudience.clearTitle()
        playersAudience.sendTitlePart(TitlePart.TIMES, Title.Times.times
            (Duration.ofMillis(200), Duration.ofMillis(1000), Duration.ofMillis(200)))
        playersAudience.sendTitlePart(TitlePart.TITLE,
            Component.text("Grass!").color(NamedTextColor.GREEN))
        playersAudience.sendMessage(
            Component.text("Grass!\n").color(NamedTextColor.GREEN).
                append(Component.text("Build for ${phaseParameters.duration} seconds.").color(NamedTextColor.YELLOW)))
        playersAudience.playSound(
            Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0f, 2.0f))

        grassBossBar?.let { playersAudience.hideBossBar(it) }
        conflictsBossBar?.let { playersAudience.hideBossBar(it) }
        grassBossBar = null
        conflictsBossBar = null
        grassBossBar = BossBar.bossBar(
            Component.text("Grass!").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)
                .append(Component.text(" Build for ${phaseParameters.duration} seconds.").decoration(TextDecoration.BOLD, false)),
            1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS)
        playersAudience.showBossBar(grassBossBar!!)

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            val rnd = Random()
            for (i in 0 until rnd.nextInt
                (gameParameters.phases[phaseNumber].airDropMax - gameParameters.phases[phaseNumber].airDropMin + 1) +
                gameParameters.phases[phaseNumber].airDropMin)
                AirDrop.spawnOnBothSides(gameParameters, borderZCoordinate)
        }, gameParameters.phases[phaseNumber].airDropDelay.toLong() * 20)

        if (phaseNumber == 0) return

        for (gcPlayer in gamePlayers) {
            var arrows = 0
            for (itemStack in gcPlayer.player.inventory.contents) {
                if (itemStack != null && itemStack.type == Material.ARROW)
                    arrows += itemStack.amount
            }
            var blocks = 0
            for (itemStack in gcPlayer.player.inventory.contents) {
                if (itemStack != null && itemStack.type == gcPlayer.team.material)
                    blocks += itemStack.amount
            }

            if (arrows < gameParameters.maxArrows)
                gcPlayer.player.inventory.addItem(
                    ItemStack(Material.ARROW, min(gameParameters.maxArrows - arrows, gameParameters.arrowsPerGrassPhase)))

            if (blocks < gameParameters.maxBlocks)
                gcPlayer.player.inventory.addItem(
                    ItemStack(gcPlayer.team.material, min(gameParameters.maxBlocks - blocks, gameParameters.blocksPerGrassPhase)))
        }

    }
    private fun startConflictsPhase(phaseParameters: PhaseParameters) {
        phase = Phase.CONFLICTS
        playersAudience.clearTitle()
        playersAudience.sendTitlePart(TitlePart.TIMES, Title.Times.times
            (Duration.ofMillis(200), Duration.ofMillis(1000), Duration.ofMillis(200)))
        playersAudience.sendTitlePart(TitlePart.TITLE,
            Component.text("Conflicts!").color(NamedTextColor.RED))
        playersAudience.sendMessage(
            Component.text("Conflicts!\n").color(NamedTextColor.RED).
            append(Component.text("Fight for ${phaseParameters.duration} seconds.").color(NamedTextColor.YELLOW)))
        playersAudience.playSound(
            Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0f, 0.0f))

        grassBossBar?.let { playersAudience.hideBossBar(it) }
        conflictsBossBar?.let { playersAudience.hideBossBar(it) }
        grassBossBar = null
        conflictsBossBar = null
        conflictsBossBar = BossBar.bossBar(
            Component.text("Conflicts!").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text(" Fight for ${phaseParameters.duration} seconds").decoration(TextDecoration.BOLD, false)),
            1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
        playersAudience.showBossBar(conflictsBossBar!!)

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            val rnd = Random()
            for (i in 0 until rnd.nextInt
                (gameParameters.phases[phaseNumber].airDropMax - gameParameters.phases[phaseNumber].airDropMin + 1) +
                gameParameters.phases[phaseNumber].airDropMin)
                AirDrop.spawnOnBothSides(gameParameters, borderZCoordinate)
        }, gameParameters.phases[phaseNumber].airDropDelay.toLong() * 20)
    }

    private fun onGameTimerSecondPassed() {
        if (gameEnded) return

        if (phaseNumber < gameParameters.phases.size - 1) {
            var phaseParameters: PhaseParameters = gameParameters.phases[phaseNumber]
            if (currentPhaseSecondsPassed >= phaseParameters.duration) {
                phaseNumber++
                currentPhaseSecondsPassed = 0
                phaseParameters = gameParameters.phases[phaseNumber]
                if (phaseParameters.phase == Phase.GRASS)
                    startGrassPhase(phaseParameters)
                else if (phaseParameters.phase == Phase.CONFLICTS)
                    startConflictsPhase(phaseParameters)
            }

            val secondsLeft = phaseParameters.duration - currentPhaseSecondsPassed
            var progress: Float = 1 - currentPhaseSecondsPassed * 1f / phaseParameters.duration
            var secondString = "seconds"
            if (secondsLeft <= 3)
                playersAudience.playSound(
                    Sound.sound(Key.key("minecraft:block.note_block.guitar"), Sound.Source.MASTER, 1.0f, 1.0f))
            if (secondsLeft == 1)
                secondString = "second"
            if (grassBossBar != null) {
                grassBossBar!!.name(Component.text("Grass!").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)
                    .append(Component.text(" Build for ${phaseParameters.duration - currentPhaseSecondsPassed} $secondString.")
                        .decoration(TextDecoration.BOLD, false)))
                grassBossBar!!.progress(progress)
            }
            if (conflictsBossBar != null) {
                conflictsBossBar!!.name(Component.text("Conflicts!").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true)
                    .append(Component.text(" Fight for ${phaseParameters.duration - currentPhaseSecondsPassed} $secondString.")
                        .decoration(TextDecoration.BOLD, false)))
                conflictsBossBar!!.progress(progress)
            }

            currentPhaseSecondsPassed++
        }

        if (laneChangeNumber < gameParameters.laneChanges.size - 1) {
            var laneChangeParameters: LaneChangeParameters = gameParameters.laneChanges[laneChangeNumber]
            if (currentLaneChangeSecondsPassed >= laneChangeParameters.duration) {
                laneChangeNumber++
                currentLaneChangeSecondsPassed = -1
                laneChangeParameters = gameParameters.laneChanges[laneChangeNumber]
                Bukkit.broadcast(Component.text("Kills are now worth ").color(NamedTextColor.YELLOW)
                    .append(Component.text("${laneChangeParameters.value} lanes").color(NamedTextColor.RED))
                    .append(Component.text(".").color(NamedTextColor.YELLOW)))
            }
            currentLaneChangeSecondsPassed++
        }
    }

    private fun getTeamPlayers(team: Team): MutableList<GCPlayer> {
        return gamePlayers.filter { gcPlayer -> gcPlayer.team == team}.toMutableList()
    }

    override fun onPlayerDeath(gcPlayer: GCPlayer) {
        if (gcPlayer.team == Team.MYCELIUM) {
            WorldEditUtil.moveBorder(Material.GRASS_BLOCK, borderZCoordinate, gameParameters.laneChanges[laneChangeNumber].value, gameParameters)
            borderZCoordinate += gameParameters.laneChanges[laneChangeNumber].value
        }
        else {
            WorldEditUtil.moveBorder(Material.MYCELIUM, borderZCoordinate, -gameParameters.laneChanges[laneChangeNumber].value, gameParameters)
            borderZCoordinate -= gameParameters.laneChanges[laneChangeNumber].value
        }


        if (borderZCoordinate + 1 > gameParameters.corner1Coords.blockZ || borderZCoordinate - 1 < gameParameters.corner2Coords.blockZ) {
            onGameOver(gcPlayer.team)
        }
        else {
            var respawnCooldown = gameParameters.respawnTime
            val task = GrassConflicts.plugin.server.scheduler.runTaskTimer(GrassConflicts.plugin, Runnable {
                onDeathCooldownSecondPassed(gcPlayer, respawnCooldown)
                respawnCooldown--
            }, 0L, 20L)
            respawnCooldownTasks[gcPlayer.player.uniqueId] = task
        }
    }


    override fun onSpecialItemUsage(itemType: AirDropItemType, player: GCPlayer, block: Block?) {
        when (itemType) {
            AirDropItemType.CHEESIER_PASTA ->
                specialItemManager.onCheesierPastaUsage(player)
            AirDropItemType.INSTA_WALL ->
                specialItemManager.onInstaWallUsage(player, block!!)

            else -> {throw IllegalArgumentException("Unsupported special item $itemType")}
        }
    }

    private fun onDeathCooldownSecondPassed(gcPlayer: GCPlayer, respawnCooldown: Int) {
        if (respawnCooldown == 0) {
            gcPlayer.player.clearTitle()
            gcPlayer.isDead = false
            gcPlayer.player.isInvulnerable = false
            gcPlayer.player.allowFlight = false
            gcPlayer.player.isFlying = false

            ItemsUtil.setGameItems(gcPlayer.player, gcPlayer.team, gameParameters, gcPlayer)
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(GrassConflicts.plugin, gcPlayer.player)
            }
            ItemsUtil.setInvulnerabilityItems(gcPlayer.player)
            GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
                ItemsUtil.removeInvulnerabilityItems(gcPlayer.player, gcPlayer.team)
            }, 100L)

            for (item in gcPlayer.player.inventory)
                if (item?.itemMeta is CrossbowMeta) { //fix bug where players would respawn with an unloaded crossbow
                    val meta = item?.itemMeta as CrossbowMeta
                    var itemName = PlainTextComponentSerializer.plainText().serialize(item.displayName())
                    itemName = itemName.substring(1, itemName.length - 1)
                    if (itemName == "Bigger Gun") {
                        meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
                        meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
                        meta.addChargedProjectile(ItemStack(Material.ARROW, 1))
                    }
                    else
                        meta.setChargedProjectiles(mutableListOf(ItemStack(Material.ARROW, 1)))
                    item.itemMeta = meta
                }

            respawnCooldownTasks[gcPlayer.player.uniqueId]?.cancel()
            respawnCooldownTasks.remove(gcPlayer.player.uniqueId)
            if (gcPlayer.team == Team.GRASS)
                gcPlayer.player.teleport(gameParameters.grassTeleportLocation)
            else
                gcPlayer.player.teleport(gameParameters.myceliumTeleportLocation)
            return
        }
        gcPlayer.player.sendTitlePart(TitlePart.TIMES, Title.Times.times(
            Duration.ofMillis(10), Duration.ofMillis(1500), Duration.ofMillis(10)))
        gcPlayer.player.sendTitlePart(TitlePart.TITLE,
            Component.text("Respawning in ").color(NamedTextColor.YELLOW)
                .append(Component.text(respawnCooldown).color(NamedTextColor.RED)))
    }

    //gets losing team
    private fun onGameOver(team: Team) {
        gameEnded = true
        var winningTeam: Team = Team.MYCELIUM
        if (team == Team.MYCELIUM)
            winningTeam = Team.GRASS
        for (losingPlayer in gamePlayers.filter { gamePlayer -> gamePlayer.team == team }) {
            losingPlayer.player.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                Duration.ofMillis(10), Duration.ofMillis(4000), Duration.ofMillis(500)))
            losingPlayer.player.sendTitlePart(TitlePart.TITLE,
                Component.text("${winningTeam.name}").color(winningTeam.textColor)
                    .append(Component.text(" won!").color(winningTeam.textColor)))
            losingPlayer.player.sendTitlePart(TitlePart.SUBTITLE,
                Component.text("Your team lost. L").color(NamedTextColor.RED))
        }

        for (winningPlayer in gamePlayers.filter { gamePlayer -> gamePlayer.team != team }) {
            winningPlayer.player.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                Duration.ofMillis(10), Duration.ofMillis(3000), Duration.ofMillis(200)))
            winningPlayer.player.sendTitlePart(TitlePart.TITLE,
                Component.text("${winningTeam.name}").color(winningTeam.textColor)
                    .append(Component.text(" won!").color(winningTeam.textColor)))
            winningPlayer.player.sendTitlePart(TitlePart.SUBTITLE,
                Component.text("You won!").color(NamedTextColor.GREEN))
        }

        playersAudience.playSound(
            Sound.sound(Key.key("minecraft:ui.toast.challenge_complete"), Sound.Source.MASTER, 1.0f, 2.0f))

        for (gcPlayer in gamePlayers) {
            gcPlayer.player.allowFlight = true
            gcPlayer.player.isFlying = true
            gcPlayer.player.isInvulnerable = true
        }
        grassBossBar?.let { playersAudience.hideBossBar(it) }
        conflictsBossBar?.let { playersAudience.hideBossBar(it) }
        grassBossBar = null
        conflictsBossBar = null
        preGameTimerTask?.cancel()
        gameTimerTask?.cancel()
        gameTickTimerTask?.cancel()


        val playersSortedByKills = gamePlayers.sortedByDescending { it.kills }
        val playersSortedByBlocksPlaced = gamePlayers.sortedByDescending { it.blocksPlaced }

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            Bukkit.broadcast(Component.text("Most kills:").color(NamedTextColor.YELLOW))
        }, 40L)

        val gcPlayer = playersSortedByKills[0]
        val playerName = gcPlayer.player.name
        val kills = gcPlayer.kills
        var message = Component.text("1. ").color(NamedTextColor.YELLOW)
            .append(Component.text(playerName).color(gcPlayer.team.textColor))
            .append(Component.text(": ").color(NamedTextColor.YELLOW))
            .append(Component.text("$kills kills").color(NamedTextColor.RED))

        for (index in 1 until playersSortedByKills.size) { //shows top 3
            if (index >= playersSortedByKills.size || index > 2) break
            val gcPlayer = playersSortedByKills[index]
            val playerName = gcPlayer.player.name
            val kills = gcPlayer.kills
            if (kills == 0) break
            val subMessage = Component.text("\n${index + 1}. ").color(NamedTextColor.YELLOW)
                .append(Component.text(playerName).color(gcPlayer.team.textColor))
                .append(Component.text(": ").color(NamedTextColor.YELLOW))
                .append(Component.text("$kills kills").color(NamedTextColor.RED))
            message = message.append(subMessage)
        }

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            Bukkit.broadcast(message)
        }, 80L)

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            Bukkit.broadcast(Component.text("Placed the most blocks:").color(NamedTextColor.YELLOW))
        }, 120L)

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            val gcPlayer = playersSortedByBlocksPlaced[0]
            Bukkit.broadcast(Component.text("${gcPlayer.player.name}").color(gcPlayer.team.textColor)
                .append(Component.text(": ").color(NamedTextColor.YELLOW))
                .append(Component.text(gcPlayer.blocksPlaced).color(NamedTextColor.GREEN))
                .append(Component.text(" blocks placed.").color(NamedTextColor.YELLOW))
            )

        }, 160L)

        GrassConflicts.plugin.server.scheduler.runTaskLater(GrassConflicts.plugin, Runnable {
            for (gcPlayer in gamePlayers) {
                var bowAccuracy = 0
                if (gcPlayer.arrowLanded > 0)
                    bowAccuracy = ceil(gcPlayer.arrowLanded * 100.0f / gcPlayer.arrowsShot)
                gcPlayer.player.sendMessage(
                    Component.text("You got ").color(NamedTextColor.YELLOW)
                    .append(Component.text(gcPlayer.kills).color(NamedTextColor.RED)
                        .append(Component.text(" kills, and died ").color(NamedTextColor.YELLOW))
                        .append(Component.text(gcPlayer.deaths).color(NamedTextColor.RED))
                        .append(Component.text(" times.\nYour bow accuracy was ").color(NamedTextColor.YELLOW))
                        .append(Component.text("$bowAccuracy%").color(NamedTextColor.GOLD))
                        .append(Component.text(".").color(NamedTextColor.YELLOW))
                    ))
            }
            GrassConflicts.gameManagers.remove(this)
        }, 200L)
    }


}


