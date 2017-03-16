package com.udacity.stockhawk.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

public class CircularRevealAnimation {

    public static final long DURATION = 300;

    private AnimListener animListener;

    public interface AnimListener {
        void onShowEnd ();
        void onDismissEnd ();
    }

    private void createReveal (final boolean isShow, final View animView) {

        int[] vPosition = new int[2];
        animView.getLocationInWindow(vPosition);

        int cx = vPosition[0] + animView.getWidth() / 2;
        int cy = vPosition[1] + animView.getHeight() / 2;

        int startRadius = 0;
        int finalRadius = Math.max(animView.getWidth(), animView.getHeight());

        if (!isShow) {
            startRadius = finalRadius;
            finalRadius = 0;
        }

        Animator animation = ViewAnimationUtils.createCircularReveal(animView, cx, cy, startRadius, finalRadius);
        animation.setDuration(DURATION);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isShow) {
                    animView.setVisibility(View.VISIBLE);
                    if (animListener != null) animListener.onShowEnd();
                } else {
                    animView.setVisibility(View.GONE);
                    if (animListener != null) animListener.onDismissEnd();
                }
            }
        });

        animation.start();
    }

    public void show (View animView) {
        createReveal(true, animView);
    }

    public void dismiss (View animView) {
        createReveal(false, animView);
    }

    public void setAnimListener (AnimListener listener) {
        animListener = listener;
    }
}
