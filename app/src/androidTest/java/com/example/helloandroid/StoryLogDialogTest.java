package com.example.helloandroid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** ボタン未実装でも、公開メソッドから実際の重ね表示を検証します。 */
@RunWith(AndroidJUnit4.class)
public class StoryLogDialogTest {
    @Test
    public void repeatedOpenAndClosePreserveTheStoryPage() {
        try (ActivityScenario<MainActivity2> scenario = launchStory()) {
            String[] page = new String[1];
            scenario.onActivity(activity -> {
                String firstLine = activity.getResources().getStringArray(
                        StoryData.linesArrayRes(StoryEngine.S1_CLASSROOM))[0];
                if (!((TextView) activity.findViewById(R.id.text1)).getText().toString()
                        .equals(StoryLine.body(firstLine))) {
                    activity.findViewById(R.id.root).performClick();
                }
                page[0] = ((TextView) activity.findViewById(R.id.text1)).getText().toString();
                activity.showStoryLog();
                activity.showStoryLog();
                assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
            });
            onView(withId(R.id.storyLogList)).check(matches(isDisplayed()));
            onView(withId(R.id.closeStoryLog)).perform(click());
            scenario.onActivity(activity -> {
                assertNull(activity.getSupportFragmentManager()
                        .findFragmentByTag(StoryLogDialogFragment.TAG));
                assertEquals(page[0], ((TextView) activity.findViewById(R.id.text1))
                        .getText().toString());
                activity.showStoryLog();
            });
            pressBack();
            scenario.onActivity(activity -> assertEquals(page[0],
                    ((TextView) activity.findViewById(R.id.text1)).getText().toString()));
        }
    }

    @Test
    public void recreationKeepsOverlayAndDoesNotDuplicateTheCurrentPage() {
        try (ActivityScenario<MainActivity2> scenario = launchStory()) {
            scenario.onActivity(MainActivity2::showStoryLog);
            scenario.recreate();
            onView(withId(R.id.closeStoryLog)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> {
                assertNotNull(activity.getSupportFragmentManager()
                        .findFragmentByTag(StoryLogDialogFragment.TAG));
                try (StoryLogStore store = new StoryLogStore(activity);
                     Cursor cursor = store.read()) {
                    assertEquals(1, cursor.getCount());
                }
            });
            onView(withId(R.id.closeStoryLog)).perform(click());
        }
    }

    @Test
    public void selectingAnAnswerRecordsOnlyTheChosenAnswer() {
        try (ActivityScenario<MainActivity2> scenario = launchStory()) {
            scenario.onActivity(activity -> {
                String[] lines = activity.getResources().getStringArray(
                        StoryData.linesArrayRes(StoryEngine.S1_CLASSROOM));
                int lineCount = lines.length;
                View root = activity.findViewById(R.id.root);
                for (int i = 0; i < lineCount; i++) {
                    if (!((TextView) activity.findViewById(R.id.text1)).getText().toString()
                            .equals(StoryLine.body(lines[i]))) {
                        root.performClick(); // 全文表示
                    }
                    if (i < lineCount - 1) {
                        root.performClick(); // 次ページ
                    }
                }
                activity.findViewById(R.id.choiceA).performClick();
                try (StoryLogStore store = new StoryLogStore(activity);
                     Cursor cursor = store.read()) {
                    assertEquals(lineCount + 2, cursor.getCount());
                    cursor.moveToPosition(lineCount);
                    assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow(
                            StoryLogStore.IS_CHOICE)));
                    assertEquals(activity.getString(StoryData.choiceARes(StoryEngine.S1_CLASSROOM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(StoryLogStore.BODY)));
                }
            });
        }
    }

    private ActivityScenario<MainActivity2> launchStory() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return ActivityScenario.launch(new Intent(context, MainActivity2.class)
                .putExtra("continue", false));
    }
}
