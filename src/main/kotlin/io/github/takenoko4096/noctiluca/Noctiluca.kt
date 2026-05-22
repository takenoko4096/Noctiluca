package io.github.takenoko4096.noctiluca

import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import io.github.takenoko4096.noctiluca.text.RgbColor
import io.github.takenoko4096.noctiluca.text.component
import io.github.takenoko4096.noctiluca.ui.container.ContainerInteraction
import io.github.takenoko4096.noctiluca.ui.container.ItemButton
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Items

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

        val interaction = ContainerInteraction {
            title {
                text("container interaction test")
            }

            onClose {
                player.sendSystemMessage(component {
                    text("closed")
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
                text(CustomContainerMenu.menuOpens.size.toString())
            }
        }
    }
}
