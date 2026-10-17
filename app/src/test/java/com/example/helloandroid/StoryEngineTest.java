package com.example.helloandroid;

import org.junit.Test;

import static com.example.helloandroid.StoryEngine.*;
import static org.junit.Assert.*;

public class StoryEngineTest {
    @Test
    public void chipsNoAppleDestroy_isInnocent() {
        assertEnding(false, false, true, END_INNOCENT);
    }

    @Test
    public void chipsNoAppleKeep_isArrest() {
        assertEnding(false, false, false, END_ARREST);
    }

    @Test
    public void chipsAppleDestroy_isMastermind() {
        assertEnding(false, true, true, END_MASTERMIND);
    }

    @Test
    public void chipsAppleKeep_isArrest() {
        assertEnding(false, true, false, END_ARREST);
    }

    @Test
    public void onigiriNoAppleDestroy_isArrest() {
        assertEnding(true, false, true, END_ARREST);
    }

    @Test
    public void onigiriNoAppleKeep_isArrest() {
        assertEnding(true, false, false, END_ARREST);
    }

    @Test
    public void onigiriAppleDestroy_isArrest() {
        assertEnding(true, true, true, END_ARREST);
    }

    @Test
    public void onigiriAppleKeep_isArrest() {
        assertEnding(true, true, false, END_ARREST);
    }

    private void assertEnding(boolean onigiri, boolean apple, boolean destroy, int expected) {
        GameState state = new GameState();
        state.scene = choose(S4_SNACK, onigiri ? CHOICE_B : CHOICE_A, state);
        assertEquals(S5_YAOYA, state.scene);
        assertEquals(onigiri, state.ateOnigiri);
        state.scene = choose(state.scene, apple ? CHOICE_A : CHOICE_B, state);
        assertEquals(S6_DECISION, state.scene);
        assertEquals(apple, state.boughtApple);
        assertEquals(expected, choose(state.scene, destroy ? CHOICE_A : CHOICE_B, state));
    }

    @Test
    public void pickNote_goesToKonbini() {
        assertEquals(S2_KONBINI, choose(S1_CLASSROOM, CHOICE_A, new GameState()));
    }

    @Test
    public void leaveNote_endsStory() {
        assertEquals(END_NOT_PICKED, choose(S1_CLASSROOM, CHOICE_B, new GameState()));
    }

    @Test
    public void linearScenes_advanceInOrder() {
        assertEquals(S3_DETECTIVE, nextLinear(S2_KONBINI));
        assertEquals(S4_SNACK, nextLinear(S3_DETECTIVE));
    }

    @Test
    public void choiceScenes_areRecognized() {
        assertTrue(hasChoices(S1_CLASSROOM));
        assertTrue(hasChoices(S4_SNACK));
        assertTrue(hasChoices(S5_YAOYA));
        assertTrue(hasChoices(S6_DECISION));
        int[] otherScenes = {S2_KONBINI, S3_DETECTIVE, END_NOT_PICKED,
                END_MASTERMIND, END_INNOCENT, END_ARREST, -1};
        for (int scene : otherScenes) {
            assertFalse(hasChoices(scene));
        }
    }

    @Test
    public void endingScenes_areRecognized() {
        assertTrue(isEnding(END_NOT_PICKED));
        assertTrue(isEnding(END_MASTERMIND));
        assertTrue(isEnding(END_INNOCENT));
        assertTrue(isEnding(END_ARREST));
        int[] otherScenes = {S1_CLASSROOM, S2_KONBINI, S3_DETECTIVE,
                S4_SNACK, S5_YAOYA, S6_DECISION, -1};
        for (int scene : otherScenes) {
            assertFalse(isEnding(scene));
        }
    }

    @Test
    public void invalidChoices_throw() {
        int[] scenes = {S1_CLASSROOM, S4_SNACK, S5_YAOYA, S6_DECISION};
        for (int scene : scenes) {
            assertThrows(IllegalArgumentException.class, () -> choose(scene, -1, new GameState()));
            assertThrows(IllegalArgumentException.class, () -> choose(scene, 2, new GameState()));
        }
    }

    @Test
    public void choosingInOtherScenes_throws() {
        int[] scenes = {S2_KONBINI, S3_DETECTIVE, END_NOT_PICKED,
                END_MASTERMIND, END_INNOCENT, END_ARREST, -1};
        for (int scene : scenes) {
            assertThrows(IllegalArgumentException.class,
                    () -> choose(scene, CHOICE_A, new GameState()));
            assertThrows(IllegalArgumentException.class,
                    () -> choose(scene, CHOICE_B, new GameState()));
        }
    }

    @Test
    public void advancingNonlinearScenes_throws() {
        int[] scenes = {S1_CLASSROOM, S4_SNACK, S5_YAOYA, S6_DECISION,
                END_NOT_PICKED, END_MASTERMIND, END_INNOCENT, END_ARREST, -1};
        for (int scene : scenes) {
            assertThrows(IllegalArgumentException.class, () -> nextLinear(scene));
        }
    }

    @Test
    public void reset_restoresStartingState() {
        GameState state = new GameState();
        assertEquals(S1_CLASSROOM, state.scene);
        assertEquals(0, state.lineIndex);
        assertFalse(state.ateOnigiri);
        assertFalse(state.boughtApple);

        state.scene = END_MASTERMIND;
        state.lineIndex = 3;
        state.ateOnigiri = true;
        state.boughtApple = true;
        state.reset();

        assertEquals(S1_CLASSROOM, state.scene);
        assertEquals(0, state.lineIndex);
        assertFalse(state.ateOnigiri);
        assertFalse(state.boughtApple);
    }
}
