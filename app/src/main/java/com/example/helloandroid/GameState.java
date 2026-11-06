package com.example.helloandroid;

import java.util.ArrayList;

public class GameState {
    public int scene = StoryEngine.S1_CLASSROOM;
    public int lineIndex = 0;
    public boolean ateOnigiri;
    public boolean boughtApple;
    /** 通過した場面の番号（古い順）。今いる場面は含みません。ログの組み立てに使います。 */
    public final ArrayList<Integer> history = new ArrayList<>();

    /** 次の場面へ進みます。今の場面を履歴に積みます。 */
    public void moveTo(int nextScene) {
        history.add(scene);
        scene = nextScene;
        lineIndex = 0;
    }

    public void reset() {
        scene = StoryEngine.S1_CLASSROOM;
        lineIndex = 0;
        ateOnigiri = false;
        boughtApple = false;
        history.clear();
    }
}