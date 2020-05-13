package com.mineinabyss.buildy

import com.derongan.minecraft.guiy.GuiListener
import org.bukkit.plugin.java.JavaPlugin

/** Gets [Buildy] via Bukkit once, then sends that reference back afterwards */
val buildy: Buildy by lazy { JavaPlugin.getPlugin(Buildy::class.java) }

class Buildy : JavaPlugin() {
    override fun onEnable() {
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()
        BuildyConfig

        //Register events
        server.pluginManager.registerEvents(GuiListener(this), this)

        //Register commands
        BuildyCommands
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
    }
}