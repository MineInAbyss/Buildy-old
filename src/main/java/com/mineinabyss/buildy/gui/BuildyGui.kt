package com.mineinabyss.buildy.gui

import com.derongan.minecraft.guiy.gui.GridContainable
import com.derongan.minecraft.guiy.gui.ListContainable
import com.derongan.minecraft.guiy.gui.elements.lists.ScrollAlignment
import com.derongan.minecraft.guiy.gui.elements.lists.ScrollType
import com.derongan.minecraft.guiy.gui.inputs.BooleanInput
import com.derongan.minecraft.guiy.gui.layouts.GuiyExperimentalKotlinAPI
import com.derongan.minecraft.guiy.gui.layouts.HistoryGuiHolder
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.derongan.minecraft.guiy.kotlin_dsl.scrollingPallet
import com.derongan.minecraft.guiy.kotlin_dsl.wrappedList
import com.mineinabyss.buildy.BuildyConfig
import com.mineinabyss.buildy.buildy
import com.mineinabyss.buildy.model.BuildArea
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.serialization.toSerializable
import de.erethon.headlib.HeadLib
import org.bukkit.entity.Player

@GuiyExperimentalKotlinAPI
class BuildyGui(private val player: Player) : HistoryGuiHolder(6, "Buildy", buildy) {
    override val root = guiyLayout {
        scrollingPallet(9) {
            BuildyConfig.buildAreas.filter { it.hasMember(player) || it.hasLead(player) }.forEach { area ->
                areaToButton(area)
            }
        }.at(0, 0)

        val areas = buildAreaList { filter { !it.isComplete } }.at(0, 1)

        button(HeadLib.OBJECT_MONITOR.toCell("&7Complete".color())) {
            setElement(completeList())
        }.at(1, 5)

        val input = BooleanInput(false).at(4, 4)
        input.setSubmitAction {
            player.info("hello! ${input.result}")
        }
        button(HeadLib.WOODEN_PLUS.toCell("&aCreate area".color())) {
            input.onSubmit()
            val area = BuildArea(
                    "test",
                    player.inventory.itemInMainHand.toSerializable(),
                    player.world.name,
                    regionName = "test",
                    teleportLoc = player.location
            )
            areas += area
            render()
            BuildyConfig.saveConfig()
        }.at(0, 5)
    }

    fun completeList() = guiyLayout {
        buildAreaList { filter { it.isComplete } }.at(0, 1)
    }

    private fun ListContainable.areaToButton(area: BuildArea) = button(area.icon.toItemStack().toCell(area.name)) {
        this@BuildyGui.setElement(BuildAreaLayout(this@BuildyGui, player, area))
    }

    private fun GridContainable.buildAreaList(filter: MutableList<BuildArea>.() -> List<BuildArea>) =
            wrappedList(9, 3,
                    BuildyConfig.buildAreas,
                    filter,
                    { areaToButton(it) }) {
                scrollType = ScrollType.VERTICAL
                scrollBarAlignment = ScrollAlignment.RIGHT
            }

}