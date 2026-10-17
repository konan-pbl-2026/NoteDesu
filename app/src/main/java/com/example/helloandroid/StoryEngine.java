package com.example.helloandroid;

public class StoryEngine {
    public static final int S1_CLASSROOM = 1;
    public static final int S2_KONBINI = 2;
    public static final int S3_DETECTIVE = 3;
    public static final int S4_SNACK = 4;
    public static final int S5_YAOYA = 5;
    public static final int S6_DECISION = 6;
    public static final int END_NOT_PICKED = 7;
    public static final int END_MASTERMIND = 8;
    public static final int END_INNOCENT = 9;
    public static final int END_ARREST = 10;

    public static final int CHOICE_A = 0;
    public static final int CHOICE_B = 1;

    public static boolean isEnding(int scene) {
        return scene == END_NOT_PICKED || scene == END_MASTERMIND
                || scene == END_INNOCENT || scene == END_ARREST;
    }

    public static boolean hasChoices(int scene) {
        return scene == S1_CLASSROOM || scene == S4_SNACK
                || scene == S5_YAOYA || scene == S6_DECISION;
    }

    public static int nextLinear(int scene) {
        switch (scene) {
            case S2_KONBINI:
                return S3_DETECTIVE;
            case S3_DETECTIVE:
                return S4_SNACK;
            default:
                throw new IllegalArgumentException("次の場面がありません: " + scene);
        }
    }

    public static int choose(int scene, int choice, GameState state) {
        if (choice != CHOICE_A && choice != CHOICE_B) {
            throw new IllegalArgumentException("選択肢が不正です: " + choice);
        }
        switch (scene) {
            case S1_CLASSROOM:
                return choice == CHOICE_A ? S2_KONBINI : END_NOT_PICKED;
            case S4_SNACK:
                if (choice == CHOICE_B) {
                    state.ateOnigiri = true;
                }
                return S5_YAOYA;
            case S5_YAOYA:
                if (choice == CHOICE_A) {
                    state.boughtApple = true;
                }
                return S6_DECISION;
            case S6_DECISION:
                return resolveEnding(state, choice == CHOICE_A);
            default:
                throw new IllegalArgumentException("選択肢のない場面です: " + scene);
        }
    }

    public static int resolveEnding(GameState state, boolean destroyedNote) {
        // 仕様に矛盾があるため、このエンディングの優先順位はチームで確認してください。
        if (state.ateOnigiri || !destroyedNote) {
            return END_ARREST;
        }
        if (state.boughtApple) {
            return END_MASTERMIND;
        }
        return END_INNOCENT;
    }
}
