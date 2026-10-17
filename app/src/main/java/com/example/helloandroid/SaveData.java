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
                .apply();
    }

    static boolean hasSave(Context context) {
        return preferences(context).contains("scene");
    }

    static boolean load(Context context, GameState state) {
        SharedPreferences saved = preferences(context);
        int scene = saved.getInt("scene", 0);
        if (!StoryEngine.isValidScene(scene) || StoryEngine.isEnding(scene)) {
            clear(context);
            return false;
        }
        state.scene = scene;
        state.lineIndex = saved.getInt("lineIndex", 0);
        state.ateOnigiri = saved.getBoolean("ateOnigiri", false);
        state.boughtApple = saved.getBoolean("boughtApple", false);
        return true;
    }

    static void clear(Context context) {
        // エンディングの収集記録は、新しく始めても残します。
        preferences(context).edit()
                .remove("scene")
                .remove("lineIndex")
                .remove("ateOnigiri")
                .remove("boughtApple")
                .apply();
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
