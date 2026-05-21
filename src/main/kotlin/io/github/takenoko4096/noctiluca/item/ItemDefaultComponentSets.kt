package io.github.takenoko4096.noctiluca.item

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks

class ItemDefaultComponentSets internal constructor() {
    fun sword(durability: Int, attackDamage: Double) = ItemDefaultComponentSet {
        maxStackSize(1)
        maxDamage(durability)
        damage(0)
        weapon {
            itemDamagePerAttack = 1
        }
        attributeModifiers {
            attackDamage {
                slot {
                    weapon.mainhand()
                }
                operation {
                    addValue()
                }
                value = attackDamage
                display {
                    builtin()
                }
            }
            attackSpeed {
                slot {
                    weapon.mainhand()
                }
                operation {
                    addValue()
                }
                value = -2.4
                display {
                    builtin()
                }
            }
        }
        tool {
            damagePerBlock = 2
            defaultMiningSpeed = 1.0f
            canDestroyBlocksInCreative = false

            rules {
                tag(BlockTags.SWORD_INSTANTLY_MINES) {
                    speed = Float.MAX_VALUE
                }
                tag(BlockTags.SWORD_EFFICIENT) {
                    speed = 1.5f
                }
                blocks(Blocks.COBWEB) {
                    speed = 15.0f
                }
            }
        }
    }
}