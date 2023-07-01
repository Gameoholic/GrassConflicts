package com.github.gameoholic.grassconflicts

import org.bukkit.plugin.java.JavaPlugin

class GrassConflictsPlugin: JavaPlugin() {
    override fun onEnable() {
        GrassConflicts.onEnable(this)
    }
}