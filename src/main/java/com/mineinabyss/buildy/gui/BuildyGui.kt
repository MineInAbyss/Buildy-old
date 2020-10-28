package com.mineinabyss.buildy.gui

import com.derongan.minecraft.guiy.gui.Element
import com.derongan.minecraft.guiy.gui.containers.Containable
import com.derongan.minecraft.guiy.gui.containers.GridContainable
import com.derongan.minecraft.guiy.gui.holders.HistoryGuiHolder
import com.derongan.minecraft.guiy.gui.inputs.ToggleElement
import com.derongan.minecraft.guiy.gui.properties.scroll.ScrollAlignment
import com.derongan.minecraft.guiy.gui.properties.scroll.ScrollType
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.*
import com.mineinabyss.buildy.BuildyConfig
import com.mineinabyss.buildy.buildy
import com.mineinabyss.buildy.model.BuildArea
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.serialization.toSerializable
import de.erethon.headlib.HeadLib
import org.bukkit.Material
import org.bukkit.entity.Player

class BuildyGui(private val player: Player) : HistoryGuiHolder(6, "Buildy", buildy) {
    override var root: Element = guiyLayout {
//        scrollingPallet(9) {
//            BuildyConfig.buildAreas.filter { it.hasMember(player) || it.hasLead(player) }.forEach { area ->
//                areaToButton(area)
//            }
//        }.at(0, 0)
        column {
            row {
                addElement(Material.STONE.toCell())
                addElement(Material.STONE.toCell())
            }
            row {
                addElement(Material.GRASS.toCell())
                addElement(Material.DIRT.toCell())
            }
//            BuildyConfig.buildAreas.forEach { area ->
//                areaToButton(area)
//            }
        }.at(0, 0)

//        val input = BooleanInput(false).at(4, 4)
//        input.setSubmitAction {
//            player.info("hello! ${input.result}")
//        }
        val toggle = ToggleElement(HeadLib.CHECKMARK.toCell("Owned by me"), HeadLib.RED_X.toCell("Not owned by me")).at(4, 4)

//        val areas = buildAreaList {
//            filter {
//                !it.isComplete && (toggle.enabled || it.hasLead(player))
//            }
//        }.at(0, 1)

        button(HeadLib.OBJECT_MONITOR.toCell("&7Complete".color())) {
            setElement(completeList())
        }.at(1, 5)
        button(HeadLib.WOODEN_PLUS.toCell("&aCreate area".color())) {
//            input.onSubmit()
            val area = BuildArea(
                    "test",
                    player.inventory.itemInMainHand.toSerializable(),
                    player.world.name,
                    regionName = "test",
                    teleportLoc = player.location
            )
//            areas += area
            render()
            BuildyConfig.queueSave()
        }.at(0, 5)
    }

    fun completeList() = guiyLayout {
        buildAreaList { filter { it.isComplete } }.at(0, 1)
    }

    private fun Containable.areaToButton(area: BuildArea) = button(area.icon.toItemStack().toCell(area.name)) {
        this@BuildyGui.setElement(BuildAreaLayout(this@BuildyGui, player, area))
    }

    private fun GridContainable.buildAreaList(filter: MutableList<BuildArea>.() -> List<BuildArea>) =
            wrappedList(9, 3, BuildyConfig.buildAreas) {
                convertBy = { areaToButton(it) }
                filterBy = filter
                scrollBar.scrollType = ScrollType.VERTICAL
                scrollBar.alignment = ScrollAlignment.INLINE
            }
}