package com.mineinabyss.buildy

import com.mineinabyss.idofront.commands.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.onExecuteByPlayer
import com.mineinabyss.idofront.messaging.success

object BuildyCommands : IdofrontCommandExecutor() {
    override val commands = commands(buildy) {
        command("buildy") {
            onExecuteByPlayer {
                player.success("Opening gui!")
            }

            command("create"){
                onExecuteByPlayer {
                    player.success("Created!")
                }
            }
        }
    }
}