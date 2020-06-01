@file:UseSerializers(UUIDSerializer::class, LocationSerializer::class)

package com.mineinabyss.buildy.model

import com.mineinabyss.buildy.BuildyKeys.BUILD_ZONE_REGION
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.serialization.LocationSerializer
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.UUIDSerializer
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
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
    val region: ProtectedCuboidRegion = ((manager.getRegion(regionName)
            ?: ProtectedCuboidRegion(regionName, BlockVector3.ZERO, BlockVector3.ZERO).apply {
                parent = manager.getRegion(BUILD_ZONE_REGION)
                manager.addRegion(this)
            }) as? ProtectedCuboidRegion) ?: error("A region existed but was not a Cuboid")

    @Transient
    val leads: Set<UUID> = region.owners.uniqueIds

    @Transient
    val members: Set<UUID> = region.members.uniqueIds

    init {
        //TODO ensure parent is set to build zone, create build zone in this world if it doesn't exist
//        region.parent
    }

    fun changeRegion(player: Player) {
        val selection = player.worldEditSelection ?: return
        region.maximumPoint = selection.maximumPoint
        region.minimumPoint = selection.minimumPoint
        player.info(selection.toString())
    }

    val Player.worldEditSelection: Region?
        get() = runCatching {
            WorldEdit.getInstance().sessionManager.get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(world))
        }.getOrNull()

    fun hasLead(player: Player) = region.owners.contains(player.uniqueId)
    fun hasMember(player: Player) = region.members.contains(player.uniqueId)

    fun addLead(player: Player) = region.owners.addPlayer(player.uniqueId)
    fun addMember(player: Player) = region.members.addPlayer(player.uniqueId)

    fun removeLead(player: Player) = region.owners.removePlayer(player.uniqueId)
    fun removeMember(player: Player) = region.members.removePlayer(player.uniqueId)

    //TODO methods to sync between WorldGuard members and region members
}