package com.example.helloandroid;

import org.junit.Test;

import static org.junit.Assert.*;

public class StoryDataTest {
    // 場面を追加して StoryData の switch に書き忘れると、遊んでいる途中で落ちるので先に検出します。
    @Test
    public void everyScene_hasAllResources() {
        for (int scene = 1; StoryEngine.isValidScene(scene); scene++) {
            assertNotEquals("背景: " + scene, 0, StoryData.backgroundRes(scene));
            assertNotEquals("本文: " + scene, 0, StoryData.linesArrayRes(scene));
            assertNotEquals("BGM: " + scene, 0, StoryData.bgmRes(scene));
            StoryData.characterRes(scene);
            if (StoryEngine.hasChoices(scene)) {
                assertNotEquals("選択肢A: " + scene, 0, StoryData.choiceARes(scene));
                assertNotEquals("選択肢B: " + scene, 0, StoryData.choiceBRes(scene));
            }
        }
    }
}
