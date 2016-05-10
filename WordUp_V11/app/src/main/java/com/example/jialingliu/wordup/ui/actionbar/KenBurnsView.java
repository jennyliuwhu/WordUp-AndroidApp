package com.example.jialingliu.wordup.ui.actionbar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jialingliu.wordup.R;

import java.util.Random;

/**
 * Created by jialingliu on 4/14/16.
 */
public class KenBurnsView extends FrameLayout {

    private final Handler handler;
    private int[] resourceids;
    private ImageView[] imageviews;
    private int activeimage = -1;

    private final Random random = new Random();
    private int swaptime = 15000;
    private int fadeinouttime = 500;

    private float maxfactor = 1.5F;
    private float minfactor = 1.2F;

    private Runnable SwapRunnable = new Runnable() {
        @Override
        public void run() {
            swapImage();
            handler.postDelayed(SwapRunnable, swaptime - fadeinouttime * 2);
        }
    };

    public KenBurnsView(Context context) {
        this(context, null);
    }

    public KenBurnsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KenBurnsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handler = new Handler();
    }

    public void setResourceIds(int... resourceIds) {
        resourceids = resourceIds;
        fillImageViews();
    }

    private void swapImage() {
        if (activeimage == -1) {
            activeimage = 1;
            animate(imageviews[activeimage]);
            return;
        }

        int inactiveIndex = activeimage;
        activeimage = (1 + activeimage) % imageviews.length;

        final ImageView activeImageView = imageviews[activeimage];
        activeImageView.setAlpha(0.0f);
        ImageView inactiveImageView = imageviews[inactiveIndex];

        animate(activeImageView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(fadeinouttime);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(inactiveImageView, "alpha", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(activeImageView, "alpha", 0.0f, 1.0f)
        );
        animatorSet.start();
    }

    private void start(View view, long duration, float fromscale, float toscale, float fromtransX, float fromtransY, float totransX, float totransY) {
        view.setScaleX(fromscale);
        view.setScaleY(fromscale);
        view.setTranslationX(fromtransX);
        view.setTranslationY(fromtransY);
        ViewPropertyAnimator propertyAnimator = view.animate().translationX(totransX).translationY(totransY).scaleX(toscale).scaleY(toscale).setDuration(duration);
        propertyAnimator.start();
    }

    private float pickScale() {
        return this.minfactor + this.random.nextFloat() * (this.maxfactor - this.minfactor);
    }

    private float pickTranslation(int value, float ratio) {
        return value * (ratio - 1.0f) * (this.random.nextFloat() - 0.5f);
    }

    public void animate(View view) {
        float fromscale = pickScale();
        float toscale = pickScale();
        float fromtransX = pickTranslation(view.getWidth(), fromscale);
        float fromtransY = pickTranslation(view.getHeight(), fromscale);
        float totransX = pickTranslation(view.getWidth(), toscale);
        float totransY = pickTranslation(view.getHeight(), toscale);
        start(view, this.swaptime, fromscale, toscale, fromtransX, fromtransY, totransX, totransY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startKenBurnsAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(SwapRunnable);
    }

    private void startKenBurnsAnimation() {
        handler.post(SwapRunnable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.view_kenburns, this);

        imageviews = new ImageView[2];
        imageviews[0] = (ImageView) view.findViewById(R.id.image0);
        imageviews[1] = (ImageView) view.findViewById(R.id.image1);
    }

    private void fillImageViews() {
        for (int i = 0; i < imageviews.length; i++) {
            imageviews[i].setImageResource(resourceids[i]);
        }
    }
}
