package com.example.helloandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button continueButton;
    private TextView endingCount;
    private boolean opening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = findViewById(R.id.start);
        continueButton = findViewById(R.id.continueButton);
        endingCount = findViewById(R.id.endingCount);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SaveData.hasSave(MainActivity.this)) {
                    openStory(false);
                    return;
                }
                // 続きのデータを消してしまう前に確認します。
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm_new_game_title)
                        .setMessage(R.string.confirm_new_game_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> openStory(false))
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStory(true);
            }
        });
    }

    private void openStory(boolean continueGame) {
        // 連打でストーリー画面が二重に開かないようにします。onResume で戻します。
        if (opening) {
            return;
        }
        opening = true;
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("continue", continueGame);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        opening = false;
        continueButton.setVisibility(SaveData.hasSave(this) ? View.VISIBLE : View.GONE);
        int seen = SaveData.seenEndingCount(this);
        endingCount.setText(getString(R.string.ending_count, seen, StoryEngine.ENDINGS.length));
        endingCount.setVisibility(seen == 0 ? View.GONE : View.VISIBLE);
        BgmPlayer.play(this, R.raw.bgm_dark);
        // 同じ曲のままタイトルに戻った場合も、一時停止を解除します。
        BgmPlayer.resume();
    }

    @Override
    protected void onPause() {
        BgmPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // タイトルを閉じたらアプリ終了なので、共有の MediaPlayer を解放します。
        if (isFinishing()) {
            BgmPlayer.stop();
        }
        super.onDestroy();
    }
}
