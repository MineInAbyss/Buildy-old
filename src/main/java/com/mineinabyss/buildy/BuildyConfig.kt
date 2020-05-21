package com.mineinabyss.buildy

import com.charleskorn.kaml.Yaml
import com.mineinabyss.buildy.model.BuildArea
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import kotlinx.serialization.Serializable
import org.bukkit.command.CommandSender

object BuildyConfig {
    lateinit var serialized: SerializedBuildyConfig; private set

    val buildAreas get() = serialized.buildAreas

    @Serializable
    data class SerializedBuildyConfig(
            val buildAreas: MutableList<BuildArea>
    )

    private fun loadSerializedValues() {
        serialized = Yaml.default.parse(SerializedBuildyConfig.serializer(), buildy.config.saveToString())
    }

    init {
        loadSerializedValues()
    }

    fun saveConfig() {
        buildy.config.loadFromString(Yaml.default.stringify(SerializedBuildyConfig.serializer(), serialized))
        buildy.saveConfig()
    }

    /**
     * Reloads the configurations stored in the plugin. Will re-serialize a new instance of BuildyConfig.
     * Some things require a full plugin reload.
     */
    fun reload(sender: CommandSender = buildy.server.consoleSender) {
        val consoleSender = buildy.server.consoleSender
        fun attempt(success: String, fail: String, block: () -> Unit) {
            try {
                block()
                sender.success(success)
                if (sender != consoleSender) consoleSender.success(success)
            } catch (e: Exception) {
                sender.error(fail)
                if (sender != consoleSender) consoleSender.error(fail)
                e.printStackTrace()
                return
            }
        }

        logSuccess("Reloading Buildy config")

        attempt("Loaded serialized config values", "Failed to load serialized config values") {
            buildy.reloadConfig()
            loadSerializedValues()
        }

        sender.success("Successfully reloaded config")
    }
}