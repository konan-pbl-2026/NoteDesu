package com.example.helloandroid;

import java.util.ArrayList;
import java.util.List;

public final class StoryLog {
    public interface LinesSource {
        String[] lines(int scene);
    }

    private StoryLog() {}

    /**
     * 表示済みの本文を、読んだ順に「名前｜本文」のままの1行ずつで返します。
     * currentLineShown は、今の行を打ち終えているかどうかです。
     */
    public static List<String> build(GameState state, boolean currentLineShown, LinesSource source) {
        List<String> out = new ArrayList<>();
        for (int scene : state.history) {
            for (String line : source.lines(scene)) {
                out.add(line);
            }
        }
        String[] current = source.lines(state.scene);
        int end = Math.min(current.length, state.lineIndex + (currentLineShown ? 1 : 0));
        for (int i = 0; i < end; i++) {
            out.add(current[i]);
        }
        return out;
    }
}