# Gaplay (ギャプレイ)

“カップ麺が出来上がるまでの3分間”、
”冷凍食品のレンチンの4分間”、
“コーヒーができるまでの3分間”
手軽でおいしい食べ物には必ず”Gap(空白)”時間と
高カロリーが存在します。
何かをするにも何もできないし、だからといって暇な時間は作りたくないと思う瞬間が日常に潜んでいます。

そんな”Gap(空白)”を”Play(再生・運動)”で満たす
解決策が”Gaplay”です

## 📱 アプリ概要

ユーザーが設定した「目標時間」に合わせて、YouTube動画を検索します。
動画の再生時間と目標時間の差分を自動計算し、その時間をエクササイズ（スマホを振る「シェイク」など）の時間として割り当てます。

- **例**: 目標 10分、動画 7分の場合
    - 3分間のエクササイズ ＋ 7分間の動画視聴 = 合計 10分

## ✨ 主な機能

- **YouTube動画検索**: キーワードと目標時間を指定して動画を検索。
- **ギャップ計算**: 動画の長さに基づいて、必要なエクササイズ時間を自動算出。
- **エクササイズタイマー**: 動画再生前後にエクササイズパートを挿入し、カウントダウン。
- **シェイク検知**: `ShakeDetector` を使用し、スマホを振る運動に対応。
- **履歴管理**: Roomデータベースにより、一度検索・選択した動画情報をローカルに保存。

## 🛠 技術スタック

- **言語**: Kotlin
- **UI**: Jetpack Compose
- **非同期処理**: Coroutines, Flow
- **アーキテクチャ**: MVVM (Model-View-ViewModel) + Repositoryパターン
- **DI (依存注入)**: Hilt
- **ネットワーク**: Retrofit / OkHttp (YouTube Data API v3)
- **データベース**: Room
- **画像読み込み**: Coil
- **動画再生**: Android YouTube Player
- **ビルドツール**: Gradle (Kotlin DSL), Version Catalog (libs.versions.toml)

## 🚀 セットアップ

このプロジェクトをビルドするには、YouTube Data API v3 のキーが必要です。

1. [Google Cloud Console](https://console.cloud.google.com/) でプロジェクトを作成し、YouTube Data API v3 を有効にします。
2. APIキーを取得します。
3. プロジェクトルートにある `local.properties` ファイルに、以下の行を追加します。

```properties
YOUTUBE_API_KEY=あなたのAPIキー
```

4. Android Studioでプロジェクトを開き、ビルドしてください。

## 📖 使用方法

1. **検索画面**:
    - 検索バーにキーワードを入力。
    - 目標時間（分）を選択。
    - エクササイズタイプ（None / Shake）を選択して検索。
2. **動画選択**:
    - リストに表示された動画から見たいものをタップ。
    - 各項目には、目標時間に合わせるために必要なエクササイズ時間が表示されています。
3. **再生 & タイマー**:
    - エクササイズフェーズが開始され、タイマーがカウントダウンされます（Shakeの場合はスマホを振ってください）。
    - エクササイズ終了後、シームレスにYouTube動画が再生されます。

## 📂 ディレクトリ構造

```text
com.example.ry0000tarodojo2026/
├── data/
│   ├── api/        # YouTube API (Retrofit)
│   ├── local/      # Room Database, SharedPrefs
│   ├── model/      # Data Classes, Enums
│   └── repository/ # Repository implementation
├── di/             # Hilt Modules
├── ui/
│   ├── components/ # 共通Compose部品
│   ├── screens/    # 画面UI (Search, Timer)
│   ├── theme/      # Compose Theme
│   └── viewmodel/  # ViewModel, UiState, TimerManager
└── utils/          # ShakeDetectorなど
```

## 📄 ライセンス

[MIT License](LICENSE) (またはプロジェクトに適したライセンスを記載してください)
