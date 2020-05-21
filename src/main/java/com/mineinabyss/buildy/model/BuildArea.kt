@file:UseSerializers(UUIDSerializer::class, LocationSerializer::class)

package com.mineinabyss.buildy.model

import com.mineinabyss.idofront.serialization.LocationSerializer
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.UUIDSerializer
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldguard.protection.regions.RegionContainer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*


@Serializable
data class BuildArea(
        var name: String,
        var icon: SerializableItemStack,
        val worldName: String,
        val regionName: String,
        val leads: MutableList<UUID> = mutableListOf(),
        val members: MutableList<UUID> = mutableListOf(),
        val children: MutableList<BuildArea> = mutableListOf(),
        var teleportLoc: Location,
        var isComplete: Boolean = false
) {
    init {
        if (icon.toItemStack().itemMeta == null)
            icon.type = Material.STONE
    }

    @Transient
    val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer

    @Transient
    val manager = container[BukkitAdapter.adapt(Bukkit.getWorld(worldName))]
            ?: error("WorldGuard manager not found for world $worldName")

    @Transient
    val region: ProtectedRegion? = manager.getRegion(regionName)
            ?: com.sk89q.worldguard.protection.regions.GlobalProtectedRegion(regionName)
        init {
                //TODO ensure parent is set to build zone, create build zone in this world if it doesn't exist
//        region.parent
            }

    fun hasLead(player: Player) = leads.contains(player.uniqueId)

    fun hasMember(player: Player) = members.contains(player.uniqueId)

    //TODO methods to sync between WorldGuard members and region members
}