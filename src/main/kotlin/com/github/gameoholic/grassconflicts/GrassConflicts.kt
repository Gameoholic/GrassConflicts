package com.github.gameoholic.grassconflicts

import com.github.gameoholic.grassconflicts.commands.StartCommand
import com.github.gameoholic.grassconflicts.commands.TestCommand
import com.github.gameoholic.grassconflicts.gamemanager.GameManager
import com.github.gameoholic.grassconflicts.listeners.*
import com.github.gameoholic.grassconflicts.nms.PacketManager
import org.bukkit.Bukkit
import org.bukkit.World

object GrassConflicts {
    val gameManagers = mutableListOf<GameManager>()
    val world: World = Bukkit.getWorlds()[0]
    lateinit var plugin: GrassConflictsPlugin
        private set
    lateinit var packetManager: PacketManager
        private set

    fun onEnable(grassConflictsPlugin: GrassConflictsPlugin) {
        plugin = grassConflictsPlugin

        packetManager = createPacketManager()

        plugin.getCommand("start")?.setExecutor(StartCommand)
        plugin.getCommand("test")?.setExecutor(TestCommand)

        Bukkit.getPluginManager().registerEvents(ProjectileHitListener, plugin)
        Bukkit.getPluginManager().registerEvents(PlayerInteractListener, plugin)
        Bukkit.getPluginManager().registerEvents(PlayerDamageListener, plugin)
        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, plugin)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, plugin)
        Bukkit.getPluginManager().registerEvents(BlockPlaceListener, plugin)
        Bukkit.getPluginManager().registerEvents(BlockBreakListener, plugin)
        Bukkit.getPluginManager().registerEvents(InventoryCloseListener, plugin)
        Bukkit.getPluginManager().registerEvents(EntityBlockFormListener, plugin)
        Bukkit.getPluginManager().registerEvents(EntityRegainHealthListener, plugin)
        Bukkit.getPluginManager().registerEvents(EntityShootBowListener, plugin)
        Bukkit.getPluginManager().registerEvents(InventoryClickListener, plugin)
        Bukkit.getPluginManager().registerEvents(PlayerDropItemListener, plugin)
        Bukkit.getPluginManager().registerEvents(LightningStrikeListener, plugin)
        Bukkit.getPluginManager().registerEvents(BlockDropItemListener, plugin)

    }

    private fun createPacketManager(): PacketManager = when (val serverVersion = Bukkit.getServer().minecraftVersion) {
        "1.19.4" -> com.github.gameoholic.grassconflicts.nms.v1_19_R3.PacketManager()
        else -> throw UnsupportedOperationException("Unsupported Minecraft version: $serverVersion")
    }
}