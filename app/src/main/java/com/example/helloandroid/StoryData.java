package com.example.helloandroid;

public class StoryData {
    public static int backgroundRes(int scene) {
        // 「普通の生活」に戻るエンディングでは、意図的に明るい空を使います。
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
            case StoryEngine.S1_REFUSE:
            case StoryEngine.S1_PICKED:
                return R.drawable.classroom_day;
            case StoryEngine.S2_KONBINI:
                return R.drawable.konbini_night;
            case StoryEngine.S2_AFTER:
            case StoryEngine.S3_BAIT_IGNORED:
            case StoryEngine.S4_CHIPS:
            case StoryEngine.S4_ONIGIRI:
            case StoryEngine.S5_APPLE:
            case StoryEngine.S5_NO_APPLE:
                return R.drawable.room_night;
            case StoryEngine.S3_BROADCAST:
            case StoryEngine.S3_BAIT_TAKEN:
                return R.drawable.city_screen_night;
            case StoryEngine.S3_DETECTIVE:
                return R.drawable.living_night;
            case StoryEngine.S4_SNACK:
                return R.drawable.kitchen_night;
            case StoryEngine.S5_CAMPUS:
                return R.drawable.campus_day;
            case StoryEngine.S5_YAOYA:
                return R.drawable.yaoya;
            case StoryEngine.S6_DECISION:
                return R.drawable.rooftop_night;
            case StoryEngine.END_NOT_PICKED:
                return R.drawable.haikei_sora;
            case StoryEngine.END_MASTERMIND:
                return R.drawable.apple_night;
            case StoryEngine.END_INNOCENT:
                return R.drawable.haikei_sora;
            case StoryEngine.END_ARREST:
                return R.drawable.arrest_night;
            default:
                throw new IllegalArgumentException("場面が不正です: " + scene);
        }
    }

    public static int linesArrayRes(int scene) {
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
                return R.array.story_s1_lines;
            case StoryEngine.S1_REFUSE:
                return R.array.story_s1_refuse_lines;
            case StoryEngine.S1_PICKED:
                return R.array.story_s1_picked_lines;
            case StoryEngine.S2_KONBINI:
                return R.array.story_s2_lines;
            case StoryEngine.S2_AFTER:
                return R.array.story_s2_after_lines;
            case StoryEngine.S3_BROADCAST:
                return R.array.story_s3_broadcast_lines;
            case StoryEngine.S3_BAIT_TAKEN:
                return R.array.story_s3_bait_taken_lines;
            case StoryEngine.S3_BAIT_IGNORED:
                return R.array.story_s3_bait_ignored_lines;
            case StoryEngine.S3_DETECTIVE:
                return R.array.story_s3_lines;
            case StoryEngine.S4_SNACK:
                return R.array.story_s4_lines;
            case StoryEngine.S4_CHIPS:
                return R.array.story_s4_chips_lines;
            case StoryEngine.S4_ONIGIRI:
                return R.array.story_s4_onigiri_lines;
            case StoryEngine.S5_CAMPUS:
                return R.array.story_s5_campus_lines;
            case StoryEngine.S5_YAOYA:
                return R.array.story_s5_lines;
            case StoryEngine.S5_APPLE:
                return R.array.story_s5_apple_lines;
            case StoryEngine.S5_NO_APPLE:
                return R.array.story_s5_no_apple_lines;
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
            case StoryEngine.S1_REFUSE:
                return R.string.story_s1_refuse_choice_a;
            case StoryEngine.S3_BROADCAST:
                return R.string.story_s3_broadcast_choice_a;
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
            case StoryEngine.S1_REFUSE:
                return R.string.story_s1_refuse_choice_b;
            case StoryEngine.S3_BROADCAST:
                return R.string.story_s3_broadcast_choice_b;
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

    public static int characterRes(int scene) {
        switch (scene) {
            case StoryEngine.S1_PICKED:
            case StoryEngine.S2_AFTER:
            case StoryEngine.S5_APPLE:
            case StoryEngine.S5_NO_APPLE:
            case StoryEngine.END_MASTERMIND:
                return R.drawable.shinigami;
            case StoryEngine.S6_DECISION:
                return R.drawable.note_item;
            case StoryEngine.S1_CLASSROOM:
            case StoryEngine.S1_REFUSE:
            case StoryEngine.S2_KONBINI:
            case StoryEngine.S3_BROADCAST:
            case StoryEngine.S3_BAIT_TAKEN:
            case StoryEngine.S3_BAIT_IGNORED:
            case StoryEngine.S3_DETECTIVE:
            case StoryEngine.S4_SNACK:
            case StoryEngine.S4_CHIPS:
            case StoryEngine.S4_ONIGIRI:
            case StoryEngine.S5_CAMPUS:
            case StoryEngine.S5_YAOYA:
            case StoryEngine.END_NOT_PICKED:
            case StoryEngine.END_INNOCENT:
            case StoryEngine.END_ARREST:
                return 0;
            default:
                throw new IllegalArgumentException("場面が不正です: " + scene);
        }
    }

    public static int bgmRes(int scene) {
        switch (scene) {
            case StoryEngine.S1_CLASSROOM:
            case StoryEngine.S1_REFUSE:
            case StoryEngine.S5_YAOYA:
            case StoryEngine.END_NOT_PICKED:
            case StoryEngine.END_INNOCENT:
                return R.raw.bgm_daily;
            case StoryEngine.S1_PICKED:
            case StoryEngine.S2_AFTER:
            case StoryEngine.S3_BROADCAST:
            case StoryEngine.S3_BAIT_TAKEN:
            case StoryEngine.S3_BAIT_IGNORED:
            case StoryEngine.S5_APPLE:
            case StoryEngine.S5_NO_APPLE:
            case StoryEngine.END_MASTERMIND:
                return R.raw.bgm_dark;
            case StoryEngine.S2_KONBINI:
            case StoryEngine.S3_DETECTIVE:
            case StoryEngine.S4_SNACK:
            case StoryEngine.S4_CHIPS:
            case StoryEngine.S4_ONIGIRI:
            case StoryEngine.S5_CAMPUS:
            case StoryEngine.S6_DECISION:
            case StoryEngine.END_ARREST:
                return R.raw.bgm_tension;
            default:
                throw new IllegalArgumentException("場面が不正です: " + scene);
        }
    }
}
