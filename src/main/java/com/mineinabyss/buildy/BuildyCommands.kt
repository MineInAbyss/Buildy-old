package com.mineinabyss.buildy

import com.mineinabyss.buildy.gui.BuildyGui
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.success

@ExperimentalCommandDSL
object BuildyCommands : IdofrontCommandExecutor() {
    override val commands = commands(buildy) {
        command("buildy", desc = "Opens the buildy gui if no arguments given") {
            command("create", desc = "creates a new area") {
                val name by stringArg()
                action {
                    sender.success("Creating region called $name")
                }
            }

            playerAction {
                BuildyGui(player).show(player)
            }
        }
    }
}