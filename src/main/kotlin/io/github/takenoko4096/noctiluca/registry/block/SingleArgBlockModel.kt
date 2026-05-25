package io.github.takenoko4096.noctiluca.registry.block

class SingleArgBlockModel internal constructor(val textureMap: SingleArgBlockTextureMap) {
    enum class SingleArgBlockTextureMap {
        TRIVIAL_CUBE,
        TRIVIAL_COLUMN,
        TRIVIAL_COLUMN_ALT,
        TRIVIAL_COLUMN_HORIZONTAL,
        TRIVIAL_COLUMN_HORIZONTAL_ALT,
        ANVIL,
        DOOR,
        LANTERN
    }
}
