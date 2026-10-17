package com.example.helloandroid;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoryLineTest {
    @Test
    public void narration_hasNoSpeaker() {
        assertEquals("", StoryLine.speaker("教室にノートが落ちていた。"));
        assertEquals("教室にノートが落ちていた。", StoryLine.body("教室にノートが落ちていた。"));
    }

    @Test
    public void dialogue_splitsNameAndBody() {
        assertEquals("ヨミ", StoryLine.speaker("ヨミ｜「台詞」"));
        assertEquals("「台詞」", StoryLine.body("ヨミ｜「台詞」"));
    }

    @Test
    public void nameSpaces_areTrimmedWithoutChangingBody() {
        assertEquals("ヨミ", StoryLine.speaker("  ヨミ  ｜ 本文 "));
        assertEquals(" 本文 ", StoryLine.body("  ヨミ  ｜ 本文 "));
    }

    @Test
    public void emptyName_hasNoSpeaker() {
        assertEquals("", StoryLine.speaker("｜本文"));
        assertEquals("本文", StoryLine.body("｜本文"));
        assertEquals("", StoryLine.speaker("  ｜本文"));
        assertEquals("本文", StoryLine.body("  ｜本文"));
    }

    @Test
    public void twoSeparators_splitOnlyAtFirst() {
        assertEquals("ヨミ", StoryLine.speaker("ヨミ｜本文｜続き"));
        assertEquals("本文｜続き", StoryLine.body("ヨミ｜本文｜続き"));
    }

    @Test
    public void asciiBar_isNotASeparator() {
        assertEquals("", StoryLine.speaker("ヨミ|本文"));
        assertEquals("ヨミ|本文", StoryLine.body("ヨミ|本文"));
    }

    @Test
    public void emptyBody_isPreserved() {
        assertEquals("ヨミ", StoryLine.speaker("ヨミ｜"));
        assertEquals("", StoryLine.body("ヨミ｜"));
        assertEquals("", StoryLine.speaker(""));
        assertEquals("", StoryLine.body(""));
    }
}
