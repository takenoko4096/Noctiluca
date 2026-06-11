package io.github.takenoko4096.noctiluca.render.model.block

class PropertyVariants0(internal var variant: NonClientBlockModelVariant? = null) : PropertyDispatching() {
    companion object {
        fun getVariant(p: PropertyVariants0): NonClientBlockModelVariant {
            return p.variant ?: throw IllegalStateException()
        }
    }
}
