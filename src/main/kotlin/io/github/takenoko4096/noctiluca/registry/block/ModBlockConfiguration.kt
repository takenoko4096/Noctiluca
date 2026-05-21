package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.registry.translation.ModTranslationConfiguration
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelHandle
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.BlockFamily
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.WallBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType

@NoctilucaDsl
class ModBlockConfiguration(internal val registry: ModBlockRegistry, internal val identifier: String) {
    val blockResourceKey: ResourceKey<Block> = ResourceKey.create(
        Registries.BLOCK,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    val itemResourceKey: ResourceKey<Item> = ResourceKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    internal var blockProperties: BlockBehaviour.Properties? = null

    internal var itemProperties: Item.Properties? = null

    internal var customBehaviourCreator: ((BlockBehaviour.Properties) -> Block) = { Block(it) }

    internal var renderingConfig: BlockRenderingConfiguration = BlockRenderingConfiguration(this)

    internal var translation = ModTranslationConfiguration()

    internal var withSlab: Boolean = false
    internal var withStairs: Boolean = false
    internal var withWall: Boolean = false
    internal var withFence: Boolean = false
    internal var withFenceGate: WoodType? = null

    internal var familyBuilder: BlockFamily.Builder? = null

    fun blockProperties(callback: BlockPropertiesConfiguration.() -> Unit) {
        val bpc = BlockPropertiesConfiguration(this, callback)
        blockProperties = bpc.build()
    }

    fun itemProperties(callback: BlockItemPropertiesConfiguration.() -> Unit) {
        val ipc = BlockItemPropertiesConfiguration(this, callback)
        itemProperties = ipc.build()
    }

    fun rendering(callback: BlockRenderingConfiguration.() -> Unit) {
        val brc = BlockRenderingConfiguration(this)
        brc.callback()
        renderingConfig = brc
        if (renderingConfig.modelConfig == null) {
            throw IllegalStateException("ブロック '${blockResourceKey.identifier()}' のモデル設定がありません")
        }
    }

    fun customBehaviour(callback: CustomBehaviourConfiguration.() -> Unit): CustomBehaviourInfo {
        val cbc = CustomBehaviourConfiguration()
        cbc.callback()
        customBehaviourCreator = cbc.build()
        return CustomBehaviourInfo(Properties(cbc.propertyDefinitions.toSet()))
    }

    fun translation(callback: ModTranslationConfiguration.() -> Unit) {
        val tc = ModTranslationConfiguration()
        tc.callback()
        translation = tc
    }

    fun withItem() {
        itemProperties {}
    }

    fun withSlab() {
        withSlab = true
    }

    fun withStairs() {
        withStairs = true
    }

    fun withWall() {
        withWall = true
    }

    fun withFence() {
        withFence = true
    }

    fun withFenceGate(woodType: WoodType = WoodType.OAK) {
        withFenceGate = woodType
    }

    internal fun register(): Block {
        if (blockProperties == null) {
            throw IllegalStateException("'blockProperties' is unset!")
        }

        val boundBlockProperties = blockProperties!!

        val fullBlock = withSlab || withStairs || withWall || withFence || (withFenceGate != null)

        if (fullBlock) {
            itemProperties {}
        }

        if (!fullBlock && renderingConfig.modelConfig?.blockModelConfig == null) {
            throw IllegalStateException("failed to register block '${blockResourceKey.identifier()}': 'rendering.models' is unset despite there are no withXX()")
        }
        else if (fullBlock && renderingConfig.modelConfig?.blockModelConfig != null) {
            throw IllegalStateException("failed to register block '${blockResourceKey.identifier()}': do not set 'rendering.models.block' while using withXX(): these options require parent block is a 'cube all' block")
        }

        val block = customBehaviourCreator(boundBlockProperties)
        Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block)

        if (itemProperties != null) {
            val item = BlockItem(block, itemProperties!!)
            Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item)
        }

        if (withSlab) {
            val entry = registerFamilyMember(block, "slab", BlockFamily.Builder::slab) { a, b ->
                SlabBlock(b)
            }

            registry.mod.tagRegistry.registerOfBlock(BlockTags.SLABS) {
                entry(entry)
                replace = false
            }
        }

        if (withStairs) {
            val entry = registerFamilyMember(block, "stairs", BlockFamily.Builder::stairs) { a, b ->
                StairBlock(a.defaultBlockState(), b)
            }

            registry.mod.tagRegistry.registerOfBlock(BlockTags.STAIRS) {
                entry(entry)
                replace = false
            }
        }

        if (withWall) {
            val entry = registerFamilyMember(block, "wall", BlockFamily.Builder::wall) { a, b ->
                WallBlock(b)
            }

            registry.mod.tagRegistry.registerOfBlock(BlockTags.WALLS) {
                entry(entry)
                replace = false
            }
        }

        if (withFence) {
            val entry = registerFamilyMember(block, "fence", BlockFamily.Builder::fence) { a, b ->
                FenceBlock(b)
            }

            registry.mod.tagRegistry.registerOfBlock(BlockTags.FENCES) {
                entry(entry)
                replace = false
            }
        }

        if (withFenceGate != null) {
            val entry = registerFamilyMember(block, "fence_gate", BlockFamily.Builder::fenceGate) { a, b ->
                val woodType = withFenceGate!!
                FenceGateBlock(woodType, b)
            }

            registry.mod.tagRegistry.registerOfBlock(BlockTags.FENCE_GATES) {
                entry(entry)
                replace = false
            }
        }

        return block
    }

    private fun <T : Block> registerFamilyMember(parent: Block, suffix: String, familyAppender: BlockFamily.Builder.(T) -> BlockFamily.Builder, constructor: (Block, BlockBehaviour.Properties) -> T): T {
        val identifier = registry.mod.identifierOf(blockResourceKey.identifier().path + '_' + suffix)
        val blockKey = ResourceKey.create(Registries.BLOCK, identifier)
        val itemKey = ResourceKey.create(Registries.ITEM, identifier)

        val block = constructor(parent, BlockBehaviour.Properties.ofFullCopy(parent).setId(blockKey))
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block)

        val translationKey = "block.${registry.mod.identifier}.${identifier.path}"
        registry.mod.translationRegistry.register(translationKey) {
            enUs = identifier.path
                .replace("_([a-z])".toRegex()) { ' ' + it.groupValues[1].uppercase() }
                .replace("^([a-z])".toRegex()) { it.groupValues[1].uppercase() }
        }

        val blockItem = BlockItem(block, Item.Properties().setId(itemKey).overrideDescription(translationKey))
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem)

        if (familyBuilder == null) familyBuilder = BlockFamily.Builder(parent)
        familyBuilder!!.familyAppender(block)
        registry.blocks[blockKey] = block
        return block
    }

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun blockModelLegacy(): BlockRenderingConfiguration.SingleArgBlockModel? {
            return configuration.renderingConfig.modelConfig?.blockModelConfig?.model
        }

        fun family(): BlockFamily? {
            return configuration.familyBuilder?.family
        }

        fun blockModelVariants(): PropertyVariants? {
            return configuration.renderingConfig.modelConfig?.blockModelConfig?.variants
        }

        fun blockItemModel(): ItemModelHandle? {
            return configuration.renderingConfig.modelConfig?.itemModelConfig?.handle
        }

        fun translation(): ModTranslationConfiguration {
            return configuration.translation
        }

        fun defaultTint(): (BlockState) -> Int {
            return configuration.renderingConfig.tintConfig.defaultColorGetter
        }

        fun inWorldTint(): (BlockState, BlockPos, Level) -> Int {
            return configuration.renderingConfig.tintConfig.colorGetter
        }

        fun terrainParticleTint(): (BlockState, BlockPos, Level) -> Int {
            return configuration.renderingConfig.tintConfig.particleColorGetter
        }
    }

    companion object {
        fun getAccessorForClient(configuration: ModBlockConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
