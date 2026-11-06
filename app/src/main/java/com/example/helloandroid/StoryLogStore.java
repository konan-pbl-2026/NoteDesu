package com.example.helloandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** 現在のプレイの履歴。本文を保存するため、再起動後も同じ文章を読めます。 */
final class StoryLogStore extends SQLiteOpenHelper {
    private static final String TABLE = "story_log";
    static final String SPEAKER = "speaker";
    static final String BODY = "body";
    static final String IS_CHOICE = "is_choice";

    StoryLogStore(Context context) {
        super(context.getApplicationContext(), "notedesu_story_log.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "_id INTEGER PRIMARY KEY, scene INTEGER NOT NULL, "
                + "line_index INTEGER NOT NULL, is_choice INTEGER NOT NULL, "
                + "speaker TEXT NOT NULL, body TEXT NOT NULL, "
                + "UNIQUE(scene, line_index, is_choice))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // バージョンを上げる際は、既存の履歴を残す移行処理を追加します。
    }

    void recordLine(int scene, int lineIndex, String line) {
        record(scene, lineIndex, false, StoryLine.speaker(line), StoryLine.body(line));
    }

    void recordChoice(int scene, int lineIndex, String answer) {
        record(scene, lineIndex, true, "", answer);
    }

    private void record(int scene, int lineIndex, boolean choice, String speaker, String body) {
        ContentValues values = new ContentValues();
        values.put("scene", scene);
        values.put("line_index", lineIndex);
        values.put(IS_CHOICE, choice ? 1 : 0);
        values.put(SPEAKER, speaker);
        values.put(BODY, body);
        // 復元や再描画で同じページを二重に記録しません。
        getWritableDatabase().insertWithOnConflict(TABLE, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    Cursor read() {
        // Cursor のウィンドウと ListView の行再利用で、全履歴の View を作りません。
        return getReadableDatabase().query(TABLE,
                new String[]{"_id", SPEAKER, BODY, IS_CHOICE},
                null, null, null, null, "_id ASC");
    }

    void restoreTo(int scene, int lineIndex) {
        // 進行の自動保存直前にプロセスが終了した場合も、保存位置より先は見せません。
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.query(TABLE, new String[]{"_id"},
                "scene = ? AND line_index = ? AND is_choice = 0",
                new String[]{String.valueOf(scene), String.valueOf(lineIndex)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                db.delete(TABLE, "_id > ?", new String[]{String.valueOf(cursor.getLong(0))});
            } else {
                // ログ機能追加前のセーブには過去の選択履歴がないため、推測で補いません。
                clear();
            }
        }
    }

    void clear() {
        getWritableDatabase().delete(TABLE, null, null);
    }
}
