package com.github.gameoholic.grassconflicts.datatypes

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material

data class Team(val name: String, val textColor: TextColor, val color: Color, var material: Material) {
    companion object {
        val GRASS = Team("Grass", NamedTextColor.GREEN, Color.LIME, Material.GRASS_BLOCK)
        val MYCELIUM = Team("Mycelium", TextColor.color(125, 96, 124),
            Color.fromBGR(125, 96, 124), Material.MYCELIUM)
    }
}