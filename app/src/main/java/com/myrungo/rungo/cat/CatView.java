package com.myrungo.rungo.cat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.myrungo.rungo.R;

public class CatView extends ConstraintLayout {
    private static final int DURATION = 250;
    private float headWidth;
    private float headHeight;
    private float handWidth;
    private float eyeWidth;
    private float eyeHeight;

    private ImageView head;
    private ImageView body;
    private ImageView handLeft;
    private ImageView handRight;
    private ImageView legLeft;
    private ImageView legRight;
    private ImageView tail;
    private ImageView eyeRight;
    private ImageView eyeLeft;

    private Heads currentHead;
    private Animation.AnimationListener animationListener = null;

    public CatView(Context context) {
        super(context);
        init();
    }

    public CatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAnimationListener(Animation.AnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    private void init() {
        inflate(getContext(), R.layout.view_cat, this);
        head = findViewById(R.id.cat_head_image_view);
        body = findViewById(R.id.cat_body_image_view);
        handLeft = findViewById(R.id.cat_hand_left_image_view);
        handRight = findViewById(R.id.cat_hand_right_image_view);
        legLeft = findViewById(R.id.cat_leg_left_image_view);
        legRight = findViewById(R.id.cat_leg_right_image_view);
        tail = findViewById(R.id.cat_tail_image_view);
        eyeLeft = findViewById(R.id.cat_eye_left_image_view);
        eyeRight = findViewById(R.id.cat_eye_right_image_view);

        Glide.with(this).load(R.drawable.tall_common).into(tail);
        Glide.with(this).load(R.drawable.eye_sad).into(eyeLeft);
        Glide.with(this).load(R.drawable.eye_sad).into(eyeRight);

        setSkin(Skins.COMMON);
        setHead(Heads.COMMON);

        head.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        headWidth = head.getWidth();
                        headHeight = head.getHeight();
                    }
                });

        handLeft.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        handWidth = handLeft.getWidth();
                    }
                });

        eyeLeft.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        eyeWidth = eyeLeft.getMeasuredWidth();
                        eyeHeight = eyeLeft.getMeasuredHeight();
                    }
                });
    }

    public enum Skins {
        COMMON,
        BAD,
        BUSINESS,
        KARATE,
        NORMAL
    }

    public void setSkin(Skins skin) {
        switch (skin) {
            case COMMON:
                setHead(Heads.COMMON);
                Glide.with(this).load(R.drawable.common_cat_body).into(body);
                Glide.with(this).load(R.drawable.common_cat_hand).into(handRight);
                Glide.with(this).load(R.drawable.common_cat_hand_left).into(handLeft);
                Glide.with(this).load(R.drawable.common_cat_leg).into(legRight);
                Glide.with(this).load(R.drawable.common_cat_leg).into(legLeft);
                break;

            case BAD:
                setHead(Heads.ANGRY);
                Glide.with(this).load(R.drawable.bad_cat_body).into(body);
                Glide.with(this).load(R.drawable.bad_cat_hand).into(handRight);
                Glide.with(this).load(R.drawable.bad_cat_hand_left).into(handLeft);
                Glide.with(this).load(R.drawable.bad_cat_leg).into(legRight);
                Glide.with(this).load(R.drawable.bad_cat_leg).into(legLeft);
                break;

            case BUSINESS:
                setHead(Heads.COMMON);
                Glide.with(this).load(R.drawable.bussiness_cat_body).into(body);
                Glide.with(this).load(R.drawable.bussiness_cat_hand).into(handRight);
                Glide.with(this).load(R.drawable.bussiness_cat_hand_left).into(handLeft);
                Glide.with(this).load(R.drawable.bussiness_cat_leg).into(legRight);
                Glide.with(this).load(R.drawable.bussiness_cat_leg).into(legLeft);
                break;

            case KARATE:
                setHead(Heads.COMMON);
                Glide.with(this).load(R.drawable.karate_cat_body).into(body);
                Glide.with(this).load(R.drawable.karate_cat_hand).into(handRight);
                Glide.with(this).load(R.drawable.karate_cat_hand_left).into(handLeft);
                Glide.with(this).load(R.drawable.karate_cat_leg).into(legRight);
                Glide.with(this).load(R.drawable.karate_cat_leg).into(legLeft);
                break;

            case NORMAL:
                setHead(Heads.COMMON);
                Glide.with(this).load(R.drawable.normal_cat_body).into(body);
                Glide.with(this).load(R.drawable.normal_cat_hand).into(handRight);
                Glide.with(this).load(R.drawable.normal_cat_hand_left).into(handLeft);
                Glide.with(this).load(R.drawable.normal_cat_leg).into(legRight);
                Glide.with(this).load(R.drawable.normal_cat_leg).into(legLeft);
                break;
        }
    }

    public enum Heads {
        COMMON,
        HAPPY_1,
        HAPPY_2,
        SAD,
        SHOCK,
        ANGRY
    }

    public Heads getCurrentHead() {
        return currentHead;
    }

    public void setHead(Heads head) {
        eyeLeft.setVisibility(View.GONE);
        eyeRight.setVisibility(View.GONE);
        currentHead = head;

        switch (head) {
            case COMMON:
                Glide.with(this).load(R.drawable.head_common_png).into(this.head);
                break;

            case HAPPY_1:
                Glide.with(this).load(R.drawable.head_happy_1).into(this.head);
                break;

            case HAPPY_2:
                Glide.with(this).load(R.drawable.head_happy_2).into(this.head);
                break;

            case SAD:
                Glide.with(this).load(R.drawable.head_sad_png).into(this.head);
                eyeLeft.setVisibility(View.VISIBLE);
                eyeRight.setVisibility(View.VISIBLE);
                break;

            case SHOCK:
                Glide.with(this).load(R.drawable.head_shock).into(this.head);
                break;

            case ANGRY:
                Glide.with(this).load(R.drawable.head_angry).into(this.head);
                break;
        }
    }

    public void stop() {
        if (head.getAnimation() != null) head.clearAnimation();
        if (legLeft.getAnimation() != null) legLeft.clearAnimation();
        if (legRight.getAnimation() != null) legRight.clearAnimation();
        if (handLeft.getAnimation() != null) handLeft.clearAnimation();
        if (handRight.getAnimation() != null) handRight.clearAnimation();
        if (eyeLeft.getAnimation() != null) eyeLeft.clearAnimation();
        if (eyeRight.getAnimation() != null) eyeRight.clearAnimation();
    }

    public void pause() {
        if (head.getAnimation() != null) head.getAnimation().cancel();
        if (legLeft.getAnimation() != null) legLeft.getAnimation().cancel();
        if (legRight.getAnimation() != null) legRight.getAnimation().cancel();
        if (handLeft.getAnimation() != null) handLeft.getAnimation().cancel();
        if (handRight.getAnimation() != null) handRight.getAnimation().cancel();
        if (eyeLeft.getAnimation() != null) eyeLeft.getAnimation().cancel();
        if (eyeRight.getAnimation() != null) eyeRight.getAnimation().cancel();
    }

    public void resume() {
        if (head.getAnimation() != null) head.getAnimation().start();
        if (legLeft.getAnimation() != null) legLeft.getAnimation().start();
        if (legRight.getAnimation() != null) legRight.getAnimation().start();
        if (handLeft.getAnimation() != null) handLeft.getAnimation().start();
        if (handRight.getAnimation() != null) handRight.getAnimation().start();
        if (eyeLeft.getAnimation() != null) eyeLeft.getAnimation().start();
        if (eyeRight.getAnimation() != null) eyeRight.getAnimation().start();
    }

    public void greet() {
        stop();

        Animation rotateStart = new RotateAnimation(0, -90, 0, 10);
        rotateStart.setDuration(700);

        final Animation rotate = new RotateAnimation(-90, -45, 0, 10);
        rotate.setDuration(700);
        rotate.setRepeatMode(ValueAnimator.REVERSE);
        rotate.setRepeatCount(3);

        final Animation rotateEnd = new RotateAnimation(-90, 0, 0, 10);
        rotateEnd.setDuration(700);

        rotateStart.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handRight.startAnimation(rotate);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        rotate.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handRight.startAnimation(rotateEnd);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        handRight.startAnimation(rotateStart);
    }

    public void run() {
        stop();

        Animation rotatingLeft = new RotateAnimation(0, 30, handWidth, 10);
        rotatingLeft.setDuration(DURATION);
        rotatingLeft.setRepeatMode(ValueAnimator.REVERSE);
        rotatingLeft.setRepeatCount(Animation.INFINITE);
        rotatingLeft.setFillAfter(true);

        Animation rotatingRight = new RotateAnimation(-30, 0, 0, 10);
        rotatingRight.setDuration(DURATION);
        rotatingRight.setRepeatMode(ValueAnimator.REVERSE);
        rotatingRight.setRepeatCount(Animation.INFINITE);
        rotatingRight.setFillAfter(true);

        Animation stepLeft = new TranslateAnimation(0, 0, -10, 20);
        stepLeft.setDuration(DURATION);
        stepLeft.setRepeatMode(ValueAnimator.REVERSE);
        stepLeft.setRepeatCount(Animation.INFINITE);
        stepLeft.setFillAfter(true);

        Animation stepRight = new TranslateAnimation(0, 0, 20, -10);
        stepRight.setDuration(DURATION);
        stepRight.setRepeatMode(ValueAnimator.REVERSE);
        stepRight.setRepeatCount(Animation.INFINITE);
        stepRight.setFillAfter(true);

        handLeft.startAnimation(rotatingLeft);
        handRight.startAnimation(rotatingRight);
        legLeft.startAnimation(stepLeft);
        legRight.startAnimation(stepRight);
    }

    public void cry() {
        stop();

        Animation cryAnimation = new RotateAnimation(0, -30, eyeLeft.getWidth()/2, eyeLeft.getHeight()/2);
        cryAnimation.setDuration(100);
        cryAnimation.setRepeatMode(ValueAnimator.REVERSE);
        cryAnimation.setRepeatCount(Animation.INFINITE);
        cryAnimation.setFillAfter(true);

        eyeLeft.startAnimation(cryAnimation);
        eyeRight.startAnimation(cryAnimation);

        Animation headAnimation = new TranslateAnimation(0, 0, 0, 10);
        headAnimation.setDuration(600);
        headAnimation.setRepeatMode(ValueAnimator.REVERSE);
        headAnimation.setRepeatCount(Animation.INFINITE);
        headAnimation.setFillAfter(true);

        Animation handAnimation = new TranslateAnimation(0, 0, 0, -10);
        handAnimation.setDuration(600);
        handAnimation.setRepeatMode(ValueAnimator.REVERSE);
        handAnimation.setRepeatCount(Animation.INFINITE);
        handAnimation.setFillAfter(true);

        head.startAnimation(headAnimation);
        handRight.startAnimation(handAnimation);
        handLeft.startAnimation(handAnimation);
    }

    public void slap() {
        stop();

        Heads curhead = currentHead;

        Animation headAnimation = new RotateAnimation(0, 10, headWidth/2, headHeight/2);
        headAnimation.setDuration(500);
        headAnimation.setRepeatMode(ValueAnimator.REVERSE);
        headAnimation.setRepeatCount(1);
        headAnimation.setFillAfter(true);

        Animation handLeftAnimation = new RotateAnimation(0, 60, handWidth, -10);
        handLeftAnimation.setDuration(400);
        handLeftAnimation.setRepeatMode(ValueAnimator.REVERSE);
        handLeftAnimation.setRepeatCount(1);
        handLeftAnimation.setFillAfter(true);

        Animation handRightAnimation = new RotateAnimation(0, -60, 0, -10);
        handRightAnimation.setDuration(400);
        handRightAnimation.setRepeatMode(ValueAnimator.REVERSE);
        handRightAnimation.setRepeatCount(1);
        handRightAnimation.setFillAfter(true);

        headAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (animationListener != null) animationListener.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animationListener != null) animationListener.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        head.startAnimation(headAnimation);
        handLeft.startAnimation(handLeftAnimation);
        handRight.startAnimation(handRightAnimation);
    }
}