package com.mineinabyss.buildy

import com.mineinabyss.buildy.model.BuildArea
import com.mineinabyss.idofront.config.IdofrontConfig
import kotlinx.serialization.Serializable

object BuildyConfig : IdofrontConfig<BuildyConfig.Data>(
        buildy,
        Data.serializer()
) {
    val buildAreas get() = data.buildAreas

    @Serializable
    data class Data(
            val buildAreas: MutableList<BuildArea>
    )
}

fun getBuildArea(name: String) = BuildyConfig.buildAreas.first { it.regionName == name }