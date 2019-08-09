package com.example.xyzreader.ui.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.example.xyzreader.R;

/**
 * Class describes and executes coordinator layout behaviors for a subtitle text view,
 * to be used in conjunction with collapsing toolbar layout.
 * References:
 * http://saulmm.github.io/mastering-coordinator
 *
 */
public class CollapsingSubtitleBehavior extends CoordinatorLayout.Behavior<TextView> {

    private static final String LOG_TAG = CollapsingSubtitleBehavior.class.getSimpleName();

    private Context mContext;

    private float startPaddingLeft;
    private float startMarginRight;
    private float finalMarginTop;
    private float finalPaddingLeft;
    private float startPaddingBottom;
    private float mChangeBehaviorPoint;

    public CollapsingSubtitleBehavior(Context context, AttributeSet attrs) {
        mContext = context;

        // If AttributeSet is null, get attributes directly.
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsingSubtitleBehavior);
            startPaddingLeft = a.getDimension(R.styleable.CollapsingSubtitleBehavior_startLeftPadding, 0);
            finalPaddingLeft = a.getDimension(R.styleable.CollapsingSubtitleBehavior_finalLeftPadding, 0);
            startMarginRight = a.getDimension(R.styleable.CollapsingSubtitleBehavior_startRightMargin, 0);
            startPaddingBottom = a.getDimension(R.styleable.CollapsingSubtitleBehavior_startBottomPadding, 0);
            finalMarginTop = a.getDimension(R.styleable.CollapsingSubtitleBehavior_finalMarginTop, 0);
            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        maybeInitProperties(child, dependency);

        int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
        float percentage = Math.abs(dependency.getY()) / (float) maxScroll;

        float childPosition = dependency.getHeight()
                + dependency.getY()
                - child.getHeight()
                - ((getToolbarHeight() - finalMarginTop - child.getHeight()) * percentage / 2);

        Log.i(LOG_TAG, "Dependency Height :" + dependency.getHeight()
                + "+ Dependency Y:" + dependency.getY()
                + "- Child Height: " + child.getHeight()
                + "- Final Pos : " + ((getToolbarHeight() - child.getHeight()) * percentage / 2) + 16) ;

        childPosition = childPosition - startPaddingBottom * (1f - percentage);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

        lp.leftMargin = (int) ((percentage * finalPaddingLeft) + startPaddingLeft);
        lp.rightMargin = (int) startMarginRight;

        child.setLayoutParams(lp);

        child.setY(childPosition);
        return true;
    }

    private void maybeInitProperties(TextView child, View dependency) {
        if (startPaddingLeft == 0)
            startPaddingLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.default_padding);

        if (finalPaddingLeft == 0)
            finalPaddingLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.collapsing_tv_final_padding);
    }

    private int getToolbarHeight() {
        TypedValue toolbarHeightTypedValue = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize,
                toolbarHeightTypedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(toolbarHeightTypedValue.data,
                    mContext.getResources().getDisplayMetrics());
        } return 0;
    }
}
