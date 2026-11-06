package com.example.helloandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ScrollView;
import java.util.ArrayList;

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

    private ScrollView storyScroll;
    private boolean isUserScrolling = false;

    private ArrayList<String> history = new ArrayList<>();

    private final Runnable typeNextCharacter = new Runnable() {
        @Override
        public void run() {
            // サロゲートペアを途中で分けず、絵文字も一文字として表示します。
            visibleCharacters = body.offsetByCodePoints(visibleCharacters, 1);
            updateStoryText(body.substring(0, visibleCharacters));
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
        storyScroll = findViewById(R.id.storyScroll);
        storyScroll.setOnScrollChangeListener(
                (View v, int scrollX, int scrollY,
                 int oldScrollX, int oldScrollY) -> {

                    // 上にスワイプして過去ログを読んでいるか判定
                    isUserScrolling = scrollY > 24;
                }
        );
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
                saveCurrentLineToHistory();
                state.lineIndex++;

            } else if (!StoryEngine.hasChoices(state.scene)
                    && !StoryEngine.isEnding(state.scene)) {

                saveCurrentLineToHistory();

                state.scene = StoryEngine.nextLinear(state.scene);
                state.lineIndex = 0;

            }else {
                return;
            }
            render();
        });
        choiceA.setOnClickListener(view -> choose(StoryEngine.CHOICE_A));
        choiceB.setOnClickListener(view -> choose(StoryEngine.CHOICE_B));
        endButton.setOnClickListener(view -> {
            SaveData.clear(this);
            finish();
        });

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
        saveCurrentLineToHistory();
        state.scene = StoryEngine.choose(state.scene, choice, state);
        state.lineIndex = 0;
        render();
    }

    private void render() {
        handler.removeCallbacks(typeNextCharacter);
        typing = false;
        isUserScrolling = false;
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
        // 現在の文章を履歴に保存

        // 読み上げでは話す人の名前も一緒に伝えます。
        text.setContentDescription(name.isEmpty() ? null : name + "、" + body);
        choices.setVisibility(View.GONE);
        endButton.setVisibility(View.GONE);
        nextIndicator.setVisibility(View.GONE);
        nextIndicator.clearAnimation();

        // 読み上げ（TalkBack）中は一文字ずつ出すと何度も読み上げられるので、一度に表示します。
        if (showLineImmediately || body.isEmpty() || isScreenReaderOn()) {
            showLineImmediately = false;
            finishTyping();
        } else {
            updateStoryText("");
            visibleCharacters = 0;
            typing = true;
            handler.postDelayed(typeNextCharacter, 35);
        }

        // エンディングの途中で閉じても続きから読めるよう、エンディングも保存します。
        SaveData.save(this, state);
    }
    // 今表示している文章を履歴に保存する
    private void saveCurrentLineToHistory() {
        if (body == null) return;

        String name = speaker.getText().toString();

        String historyText = name.isEmpty()
                ? body
                : name + "\n" + body;

        history.add(historyText);
    }

    // 履歴と現在の文章をまとめて表示する
    private void updateStoryText(String currentText) {

        StringBuilder displayText = new StringBuilder();

        // 最新の文章を先に表示
        displayText.append(currentText);

        // その後ろに過去ログを追加
        for (int i = history.size() - 1; i >= 0; i--) {
            displayText.append("\n\n").append(history.get(i));
        }

        text.setText(displayText.toString());

        // 最新の文章が先頭に来るようにする
        storyScroll.post(() -> {
            storyScroll.scrollTo(0, 0);
        });
    }

    private boolean isScreenReaderOn() {
        AccessibilityManager manager = getSystemService(AccessibilityManager.class);
        return manager != null && manager.isTouchExplorationEnabled();
    }

    private void finishTyping() {
        handler.removeCallbacks(typeNextCharacter);
        typing = false;
        updateStoryText(body);
        showLineActions();
    }

    private void showLineActions() {
        if (state.lineIndex < lines.length - 1) {
            nextIndicator.setVisibility(View.VISIBLE);
        } else if (StoryEngine.isEnding(state.scene)) {
            // 最後のページまで読んだ時点で、エンディングを見たことにします。
            SaveData.markEndingSeen(this, state.scene);
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
