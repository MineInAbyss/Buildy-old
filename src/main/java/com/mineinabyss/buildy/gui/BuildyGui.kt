package com.mineinabyss.buildy.gui

import com.derongan.minecraft.guiy.gui.elements.ListElement
import com.derongan.minecraft.guiy.gui.layouts.GuiyExperimentalKotlinAPI
import com.derongan.minecraft.guiy.gui.layouts.HistoryGuiHolder
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.mineinabyss.buildy.BuildyConfig
import com.mineinabyss.buildy.buildy
import com.mineinabyss.buildy.model.BuildArea
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.serialization.toSerializable
import de.erethon.headlib.HeadLib
import org.bukkit.entity.Player

@GuiyExperimentalKotlinAPI
class BuildyGui(private val player: Player) : HistoryGuiHolder(6, "Buildy", buildy) {
    override val root = guiyLayout {
        val list = setElement(0, 0, ListElement(9, 4, BuildyConfig.buildAreas.filter { !it.isComplete }) { area ->
            button(area.icon.toItemStack().toCell(area.name)) {
                this@BuildyGui.setElement(BuildAreaLayout(this@BuildyGui, player, area))
            }
        })

        button(0, 5, HeadLib.WOODEN_PLUS.toCell("&aCreate area".color())) {
            val area = BuildArea(
                    "test",
                    player.inventory.itemInMainHand.toSerializable(),
                    player.world.name,
                    regionName = "test",
                    teleportLoc = player.location
            )
            BuildyConfig.serialized.buildAreas += area
            list.add(area)
            render()
            BuildyConfig.saveConfig()
        }
    }
}