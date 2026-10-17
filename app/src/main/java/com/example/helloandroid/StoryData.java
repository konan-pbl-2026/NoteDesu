package com.example.helloandroid;

public class StoryData {
    public static int backgroundRes(int scene) {
        // 背景画像は後のPRで差し替えるため、今は全場面で同じ画像を使います。
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
            case StoryEngine.S2_KONBINI:
            case StoryEngine.S3_DETECTIVE:
            case StoryEngine.S4_SNACK:
            case StoryEngine.S5_YAOYA:
            case StoryEngine.S6_DECISION:
            case StoryEngine.END_NOT_PICKED:
            case StoryEngine.END_MASTERMIND:
            case StoryEngine.END_INNOCENT:
            case StoryEngine.END_ARREST:
                return R.drawable.haikei_sora;
            default:
                throw new IllegalArgumentException("場面が不正です: " + scene);
        }
    }

    public static int linesArrayRes(int scene) {
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
                return R.array.story_s1_lines;
            case StoryEngine.S2_KONBINI:
                return R.array.story_s2_lines;
            case StoryEngine.S3_DETECTIVE:
                return R.array.story_s3_lines;
            case StoryEngine.S4_SNACK:
                return R.array.story_s4_lines;
            case StoryEngine.S5_YAOYA:
                return R.array.story_s5_lines;
            case StoryEngine.S6_DECISION:
                return R.array.story_s6_lines;
            case StoryEngine.END_NOT_PICKED:
                return R.array.story_end_not_picked_lines;
            case StoryEngine.END_MASTERMIND:
                return R.array.story_end_mastermind_lines;
            case StoryEngine.END_INNOCENT:
                return R.array.story_end_innocent_lines;
            case StoryEngine.END_ARREST:
                return R.array.story_end_arrest_lines;
            default:
                throw new IllegalArgumentException("場面が不正です: " + scene);
        }
    }

    public static int choiceARes(int scene) {
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
                return R.string.story_s1_choice_a;
            case StoryEngine.S4_SNACK:
                return R.string.story_s4_choice_a;
            case StoryEngine.S5_YAOYA:
                return R.string.story_s5_choice_a;
            case StoryEngine.S6_DECISION:
                return R.string.story_s6_choice_a;
            default:
                throw new IllegalArgumentException("選択肢のない場面です: " + scene);
        }
    }

    public static int choiceBRes(int scene) {
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
                return R.string.story_s1_choice_b;
            case StoryEngine.S4_SNACK:
                return R.string.story_s4_choice_b;
            case StoryEngine.S5_YAOYA:
                return R.string.story_s5_choice_b;
            case StoryEngine.S6_DECISION:
                return R.string.story_s6_choice_b;
            default:
                throw new IllegalArgumentException("選択肢のない場面です: " + scene);
        }
    }
}
