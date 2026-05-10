package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.item.ItemStackBuilder
import io.github.takenoko4096.starlight.text.RgbColor
import io.github.takenoko4096.starlight.text.component
import io.github.takenoko4096.starlight.container.CustomContainerMenuBuilder
import io.github.takenoko4096.starlight.ui.container.ContainerInteraction
import io.github.takenoko4096.starlight.ui.container.ItemButton
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments

object Noctiluca : NoctilucaModInitializer("noctiluca") {
    override fun onInitialize() {
        val debuggerNameArgumentType = commandRegistry.registerArgumentType<Debugger>("debugger") {
            parses {
                val first = reader.readUnquotedString()

                return@parses if (reader.canRead() && reader.peek() == ':') {
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
            }

            "debugger" {
                "debugger_name"(debuggerNameArgumentType) {
                    executes {
                        "debugger_name"<Debugger>().call(this)
                    }

                    catches {
                        val name = "debugger_name"<Debugger>()

                        logger.error("error on debugger: ${name.identifier}", error)
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

        debugger("menu_1") {
            val registryAccess = context.source.registryAccess()

            val item = ItemStackBuilder(Items.DIAMOND).build(this@Noctiluca, registryAccess)
            val context = context

            val menu = CustomContainerMenuBuilder {
                title {
                    text("test menu of 1")
                }

                contents(1) {
                    setItem(4, item)
                }

                onClick { player, i, i1, input, slots ->
                    context.source.sendSystemMessage(component {
                        text("player=${player.name.string}")
                        linebreak()
                        text("slot=$i")
                        linebreak()
                        text("button=$i1")
                        linebreak()
                        text("input=${input.name}")
                    })
                }
            }

            context.source.player?.openMenu(menu.build())
        }

        debugger("menu_2") {
            val registryAccess = context.source.registryAccess()

            val item = ItemStackBuilder(Items.DIAMOND).build(this@Noctiluca, registryAccess)
            val context = context

            val menu = CustomContainerMenuBuilder {
                title {
                    text("test menu of 2")
                }

                contents(2) {
                    setItem(4, item)
                }

                onClick { player, i, i1, input, slots ->
                    context.source.sendSystemMessage(component {
                        text("player=${player.name.string}")
                        linebreak()
                        text("slot=$i")
                        linebreak()
                        text("button=$i1")
                        linebreak()
                        text("input=${input.name}")
                    })
                }
            }

            context.source.player?.openMenu(menu.build())
        }

        debugger("menu_3") {
            val registryAccess = context.source.registryAccess()

            val item = ItemStackBuilder(Items.DIAMOND).build(this@Noctiluca, registryAccess)
            val context = context

            val menu = CustomContainerMenuBuilder {
                title {
                    text("test menu of 3")
                }

                contents(3) {
                    setItem(4, item)
                }

                onClick { player, i, i1, input, slots ->
                    context.source.sendSystemMessage(component {
                        text("player=${player.name.string}")
                        linebreak()
                        text("slot=$i")
                        linebreak()
                        text("button=$i1")
                        linebreak()
                        text("input=${input.name}")
                    })
                }
            }

            context.source.player?.openMenu(menu.build())
        }

        debugger("menu_6_free") {
            val registryAccess = context.source.registryAccess()

            val item = ItemStackBuilder(Items.DIAMOND).build(this@Noctiluca, registryAccess)
            val context = context

            val menu = CustomContainerMenuBuilder {
                title {
                    text("test menu of 6")
                }

                contents(6) {
                    setItem(4, item)
                }
            }

            context.source.player?.openMenu(menu.build())
        }

        debugger("container_interaction") {
            val interaction = ContainerInteraction.create {
                title {
                    text("container interaction test")
                }

                contents(6) {
                    val button = ItemButton.of(Items.EMERALD) {
                        components {
                            itemName(component {
                                text("button test 0 - 1")
                            })

                            enchantments {
                                enchant(Enchantments.LUNGE, 1)
                            }
                        }

                        onClick {
                            player.sendSystemMessage(component {
                                text("button 0 pressed!")
                            })

                            set(0, ItemButton.of(Items.DIAMOND) {
                                components {
                                    itemName(component {
                                        text("button test 0 - 2")
                                    })
                                }

                                onClick {
                                    set(0, button)
                                }
                            })
                        }
                    }

                    set(0, button)
                }
            }

            context.source.player?.run {
                interaction.open(this)
            }
        }
    }
}
