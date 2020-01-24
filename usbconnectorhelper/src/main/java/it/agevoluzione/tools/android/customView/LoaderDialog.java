package it.agevoluzione.tools.android.customView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.agevoluzione.tools.android.usbconnectorhelper.R;
import it.agevoluzione.tools.android.utils.AnimatorUtils;


public class LoaderDialog extends AlertDialog {

    public final static int TYPE_WAIT_FOR_SETUP = 1;
    public final static int TYPE_LOCK_CONTROLL = 2;
    public final static int TYPE_SERVICE_ERROR = 3;
    public final static int TYPE_LAMP_ERR = 4;

    private int type;
    private Animator animator;
    private TextView textUp;
    private ProgressBar progressUp;
    private ProgressBar progressDown;
    private TextView textDown;

    public LoaderDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_app_status);
        Window window = getWindow();
        if (null != window) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }

//    public void startAnimator(Animator animator) {
//        this.animator = animator;
//    }

    public void startAnimator() {
        if (null != this.animator) {
            animator.start();
        }
    }

    public void dismissNow() {
        if (null != animator && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        if (null != animator && animator.isRunning()) {
            animator.addListener(new AnimatorUtils.ShortenAnimatorListner() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoaderDialog.super.dismiss();
                    animator = null;
                }
            });
        } else {
            animator = null;
            super.dismiss();
        }
    }

    public void updateStatusDialog(String statusName, int progress) {
        if (canOperate()){
            if (type == TYPE_WAIT_FOR_SETUP) {
                int tmpProg = progressDown.getMax();
                if (tmpProg == progress) {
                    startAnimator();
                }
                progressDown.setProgress(progress);
                textDown.setText(statusName);
            }else if (type == TYPE_SERVICE_ERROR) {
                int tmpProg = progressDown.getMax();
                if (tmpProg == progress) {
                    startAnimator();
                }
                progressDown.setProgress(progress);
                textDown.setText(statusName);
            }
        }
    }

    private boolean bindView() {
        if (null == textUp) {
            textUp = findViewById(R.id.status_of_app_text_up);
        }
        if (null == progressUp) {
            progressUp = findViewById(R.id.status_of_app_progress_up);
        }
        if (null == progressDown) {
            progressDown = findViewById(R.id.status_of_app_progress_down);
        }
        if (null == textDown) {
            textDown = findViewById(R.id.status_of_app_text_down);
        }
        return null != textUp && null != progressUp && null != progressDown && null != textDown;
    }

    private boolean canOperate() {
        return isShowing() && bindView();
    }

//    public void showWaiter(Source source) {
//        showDialog(TYPE_WAIT_FOR_SETUP, source.name());
//    }

    public void dismissWaiters() {
        dismissDialog(TYPE_WAIT_FOR_SETUP);
    }

    public void showLocker(String message) {
        showDialog(TYPE_LOCK_CONTROLL, message);
    }
    public void dismissLocker() {
        dismissDialog(TYPE_LOCK_CONTROLL);
    }

//    public void showServiceErr(Errors error) {
//        showDialog(TYPE_SERVICE_ERROR, error.name());
//    }

    public void dismissServiceErr() {
        dismissDialog(TYPE_SERVICE_ERROR);
    }

//    public void showLampErr(Errors error) {
//        showDialog(TYPE_LAMP_ERR, error.name());
//    }

    private void showDialog(int type, String message) {
        if (type != this.type) {
            if (isShowing()) {
                dismissNow();
            }
        }
        if (!isShowing()) {
            show();
        }
        prepare(type, message);
    }

    private void dismissDialog(int type) {
        if (this.type == type) {
            dismissNow();
        }
    }

    public void dismissLampErr() {
        dismissDialog(TYPE_LAMP_ERR);
    }

    private synchronized void prepare(int type, String message) {
        if (canOperate() && this.type != type) {
            this.type = type;
            switch (type) {
                case TYPE_SERVICE_ERROR:
                case TYPE_WAIT_FOR_SETUP:
                    textUp.setText("");
                    textUp.setAlpha(0);
                    textUp.setBackgroundResource(R.drawable.ic_done_accent);
                    textUp.setVisibility(View.GONE);

                    progressUp.setVisibility(View.VISIBLE);
                    progressUp.setIndeterminate(true);

                    progressDown.setVisibility(View.VISIBLE);
//                    progressDown.setMax(Source.values().length - 1);

                    textDown.setVisibility(View.VISIBLE);
                    textDown.setText("");

                    Animator fadeOutProgressUp = AnimatorUtils.fadeOut(progressUp);
                    fadeOutProgressUp.setDuration(200);
                    fadeOutProgressUp.setInterpolator(new AccelerateInterpolator());
                    Animator zoomOutProgressUp = AnimatorUtils.scale(progressUp, 1f, 0);
                    zoomOutProgressUp.setDuration(200);
                    zoomOutProgressUp.setInterpolator(new AccelerateInterpolator());

                    Animator fadeInOk = AnimatorUtils.fadeIn(textUp);
                    fadeInOk.setDuration(200);
                    fadeInOk.setInterpolator(new DecelerateInterpolator());
                    Animator zoomOutOk = AnimatorUtils.scale(textUp, 0, 1);
                    zoomOutOk.setDuration(300);
                    zoomOutOk.setInterpolator(new AnticipateOvershootInterpolator());

                    AnimatorSet animatioSet = new AnimatorSet();
//            animatioSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    animatioSet.play(fadeOutProgressUp).with(zoomOutProgressUp);
                    animatioSet.play(zoomOutProgressUp).before(fadeInOk);
                    animatioSet.play(fadeInOk).with(zoomOutOk);

                    setAnimator(animatioSet);
                    setCancelable(true);
                    break;
                case TYPE_LOCK_CONTROLL:
                    animator = null;
                    textUp.setVisibility(View.GONE);

                    setCancelable(false);

                    progressUp.setAlpha(1);
                    progressUp.setScaleX(1);
                    progressUp.setScaleY(1);
                    progressUp.setVisibility(View.VISIBLE);
                    progressUp.setIndeterminate(true);

                    progressDown.setVisibility(View.GONE);

                    textDown.setVisibility(View.VISIBLE);
                    textDown.setBackgroundResource(android.R.color.transparent);
                    textDown.setText(message);
                    setCancelable(false);
                    break;
            }
        }

    }


}
