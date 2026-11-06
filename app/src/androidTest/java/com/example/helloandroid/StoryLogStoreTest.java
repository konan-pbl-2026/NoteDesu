package com.example.helloandroid;

import android.content.Context;
import android.database.Cursor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** テスト用インストールで実行すること（履歴を初期化します）。 */
@RunWith(AndroidJUnit4.class)
public class StoryLogStoreTest {
    private Context context;
    private StoryLogStore store;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        store = new StoryLogStore(context);
        store.clear();
    }

    @After
    public void tearDown() {
        store.clear();
        store.close();
    }

    @Test
    public void preservesTextAndSelectedAnswerInOrderAfterReopening() {
        store.recordLine(1, 0, "地の文");
        store.recordLine(1, 1, "主人公｜セリフ");
        store.recordChoice(1, 1, "拾う");
        store.recordLine(12, 0, "次の場面");
        store.close();
        store = new StoryLogStore(context);
        try (Cursor cursor = store.read()) {
            assertEquals(4, cursor.getCount());
            assertTrue(cursor.moveToFirst());
            assertEquals("", value(cursor, StoryLogStore.SPEAKER));
            assertEquals("地の文", value(cursor, StoryLogStore.BODY));
            assertTrue(cursor.moveToNext());
            assertEquals("主人公", value(cursor, StoryLogStore.SPEAKER));
            assertEquals("セリフ", value(cursor, StoryLogStore.BODY));
            assertTrue(cursor.moveToNext());
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow(StoryLogStore.IS_CHOICE)));
            assertEquals("拾う", value(cursor, StoryLogStore.BODY));
            assertTrue(cursor.moveToNext());
            assertEquals("次の場面", value(cursor, StoryLogStore.BODY));
            assertFalse(cursor.moveToNext());
        }
    }

    @Test
    public void restoringAndRenderingDoesNotDuplicateEntries() {
        store.recordLine(1, 0, "最初のページ");
        store.recordLine(1, 1, "最後のページ");
        store.restoreTo(1, 1);
        store.recordLine(1, 1, "最後のページ");
        try (Cursor cursor = store.read()) {
            assertEquals(2, cursor.getCount());
        }
    }

    @Test
    public void restoreDiscardsUnsavedFutureAndAllowsADifferentAnswer() {
        store.recordLine(1, 0, "選択前");
        store.recordChoice(1, 0, "拾う");
        store.recordLine(12, 0, "保存されていない次の場面");
        store.restoreTo(1, 0);
        store.recordChoice(1, 0, "拾わない");
        try (Cursor cursor = store.read()) {
            assertEquals(2, cursor.getCount());
            assertTrue(cursor.moveToLast());
            assertEquals("拾わない", value(cursor, StoryLogStore.BODY));
        }
    }

    @Test
    public void newGameClearsHistoryButDoesNotChangeEndingCollection() {
        boolean endingBefore = context.getSharedPreferences("notedesu_save", Context.MODE_PRIVATE)
                .getBoolean("ending_7", false);
        store.recordLine(1, 0, "前のプレイ");
        store.clear();
        store.recordLine(1, 0, "新しいプレイ");
        try (Cursor cursor = store.read()) {
            assertEquals(1, cursor.getCount());
            assertTrue(cursor.moveToFirst());
            assertEquals("新しいプレイ", value(cursor, StoryLogStore.BODY));
        }
        assertEquals(endingBefore,
                context.getSharedPreferences("notedesu_save", Context.MODE_PRIVATE)
                        .getBoolean("ending_7", false));
    }

    @Test
    public void legacySaveStartsHistoryAtItsCurrentPage() {
        store.recordLine(1, 0, "別の履歴");
        store.restoreTo(3, 5);
        store.recordLine(3, 5, "セーブの現在位置");
        try (Cursor cursor = store.read()) {
            assertEquals(1, cursor.getCount());
            assertTrue(cursor.moveToFirst());
            assertEquals("セーブの現在位置", value(cursor, StoryLogStore.BODY));
        }
    }

    private String value(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }
}
