package com.example.jialingliu.wordup.entities;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

/**
 * Created by jialingliu on 4/27/16.
 */
public class PullScrollView extends ScrollView {
    private static final float SCROLL_RATIO = 0.33f;
    private static final int TURN_DISTANCE = 50;

    private ImageView mHeadImage;
    private int mHeadImageH;
    private View mChildView;

    private Rect mRect = new Rect();
    private float mTouchDownY;

    private boolean mEnableTouch = false;
    private boolean isMoving = false;

    private int mInitTop, mInitBottom;
    private int mCurrentTop, mCurrentBottom;

    private OnTurnListener mOnTurnListener;

    private enum State {
        UP, DOWN, NORMAL
    }

    public static int mWidth;
    private boolean aBoolean = false;

    /**
     * 状态.
     */
    private State state = State.NORMAL;

    public PullScrollView(Context context) {
        super(context);
    }

    public PullScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(ImageView imageView) {
        mHeadImage = imageView;
        mHeadImageH = 94;
    }

    public void setOnTurnListener(OnTurnListener turnListener) {
        mOnTurnListener = turnListener;
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            mChildView = getChildAt(0);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (getScrollY() == 0) {
            state = State.NORMAL;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mChildView != null) {
            doTouchEvent(ev);
        }

        if (mEnableTouch) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    private void doTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownY = event.getY();
                mCurrentTop = mInitTop = mHeadImage.getTop();
                mCurrentBottom = mInitBottom = mHeadImage.getBottom();
                if (event.getY() < mWidth) {
                    aBoolean = true;
                } else {
                    aBoolean = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = event.getY() - mTouchDownY;
                if (aBoolean) {
                    doActionMove(deltaY);
                } else {
                    doActionMove(0);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    rollBackAnimation();
                }

                if (getScrollY() == 0) {
                    state = State.NORMAL;
                }

                isMoving = false;
                mEnableTouch = false;
                aBoolean = false;
                break;

            default:
                break;
        }
    }

    private void doActionMove(float deltaY) {
        if (deltaY < 0 && state == State.NORMAL) {
            state = State.UP;
        } else if (deltaY > 0 && state == State.NORMAL) {
            state = State.DOWN;
        }

        if (state == State.UP) {
            deltaY = deltaY < 0 ? deltaY : 0;

            isMoving = false;
            mEnableTouch = false;

        } else if (state == State.DOWN) {
            if (getScrollY() <= deltaY) {
                mEnableTouch = true;
                isMoving = true;
            }
            deltaY = deltaY < 0 ? 0 : deltaY;
        }

        if (isMoving) {
            if (mRect.isEmpty()) {
                mRect.set(
                        mChildView.getLeft(), mChildView.getTop(), mChildView.getRight(),
                        mChildView.getBottom()
                );
            }

            float bgMoveH = deltaY * 0.5f * SCROLL_RATIO;
            mCurrentTop = (int) (mInitTop + bgMoveH);
            mCurrentBottom = (int) (mInitBottom + bgMoveH);
            mHeadImage.layout(
                    mHeadImage.getLeft(), mCurrentTop, mHeadImage.getRight(),
                    mCurrentBottom
            );

            float childMoveH = deltaY * SCROLL_RATIO;

            int top = mCurrentBottom - mHeadImageH;
            if (mRect.top + childMoveH > top) {
                childMoveH -= mRect.top + childMoveH - top;
            }

            mChildView.layout(
                    mRect.left, (int) (mRect.top + childMoveH),
                    mRect.right, (int) (mRect.bottom + childMoveH)
            );
        }
    }

    private void rollBackAnimation() {
        TranslateAnimation image_Anim = new TranslateAnimation(
                0, 0,
                Math.abs(mInitTop - mCurrentTop), 0
        );
        image_Anim.setDuration(200);
        mHeadImage.startAnimation(image_Anim);

        mHeadImage.layout(mHeadImage.getLeft(), mInitTop, mHeadImage.getRight(), mInitBottom);

        TranslateAnimation inner_Anim = new TranslateAnimation(0, 0, mChildView.getTop(), mRect.top);
        inner_Anim.setDuration(200);
        mChildView.startAnimation(inner_Anim);
        mChildView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);

        mRect.setEmpty();

        if (mCurrentTop > mInitTop + TURN_DISTANCE && mOnTurnListener != null) {
            mOnTurnListener.onTurn();
        }
    }

    private boolean isNeedAnimation() {
        return !mRect.isEmpty() && isMoving;
    }

    public interface OnTurnListener {
        public void onTurn();
    }
}
