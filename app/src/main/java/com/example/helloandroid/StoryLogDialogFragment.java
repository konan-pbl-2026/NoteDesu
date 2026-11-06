package com.example.helloandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;

/** ストーリー画面と進行を保ったまま表示するログ画面。 */
public class StoryLogDialogFragment extends DialogFragment {
    public static final String TAG = "story_log_dialog";
    private StoryLogStore store;
    private CursorAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_story_log, container, false);
        ViewCompat.setAccessibilityHeading(view.findViewById(R.id.storyLogTitle), true);
        ListView list = view.findViewById(R.id.storyLogList);
        list.setEmptyView(view.findViewById(R.id.storyLogEmpty));
        store = new StoryLogStore(requireContext());
        adapter = new LogAdapter(requireContext(), store.read());
        list.setAdapter(adapter);
        // 初回は直近の発言を表示。再作成時のスクロール位置は ListView が復元します。
        if (savedInstanceState == null && adapter.getCount() > 0) {
            list.setSelection(adapter.getCount() - 1);
        }
        view.findViewById(R.id.closeStoryLog).setOnClickListener(button -> dismiss());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = requireDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.45f;
            window.setAttributes(params);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onDestroyView() {
        if (adapter != null) {
            adapter.changeCursor(null);
            adapter = null;
        }
        if (store != null) {
            store.close();
            store = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof MainActivity2) {
            ((MainActivity2) getActivity()).onStoryLogDismissed();
        }
    }

    private static class LogAdapter extends CursorAdapter {
        LogAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_story_log, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            boolean choice = cursor.getInt(cursor.getColumnIndexOrThrow(
                    StoryLogStore.IS_CHOICE)) != 0;
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StoryLogStore.SPEAKER));
            TextView heading = view.findViewById(R.id.storyLogSpeaker);
            heading.setText(choice ? context.getString(R.string.story_log_answer) : name);
            heading.setVisibility(choice || !name.isEmpty() ? View.VISIBLE : View.GONE);
            TextView body = view.findViewById(R.id.storyLogBody);
            body.setText(cursor.getString(cursor.getColumnIndexOrThrow(StoryLogStore.BODY)));
            int color = ContextCompat.getColor(context,
                    choice ? R.color.story_log_answer : R.color.text_main);
            heading.setTextColor(color);
            body.setTextColor(color);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }
}
