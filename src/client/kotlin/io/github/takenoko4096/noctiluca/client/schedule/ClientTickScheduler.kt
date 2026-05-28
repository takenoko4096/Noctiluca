package io.github.takenoko4096.noctiluca.client.schedule

import io.github.takenoko4096.noctiluca.schedule.AbstractGameTickScheduler
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

class ClientTickScheduler : AbstractGameTickScheduler<Minecraft>(ClientSchedulerTicker) {
    private object ClientSchedulerTicker : SchedulerTicker<Minecraft>() {
        init {
            ClientTickEvents.END_CLIENT_TICK.register(::tick)
        }
    }
}
