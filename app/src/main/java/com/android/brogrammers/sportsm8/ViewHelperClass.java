package com.android.brogrammers.sportsm8;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Korbi on 07.06.2017.
 */

public class ViewHelperClass {

    public static void setInvisible(View[] view) {
        for (int i = 0; i < view.length; i++) {
            view[i].setVisibility(View.GONE);
        }
    }


    public static void setVisible(View[] view) {
        for (int i = 0; i < view.length; i++) {
            view[i].setVisibility(View.VISIBLE);
        }
    }

    public static void expand(final View v, int duration) {
        final boolean expand = v.getVisibility() != View.VISIBLE;

        int prevHeight = v.getHeight();
        int height = 0;
        if (expand) {
            int measureSpecParams = View.MeasureSpec.getSize(View.MeasureSpec.UNSPECIFIED);
            v.measure(measureSpecParams, measureSpecParams);
            height = v.getMeasuredHeight();
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expand) {
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expand) {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}
