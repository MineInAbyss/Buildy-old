package com.mineinabyss.buildy.gui

import com.derongan.minecraft.guiy.gui.FillableElement
import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.gui.ScrollingPallet
import com.derongan.minecraft.guiy.gui.addAll
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.derongan.minecraft.guiy.kotlin_dsl.setElement
import com.mineinabyss.buildy.BuildyConfig
import com.mineinabyss.buildy.model.BuildArea
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.color
import de.erethon.headlib.HeadLib
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class BuildAreaLayout(
        private val mainGUI: BuildyGui,
        val player: Player,
        val buildArea: BuildArea
) : Layout() {
    init {
        setElement(0, 0, Material.NAME_TAG.toCell("&6Project leads".color()))
        setElement(1, 0, ScrollingPallet(8)) {
            addAll(this@BuildAreaLayout.buildArea.leads.toPlayerHeads())
        }

        setElement(0, 1, Material.NAME_TAG.toCell("&7Project members".color()))
        setElement(1, 1, ScrollingPallet(8)) {
            addAll(this@BuildAreaLayout.buildArea.members.toPlayerHeads())
        }

        button(1, 5, HeadLib.QUARTZ_L.toCell("Teleport to")) {
            player.teleport(buildArea.teleportLoc)
        }
        if (player.hasPermission("buildy.srbuilder")) {
            if (!buildArea.hasLead(player)) {
                button(6, 5, Material.PAPER.toCell("&6Join as lead".color())) {
                    if (!buildArea.hasLead(player)) buildArea.leads += player.uniqueId
                    buildArea.members -= player.uniqueId
                }
                if (!buildArea.hasMember(player))
                    button(5, 5, Material.PAPER.toCell("Join as member")) {
                        if (!buildArea.hasMember(player)) buildArea.members += player.uniqueId
                    }
            }
        } else if (!buildArea.hasMember(player) && !buildArea.hasLead(player))
            button(6, 5, Material.PAPER.toCell("Request join")) {
                TODO("create request")
            }

        if (buildArea.leads.contains(player.uniqueId))
            button(7, 5, Material.PAPER.toCell("Edit area")) {
                mainGUI.setElement(settingsMenu())
            }
    }

    private fun settingsMenu() = guiyLayout {
        button(2, 5, HeadLib.STONE_L.toCell("&6Update location".color())) {
            this@BuildAreaLayout.buildArea.teleportLoc = this@BuildAreaLayout.player.location
            BuildyConfig.saveConfig()
        }

        button(3, 5, HeadLib.WOODEN_PLUS.toCell("&6Set complete".color())) {
            //TODO toggle button
            val buildArea = this@BuildAreaLayout.buildArea
            buildArea.isComplete = !buildArea.isComplete
            BuildyConfig.saveConfig()
        }
    }
}

fun List<UUID>.toPlayerHeads() = map {
    ItemStack(Material.PLAYER_HEAD).editItemMeta {
        (this as SkullMeta).owningPlayer = Bukkit.getOfflinePlayer(it)
    }.toCell()
}