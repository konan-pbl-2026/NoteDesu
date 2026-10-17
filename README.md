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

- **1つの `<item>` が1ページ**。タップで次へ進む。
- 1ページ **60文字以内**が目安。
- 次の文字・改行は、右側の表記で書く。

```text
'    → \'
&    → &amp;
改行 → \n
```

## 背景画像の追加

- **`app/src/main/res/drawable-nodpi/` に置く。** `drawable/` に置くと、高解像度端末で巨大化してクラッシュする恐れがある。
- 縦長 **1080×1920 前後**、**WebP 推奨**。
- `StoryData.backgroundRes` で場面に割り当てる。

## 画面・コード構成（開発担当向け）

- `MainActivity`：タイトル画面。
- `MainActivity2`：ストーリー画面。全場面を1画面で表示。
- `StoryEngine.java`：場面の進み方とエンディング判定。Android 非依存。
- `GameState.java`：現在の場面・行・おにぎり／リンゴのフラグ。
- `StoryData.java`：場面ごとの背景・本文・選択肢のリソース対応。
- `StoryEngineTest.java`：全エンディングのテスト。

## エンディング（仮決め）

**チームで確認待ち。変更する箇所は `StoryEngine.resolveEnding` だけ。**

| 条件 | エンディング |
| --- | --- |
| 拾わない | END1 いつもの日常 |
| おにぎりを選んだ、またはノートを破棄しない | 逮捕 |
| 破棄する＋リンゴを買った | 黒幕 |
| 破棄する＋リンゴを買っていない | 潔白 |

**企画書に矛盾あり：** 第五場面の「リンゴを買わない→逮捕」と、第六場面の「破棄＋リンゴなし→潔白」が矛盾している。

## 素材とライセンス

- フォント：**Zen Old Mincho Medium**。SIL Open Font License 1.1（`licenses/ZenOldMincho-OFL.txt`）。
- 背景 `classroom_day` / `konbini_night` / `room_night` / `kitchen_night` / `yaoya` は画像生成AIで作成。
- **`haikei_sora.webp` と `home_visual.jpg` は出典未記入。追加した人が記入すること。**
- **原作の画像・ロゴ・キャラクターは使わない。**

## ブランチ運用

`main` から作業ブランチを切って PR を出す。
