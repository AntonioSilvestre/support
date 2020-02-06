package it.agevoluzione.tools.android.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import it.agevoluzione.tools.android.R;


public final class HorizontalDivider extends View {

    int color;
    float alpha;

    public HorizontalDivider(Context context) {
        super(context);
    }

    public HorizontalDivider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HorizontalDivider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalDivider(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs){
        if (initResource(attrs)) {
            setBackgroundColor(color);
            setAlpha(alpha);
        }
    }

    private boolean initResource(@Nullable AttributeSet attrs){
        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalDivider);
            int type = a.getResourceId(R.styleable.HorizontalDivider_color_theme, -1);
            a.recycle();

            switch (type) {
                case 1:
                    color = 0x000000;
                    alpha = 0.12f;
                    return true;
                case 2:
                    color = 0xffffff;
                    alpha = 0.20f;
                    return true;
            }

        }
        return false;
    }



}
