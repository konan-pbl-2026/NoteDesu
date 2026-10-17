package com.example.helloandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
    private final GameState state = new GameState();
    private ImageView background;
    private TextView text;
    private View choices;
    private Button choiceA;
    private Button choiceB;
    private Button endButton;
    private View nextIndicator;
    private String[] lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        background = findViewById(R.id.imageView);
        text = findViewById(R.id.text1);
        choices = findViewById(R.id.choices);
        choiceA = findViewById(R.id.choiceA);
        choiceB = findViewById(R.id.choiceB);
        endButton = findViewById(R.id.endButton);
        nextIndicator = findViewById(R.id.nextIndicator);

        if (savedInstanceState != null) {
            state.scene = savedInstanceState.getInt("scene", StoryEngine.S1_CLASSROOM);
            state.lineIndex = savedInstanceState.getInt("lineIndex", 0);
            state.ateOnigiri = savedInstanceState.getBoolean("ateOnigiri", false);
            state.boughtApple = savedInstanceState.getBoolean("boughtApple", false);
        }

        findViewById(R.id.root).setOnClickListener(view -> {
            if (state.lineIndex < lines.length - 1) {
                state.lineIndex++;
            } else if (!StoryEngine.hasChoices(state.scene) && !StoryEngine.isEnding(state.scene)) {
                state.scene = StoryEngine.nextLinear(state.scene);
                state.lineIndex = 0;
            } else {
                return;
            }
            render();
        });
        choiceA.setOnClickListener(view -> choose(StoryEngine.CHOICE_A));
        choiceB.setOnClickListener(view -> choose(StoryEngine.CHOICE_B));
        endButton.setOnClickListener(view -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (StoryEngine.isEnding(state.scene)) {
                    finish();
                    return;
                }
                new AlertDialog.Builder(MainActivity2.this)
                        .setTitle(R.string.confirm_exit_title)
                        .setMessage(R.string.confirm_exit_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        render();
    }

    private void choose(int choice) {
        // 連打で同じ選択を繰り返さないよう、画面を切り替える前に無効にします。
        choiceA.setEnabled(false);
        choiceB.setEnabled(false);
        state.scene = StoryEngine.choose(state.scene, choice, state);
        state.lineIndex = 0;
        render();
    }

    private void render() {
        background.setImageResource(StoryData.backgroundRes(state.scene));
        lines = getResources().getStringArray(StoryData.linesArrayRes(state.scene));
        text.setText(lines[state.lineIndex]);
        choices.setVisibility(View.GONE);
        endButton.setVisibility(View.GONE);
        nextIndicator.setVisibility(View.GONE);

        if (state.lineIndex < lines.length - 1) {
            nextIndicator.setVisibility(View.VISIBLE);
        } else if (StoryEngine.isEnding(state.scene)) {
            endButton.setVisibility(View.VISIBLE);
        } else if (StoryEngine.hasChoices(state.scene)) {
            choiceA.setText(StoryData.choiceARes(state.scene));
            choiceB.setText(StoryData.choiceBRes(state.scene));
            choiceA.setEnabled(true);
            choiceB.setEnabled(true);
            choices.setVisibility(View.VISIBLE);
        } else {
            nextIndicator.setVisibility(View.VISIBLE);
        }

        if (nextIndicator.getVisibility() == View.VISIBLE) {
            if (nextIndicator.getAnimation() == null) {
                nextIndicator.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(this, R.anim.blink));
            }
        } else {
            nextIndicator.clearAnimation();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("scene", state.scene);
        outState.putInt("lineIndex", state.lineIndex);
        outState.putBoolean("ateOnigiri", state.ateOnigiri);
        outState.putBoolean("boughtApple", state.boughtApple);
        super.onSaveInstanceState(outState);
    }
}
