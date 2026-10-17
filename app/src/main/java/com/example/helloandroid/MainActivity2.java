package com.example.helloandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
    private final GameState state = new GameState();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView background;
    private ImageView character;
    private TextView text;
    private TextView speaker;
    private View choices;
    private Button choiceA;
    private Button choiceB;
    private Button endButton;
    private View nextIndicator;
    private String[] lines;
    private String body;
    private int visibleCharacters;
    private boolean typing;
    private boolean showLineImmediately;
    private int displayedScene;
    private int displayedCharacter;

    private final Runnable typeNextCharacter = new Runnable() {
        @Override
        public void run() {
            // サロゲートペアを途中で分けず、絵文字も一文字として表示します。
            visibleCharacters = body.offsetByCodePoints(visibleCharacters, 1);
            text.setText(body.substring(0, visibleCharacters));
            if (visibleCharacters < body.length()) {
                handler.postDelayed(this, 35);
            } else {
                typing = false;
                showLineActions();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        background = findViewById(R.id.imageView);
        character = findViewById(R.id.character);
        text = findViewById(R.id.text1);
        speaker = findViewById(R.id.speaker);
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
            showLineImmediately = true;
        } else if (getIntent().getBooleanExtra("continue", false)) {
            if (!SaveData.load(this, state)) {
                state.reset();
            }
        } else {
            SaveData.clear(this);
        }

        findViewById(R.id.root).setOnClickListener(view -> {
            if (typing) {
                finishTyping();
                return;
            }
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
        if (typing || choices.getVisibility() != View.VISIBLE) {
            return;
        }
        // 連打で同じ選択を繰り返さないよう、画面を切り替える前に無効にします。
        choiceA.setEnabled(false);
        choiceB.setEnabled(false);
        state.scene = StoryEngine.choose(state.scene, choice, state);
        state.lineIndex = 0;
        render();
    }

    private void render() {
        handler.removeCallbacks(typeNextCharacter);
        typing = false;
        if (displayedScene != state.scene) {
            background.animate().cancel();
            background.setImageResource(StoryData.backgroundRes(state.scene));
            background.setAlpha(0f);
            background.animate().alpha(1f).setDuration(450).start();
            displayedScene = state.scene;
        }

        int characterRes = StoryData.characterRes(state.scene);
        if (displayedCharacter != characterRes) {
            character.animate().cancel();
            if (characterRes == 0) {
                character.setVisibility(View.GONE);
            } else {
                character.setImageResource(characterRes);
                character.setVisibility(View.VISIBLE);
                character.setAlpha(0f);
                character.animate().alpha(1f).setDuration(450).start();
            }
            displayedCharacter = characterRes;
        }

        BgmPlayer.play(this, StoryData.bgmRes(state.scene));
        lines = getResources().getStringArray(StoryData.linesArrayRes(state.scene));
        // 本文の改稿で行数が減っても、保存した位置でクラッシュしないようにします。
        if (state.lineIndex < 0 || state.lineIndex >= lines.length) {
            state.lineIndex = 0;
        }
        String name = StoryLine.speaker(lines[state.lineIndex]);
        speaker.setText(name);
        speaker.setVisibility(name.isEmpty() ? View.GONE : View.VISIBLE);
        body = StoryLine.body(lines[state.lineIndex]);
        choices.setVisibility(View.GONE);
        endButton.setVisibility(View.GONE);
        nextIndicator.setVisibility(View.GONE);
        nextIndicator.clearAnimation();

        if (showLineImmediately || body.isEmpty()) {
            showLineImmediately = false;
            finishTyping();
        } else {
            text.setText("");
            visibleCharacters = 0;
            typing = true;
            handler.postDelayed(typeNextCharacter, 35);
        }

        if (StoryEngine.isEnding(state.scene)) {
            SaveData.markEndingSeen(this, state.scene);
            SaveData.clear(this);
        } else {
            SaveData.save(this, state);
        }
    }

    private void finishTyping() {
        handler.removeCallbacks(typeNextCharacter);
        typing = false;
        text.setText(body);
        showLineActions();
    }

    private void showLineActions() {
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
    protected void onPause() {
        BgmPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BgmPlayer.resume();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        background.animate().cancel();
        character.animate().cancel();
        super.onDestroy();
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
