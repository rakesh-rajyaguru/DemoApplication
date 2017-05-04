/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 daimajia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.rakesh.demoapplication.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.List;

public abstract class BaseViewAnimator {

    private int mRepeat;
    private boolean mRest;

    protected View mTarget;
    protected AnimatorSet mAnimatorSet = new AnimatorSet();

    protected abstract void prepare();

    /**
     * start to animate
     */
    public void start() {
        reset();
        prepare();
        if (mRepeat != 0) {
            for (Animator animator : mAnimatorSet.getChildAnimations()) {
                ((ValueAnimator) animator).setRepeatCount(mRepeat > 0 ? mRepeat - 1 : mRepeat);
                //((ValueAnimator) animator).setRepeatMode(ValueAnimator.REVERSE);
            }
        }
        if (mRest) {
            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    reset();
                }
            });
        }
        mAnimatorSet.start();
    }

    /**
     * reset the view to default status
     */
    public void reset() {
        mTarget.setAlpha(1);
        mTarget.setScaleX(1);
        mTarget.setScaleY(1);
        mTarget.setTranslationX(0);
        mTarget.setTranslationY(0);
        mTarget.setRotation(0);
        mTarget.setRotationX(0);
        mTarget.setRotationY(0);
        mTarget.setPivotX(mTarget.getMeasuredWidth() / 2.0f);
        mTarget.setPivotY(mTarget.getMeasuredHeight() / 2.0f);
    }

    public void cancel() {
        mAnimatorSet.cancel();
    }

    public boolean isRunning() {
        return mAnimatorSet.isRunning();
    }

    public boolean isStarted() {
        return mAnimatorSet.isStarted();
    }

    public BaseViewAnimator addAnimatorListener(Animator.AnimatorListener l) {
        mAnimatorSet.addListener(l);
        return this;
    }

    public BaseViewAnimator addAllListeners(List<Animator.AnimatorListener> ls) {
        for (Animator.AnimatorListener l : ls) {
            mAnimatorSet.addListener(l);
        }
        return this;
    }

    public void removeAnimatorListener(Animator.AnimatorListener l) {
        mAnimatorSet.removeListener(l);
    }

    public void removeAllListener() {
        mAnimatorSet.removeAllListeners();
    }

    public BaseViewAnimator setTarget(View target) {
        mTarget = target;
        mAnimatorSet.setTarget(target);
        return this;
    }

    public BaseViewAnimator setInterpolator(Interpolator interpolator) {
        mAnimatorSet.setInterpolator(interpolator);
        return this;
    }

    public BaseViewAnimator setDuration(long duration) {
        mAnimatorSet.setDuration(duration);
        return this;
    }

    public BaseViewAnimator setStartDelay(long delay) {
        mAnimatorSet.setStartDelay(delay);
        return this;
    }

    public BaseViewAnimator setRest(boolean rest) {
        mRest = rest;
        return this;
    }

    public BaseViewAnimator setRepeat(int repeat) {
        mRepeat = repeat;
        return this;
    }

}
