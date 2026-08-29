<div align="center">

# Gaplay (ギャップレイ)

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-3DDC84?style=flat&logo=android&logoColor=white)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/DB-Room-3DDC84?style=flat&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

<br>

> “カップ麺が出来上がるまでの3分間”、
> ”冷凍食品のレンチンの4分間”、
> “コーヒーができるまでの3分間”
> 手軽でおいしい食べ物には必ず”Gap(空白)”時間と
> 高カロリーが存在します。
> 何かをするにも何もできないし、だからといって暇な時間は作りたくないと思う瞬間が日常に潜んでいます。
>
> そんな”Gap(空白)”を”Play(再生・運動)”で満たす
> 解決策が”Gaplay”です

<br>

## 目次
- [🍜 課題](#-課題)
- [📱 ユーザーストーリー&アプリ概要](#-ユーザーストーリーアプリ概要)
- [✨ 主な機能](#-主な機能)
- [🛠 技術スタック](#-技術スタック)
- [⭐️ こだわった点](#-こだわった点)
- [🚀 セットアップ](#-セットアップ)
- [📖 使用方法](#-使用方法)
- [📂 ディレクトリ構造](#-ディレクトリ構造)
- [📄 ライセンス](#-ライセンス)

<br>

## 🍜 課題

このアプリは"毎日使いたくなるコンテンツポータルアプリ"というテーマのもと作られたアプリです。
そこで大学生である私にとって身近な存在でほぼ毎日触っているものはなにかと考えました。

"毎日使いたくなる"を実現するには、日常の隙間時間に入り込めることが重要だと考えました。
毎日欠かさず使っているものといえばYouTube、そして隙間時間の代表格といえばカップラーメンができあがるまでの3分間です。
自分自身、その3分間がどうしても待てず、いつも硬いままの麺を啜ってしまいます。

この「YouTube」と「カップラーメンの3分間」を組み合わせれば、"毎日使いたくなる"を実現できるのではないかと考え、Gaplayの開発に至りました。

<br>

## 📱 ユーザーストーリー&アプリ概要

Gaplayを使う場面を「カップ麺を食べると決める」「3分待つ」という2つのシーンに分け、
それぞれのユーザーの心情から必要な機能要件を考えました。

### 1. カップ麺を食べると決める

| ユーザーの心情            | Gaplayでの解決策                    |
|--------------------|--------------------------------|
| ささっと食事を済ませたい      | アプリを開いてから動画を再生するまでの導線をなるべく短くする |
| カロリーが高いから運動して痩せないと | ちょっとした運動（エクササイズ）を提案する          |
| 罪悪感がある             | ほんの少しの運動で罪悪感を最大限軽減できるようにする     |
| 待ち時間を調べるのがめんどくさい   | バーコード読み取りで待ち時間を自動で検出する         |

### 2. 3分待つ

| ユーザーの心情            | Gaplayでの解決策                   |
|--------------------|-------------------------------|
| 何かしたいけど、この短時間でできることは限られている | YouTube動画の視聴を提案する             |
| 早く終わらないかな          | 夢中になれるコンテンツ（YouTube）で待ち時間を埋める |
| わざわざタイマーを回すのは面倒    | 経過時間を秒単位で視覚的にわかるようにする         |
| YouTubeを見ても途中で終わってしまう | 動画の長さを待ち時間以内に収める              |

### アプリ概要

ユーザーが設定した「目標時間」に合わせて、YouTube動画を検索します。
動画の再生時間と目標時間の差分を自動計算し、その時間をエクササイズ（スマホを振る「シェイク」など）の時間として割り当てます。

> **例**: 目標 10分、動画 7分の場合
> 3分間のエクササイズ ＋ 7分間の動画視聴 = 合計 10分

<br>

## ✨ 主な機能

| 機能 | 説明 |
| --- | --- |
| 🔍 **YouTube動画検索** | キーワードと目標時間を指定して動画を検索。 |
| ⏱ **ギャップ計算** | 動画の長さに基づいて、必要なエクササイズ時間を自動算出。 |
| ⏳ **エクササイズタイマー** | 動画再生前後にエクササイズパートを挿入し、カウントダウン。 |
| 📳 **シェイク検知** | `ShakeDetector` を使用し、スマホを振る運動に対応。 |
| 🗂 **履歴管理** | Roomデータベースにより、一度検索・選択した動画情報をローカルに保存。 |
| 🍜 **待ち時間の自動検出** | カップ麺のバーコードをスキャンし、noodle APIから調理時間を取得して目標時間に自動入力。 |

<br>

## ⭐️ こだわった点


z
<br>

## 🛠 技術スタック

| カテゴリ | 採用技術 |
| --- | --- |
| 言語 | Kotlin |
| UI | Jetpack Compose |
| 非同期処理 | Coroutines, Flow |
| アーキテクチャ | MVVM (Model-View-ViewModel) + Repositoryパターン |
| DI (依存注入) | Hilt |
| ネットワーク | Retrofit / OkHttp (YouTube Data API v3) |
| データベース | Room |
| 画像読み込み | Coil |
| 動画再生 | Android YouTube Player |
| ビルドツール | Gradle (Kotlin DSL), Version Catalog (libs.versions.toml) |

<br>

## 🚀 セットアップ

このプロジェクトをビルドするには、YouTube Data API v3 のキーが必要です。

1. [Google Cloud Console](https://console.cloud.google.com/) でプロジェクトを作成し、YouTube Data API v3 を有効にします。
2. APIキーを取得します。
3. プロジェクトルートにある `local.properties` ファイルに、以下の行を追加します。

    ```properties
    YOUTUBE_API_KEY=あなたのAPIキー
    ```

4. Android Studioでプロジェクトを開き、ビルドしてください。

認証機能（ログイン）にはFirebase Authを利用しています。Firebaseプロジェクトの設定ファイル
（`google-services.json`）はリポジトリに同梱済みのため、追加の設定なしにビルドできます。

### バーコードスキャン機能を使う場合

カップ麺のバーコードスキャンから検索キーワードを自動入力する機能は、別リポジトリ内の
`noodle-api-temp`（ローカルのHono/TypeScriptバックエンド）と通信します。アプリは
`http://10.0.2.2:3000/`（Androidエミュレータから見たホストマシンのlocalhost:3000）に
アクセスするため、この機能をエミュレータで試す場合は事前にバックエンドを起動しておく必要があります。

```bash
cd noodle-api-temp
npm install
npm run dev        # http://localhost:3000
```

<br>

## 📖 使用方法

1. **検索画面**
    - 検索バーにキーワードを入力。
    - 目標時間（分）を選択。
    - エクササイズタイプ（None / Shake）を選択して検索。
2. **動画選択**
    - リストに表示された動画から見たいものをタップ。
    - 各項目には、目標時間に合わせるために必要なエクササイズ時間が表示されています。
3. **再生 & タイマー**
    - エクササイズフェーズが開始され、タイマーがカウントダウンされます（Shakeの場合はスマホを振ってください）。
    - エクササイズ終了後、シームレスにYouTube動画が再生されます。

<br>

## 📂 ディレクトリ構造

```text
com.example.ry0000tarodojo2026/
├── data/
│   ├── api/        # YouTube API (Retrofit)
│   ├── local/      # Room Database, DataStore
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

<br>

## 📄 ライセンス

[MIT License](LICENSE)
