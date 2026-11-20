package com.example.helloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class EndingCollectionActivity extends AppCompatActivity {

    private final String[] names = {
            "いつもの日常",
            "潔白",
            "黒幕",
            "逮捕"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending_collection);

        LinearLayout endingList = findViewById(R.id.endingList);
        TextView progress = findViewById(R.id.endingProgress);
        TextView backButton = findViewById(R.id.backButton);

        // 保存用IDは変更せず、一覧だけを本編のEND番号順に並べます。
        int[] endings = {
                StoryEngine.END_NOT_PICKED,
                StoryEngine.END_INNOCENT,
                StoryEngine.END_MASTERMIND,
                StoryEngine.END_ARREST
        };
        int seen = SaveData.seenEndingCount(this);

        progress.setText(seen + " / " + endings.length + " ENDINGS");

        for (int i = 0; i < endings.length; i++) {

            boolean cleared = SaveData.isEndingSeen(this, endings[i]);

            String endingName = cleared ? names[i] : "？？？";

            // 各エンディングの表示欄
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setPadding(dp(16), dp(12), dp(16), dp(12));

            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.rgb(26, 16, 22));
            border.setStroke(dp(1), Color.rgb(113, 50, 62));
            border.setCornerRadius(dp(4));

            item.setBackground(border);

            // END番号
            TextView number = new TextView(this);
            number.setText(String.format("END %02d", i + 1));
            number.setTextColor(Color.rgb(189, 105, 117));
            number.setTextSize(17);
            number.setLetterSpacing(0.12f);

            // エンディング名
            TextView name = new TextView(this);
            name.setText(endingName);
            name.setTextColor(
                    cleared
                            ? Color.rgb(225, 201, 205)
                            : Color.rgb(138, 119, 124)
            );
            name.setTextSize(15);
            name.setTypeface(
                    androidx.core.content.res.ResourcesCompat.getFont(
                            this,
                            R.font.zen_old_mincho_medium
                    )
            );

            // 回収状況
            TextView status = new TextView(this);
            status.setText(cleared ? "RECORDED" : "UNKNOWN");
            status.setTextColor(Color.rgb(173, 98, 110));
            status.setTextSize(10);
            status.setGravity(Gravity.END);

            item.addView(number);
            item.addView(name);
            item.addView(status);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 0, dp(12));

            endingList.addView(item, params);
        }

        backButton.setOnClickListener(v -> finish());
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}