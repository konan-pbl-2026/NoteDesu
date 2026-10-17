# NoteDesu.

チーム：メモ帳 / プロジェクト演習 D班

選択肢型ストーリーゲーム（Android, Java）。

## 開発環境

- Android Studio **2026.1.4 以上**（AGP 9.4.1 / Gradle 9.6.0）
- Gradle JDK **17 以上**。Studio 同梱の JDK でよい。
- **古い Android Studio では開けないため、その場合は相談すること。**

## ビルド・テスト・lint

```sh
# ビルド
./gradlew assembleDebug

# テスト
./gradlew testDebugUnitTest

# lint
./gradlew lintDebug
```

## 本文の書き換え（コンテンツ班向け）

`app/src/main/res/values/` の `story_s1.xml` ～ `story_s6.xml` と `story_endings.xml` にある `<item>` の本文を書き換えるだけ。

- **1つの `<string-array>` が1シーン**。各ファイルに複数シーンをまとめている（`story_s6.xml` は1シーン）。`story_s1.xml` ～ `story_s6.xml` では各配列の直前に場面のコメントがある。`story_endings.xml` は配列名で見分ける。
- **1つの `<item>` が1ページ**。文字の表示中にタップすると全文を表示し、もう一度タップすると次へ進む。場面の最後は選択肢や終了ボタンが出る場合がある。
- **名前プレートを出すときは `名前｜本文`**。区切りは全角の縦棒 **`｜`（U+FF5C）**。区切りがなければ地の文になる。
- 1ページ **60文字以内**が目安。
- 次の文字・改行は、右側の表記で書く。

```text
'    → \'
&    → &amp;
改行 → \n
```

### ストーリーの流れ（全21シーン、エンディングを含む）

- `S1_CLASSROOM`（教室）→ 拾う：`S1_PICKED` / 拾わない：`S1_REFUSE`。翌朝もう一度選び、手に取ると `S1_PICKED`、投げ捨てると **END 1 いつもの日常**。
- `S1_PICKED` → `S2_KONBINI`（コンビニ）→ `S2_AFTER` → `S3_BROADCAST`（街頭ビジョン）。
- 名前を書く：`S3_BAIT_TAKEN` / 様子を見る：`S3_BAIT_IGNORED` → どちらも `S3_DETECTIVE`（自宅）→ `S4_SNACK`（台所）。
- ポテトチップス：`S4_CHIPS` / おにぎり：`S4_ONIGIRI` → どちらも `S5_CAMPUS`（大学）→ `S5_YAOYA`（八百屋）。
- リンゴを買う：`S5_APPLE` / 買わない：`S5_NO_APPLE` → どちらも `S6_DECISION`（屋上）。
- ノートを破棄する / 破棄しない → 下記の仮ルールで **END 2 潔白 / END 3 黒幕 / END 4 逮捕**。

通常ルートの読了目安は **約15～22分**。本文は約7,700～8,100文字、294～308ページ（話者名を除き、`S1_REFUSE` 経由も含む）。序盤で終わる END 1 は45ページ。

## 背景画像の追加

- **`app/src/main/res/drawable-nodpi/` に置く。** `drawable/` に置くと、高解像度端末で巨大化してクラッシュする恐れがある。
- 縦長 **1080×1920 前後**、**WebP 推奨**。
- `StoryData.backgroundRes` で場面に割り当てる。
- 立ち絵も `drawable-nodpi/` に**透過 WebP**で置き、`StoryData.characterRes` で場面に割り当てる。現在は `shinigami.webp` と `note_item.webp`。
- BGM は `app/src/main/res/raw/*.m4a` に置き、`StoryData.bgmRes` で場面に割り当てる。

### BGM の再生成

`bgm_daily` / `bgm_dark` / `bgm_tension` は、`tools/synth_bgm.py` で音を合成したオリジナル曲。**Python 3 + numpy** が必要。出力フォルダを先に用意し、リポジトリ直下から実行する。

```sh
python3 tools/synth_bgm.py "<出力フォルダ>"
# 生成された各 WAV を macOS の afconvert で M4A に変換する
afconvert -f m4af -d aac -b 96000 in.wav out.m4a
```

`in.wav` / `out.m4a` は各曲のパスに置き換え、3曲とも同名の `.m4a` にして `app/src/main/res/raw/` に置く。ライセンス上利用できる曲なら、自由に差し替えてよい。

## 画面・コード構成（開発担当向け）

- `MainActivity`：タイトル画面。「はじめる」、保存があれば「つづきから」、収集済みなら「エンディング n / 4」を表示。
- `MainActivity2`：ストーリー画面。全場面を1画面で表示。タイプライター表示（タップで全文、次のタップで次ページ）、背景・立ち絵のフェード、名前プレートを担当。
- `StoryEngine.java`：場面の進み方とエンディング判定。Android 非依存。
- `GameState.java`：現在の場面・行・おにぎり／リンゴのフラグ。
- `StoryData.java`：場面ごとの背景・本文・選択肢・立ち絵・BGM のリソース対応。
- `StoryLine.java`：全角の `｜` で話者名と本文を分ける。
- `BgmPlayer.java`：画面間で共有する `MediaPlayer` で BGM をループ再生。
- `SaveData.java`：進行の自動保存、「つづきから」の復元、エンディングの収集数を管理。新しく始めても収集記録は残る。
- `StoryEngineTest.java`：全エンディングのテスト。

## エンディング（仮決め）

**チームで確認待ち。S6 の判定は `StoryEngine.resolveEnding`、END 1 への分岐は `StoryEngine.choose`。** 表は上から優先する。

| 条件 | エンディング |
| --- | --- |
| 拾わない→翌朝、窓から投げ捨てる | END 1 いつもの日常 |
| おにぎりを選んだ、またはノートを破棄しない | END 4 逮捕 |
| 破棄する＋リンゴを買った | END 3 黒幕 |
| 破棄する＋リンゴを買っていない | END 2 潔白 |

**企画書に矛盾あり：** 第五場面の「リンゴを買わない→逮捕」と、第六場面の「破棄＋リンゴなし→潔白」が矛盾している。

## 素材とライセンス

- フォント：**Zen Old Mincho Medium**。SIL Open Font License 1.1（`licenses/ZenOldMincho-OFL.txt`）。
- 背景 `classroom_day` / `konbini_night` / `room_night` / `kitchen_night` / `yaoya` / `city_screen_night` / `living_night` / `campus_day` / `rooftop_night` / `apple_night` / `arrest_night` は、このプロジェクト用に OpenAI の画像生成で作成したオリジナル画像。
- `haikei_sora.webp` と `home_visual.jpg` はチームの既存画像。
- `shinigami.webp` と `note_item.webp` はチーム提供のオリジナルイラスト。
- BGM 3曲は `tools/synth_bgm.py` で生成したオリジナル曲。
- **原作の画像・ロゴ・キャラクターは使わない。**

## ブランチ運用

`main` から作業ブランチを切って PR を出す。
