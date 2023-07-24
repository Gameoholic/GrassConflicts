package com.github.gameoholic.grassconflicts

import com.github.gameoholic.grassconflicts.commands.StartCommand
import com.github.gameoholic.grassconflicts.commands.TestCommand
import com.github.gameoholic.grassconflicts.gamemanager.GameManager
import com.github.gameoholic.grassconflicts.listeners.*
import org.bukkit.Bukkit
import org.bukkit.World

object GrassConflicts {
    val gameManagers = mutableListOf<GameManager>()
    val world: World = Bukkit.getWorlds()[0]
    lateinit var plugin: GrassConflictsPlugin
        private set

    fun onEnable(grassConflictsPlugin: GrassConflictsPlugin) {
        plugin = grassConflictsPlugin

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


}