package io.github.takenoko4096.noctiluca

import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import io.github.takenoko4096.noctiluca.container.PackSavable
import io.github.takenoko4096.noctiluca.math.Vector3d
import io.github.takenoko4096.noctiluca.math.toPosition3i
import io.github.takenoko4096.noctiluca.math.toVector3d
import io.github.takenoko4096.noctiluca.nbt.NbtSerializer
import io.github.takenoko4096.noctiluca.network.ServerboundDialogClosePayload
import io.github.takenoko4096.noctiluca.network.ServerboundDialogEscapePayload
import io.github.takenoko4096.noctiluca.network.ServerboundCustomPacketPayloadReceiver
import io.github.takenoko4096.noctiluca.portal.PortalAxis
import io.github.takenoko4096.noctiluca.portal.PortalFinder
import io.github.takenoko4096.noctiluca.portal.PortalType
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.noctiluca.text.RgbColor
import io.github.takenoko4096.noctiluca.text.component
import io.github.takenoko4096.noctiluca.ui.container.ContainerInteraction
import io.github.takenoko4096.noctiluca.ui.container.ItemButton
import io.github.takenoko4096.noctiluca.ui.dialog.DynamicDialogHolder
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.player.BlockEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.resources.Identifier
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.PushReaction

object Noctiluca : NoctilucaModInitializer("noctiluca") {
    private fun initializeSystem() {
        PayloadTypeRegistry.serverboundPlay().register(ServerboundDialogEscapePayload.TYPE, ServerboundDialogEscapePayload.CODEC)
        PayloadTypeRegistry.serverboundPlay().register(ServerboundDialogClosePayload.TYPE, ServerboundDialogClosePayload.CODEC)

        ServerPlayNetworking.registerGlobalReceiver(
            ServerboundDialogEscapePayload.TYPE,
            ServerboundCustomPacketPayloadReceiver::escapeDialogPayload
        )

        ServerPlayNetworking.registerGlobalReceiver(
            ServerboundDialogClosePayload.TYPE,
            ServerboundCustomPacketPayloadReceiver::closeDialogPayload
        )

        ServerPlayerEvents.LEAVE.register(CustomContainerMenu::remove)
    }

    override fun onInitialize() {
        initializeSystem()

        val debuggerArgumentType = commandRegistry.registerArgumentType<Debugger>("debugger") {
            parses {
                val first = reader.readUnquotedString()

                if (reader.canRead() && reader.peek() == ':') {
                    reader.skip()
                    Debugger.get(Identifier.fromNamespaceAndPath(first, reader.readUnquotedString()))
                        ?: throw exception("デバッガ '$identifier' は存在しません")
                }
                else {
                    Debugger.get(Identifier.fromNamespaceAndPath(identifier, first))
                        ?: throw exception("デバッガ '$identifier' は存在しません")
                }
            }

            suggests {
                strings(Debugger.keys().map(Identifier::toString))
                strings(Debugger.keys().filter { it.namespace == identifier }.map(Identifier::toString))
            }
        }

        commandRegistry.register(identifier) {
            "logger" {
                "info" {
                    "message"(greedyString()) {
                        executes {
                            val message = "message"<String>()
                            logger.info(message)
                            context.successful {
                                text("ログに書き込みました: ")
                                textColor(RgbColor.AQUA)
                                text(message)
                            }
                        }
                    }
                }

                "warn" {
                    "message"(greedyString()) {
                        executes {
                            val message = "message"<String>()
                            logger.warn(message)
                            context.successful {
                                text("ログに書き込みました: ")
                                textColor(RgbColor.GOLD)
                                text(message)
                            }
                        }
                    }
                }

                "error" {
                    "message"(greedyString()) {
                        executes {
                            val message = "message"<String>()
                            logger.error(message)
                            context.successful {
                                text("ログに書き込みました: ")
                                textColor(RgbColor.RED)
                                text(message)
                            }
                        }
                    }
                }

                "debug" {
                    "message"(greedyString()) {
                        executes {
                            val message = "message"<String>()
                            logger.debug(message)
                            context.successful {
                                text("ログに書き込みました: ")
                                textColor(RgbColor.LIGHT_PURPLE)
                                text(message)
                            }
                        }
                    }
                }
            }

            "debugger" {
                "debugger"(debuggerArgumentType) {
                    executes {
                        "debugger"<Debugger>().call(this)
                    }

                    catches {
                        val debugger = "debugger"<Debugger>()
                        logger.error("Failed to execute debugger ${debugger.identifier}", error)
                    }
                }
            }

            "version" {
                executes {
                    context.successful {
                        gradient(RgbColor.YELLOW, RgbColor.GOLD, RgbColor.YELLOW) {
                            text("✨--------------------------------------------✨")
                            linebreak()
                        }

                        gradient(RgbColor.BLUE, RgbColor.LIGHT_PURPLE) {
                            bold()
                            text("A versatile library for fabric mod - Noctiluca")
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("noctiluca version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.NOCTILUCA_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("minecraft version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.MINECRAFT_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("java version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.JAVA_VERSION.toString())
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("fabric loader version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_LOADER_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("fabric api version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_API_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("fabric loom version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_LOOM_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("kotlin loader version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.KOTLIN_LOADER_VERSION)
                        }

                        gradient(RgbColor.YELLOW, RgbColor.GOLD, RgbColor.YELLOW) {
                            linebreak()
                            text("✨--------------------------------------------✨")
                        }
                    }
                }
            }
        }

        debugger("test") {
            context.successful {
                gradient(RgbColor.BLUE, RgbColor.LIGHT_PURPLE, RgbColor.GRAY) {
                    text("THIS IS A TEST MESSAGE FROM NOCTILUCA!")
                }
            }
        }

        val interaction = ContainerInteraction {
            title {
                text("container interaction test")
            }

            onClose {
                player.sendSystemMessage(component {
                    text("container interaction closed")
                })
            }

            contents(6) {
                val decoration = ItemButton.of(Items.BLUE_STAINED_GLASS_PANE) {
                    components {
                        tooltipDisplay {
                            hideTooltip = true
                        }

                        enchantmentGlintOverride(true)
                    }
                }

                fillHorizontally(0, decoration)
                fillHorizontally(lastRowIndex(), decoration)
                fillVertically(0, decoration)
                fillVertically(lastColumnIndex(), decoration)

                put(indexAt(horizontalCenterIndex(), 1), ItemButton.of(Items.BOOK) {
                    components {
                        itemName {
                            text("test")
                        }

                        enchantmentGlintOverride(true)
                    }

                    onClick {
                        player.sendSystemMessage(component {
                            text("test message")
                        })
                    }
                })
            }
        }

        debugger("container_interaction") {
            context.source.player?.run {
                interaction.open(this)
            }
        }

        debugger("custom_container_menu_count") {
            context.successful {
                text(CustomContainerMenu.menus.size.toString())
            }
        }

        val c = DynamicDialogHolder.confirmation {
            body {
                message {
                    contents {
                        text("confirmation test")
                    }
                }

                item(Items.STONE, 16) {
                    description {
                        contents {
                            text("stone")
                        }
                    }

                    components {
                        enchantmentGlintOverride(true)
                    }
                }
            }

            yes {
                label {
                    text("yes")
                }

                onClick {
                    player.sendSystemMessage(component {
                        text("pressed: yes")
                    })

                    response.run {
                        player.sendSystemMessage(component {
                            text(this@run.toString())
                        })
                    }
                }
            }

            no {
                label {
                    text("no")
                }

                onClick {
                    player.sendSystemMessage(component {
                        text("pressed: no")
                    })

                    response.run {
                        player.sendSystemMessage(component {
                            text(this@run.toString())
                        })
                    }
                }
            }

            onEscape {
                player.sendSystemMessage(component {
                    text("escape")
                })
            }

            onClose {
                player.sendSystemMessage(component {
                    text("close")
                })
            }

            inputs {
                checkBox("foo") {
                    label {
                        text("check box foo")
                    }

                    initial = false
                }

                option("bar") {
                    initial = "a"

                    label {
                        text("a or b")
                    }

                    entries {
                        entry("a") {
                            text("A!")
                        }

                        entry("b") {
                            text("B!")
                        }
                    }
                }
            }
        }

        val n = DynamicDialogHolder.notice {
            body {
                message {
                    contents {
                        text("notice test")
                    }
                }
            }

            action {
                label {
                    text("action")
                }

                tooltip {
                    text("tooltip")
                }

                onClick {
                    player.sendSystemMessage(component {
                        text("action clicked")
                    })
                }
            }

            onEscape {
                player.sendSystemMessage(component {
                    text("escape")
                })
            }

            onClose {
                player.sendSystemMessage(component {
                    text("close")
                })
            }
        }

        val m = DynamicDialogHolder.multiAction {
            name {
                text("multi action dialog test")
            }

            body {
                message {
                    contents {
                        text("plain message test")
                    }
                }

                item(Items.DIAMOND) {
                    description {
                        contents {
                            text("item icon test")
                        }
                    }

                    components {
                        enchantmentGlintOverride(true)
                    }
                }
            }

            inputs {
                checkBox("check_box") {
                    label {
                        text("check box test")
                    }

                    initial = false
                }

                option("option") {
                    label {
                        text("option test")
                    }

                    initial = "apple"

                    entries {
                        entry("apple") {
                            text("apple")
                        }

                        entry("banana") {
                            text("banana")
                        }

                        entry("grape") {
                            text("grape")
                        }
                    }
                }

                slider("slider") {
                    label {
                        text("slider test")
                    }

                    initial = 0f

                    range = -50f..50f

                    step = 1f

                    format = "$labelTemplate is: $valueTemplate"
                }

                textField("text_field") {
                    label {
                        text("text field test")
                    }

                    initial = "initial text"

                    maxLength = 128

                    multilines(maxLines = 6, height = null)
                }
            }

            actions {
                action {
                    label {
                        text("action test 1")
                    }

                    tooltip {
                        text("tooltip test 1")
                    }

                    onClick {
                        player.sendSystemMessage(component { text("1") })
                        player.sendSystemMessage(NbtSerializer.serialize(response.toCompound()))
                    }
                }

                action {
                    label {
                        text("action test 2")
                    }

                    tooltip {
                        text("tooltip test 2")
                    }

                    onClick {
                        player.sendSystemMessage(component { text("2") })
                        player.sendSystemMessage(NbtSerializer.serialize(response.toCompound()))
                    }
                }

                action {
                    label {
                        text("action test 3")
                    }

                    tooltip {
                        text("tooltip test 3")
                    }

                    onClick {
                        player.sendSystemMessage(component { text("3") })
                        player.sendSystemMessage(NbtSerializer.serialize(response.toCompound()))
                    }
                }
            }

            exitAction {
                label {
                    text("exit action test")
                }

                tooltip {
                    text("exit action tooltip test")
                }

                onClick {
                    player.sendSystemMessage(component { text("exit") })
                    player.sendSystemMessage(NbtSerializer.serialize(response.toCompound()))
                }
            }

            onEscape {
                player.sendSystemMessage(component {
                    text("escape: ")
                    response.string("a")
                    component(NbtSerializer.serialize(response.toCompound()))
                })
            }

            onClose {
                player.sendSystemMessage(component {
                    text("close: ")
                    component(NbtSerializer.serialize(response.toCompound()))
                })
            }
        }

        val packSavable = PackSavable(
            component {
                text("pack savable")
            },
            3,
            onUpdate = {
                getSerializedContents()
            }
        )

        debugger("pack_savable") {
            context.source.player?.run {
                packSavable.openPack(this)
            }
        }

        debugger("confirmation_dialog") {
            context.source.player?.run {
                c.buildOpen(this)
            }
        }

        debugger("notice_dialog") {
            context.source.player?.run {
                n.buildOpen(this)
            }
        }

        debugger("multi_action_dialog") {
            context.source.player?.run {
                m.buildOpen(this)
            }
        }

        val customPortal = blockRegistry.register("custom_portal") {
            blockProperties {
                sound = SoundType.GLASS
                destroyTime = Float.POSITIVE_INFINITY
                occlusion = false
                collision = false
                pushReaction = PushReaction.DESTROY
                isReplaceable = true
            }

            val properties = blockStates {
                enumerationProperty("axis", PortalAxis::class) {
                    defaultValue = PortalAxis.X
                }
            }

            voxelShape {
                val x = box(Vector3d(0.0, 0.0, 6.0), Vector3d(16.0, 16.0, 10.0))
                val z = box(Vector3d(6.0, 0.0, 0.0), Vector3d(10.0, 16.0, 16.0))

                when (blockState.getValue(properties.enumeration("axis", PortalAxis::class))) {
                    PortalAxis.X -> x
                    PortalAxis.Z -> z
                }
            }

            model {
                val model = blockModels.fromParent(
                    identifierOf("block/custom_portal_ew_parent"),
                    mapOf(
                        "portal" to blockDefaultTexturePath,
                        "particle" to blockDefaultTexturePath
                    )
                )

                block {
                    variants(properties.enumeration("axis", PortalAxis::class)) {
                        case(PortalAxis.X, model.toVariant(NonClientVariantMutator.Y_ROT_90))
                        case(PortalAxis.Z, model.toVariant())
                    }
                }
            }

            color {
                default { RgbColor.AQUA.withAlpha(192) }

                inWorld { state, pos, level -> RgbColor.AQUA.withAlpha(192) }
            }
        }

        val finder = PortalFinder(PortalType(
            Blocks.GLOWSTONE,
            customPortal,
            21,
            21
        ))

        BlockEvents.USE_ITEM_ON.register { itemStack, blockState, level, blockPos, player, hand, result ->
            if (!itemStack.`is`(Items.WATER_BUCKET)) {
                return@register InteractionResult.PASS
            }

            val properties = blockRegistry.getProperties(customPortal)

            val portal = finder.findPortal(
                level,
                blockPos.toPosition3i() + result.direction.unitVec3.toVector3d().toPosition3i(false)
            ) ?: return@register InteractionResult.PASS

            if (!portal.isIgnitable()) {
                return@register InteractionResult.PASS
            }

            for (position3i in portal.portalPositions) {
                level.setBlockAndUpdate(
                    position3i.toBlockPos(),
                    portal.type.portalBlock.defaultBlockState().setValue(
                        properties.enumeration("axis", PortalAxis::class), portal.axis
                    )
                )
            }

            return@register InteractionResult.SUCCESS
        }
    }
}
