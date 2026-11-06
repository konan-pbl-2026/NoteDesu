package com.example.helloandroid;

public class StoryEngine {
    // 場面の番号はセーブデータにそのまま保存されるので、既存の番号は変えないこと。
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
    public static final int S1_REFUSE = 11;
    public static final int S1_PICKED = 12;
    public static final int S2_AFTER = 13;
    public static final int S3_BROADCAST = 14;
    public static final int S3_BAIT_TAKEN = 15;
    public static final int S3_BAIT_IGNORED = 16;
    public static final int S5_CAMPUS = 17;
    public static final int S4_CHIPS = 18;
    public static final int S4_ONIGIRI = 19;
    public static final int S5_APPLE = 20;
    public static final int S5_NO_APPLE = 21;

    public static final int[] ENDINGS = {
            END_NOT_PICKED, END_MASTERMIND, END_INNOCENT, END_ARREST
    };

    // ★追加: 「つづきから」のシーン選択に出す、各章の最初の場面。
    // strings.xml の chapter_titles と同じ順に並べること。
    public static final int[] CHAPTER_STARTS = {
            S1_CLASSROOM, S2_KONBINI, S3_BROADCAST, S4_SNACK, S5_CAMPUS, S6_DECISION
    };

    // ★追加: 場面が何章に属するか(1〜6)。エンディングは 0。
    public static int chapterOf(int scene) {
        switch (scene) {
            case S1_CLASSROOM:
            case S1_REFUSE:
            case S1_PICKED:
                return 1;
            case S2_KONBINI:
            case S2_AFTER:
                return 2;
            case S3_BROADCAST:
            case S3_BAIT_TAKEN:
            case S3_BAIT_IGNORED:
            case S3_DETECTIVE:
                return 3;
            case S4_SNACK:
            case S4_CHIPS:
            case S4_ONIGIRI:
                return 4;
            case S5_CAMPUS:
            case S5_YAOYA:
            case S5_APPLE:
            case S5_NO_APPLE:
                return 5;
            case S6_DECISION:
                return 6;
            default:
                return 0;
        }
    }

    // ★追加: from から to へ進んだ結果クリアになる章(1〜6)を返す。クリアにならなければ 0。
    // 次の章へ進んだとき、または第六章から結末へ進んだときだけクリアとする。
    // 第一章で拾わずに終わる結末(END_NOT_PICKED)は、第一章のクリアに数えない。
    public static int clearedChapter(int from, int to) {
        int fromChapter = chapterOf(from);
        if (fromChapter == 0) {
            return 0;
        }
        int toChapter = chapterOf(to);
        if (toChapter == 0) {
            return fromChapter == CHAPTER_STARTS.length ? fromChapter : 0;
        }
        return toChapter > fromChapter ? fromChapter : 0;
    }

    public static final int CHOICE_A = 0;
    public static final int CHOICE_B = 1;

    static boolean isValidScene(int scene) {
        return scene >= S1_CLASSROOM && scene <= S5_NO_APPLE;
    }

    public static boolean isEnding(int scene) {
        return scene == END_NOT_PICKED || scene == END_MASTERMIND
                || scene == END_INNOCENT || scene == END_ARREST;
    }

    public static boolean hasChoices(int scene) {
        return scene == S1_CLASSROOM || scene == S1_REFUSE
                || scene == S3_BROADCAST || scene == S4_SNACK
                || scene == S5_YAOYA || scene == S6_DECISION;
    }

    public static int nextLinear(int scene) {
        switch (scene) {
            case S1_PICKED:
                return S2_KONBINI;
            case S2_KONBINI:
                return S2_AFTER;
            case S2_AFTER:
                return S3_BROADCAST;
            case S3_BAIT_TAKEN:
            case S3_BAIT_IGNORED:
                return S3_DETECTIVE;
            case S3_DETECTIVE:
                return S4_SNACK;
            case S4_CHIPS:
            case S4_ONIGIRI:
                return S5_CAMPUS;
            case S5_CAMPUS:
                return S5_YAOYA;
            case S5_APPLE:
            case S5_NO_APPLE:
                return S6_DECISION;
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
                return choice == CHOICE_A ? S1_PICKED : S1_REFUSE;
            case S1_REFUSE:
                return choice == CHOICE_A ? S1_PICKED : END_NOT_PICKED;
            case S3_BROADCAST:
                return choice == CHOICE_A ? S3_BAIT_TAKEN : S3_BAIT_IGNORED;
            case S4_SNACK:
                if (choice == CHOICE_B) {
                    state.ateOnigiri = true;
                    return S4_ONIGIRI;
                }
                return S4_CHIPS;
            case S5_YAOYA:
                if (choice == CHOICE_A) {
                    state.boughtApple = true;
                    return S5_APPLE;
                }
                return S5_NO_APPLE;
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