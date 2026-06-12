package io.github.takenoko4096.noctiluca.datagen.model

import io.github.takenoko4096.noctiluca.datagen.model.builder.ClientItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.block.*
import io.github.takenoko4096.noctiluca.render.model.block.multipart.NonClientCombinedCondition
import io.github.takenoko4096.noctiluca.render.model.block.multipart.NonClientSingleCondition
import io.github.takenoko4096.noctiluca.render.model.block.multipart.AbstractNonClientCondition
import io.github.takenoko4096.noctiluca.render.model.block.multipart.NonClientMultiParts
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelHandle
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher
import net.minecraft.client.renderer.block.dispatch.VariantMutator
import net.minecraft.client.renderer.block.dispatch.multipart.CombinedCondition
import net.minecraft.client.renderer.block.dispatch.multipart.Condition
import net.minecraft.data.BlockFamily
import net.minecraft.util.random.WeightedList
import net.minecraft.world.level.block.Block

class BlockModelVariantsRegistrar internal constructor(
    internal val blockModelGenerators: BlockModelGenerators,
    internal val block: Block,
    internal val itemModelHandle: ItemModelHandle?,
    internal val variants: PropertyDispatching?,
    internal val family: BlockFamily?
) {
    private fun toClientMutator(nonClientMutator: NonClientVariantMutator): VariantMutator {
        return when (nonClientMutator) {
            NonClientVariantMutator.X_ROT_90 -> BlockModelGenerators.X_ROT_90
            NonClientVariantMutator.X_ROT_180 -> BlockModelGenerators.X_ROT_180
            NonClientVariantMutator.X_ROT_270 -> BlockModelGenerators.X_ROT_270
            NonClientVariantMutator.Y_ROT_90 -> BlockModelGenerators.Y_ROT_90
            NonClientVariantMutator.Y_ROT_180 -> BlockModelGenerators.Y_ROT_180
            NonClientVariantMutator.Y_ROT_270 -> BlockModelGenerators.Y_ROT_270
            NonClientVariantMutator.UV_LOCK -> BlockModelGenerators.UV_LOCK
        }
    }

    private fun toClient(nonClient: NonClientBlockModelVariant): MultiVariant {
        val clientModel = ClientModel.getOrCreate(block, nonClient.model, blockModelGenerators)
        var clientModelVariant = BlockModelGenerators.plainVariant(clientModel.identifier)
        for (mutator in nonClient.mutators) {
            clientModelVariant = clientModelVariant.with(toClientMutator(mutator))
        }
        return clientModelVariant
    }

    private fun variants0(variants0: PropertyVariants0): MultiVariantGenerator {
        return MultiVariantGenerator.dispatch(
            block,
            toClient(PropertyVariants0.getVariant(variants0))
        )
    }

    private fun <T : Comparable<T>> variants1(variants1: PropertyVariants1<T>): MultiVariantGenerator {
        val empty = MultiVariantGenerator.dispatch(block)

        val dispatch = PropertyDispatch.initial(variants1.property)

        for (select in variants1.selects) {
            dispatch.select(
                select.value1,
                toClient(select.variant)
            )
        }

        return empty.with(dispatch)
    }

    private fun <T : Comparable<T>, U : Comparable<U>> variants2(variants2: PropertyVariants2<T, U>): MultiVariantGenerator {
        val empty = MultiVariantGenerator.dispatch(block)

        val dispatch = PropertyDispatch.initial(variants2.property1, variants2.property2)

        for (select in variants2.selects) {
            dispatch.select(
                select.value1,
                select.value2,
                toClient(select.variant)
            )
        }

        return empty.with(dispatch)
    }

    private fun toClient(condition: AbstractNonClientCondition): Condition = when (condition) {
        is NonClientSingleCondition<*> -> toClientSingle(condition)
        is NonClientCombinedCondition.And -> toClientAnd(condition)
        is NonClientCombinedCondition.Or -> toClientOr(condition)
    }

    private fun <T : Comparable<T>> toClientSingle(term: NonClientSingleCondition<T>): Condition {
        return ConditionBuilder()
            .term(
                term.property,
                term.cases.first(),
                *term.firstExclusive
            )
            .build()
    }

    private fun toClientAnd(and: NonClientCombinedCondition.And): Condition {
        return CombinedCondition(
            CombinedCondition.Operation.AND,
            and.terms.map(::toClient)
        )
    }

    private fun toClientOr(or: NonClientCombinedCondition.Or): Condition {
        return CombinedCondition(
            CombinedCondition.Operation.OR,
            or.terms.map(::toClient)
        )
    }

    private fun multiPart(multiParts: NonClientMultiParts): MultiPartGenerator {
        val empty = MultiPartGenerator.multiPart(block)

        for ((`when`, apply) in multiParts.multiParts) {
            val variant = MultiVariant(WeightedList.of(apply.flatMap { toClient(it).variants.unwrap() }))

            if (`when` == null) {
                empty.with(variant)
            }
            else {
                val condition = toClient(`when`)
                empty.with(condition, variant)
            }
        }

        return empty
    }

    internal fun register() {
        if (itemModelHandle != null) {
            val client = ClientItemModelHandle.toClient(
                ItemModelGenerators(blockModelGenerators.itemModelOutput, blockModelGenerators.modelOutput),
                block.asItem(),
                itemModelHandle
            )
            blockModelGenerators.itemModelOutput.accept(block.asItem(), client.convert())
        }

        val generator = when (variants) {
            is PropertyVariants0 -> variants0(variants)
            is PropertyVariants1<*> -> variants1(variants)
            is PropertyVariants2<*, *> -> variants2(variants)
            is NonClientMultiParts -> multiPart(variants)
            null -> {
                if (family == null) {
                    throw IllegalStateException("cannot generate block family: maybe this is caused by both of models.block and withXX() is unset. please use one or the other")
                }
                else {
                    blockModelGenerators.family(block).generateFor(family)
                    return // not to duplication
                }
            }
            else -> throw IllegalStateException("NEVER HAPPENS")
        }

        blockModelGenerators.blockStateOutput.accept(generator)
    }
}
