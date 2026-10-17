package com.example.helloandroid;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

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
        assertEquals(onigiri ? S4_ONIGIRI : S4_CHIPS, state.scene);
        assertEquals(onigiri, state.ateOnigiri);
        state.scene = nextLinear(nextLinear(state.scene));
        assertEquals(S5_YAOYA, state.scene);
        state.scene = choose(state.scene, apple ? CHOICE_A : CHOICE_B, state);
        assertEquals(apple ? S5_APPLE : S5_NO_APPLE, state.scene);
        assertEquals(apple, state.boughtApple);
        state.scene = nextLinear(state.scene);
        assertEquals(S6_DECISION, state.scene);
        assertEquals(expected, choose(state.scene, destroy ? CHOICE_A : CHOICE_B, state));
    }

    @Test
    public void pickNote_goesToPickedScene() {
        assertEquals(S1_PICKED, choose(S1_CLASSROOM, CHOICE_A, new GameState()));
    }

    @Test
    public void leaveNote_goesToRefusal() {
        assertEquals(S1_REFUSE, choose(S1_CLASSROOM, CHOICE_B, new GameState()));
    }

    @Test
    public void reconsiderRefusal_picksNote() {
        assertEquals(S1_PICKED, choose(S1_REFUSE, CHOICE_A, new GameState()));
    }

    @Test
    public void refuseAgain_endsStory() {
        assertEquals(END_NOT_PICKED, choose(S1_REFUSE, CHOICE_B, new GameState()));
    }

    @Test
    public void takeBroadcastBait_goesToBaitTaken() {
        assertEquals(S3_BAIT_TAKEN, choose(S3_BROADCAST, CHOICE_A, new GameState()));
    }

    @Test
    public void ignoreBroadcastBait_goesToBaitIgnored() {
        assertEquals(S3_BAIT_IGNORED, choose(S3_BROADCAST, CHOICE_B, new GameState()));
    }

    @Test
    public void chooseChips_goesToChipsSceneWithoutOnigiri() {
        GameState state = new GameState();
        assertEquals(S4_CHIPS, choose(S4_SNACK, CHOICE_A, state));
        assertFalse(state.ateOnigiri);
    }

    @Test
    public void chooseOnigiri_goesToOnigiriSceneWithFlag() {
        GameState state = new GameState();
        assertEquals(S4_ONIGIRI, choose(S4_SNACK, CHOICE_B, state));
        assertTrue(state.ateOnigiri);
    }

    @Test
    public void linearScenes_advanceInOrder() {
        assertEquals(S2_KONBINI, nextLinear(S1_PICKED));
        assertEquals(S2_AFTER, nextLinear(S2_KONBINI));
        assertEquals(S3_BROADCAST, nextLinear(S2_AFTER));
        assertEquals(S3_DETECTIVE, nextLinear(S3_BAIT_TAKEN));
        assertEquals(S3_DETECTIVE, nextLinear(S3_BAIT_IGNORED));
        assertEquals(S4_SNACK, nextLinear(S3_DETECTIVE));
        assertEquals(S5_CAMPUS, nextLinear(S4_CHIPS));
        assertEquals(S5_CAMPUS, nextLinear(S4_ONIGIRI));
        assertEquals(S5_YAOYA, nextLinear(S5_CAMPUS));
        assertEquals(S6_DECISION, nextLinear(S5_APPLE));
        assertEquals(S6_DECISION, nextLinear(S5_NO_APPLE));
    }

    @Test
    public void choiceScenes_areRecognized() {
        assertTrue(hasChoices(S1_CLASSROOM));
        assertTrue(hasChoices(S1_REFUSE));
        assertTrue(hasChoices(S3_BROADCAST));
        assertTrue(hasChoices(S4_SNACK));
        assertTrue(hasChoices(S5_YAOYA));
        assertTrue(hasChoices(S6_DECISION));
        int[] otherScenes = {S1_PICKED, S2_KONBINI, S2_AFTER, S3_BAIT_TAKEN,
                S3_BAIT_IGNORED, S3_DETECTIVE, S4_CHIPS, S4_ONIGIRI, S5_CAMPUS,
                S5_APPLE, S5_NO_APPLE, END_NOT_PICKED,
                END_MASTERMIND, END_INNOCENT, END_ARREST, -1, 0, 22};
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
        int[] otherScenes = {S1_CLASSROOM, S1_REFUSE, S1_PICKED, S2_KONBINI, S2_AFTER,
                S3_BROADCAST, S3_BAIT_TAKEN, S3_BAIT_IGNORED, S3_DETECTIVE,
                S4_SNACK, S4_CHIPS, S4_ONIGIRI, S5_CAMPUS, S5_YAOYA, S5_APPLE, S5_NO_APPLE,
                S6_DECISION, -1, 0, 22};
        for (int scene : otherScenes) {
            assertFalse(isEnding(scene));
        }
    }

    @Test
    public void invalidChoices_throw() {
        int[] scenes = {S1_CLASSROOM, S1_REFUSE, S3_BROADCAST, S4_SNACK, S5_YAOYA, S6_DECISION};
        for (int scene : scenes) {
            assertThrows(IllegalArgumentException.class, () -> choose(scene, -1, new GameState()));
            assertThrows(IllegalArgumentException.class, () -> choose(scene, 2, new GameState()));
        }
    }

    @Test
    public void choosingInOtherScenes_throws() {
        int[] scenes = {S1_PICKED, S2_KONBINI, S2_AFTER, S3_BAIT_TAKEN,
                S3_BAIT_IGNORED, S3_DETECTIVE, S4_CHIPS, S4_ONIGIRI, S5_CAMPUS,
                S5_APPLE, S5_NO_APPLE, END_NOT_PICKED,
                END_MASTERMIND, END_INNOCENT, END_ARREST, -1, 0, 22};
        for (int scene : scenes) {
            assertThrows(IllegalArgumentException.class,
                    () -> choose(scene, CHOICE_A, new GameState()));
            assertThrows(IllegalArgumentException.class,
                    () -> choose(scene, CHOICE_B, new GameState()));
        }
    }

    @Test
    public void advancingNonlinearScenes_throws() {
        int[] scenes = {S1_CLASSROOM, S1_REFUSE, S3_BROADCAST, S4_SNACK, S5_YAOYA, S6_DECISION,
                END_NOT_PICKED, END_MASTERMIND, END_INNOCENT, END_ARREST, -1, 0, 22};
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

    @Test
    public void validScenes_areExactlyOneThroughTwentyOne() {
        for (int scene = 1; scene <= 21; scene++) {
            assertTrue(isValidScene(scene));
        }
        assertFalse(isValidScene(0));
        assertFalse(isValidScene(22));
    }

    @Test
    public void wholeGraph_reachesEverySceneAndEndingWithinFortySteps() {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> endings = new HashSet<>();
        walk(new GameState(), 0, visited, endings);

        assertEquals(21, visited.size());
        for (int scene = 1; scene <= 21; scene++) {
            assertTrue("未到達の場面: " + scene, visited.contains(scene));
        }
        assertEquals(4, ENDINGS.length);
        assertEquals(4, endings.size());
        for (int ending : ENDINGS) {
            assertTrue("未到達のエンディング: " + ending, endings.contains(ending));
        }
    }

    private void walk(GameState state, int steps, Set<Integer> visited, Set<Integer> endings) {
        assertTrue("40ステップ以内に終わりません: " + state.scene, steps <= 40);
        assertTrue(isValidScene(state.scene));
        visited.add(state.scene);
        if (isEnding(state.scene)) {
            boolean listed = false;
            for (int ending : ENDINGS) {
                if (ending == state.scene) {
                    listed = true;
                }
            }
            assertTrue("ENDINGSにない終了場面: " + state.scene, listed);
            endings.add(state.scene);
            return;
        }
        if (hasChoices(state.scene)) {
            for (int choice = CHOICE_A; choice <= CHOICE_B; choice++) {
                GameState branch = new GameState();
                branch.scene = state.scene;
                branch.lineIndex = state.lineIndex;
                branch.ateOnigiri = state.ateOnigiri;
                branch.boughtApple = state.boughtApple;
                branch.scene = choose(branch.scene, choice, branch);
                branch.lineIndex = 0;
                walk(branch, steps + 1, visited, endings);
            }
        } else {
            state.scene = nextLinear(state.scene);
            state.lineIndex = 0;
            walk(state, steps + 1, visited, endings);
        }
    }
}
