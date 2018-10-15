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
    }

    public void show(View anchor, View contentView, Size popoverSize, Margin popoverMargin) {
        show(anchor, contentView, popoverSize, popoverMargin, ScrollOrientation.HORIZONTAL);
    }

    public void show(View anchor, View contentView, Size popoverSize, ScrollOrientation orientation) {
        show(anchor, contentView, popoverSize, new Margin(), orientation);
    }

    public void show(View anchor,
                     View contentView,
                     Size popoverSize,
                     Margin popoverMargin,
                     ScrollOrientation orientation) {
        if (isShowing()) {
            return;
        }

        if (mWindowSize == null) {
            mWindowSize = Utils.getWindowSize(mContext);
            mWindowSize.height -= Utils.getStatusBarHeight(mContext);
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

        int[] anchorLocation = new int[2];
        anchor.getLocationInWindow(anchorLocation);
        Layout anchorLayout = new Layout();
        anchorLayout.x = anchorLocation[0];
        anchorLayout.y = anchorLocation[1];
        anchorLayout.width = anchor.getWidth();
        anchorLayout.height = anchor.getHeight();

        ArrowView.ArrowOrientation arrowOrientation = parseArrowOrientation(
                anchorLayout,
                popoverSize,
                popoverMargin
        );
        popoverSize = parsePopoverSize(arrowOrientation, anchorLayout, popoverSize, popoverMargin);

        int arrowTop;
        int popoverTop;
        if (arrowOrientation == ArrowView.ArrowOrientation.DOWN) {
            arrowTop = anchorLayout.y - mStatusBarHeight - mArrowSize;
            popoverTop = arrowTop - popoverSize.height;
        } else {
            arrowTop = anchorLayout.y - mStatusBarHeight + anchorLayout.height;
            popoverTop = arrowTop + mArrowSize;
        }

        int maxPopoverLeft = mWindowSize.width - popoverSize.width - popoverMargin.right;
        int popoverLeft = anchorLayout.x + (anchorLayout.width - popoverSize.width) / 2;
        if (popoverLeft < popoverMargin.left) {
            popoverLeft = popoverMargin.left;
        } else if (popoverLeft > maxPopoverLeft) {
            popoverLeft = maxPopoverLeft;
        }
        int maxArrowLeft = popoverLeft + popoverSize.width - mArrowSize;
        int arrowLeft = anchorLayout.x + (anchorLayout.width - mArrowSize) / 2;
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
        scrollViewParams.width = popoverSize.width;
        scrollViewParams.topMargin = popoverTop;
        scrollViewParams.leftMargin = popoverLeft;
        scrollView.setLayoutParams(scrollViewParams);

        LinearLayout container = view.findViewById(R.id.container);
        ViewGroup.LayoutParams contentViewParams = contentView.getLayoutParams();
        if (contentViewParams == null) {
            contentViewParams = new LinearLayout.LayoutParams(popoverSize.width, popoverSize.height);
        } else if (orientation == ScrollOrientation.HORIZONTAL) {
            contentViewParams.height = popoverSize.height;
        } else if (orientation == ScrollOrientation.VERTICAL) {
            contentViewParams.width = popoverSize.width;
        }
        container.addView(contentView, contentViewParams);

        mAlertDialog = new AlertDialog.Builder(mContext, R.style.PopoverDialog).create();
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(false);
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
            mAlertDialog = null;
        }
    }

    public boolean isShowing() {
        return mAlertDialog != null && mAlertDialog.isShowing();
    }

    private ArrowView.ArrowOrientation parseArrowOrientation(Layout anchorLayout,
                                                             Size popoverSize,
                                                             Margin popoverMargin) {
        int maxPopoverTop = anchorLayout.y
                            - mStatusBarHeight
                            + anchorLayout.height
                            + mArrowSize
                            + popoverSize.height
                            + popoverMargin.bottom;

        if (maxPopoverTop > mWindowSize.height) {
            int maxWindowSpace = mWindowSize.height - mStatusBarHeight - anchorLayout.y - anchorLayout.height;
            return anchorLayout.y > maxWindowSpace ? ArrowView.ArrowOrientation.DOWN : ArrowView.ArrowOrientation.UP;

        }
        return ArrowView.ArrowOrientation.UP;
    }

    private Size parsePopoverSize(ArrowView.ArrowOrientation arrowOrientation,
                                  Layout anchorLayout,
                                  Size popoverSize,
                                  Margin popoverMargin) {
        int width = popoverSize.width;
        int height = popoverSize.height;
        int maxWidth = mWindowSize.width - popoverMargin.left - popoverMargin.right;
        if (width <= 0 || width > maxWidth) {
            width = maxWidth;
        }
        int maxHeight;
        if (arrowOrientation == ArrowView.ArrowOrientation.DOWN) {
            maxHeight = anchorLayout.y - popoverMargin.top - mArrowSize;
        } else {
            maxHeight = mWindowSize.height
                        - mStatusBarHeight
                        - anchorLayout.y
                        - anchorLayout.height
                        - mArrowSize
                        - popoverMargin.bottom;
        }
        if (height >= maxHeight) {
            height = maxHeight;
        }
        return new Size(width, height);
    }
}
