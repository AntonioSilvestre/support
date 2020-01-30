package it.agevoluzione.tools.android.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

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

    public static Animator changeTextColor(final TextView view, final int endColor) {

        int initColor = view.getTextColors().getDefaultColor();
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), initColor, endColor);
        ValueAnimator.AnimatorUpdateListener list = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                view.setTextColor(val);
            }
        };

        anim.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationCancel(Animator animation) {
                view.setTextColor(endColor);
            }
        });
        return  anim;
    }

    public static Animator changeBackgroudColor(final View view, final int endColor) {


        Drawable drawable = view.getBackground();
        int begin;
        if (drawable == null) {
            begin = Color.WHITE;
        } else if (drawable instanceof ColorDrawable){
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            begin = colorDrawable.getColor();
        } else {
            begin = Color.WHITE;
        }
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), begin, endColor);
        ValueAnimator.AnimatorUpdateListener list = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                view.setBackgroundColor(val);
            }
        };
        anim.addUpdateListener(list);

        anim.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationCancel(Animator animation) {
                view.setBackgroundColor(endColor);
            }
        });
        return  anim;
    }

}
