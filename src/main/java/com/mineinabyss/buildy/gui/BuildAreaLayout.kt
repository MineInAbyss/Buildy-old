package com.mineinabyss.buildy.gui

import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.derongan.minecraft.guiy.kotlin_dsl.scrollingPallet
import com.derongan.minecraft.guiy.kotlin_dsl.toggle
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
        Material.NAME_TAG.toCell("&6Project leads".color()).at(0, 0)
        scrollingPallet(8) {
            addAll(this@BuildAreaLayout.buildArea.leads.toPlayerHeads())
        }.at(1, 0)

        Material.NAME_TAG.toCell("&7Project members".color()).at(0, 1)
        scrollingPallet(8) {
            addAll(this@BuildAreaLayout.buildArea.members.toPlayerHeads())
        }.at(1, 1)

        val min = buildArea.region.minimumPoint
        val max = buildArea.region.maximumPoint
        Material.GRASS_BLOCK.toCell("Size: $min -> $max").at(0, 5)

        button(HeadLib.QUARTZ_L.toCell("Teleport to")) {
            player.teleport(buildArea.teleportLoc)
        }.at(1, 5)

        if (player.hasPermission("buildy.srbuilder")) {
            if (!buildArea.hasLead(player)) {
                button(Material.PAPER.toCell("&6Join as lead".color())) {
                    if (!buildArea.hasLead(player)) buildArea.addLead(player)
                    buildArea.removeMember(player)
                }.at(6, 5)
                if (!buildArea.hasMember(player))
                    button(Material.PAPER.toCell("Join as member")) {
                        if (!buildArea.hasMember(player)) buildArea.addMember(player)
                    }.at(5, 5)
            }
        } else if (!buildArea.hasMember(player) && !buildArea.hasLead(player))
            button(Material.PAPER.toCell("Request join")) {
                TODO("create request")
            }.at(6, 5)

        if (buildArea.hasLead(player))
            button(Material.PAPER.toCell("Edit area")) {
                mainGUI.setElement(settingsMenu())
            }.at(7, 5)
    }

    private fun settingsMenu() = guiyLayout {
        button(HeadLib.STONE_L.toCell("&6Update location".color())) {
            this@BuildAreaLayout.buildArea.teleportLoc = this@BuildAreaLayout.player.location
            BuildyConfig.saveConfig()
        }.at(2, 5)

        toggle(HeadLib.PLAIN_LIGHT_GREEN.toCell("&6Progress: Open".color()),
                HeadLib.PLAIN_LIGHT_GRAY.toCell("&6Progress: Complete".color())) {
            val buildArea = this@BuildAreaLayout.buildArea
            enabled = buildArea.isComplete
            onClick {
                buildArea.isComplete = !buildArea.isComplete
                BuildyConfig.saveConfig()
            }
        }.at(3, 5)

        button(HeadLib.STONE_R.toCell("&6Set region".color())) {
            //TODO toggle button
            with(this@BuildAreaLayout) {
                buildArea.changeRegion(player)
            }
        }.at(4, 5)

        button(HeadLib.PLAIN_LIGHT_RED.toCell("&cDelete".color())) {
            val buildArea = this@BuildAreaLayout.buildArea
            BuildyConfig.buildAreas.remove(buildArea)
            this@BuildAreaLayout.player.closeInventory()
            BuildyConfig.saveConfig()
        }.at(7, 5)
    }
}

fun Collection<UUID>.toPlayerHeads() = map {
    val player = Bukkit.getOfflinePlayer(it)
    ItemStack(Material.PLAYER_HEAD).editItemMeta {
        (this as SkullMeta).owningPlayer = player
    }.toCell("${player.name}")
}