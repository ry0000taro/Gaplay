# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## これは何か

Gaplayは Android アプリ（Kotlin + Jetpack Compose）です。ユーザーが目標時間と検索キーワードを設定すると、
その時間より短いYouTube動画を検索し、動画の再生時間との差分（"Gap"）をエクササイズ（スマホのシェイク検知など）
の時間として動画再生の前後に割り当てます。カップ麺のバーコードをスキャンして、連携APIから検索キーワード・
目標時間を自動入力する機能もあります。

このリポジトリには独立した2つのプロジェクトが含まれます。
- **Androidアプリ**（リポジトリ直下、`app/`）— 本体のプロダクト。
- `noodle-api-temp/` — JANコードからカップ麺の調理時間データを返す、使い捨てのHono/TypeScriptバックエンド
  （Supabase利用）。バーコードスキャン機能から呼ばれる。Androidビルドには含まれない。

## コマンド

Androidアプリ（リポジトリ直下で実行）:
```
./gradlew assembleDebug          # デバッグAPKのビルド
./gradlew test                   # JVMユニットテスト実行 (app/src/test)
./gradlew connectedAndroidTest    # インストゥルメンテーションテスト実行 (app/src/androidTest, 実機/エミュレータ必須)
./gradlew testDebugUnitTest --tests "com.example.ry0000tarodojo2026.SomeTest"   # 単一テストクラスの実行
./gradlew lint                   # Android lint
```
補足: `app/src/test` と `app/src/androidTest` には現状デフォルトのテンプレートテスト
（`ExampleUnitTest.kt`, `ExampleInstrumentedTest.kt`）しか存在せず、実質的なテストスイートはまだない。

noodle-api-temp（Node/Honoバックエンド、Gradleビルドとは別管理）:
```
cd noodle-api-temp
npm install
npm run dev        # http://localhost:3000
```
Androidアプリの `AppModule` は `NoodleApiService` の接続先を `http://10.0.2.2:3000/`（Androidエミュレータ
から見たホストマシンのlocalhostのエイリアス）にしているため、エミュレータでバーコードスキャン→カップ麺検索
を動かすにはこのバックエンドをローカルで起動しておく必要がある。

## ローカル設定として必要なもの

YouTube Data API v3 のAPIキーが必要。`local.properties`（コミットしない）に追加する。
```
YOUTUBE_API_KEY=your_key_here
```
`app/build.gradle.kts` 経由で `BuildConfig.YOUTUBE_API_KEY` に注入される。Auth + Firestore用に
Firebaseプロジェクトも組み込まれている（`google-services.json`）。

## アーキテクチャ

パッケージルート: `com.example.ry0000tarodojo2026`（Gradleの `applicationId` である
`com.example.gaplay` とは異なる点に注意。検索やgrep時に一致すると思い込まないこと）。

**MVVM + Repositoryパターン、DIはHilt。** レイヤー構成:
- `data/api/` — Retrofitサービス: `YouTubeApiService`（YouTube Data API v3）、`NoodleApiService`
  （ローカルのnoodle-api-tempバックエンド、JANコード→名前/調理時間）。
- `data/local/` — Room（`AppDatabase`, `VideoDao`, エンティティ `VideoEntity` — これは*直近の検索結果*
  のキャッシュであり、視聴履歴ではない点に注意）と `SearchPrefs`（DataStoreベース、直近の検索クエリ/分数/
  エクササイズタイプをアプリ再起動後も保持）。
- `data/repository/` — `YouTubeRepository`（検索＋時間フィルタリング＋Roomへの書き込み）、
  `NoodleRepository`（バーコード→カップ麺情報）、`HistoryRepository`（視聴履歴をFirestoreの
  `users/{uid}/watch_history` に書き込む、Firebase認証済みユーザーが必須）。
- `di/AppModule.kt` — Hiltのバインディングはすべてここに集約。両方のRetrofitクライアントは共通の
  OkHttp/Retrofitビルダーを使わずそれぞれインラインで構築されており、さらに `YouTubeRepository` は
  Hilt提供の `YouTubeApiService` ではなく古いシングルトンの `RetrofitInstance.api` を経由している。
  つまり2つのAPIクライアントの配線に一貫性がない状態なので、ネットワーク設定を触る際は注意すること。
- `ui/viewmodel/MainViewModel.kt` — 中心的な状態管理役。Roomの動画リスト、`SearchPrefs` のFlow、
  `ExerciseTimerManager` のカウントダウン/フェーズのFlowを `combine(...)` でひとつの `MainUiState` に
  統合している。`searchVideos`、`onVideoSelect`（エクササイズ時間＝目標時間－動画時間を計算しタイマー開始・
  履歴保存）、`searchNoodle`（バーコードフロー）を持つ。
- `ui/viewmodel/TimerManager.kt` — `ExerciseTimerManager`: ViewModelではない単純なコルーチンベースの
  カウントダウン処理。`MainViewModel` から渡された `viewModelScope` 上で動く。動画フェーズのカウント
  ダウン→エクササイズフェーズのカウントダウンの順に処理する。
- `ui/viewmodel/AuthViewModel.kt` — `FirebaseAuth` の薄いラッパー。`AuthStateListener` 経由で
  `AuthState`（Loading/Authenticated/Unauthenticated）を公開する。
- `ui/screens/` — `AuthScreen`, `SearchListScreen`, `ScanScreen`（`play-services-code-scanner`/ML Kit
  によるバーコードスキャン、`MainViewModel.searchNoodle` を呼ぶ）, `TimerPlayerScreen`。
- `ui/components/PlayerOverlay.kt` ＋ `PlayerMode`（HIDDEN/MINI/FULL）— 動画プレイヤーは
  `MainActivity` の `NavHost` の上に重ねて描画されるオーバーレイであり、ナビゲーショングラフの
  destinationではない。オーバーレイがMINIの状態でエクササイズフェーズに入ると自動的にFULLへ展開される
  （`MainViewModel.init` 内の `combine` ブロックを参照）。

**ナビゲーション**: `Routes.kt` にルート定数を定義。`MainActivity` はアプリ全体を `AuthState` で
ゲーティングしている: `Unauthenticated` → `AuthScreen`、`Authenticated` → `Scaffold`/`NavHost`/
ボトムバーのUI。現状 `NavHost` に登録されているのは `SEARCH_LIST` と `SCAN` のみで、`HISTORY` と
`TIMER_PLAYER` は `Routes` に定義されているものの `composable()` destinationとしてはまだ未登録。

**検索時のデータフロー**: `SearchListScreen` → `MainViewModel.searchVideos` → `YouTubeRepository`
（YouTube検索API→動画詳細API→時間でフィルタ→Roomへ書き込み）→ Roomの `Flow` → `MainViewModel.init`
内の `combine` を経由して `MainUiState.videoList` へ反映。

**バーコードスキャン時のデータフロー**: `ScanScreen`（ML Kitスキャナ）→
`MainViewModel.searchNoodle(janCode)` → `NoodleRepository` → `NoodleApiService`
（noodle-api-tempの `GET /api/noodles/:jan_code`）→ 成功したら `SearchPrefs` を上書き
（クエリ＝カップ麺名、分数＝調理時間）し `SEARCH_LIST` へ遷移。遷移先は `SearchPrefs` のFlow経由で
新しい値を受け取る。

## コードベースで見られる規約

- UI文字列やコメントは日本語で書かれている。新規のコメント/UI文言もこれに合わせること。
- ネットワークレスポンスのDTOはcamelCaseのKotlinプロパティに `@SerializedName` を明示してバックエンドの
  snake_caseなJSONキーに対応させている（`NoodleResponse` を参照: `janCode`/`timeMinutes` が
  `jan_code`/`time_minutes` にマッピングされている）。noodle-api-temp向けの新規DTOもこのパターンに
  従うこと。