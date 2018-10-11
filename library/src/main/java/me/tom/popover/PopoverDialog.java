package me.tom.popover;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class PopoverDialog {

    public enum ScrollOrientation {
        HORIZONTAL,
        VERTICAL
    }

    private Size mWindowSize;
    private int mArrowSize;
    private int mStatusBarHeight;

    private Context mContext;
    private AlertDialog mAlertDialog;

    public PopoverDialog(Context context) {
        mContext = context;
        mAlertDialog = new AlertDialog.Builder(context, R.style.PopoverDialog).create();
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(false);
    }

    public void show(View anchor, View contentView, Size popoverSize, HorizontalMargin popoverMargin) {
        show(anchor, contentView, popoverSize, popoverMargin, ScrollOrientation.HORIZONTAL);
    }

    public void show(View anchor, View contentView, Size popoverSize, ScrollOrientation orientation) {
        show(anchor, contentView, popoverSize, new HorizontalMargin(0, 0), orientation);
    }

    public void show(View anchor,
                     View contentView,
                     Size popoverSize,
                     HorizontalMargin popoverMargin,
                     ScrollOrientation orientation) {
        if (isShowing()) {
            return;
        }

        if (mWindowSize == null) {
            mWindowSize = Utils.getWindowSize(mContext);
        }
        if (mStatusBarHeight == 0) {
            mStatusBarHeight = Utils.getStatusBarHeight(mContext);
        }
        if (mArrowSize == 0) {
            mArrowSize = mContext.getResources().getDimensionPixelSize(R.dimen.popover_arrow_size);
        }

        View view;
        if (orientation == ScrollOrientation.HORIZONTAL) {
            view = LayoutInflater.from(mContext).inflate(R.layout.popover_horizontal_layout, null);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.popover_vertical_layout, null);
        }

        FrameLayout rootView = view.findViewById(R.id.rootView);
        ViewGroup.LayoutParams rootViewParams = rootView.getLayoutParams();
        rootViewParams.width = mWindowSize.width;
        rootViewParams.height = mWindowSize.height;
        rootView.setLayoutParams(rootViewParams);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        int arrowTop;
        int popoverTop;
        ArrowView.ArrowOrientation arrowOrientation;
        int[] anchorLocation = new int[2];
        anchor.getLocationInWindow(anchorLocation);
        int anchorTop = anchorLocation[1] - mStatusBarHeight;
        int maxPopoverTop = anchorTop + anchor.getHeight() + popoverSize.height + mArrowSize;
        if (maxPopoverTop > mWindowSize.height) {
            arrowTop = anchorTop - mArrowSize;
            popoverTop = arrowTop - popoverSize.height;
            arrowOrientation = ArrowView.ArrowOrientation.DOWN;
        } else {
            arrowTop  = anchorTop + anchor.getHeight();
            popoverTop = arrowTop + mArrowSize;
            arrowOrientation = ArrowView.ArrowOrientation.UP;
        }

        int popoverLeft;
        int popoverWidth = popoverSize.width;
        if (popoverWidth > mWindowSize.width) {
            popoverLeft = 0;
            popoverWidth = mWindowSize.width;
        } else {
            int maxPopoverLeft = mWindowSize.width - popoverWidth - popoverMargin.right;
            popoverLeft = anchorLocation[0] + (anchor.getWidth() - popoverWidth) / 2;
            if (popoverLeft < popoverMargin.left) {
                popoverLeft = popoverMargin.left;
            } else if (popoverLeft > maxPopoverLeft) {
                popoverLeft = maxPopoverLeft;
            }
        }

        int maxArrowLeft = popoverLeft + popoverSize.width - mArrowSize;
        int arrowLeft = anchorLocation[0] + (anchor.getWidth() - mArrowSize) / 2;
        if (arrowLeft < popoverLeft) {
            arrowLeft = popoverLeft;
        } else if (arrowLeft > maxArrowLeft) {
            arrowLeft = maxArrowLeft;
        }

        ArrowView arrowView = view.findViewById(R.id.arrowView);
        arrowView.reload(Utils.getViewBackgroundColor(contentView), arrowOrientation);
        FrameLayout.LayoutParams arrowViewParams = (FrameLayout.LayoutParams) arrowView.getLayoutParams();
        arrowViewParams.topMargin = arrowTop;
        arrowViewParams.leftMargin = arrowLeft;
        arrowView.setLayoutParams(arrowViewParams);

        View scrollView = view.findViewById(R.id.scrollView);
        FrameLayout.LayoutParams scrollViewParams = (FrameLayout.LayoutParams) scrollView.getLayoutParams();
        scrollViewParams.height = popoverSize.height;
        scrollViewParams.width = popoverWidth;
        scrollViewParams.topMargin = popoverTop;
        scrollViewParams.leftMargin = popoverLeft;
        scrollView.setLayoutParams(scrollViewParams);

        LinearLayout container = view.findViewById(R.id.container);
        ViewGroup.LayoutParams contentViewParams = contentView.getLayoutParams();
        if (contentViewParams == null) {
            contentViewParams = new LinearLayout.LayoutParams(popoverWidth, popoverSize.height);
        } else if (orientation == ScrollOrientation.HORIZONTAL) {
            contentViewParams.height = popoverSize.height;
        } else if (orientation == ScrollOrientation.VERTICAL) {
            contentViewParams.width = popoverWidth;
        }
        container.addView(contentView, contentViewParams);
        mAlertDialog.setView(view);
        Window window = mAlertDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP | Gravity.START);
        }
        mAlertDialog.show();
    }

    public void dismiss() {
        if (isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return mAlertDialog.isShowing();
    }
}
