package it.agevoluzione.tools.android.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class AnimatorUtils {

    public static abstract class ShortenAnimatorListner implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {}
        @Override
        public void onAnimationEnd(Animator animation) {}
        @Override
        public void onAnimationCancel(Animator animation) {}
        @Override
        public void onAnimationRepeat(Animator animation) {}
    }

    public static Animator fadeOut(final View view) {
        Animator anim = ObjectAnimator.ofFloat(view,"alpha",1,0);
        anim.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setAlpha(1);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(0);
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setAlpha(0);
                view.setVisibility(View.INVISIBLE);
            }

        });
        return  anim;
    }

    public static Animator fadeIn(final View view) {
        Animator anim = ObjectAnimator.ofFloat(view,"alpha",0,1);
        anim.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setAlpha(0);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setAlpha(1);
            }

        });
        return  anim;
    }

    public static Animator scale(final View view, final float from, final float to) {
        ValueAnimator anim = ValueAnimator.ofFloat(from, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                view.setScaleY(val);
                view.setScaleX(val);
            }
        });
        anim.addListener(new ShortenAnimatorListner() {

            @Override
            public void onAnimationStart(Animator animation) {
                view.setScaleY(from);
                view.setScaleX(from);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setScaleY(to);
                view.setScaleX(to);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setScaleY(to);
                view.setScaleX(to);
            }

        });
        return  anim;
    }

}
