package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.Input
import net.minecraft.server.dialog.input.BooleanInput
import net.minecraft.server.dialog.input.InputControl
import net.minecraft.server.dialog.input.NumberRangeInput
import net.minecraft.server.dialog.input.SingleOptionInput
import net.minecraft.server.dialog.input.TextInput
import java.util.Optional

class DialogInputConfiguration(callback: DialogInputConfiguration.() -> Unit) {
    private val inputs = mutableListOf<Input>()

    init {
        callback()
    }

    fun checkBox(key: String, callback: CheckBoxInputConfiguration.() -> Unit) {
        inputs.add(Input(key, CheckBoxInputConfiguration(callback).build()))
    }

    fun option(key: String, callback: OptionInputConfiguration.() -> Unit) {
        inputs.add(Input(key, OptionInputConfiguration(callback).build()))
    }

    fun slider(key: String, callback: SliderInputConfiguration.() -> Unit) {
        inputs.add(Input(key, SliderInputConfiguration(callback).build()))
    }

    fun textField(key: String, callback: TextFieldInputConfiguration.() -> Unit) {
        inputs.add(Input(key, TextFieldInputConfiguration(callback).build()))
    }

    internal fun build(): List<Input> {
        return inputs.toList()
    }

    abstract class InputConfiguration<T, C : InputControl> {
        protected var label: Component = Component.empty()

        var initial: T? = null

        fun label(callback: SectionComponentBuilder.() -> Unit) {
            label = component(callback)
        }

        internal abstract fun build(): C
    }

    class CheckBoxInputConfiguration(callback: CheckBoxInputConfiguration.() -> Unit) : InputConfiguration<Boolean, BooleanInput>() {
        init {
            callback()
        }

        override fun build(): BooleanInput {
            return BooleanInput(
                label,
                initial ?: false,
                "true",
                "false"
            )
        }
    }

    class OptionInputConfiguration(callback: OptionInputConfiguration.() -> Unit) : InputConfiguration<String, SingleOptionInput>() {
        private var entries: EntriesConfiguration? = null

        var width: Int = 200

        var labelVisible: Boolean = true

        init {
            callback()
        }

        fun entries(callback: EntriesConfiguration.() -> Unit) {
            entries = EntriesConfiguration(callback)
        }

        override fun build(): SingleOptionInput {
            if (entries == null) {
                throw IllegalStateException("dialog builder: entries in option input is unset")
            }

            if (initial == null) {
                throw IllegalStateException("dialog builder: initial in option input is unset")
            }

            return SingleOptionInput(
                width,
                entries!!.build(initial!!),
                label,
                labelVisible
            )
        }

        class EntriesConfiguration(callback: EntriesConfiguration.() -> Unit) {
            private val entries = mutableMapOf<String, Component>()

            init {
                callback()
            }

            internal fun build(initial: String): List<SingleOptionInput.Entry> {
                if (initial !in entries.keys) {
                    throw IllegalStateException("dialog builder: 'initial' in option input is unknown")
                }

               return entries.entries.map { (key, value) -> SingleOptionInput.Entry(key, Optional.of(value), key == initial) }
            }
        }
    }

    class SliderInputConfiguration(callback: SliderInputConfiguration.() -> Unit) : InputConfiguration<Float, NumberRangeInput>() {
        var width: Int = 200

        var format: String = "options.generic_value"

        var range: ClosedRange<Float>? = null

        var step: Float? = null

        init {
            callback()
        }

        fun format(format: String) {
            this.format = format
        }

        override fun build(): NumberRangeInput {
            if (range == null) {
                throw IllegalStateException("dialog builder: 'range' in slider input is unknown")
            }

            return NumberRangeInput(
                width,
                label,
                format,
                NumberRangeInput.RangeInfo(
                    range!!.start,
                    range!!.endInclusive,
                    initial?.let { Optional.of(it) } ?: Optional.empty(),
                    step?.let { Optional.of(it) } ?: Optional.empty()
                )
            )
        }
    }

    class TextFieldInputConfiguration(callback: TextFieldInputConfiguration.() -> Unit) : InputConfiguration<String, TextInput>() {
        private var multilineOptions: TextInput.MultilineOptions? = null

        var width: Int = 200

        var labelVisible: Boolean = true

        var maxLength: Int = 32

        init {
            callback()
        }

        fun multilines(maxLines: Int?, height: Int?) {
            multilineOptions = TextInput.MultilineOptions(
                maxLines?.let { Optional.of(it) } ?: Optional.empty(),
                height?.let { Optional.of(it) } ?: Optional.empty()
            )
        }

        override fun build(): TextInput {
            return TextInput(
                width,
                label,
                labelVisible,
                initial ?: "",
                maxLength,
                multilineOptions?.let { Optional.of(it) } ?: Optional.empty()
            )
        }
    }
}
