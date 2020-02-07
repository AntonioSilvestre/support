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

public final class AnimatorUtils {

    private AnimatorUtils() {}

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

        int initColor = view.getCurrentTextColor();
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), initColor, endColor);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                view.setTextColor(val);
            }
        });

        anim.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationCancel(Animator animation) {
                view.setTextColor(endColor);
            }
        });
        return  anim;
    }

//        final ColorStateList colorStateListBegin = view.getTextColors();
//        final int color = colorStateListBegin.getDefaultColor();
//////                int alpha = (color >> 24) & 0xff; // or color >>> 24
//////                int red = (color >> 16) & 0xff;
//////                int green = (color >>  8) & 0xff;
//////                int blue = (color      ) & 0xff;
//        final int alpha = Color.alpha(color); // or color >>> 24
//        final int red = Color.red(color);
//        final int green = Color.green(color);
//        final int blue = Color.blue(color);


    public static Animator changeText(final TextView view, final String newString) {

        final boolean haveStart = !(null == view.getText() || view.getText().toString().isEmpty());
        boolean haveEnd = !(null == newString || newString.isEmpty());
//        final int beginColor = haveStart ? view.getCurrentTextColor() : view.getTextColors().getDefaultColor();
        final int originalColor =  view.getCurrentTextColor();
        final int originalAlpha = Color.alpha(originalColor);
        final int alphaToReach = Math.round(originalAlpha / 7f);
        final String newText = haveEnd ? newString : "";

        ValueAnimator fadeOut = ValueAnimator.ofInt(originalAlpha, alphaToReach);
        fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                view.setTextColor(view.getTextColors().withAlpha(val));
            }
        });

        fadeOut.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                view.setText(newText);
                view.setTextColor(originalColor);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setText(newText);
                view.setTextColor(originalColor);
            }
        });


        ValueAnimator fadeIn = ValueAnimator.ofInt(alphaToReach, originalAlpha);
        fadeIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                view.setTextColor(view.getTextColors().withAlpha(val));
            }
        });

        fadeIn.addListener(new ShortenAnimatorListner() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setText(newText);
                view.setTextColor(view.getTextColors().withAlpha(alphaToReach));
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                view.setTextColor(originalColor);
                view.setText(newText);
            }
        });

        AnimatorSet anim = new AnimatorSet();
        anim.setDuration(300);

        if (haveStart) {
            if (haveEnd) {
                anim.play(fadeOut).before(fadeIn);
            } else {
                anim.play(fadeOut);
            }
        } else {
            if (haveEnd) {
                anim.play(fadeIn);
            }
        }

        return anim;
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


    /*
      public static Animator changeText(final TextView view, final String newString) {
        boolean haveText = null == view.getText() || view.getText().toString().isEmpty();

        final ColorStateList colorStateListBegin = view.getTextColors();
//        int originalColor = colorStateListBegin.getDefaultColor();
        int originalColor = view.getCurrentTextColor();
        int alpha = Color.alpha(originalColor);
        int red = Color.red(originalColor);
        int green = Color.green(originalColor);
        int blue = Color.blue(originalColor);
        final int fadedColor = Color.argb(haveText ? Math.round(alpha / 2f) : 0,red,green,blue);


        ValueAnimator animfadedColor = ValueAnimator.ofInt(alpha,0);
        animfadedColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer)animation.getAnimatedValue();
                view.setTextColor(view.getTextColors().withAlpha(val));
            }
        });
     */
}
