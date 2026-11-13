package com.example.helloandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveData {
    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences("notedesu_save", Context.MODE_PRIVATE);
    }

    static void save(Context context, GameState state) {
        preferences(context).edit()
                .putInt("scene", state.scene)
                .putInt("lineIndex", state.lineIndex)
                .putBoolean("ateOnigiri", state.ateOnigiri)
                .putBoolean("boughtApple", state.boughtApple)
                .putString("history", android.text.TextUtils.join(",", state.history))
                .apply();
    }

    static boolean hasSave(Context context) {
        return preferences(context).contains("scene");
    }

    static boolean load(Context context, GameState state) {
        SharedPreferences saved = preferences(context);
        int scene = saved.getInt("scene", 0);
        if (!StoryEngine.isValidScene(scene)) {
            clear(context);
            return false;
        }
        state.scene = scene;
        state.lineIndex = saved.getInt("lineIndex", 0);
        state.ateOnigiri = saved.getBoolean("ateOnigiri", false);
        state.boughtApple = saved.getBoolean("boughtApple", false);
        state.history.clear();
        String csv = saved.getString("history", "");
        if (!csv.isEmpty()) {
            for (String part : csv.split(",")) {
                try {
                    int past = Integer.parseInt(part.trim());
                    if (StoryEngine.isValidScene(past)) {
                        state.history.add(past);
                    }
                } catch (NumberFormatException ignored) {
                    // 壊れた値は読み飛ばします。
                }
            }
        }
        return true;
    }

    static void clear(Context context) {
        // エンディングの収集記録と章のクリア記録は、新しく始めても残します。
        preferences(context).edit()
                .remove("scene")
                .remove("lineIndex")
                .remove("ateOnigiri")
                .remove("boughtApple")
                .apply();
    }

    // ★追加: 章をクリアしたことを記録します。chapter は 1〜6。
    static void markChapterCleared(Context context, int chapter) {
        preferences(context).edit().putBoolean("chapter_cleared_" + chapter, true).apply();
    }

    // ★追加: 章ごとのクリア状況(index 0 が第一章)。記録に加えて、以前のバージョンで保存された
    // 続きの位置からも「それより前の章はクリア済み」とみなします。
    static boolean[] clearedChapters(Context context) {
        SharedPreferences saved = preferences(context);
        boolean[] cleared = new boolean[StoryEngine.CHAPTER_STARTS.length];
        int savedScene = saved.getInt("scene", 0);
        int savedChapter = StoryEngine.chapterOf(savedScene);
        boolean savedAfterLastChapter = savedScene == StoryEngine.END_MASTERMIND
                || savedScene == StoryEngine.END_INNOCENT || savedScene == StoryEngine.END_ARREST;
        for (int i = 0; i < cleared.length; i++) {
            cleared[i] = saved.getBoolean("chapter_cleared_" + (i + 1), false)
                    || i + 1 < savedChapter || savedAfterLastChapter;
        }
        return cleared;
    }

    static void markEndingSeen(Context context, int ending) {
        preferences(context).edit().putBoolean("ending_" + ending, true).apply();
    }

    static int seenEndingCount(Context context) {
        SharedPreferences saved = preferences(context);
        int count = 0;
        for (int ending : StoryEngine.ENDINGS) {
            if (saved.getBoolean("ending_" + ending, false)) {
                count++;
            }
        }
        return count;
    }
}