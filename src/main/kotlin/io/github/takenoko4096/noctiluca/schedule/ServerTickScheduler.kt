package io.github.takenoko4096.noctiluca.schedule

import io.github.takenoko4096.noctiluca.Noctiluca
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer

class ServerTickScheduler : AbstractGameTickScheduler<MinecraftServer>(ServerSideSchedulerTicker) {
    private object ServerSideSchedulerTicker : SchedulerTicker<MinecraftServer>() {
        init {
            ServerTickEvents.END_SERVER_TICK.register(::tick)
            Noctiluca.logger.info("init server scheduler ticker")
        }
    }
}
