package io.github.takenoko4096.noctiluca

import io.github.takenoko4096.noctiluca.registry.command.execution.AssignableCommandExecution
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.Identifier
import net.minecraft.util.StringRepresentable

class Debugger private constructor(val identifier: Identifier, private val debugger: DebuggerCallable) : StringRepresentable {
    override fun getSerializedName(): String = identifier.toString()

    fun call(execution: AssignableCommandExecution<CommandSourceStack>) {
        debugger(execution)
    }

    companion object {
        private val debuggers = mutableMapOf<Identifier, DebuggerCallable>()

        fun register(identifier: Identifier, debugger: DebuggerCallable) {
            if (identifier in debuggers) {
                throw IllegalArgumentException("Mod '${identifier.namespace}' にはデバッガー '${identifier.path}' が既に登録されているため、重複して登録できません")
            }

            debuggers[identifier] = debugger
        }

        fun get(identifier: Identifier): Debugger? {
            return debuggers[identifier]?.let { Debugger(identifier, it) }
        }

        fun keys(): Set<Identifier> {
            return debuggers.keys
        }
    }

    typealias DebuggerCallable = AssignableCommandExecution<CommandSourceStack>.() -> Unit
}
