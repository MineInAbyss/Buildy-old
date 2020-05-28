package com.mineinabyss.buildy.gui

import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.gui.addAll
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.derongan.minecraft.guiy.kotlin_dsl.scrollingPallet
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
        scrollingPallet(8) {
            addAll(this@BuildAreaLayout.buildArea.leads.toPlayerHeads())
        }.at(1, 0)

        Material.NAME_TAG.toCell("&7Project members".color()).at(0, 1)
        scrollingPallet(8) {
            addAll(this@BuildAreaLayout.buildArea.members.toPlayerHeads())
        }.at(1, 1)

        button(HeadLib.QUARTZ_L.toCell("Teleport to")) {
            player.teleport(buildArea.teleportLoc)
        }.at(1, 5)

        if (player.hasPermission("buildy.srbuilder")) {
            if (!buildArea.hasLead(player)) {
                button(Material.PAPER.toCell("&6Join as lead".color())) {
                    if (!buildArea.hasLead(player)) buildArea.leads += player.uniqueId
                    buildArea.members -= player.uniqueId
                }.at(6, 5)
                if (!buildArea.hasMember(player))
                    button(Material.PAPER.toCell("Join as member")) {
                        if (!buildArea.hasMember(player)) buildArea.members += player.uniqueId
                    }.at(5, 5)
            }
        } else if (!buildArea.hasMember(player) && !buildArea.hasLead(player))
            button(Material.PAPER.toCell("Request join")) {
                TODO("create request")
            }.at(6, 5)

        if (buildArea.leads.contains(player.uniqueId))
            button(Material.PAPER.toCell("Edit area")) {
                mainGUI.setElement(settingsMenu())
            }.at(7, 5)
    }

    private fun settingsMenu() = guiyLayout {
        button(HeadLib.STONE_L.toCell("&6Update location".color())) {
            this@BuildAreaLayout.buildArea.teleportLoc = this@BuildAreaLayout.player.location
            BuildyConfig.saveConfig()
        }.at(2, 5)

        button(HeadLib.WOODEN_PLUS.toCell("&6Set complete".color())) {
            //TODO toggle button
            val buildArea = this@BuildAreaLayout.buildArea
            buildArea.isComplete = !buildArea.isComplete
            BuildyConfig.saveConfig()
        }.at(3, 5)

        button(HeadLib.PLAIN_LIGHT_RED.toCell("&cDelete".color())) {
            val buildArea = this@BuildAreaLayout.buildArea
            BuildyConfig.buildAreas.remove(buildArea)
            this@BuildAreaLayout.player.closeInventory()
            BuildyConfig.saveConfig()
        }.at(7, 5)
    }
}

fun List<UUID>.toPlayerHeads() = map {
    ItemStack(Material.PLAYER_HEAD).editItemMeta {
        (this as SkullMeta).owningPlayer = Bukkit.getOfflinePlayer(it)
    }.toCell()
}