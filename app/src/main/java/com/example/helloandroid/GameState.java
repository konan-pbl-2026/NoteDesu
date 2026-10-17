package com.example.helloandroid;

public class GameState {
    public int scene = StoryEngine.S1_CLASSROOM;
    public int lineIndex = 0;
    public boolean ateOnigiri;
    public boolean boughtApple;

    public void reset() {
        scene = StoryEngine.S1_CLASSROOM;
        lineIndex = 0;
        ateOnigiri = false;
        boughtApple = false;
    }
}
