package com.pluscubed.recyclerfastscrollsample;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.google.android.material.snackbar.Snackbar;
import com.pluscubed.recyclerfastscroll.BuildConfig;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import com.pluscubed.recyclerfastscroll.RecyclerFastScrollerUtils;

public class ScrollingActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    static final int[] CUSTOMIZATION_NAMES = {
            R.string.hiding_enabled,
            R.string.handle_normal_color,
            R.string.handle_pressed_color,
            R.string.scrollbar_color,
            R.string.hide_delay,
            R.string.touch_target_width
    };

    RecyclerFastScroller mRecyclerFastScroller;

    public static int convertPxToDp(Context context, float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView view = findViewById(R.id.recyclerview);
        view.setAdapter(new ItemAdapter());
        view.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerFastScroller = findViewById(R.id.fast_scroller);
        mRecyclerFastScroller.attachRecyclerView(view);

        setTitle(R.string.title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, int color) {
        switch (colorChooserDialog.getTitle()) {
            case R.string.handle_normal_color:
                mRecyclerFastScroller.setHandleNormalColor(color);
                break;
            case R.string.handle_pressed_color:
                mRecyclerFastScroller.setHandlePressedColor(color);
                break;
            case R.string.scrollbar_color:
                mRecyclerFastScroller.setBarColor(color);
                break;
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }

    public void showAboutDialog() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.about_dialog_title, BuildConfig.VERSION_NAME))
                .positiveText(R.string.dismiss)
                .content(Html.fromHtml(getString(R.string.about_body)))
                .iconRes(R.mipmap.ic_launcher)
                .show();
    }


    void customizeColors(int title) {
        int colorChooserDialogTitle = 0;
        int preselectColor = 0;
        switch (title) {
            case R.string.handle_normal_color:
                colorChooserDialogTitle = R.string.handle_normal_color;
                preselectColor = mRecyclerFastScroller.getHandleNormalColor();
                break;
            case R.string.handle_pressed_color:
                colorChooserDialogTitle = R.string.handle_pressed_color;
                preselectColor = mRecyclerFastScroller.getHandlePressedColor();
                break;
            case R.string.scrollbar_color:
                colorChooserDialogTitle = R.string.scrollbar_color;
                preselectColor = mRecyclerFastScroller.getBarColor();
                break;
        }
        new ColorChooserDialog.Builder(this, colorChooserDialogTitle)
                .accentMode(title == R.string.handle_pressed_color)
                .preselect(preselectColor)
                .show(this);
    }

    void customizeTouchTargetWidth() {
        new MaterialDialog.Builder(this)
                .title(R.string.touch_target_width)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(null, String.valueOf(convertPxToDp(this, mRecyclerFastScroller.getTouchTargetWidth())),
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                                try {
                                    if (Integer.parseInt(charSequence.toString()) <= 48) {
                                        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                    } else {
                                        throw new NumberFormatException();
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(ScrollingActivity.this, R.string.touch_target_size_invalid, Toast.LENGTH_SHORT).show();
                                    materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                }
                            }
                        })
                .alwaysCallInputCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        int input = Integer.parseInt(materialDialog.getInputEditText().getText().toString());
                        mRecyclerFastScroller.setTouchTargetWidth(
                                RecyclerFastScrollerUtils.convertDpToPx(ScrollingActivity.this, input));
                    }
                })
                .show();
    }

    void customizeHideDelay() {
        new MaterialDialog.Builder(this)
                .title(R.string.hide_delay)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(null, String.valueOf(mRecyclerFastScroller.getHideDelay()), false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog materialDialog,
                                                CharSequence charSequence) {
                                try {
                                    if (Integer.parseInt(charSequence.toString()) >= 0) {
                                        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                    } else {
                                        throw new NumberFormatException();
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(ScrollingActivity.this, R.string.hide_delay_invalid, Toast.LENGTH_SHORT).show();
                                    materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                }
                            }
                        })
                .alwaysCallInputCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        int input = Integer.parseInt(
                                materialDialog.getInputEditText().getText().toString());
                        mRecyclerFastScroller.setHideDelay(input);
                    }
                })
                .show();
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        ItemAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ScrollingActivity.this).inflate(R.layout.list_item_main, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (CUSTOMIZATION_NAMES.length > position) {
                if (position == 0) {
                    setHidingButtonText(holder.button);
                } else {
                    holder.button.setText(CUSTOMIZATION_NAMES[position]);
                }
            } else {
                holder.button.setText(String.format(getString(R.string.item_number), position + 1));
            }
        }

        @Override
        public int getItemCount() {
            return 1000;
        }

        void setHidingButtonText(Button button) {
            if (mRecyclerFastScroller.isHidingEnabled()) {
                button.setText(R.string.hiding_enabled);
            } else {
                button.setText(R.string.hiding_disabled);
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            Button button;

            public ViewHolder(View itemView) {
                super(itemView);

                button = (Button) itemView.findViewById(R.id.list_item_main_button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (CUSTOMIZATION_NAMES.length > position) {
                            switch (position) {
                                case 0:
                                    final boolean hidingEnabled = !mRecyclerFastScroller.isHidingEnabled();
                                    mRecyclerFastScroller.setHidingEnabled(hidingEnabled);
                                    setHidingButtonText(button);
                                    if (!hidingEnabled) {
                                        mRecyclerFastScroller.show(false);
                                    }
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                    customizeColors(CUSTOMIZATION_NAMES[position]);
                                    break;
                                case 4:
                                    customizeHideDelay();
                                    break;
                                case 5:
                                    customizeTouchTargetWidth();
                                    break;
                            }
                        } else {
                            Snackbar.make(v, String.format(getString(R.string.item_pressed_snackbar), button.getText()), Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        }
    }
}
