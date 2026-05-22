# Noctiluca

コンテンツ追加系はクライアント用コードもアセット生成コードも全部一箇所にまとめて書きたい！！！！！！

<br>っていう人(我)のためのFabric Mod 用の API

## Usage

### Features

- 新規アイテムのDSLによる完全 `main` ソースセット内定義
- 新規ブロックのDSLによる完全 `main` ソースセット内定義
- 新規コマンドのDSLによる定義
- 主にコンテンツ追加のケースにおけるクライアントコードの省略
- アイテムスタックビルダー
- テキストコンポーネントビルダー
- 新規クリエイティブタブのDSLによる定義
- NBT編集／コンポーネントへのシリアライザー
- 汎用イベントディスパッチャー
- `runDatagen` 実行によるアイテムモデル／ブロックモデル／翻訳ファイル／タグの自動定義
- その他ユーティリティ等

### Setup

> [!WARNING]
> - `fabric.mod.json` に `TestMod`, `TestClientMod`, `TestModDataGenerator` の3つすべてについて記述すること
> - `build.gradle(.kts)` に `configureDataFeneration { client = true }` を記述すること
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
