package com.example.helloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button continueButton;
    private TextView endingCount;

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
                SaveData.clear(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("continue", true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
