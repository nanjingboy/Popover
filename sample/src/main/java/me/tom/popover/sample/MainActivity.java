package me.tom.popover.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.tom.popover.HorizontalMargin;
import me.tom.popover.PopoverDialog;
import me.tom.popover.Size;

public class MainActivity extends AppCompatActivity {

    PopoverDialog mPopoverDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPopoverDialog = new PopoverDialog(this);

        findViewById(R.id.left_top_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View anchor) {
                showHorizontalScrollPopover(anchor);
            }
        });
        findViewById(R.id.left_bottom_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View anchor) {
                showHorizontalScrollPopover(anchor);
            }
        });

        findViewById(R.id.right_top_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View anchor) {
                showVerticalScrollPopover(anchor);
            }
        });
        findViewById(R.id.right_bottom_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View anchor) {
                showVerticalScrollPopover(anchor);
            }
        });
    }

    private void showHorizontalScrollPopover(View anchor) {
        TextView contentView = new TextView(MainActivity.this);
        contentView.setText("Hello World");
        ViewGroup.LayoutParams contentViewParams = new ViewGroup.LayoutParams(150, 100);
        contentView.setLayoutParams(contentViewParams);
        contentView.setBackgroundColor(Color.WHITE);
        mPopoverDialog.show(
                anchor,
                contentView,
                new Size(100, 100),
                new HorizontalMargin(0, 0),
                PopoverDialog.ScrollOrientation.HORIZONTAL
        );
    }


    private void showVerticalScrollPopover(View anchor) {
        TextView contentView = new TextView(MainActivity.this);
        contentView.setText("Hello World");
        ViewGroup.LayoutParams contentViewParams = new ViewGroup.LayoutParams(100, 200);
        contentView.setLayoutParams(contentViewParams);
        contentView.setBackgroundColor(Color.WHITE);
        mPopoverDialog.show(
                anchor,
                contentView,
                new Size(100, 100),
                new HorizontalMargin(0, 0),
                PopoverDialog.ScrollOrientation.VERTICAL
        );
    }
}