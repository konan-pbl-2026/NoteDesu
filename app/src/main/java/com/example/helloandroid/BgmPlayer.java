package com.example.helloandroid;

import android.content.Context;
import android.media.MediaPlayer;

public class BgmPlayer {
    private static MediaPlayer player;
    private static int currentResId;

    static void play(Context context, int resId) {
        if (player != null && currentResId == resId) {
            return;
        }
        stop();
        player = MediaPlayer.create(context.getApplicationContext(), resId);
        if (player == null) {
            return;
        }
        currentResId = resId;
        player.setLooping(true);
        player.setVolume(0.6f, 0.6f);
        player.start();
    }

    static void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    static void resume() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    static void stop() {
        if (player != null) {
            player.release();
            player = null;
        }
        currentResId = 0;
    }
}
