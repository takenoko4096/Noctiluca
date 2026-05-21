package io.github.takenoko4096.noctiluca.registry.translation

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry

class ModTranslationRegistry(mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val translations = mutableMapOf<String, ModTranslationConfiguration>()

    fun register(key: String, callback: ModTranslationConfiguration.() -> Unit) {
        if (translations.contains(key)) {
            throw IllegalArgumentException("翻訳キー '$key' は既に定義されているため登録できません")
        }

        val tc = ModTranslationConfiguration()
        tc.callback()
        translations[key] = tc
    }

    fun getTranslations(): Map<String, ModTranslationConfiguration> {
        return translations.toMap()
    }
}
