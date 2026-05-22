# Noctiluca

Fabric Modの分散する煩雑なコードをKotlin DSLで一箇所に記述することを目的としたライブラリMod

## Usage

### Features

#### Registries (including automatic data generation)
- `itemRegistry`
- `blockRegistry`
- `translationRegistry`
- `commandRegistry`
- `tagRegistry`
- `creativeModeTabRegistry`

#### Utilities
- `component()`
- `ItemStackBuilder`

#### Interactions
- `ContainerInteraction`

### Setup

> [!WARNING]
> - `fabric.mod.json` に `TestMod`, `TestClientMod`, `TestModDataGenerator` の3つすべてについて記述すること
> - `build.gradle(.kts)` の `fabircApi` に `configureDataGeneration { client = true }` を記述すること
> - Noctiluca は MOD であり、 `mods` フォルダに配置する必要があります

```kotlin
object TestMod : NoctilucaModInitializer("testmod") {
    override fun onInitialize() {}
}

object TestClientMod : NoctilucaClientModInitializer(TestMod)

object TestModDataGenerator : NoctilucaDataGenerator(TestMod)
```

#### 実装予定

- `BlockEntity` 追加
- モデル生成API拡張
- エンティティ追加

など

### Example Mod

[**TestMod**](https://github.com/takenoko4096/TestMod)
