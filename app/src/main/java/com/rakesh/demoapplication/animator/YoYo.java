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
import android.view.View;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

public class YoYo {

    public static Builder with(Techniques technique) {
        return new Builder(technique);
    }

    public static Builder with(BaseViewAnimator animator) {
        return new Builder(animator);
    }

    public static class Builder {

        private static final int DEFAULT_REPEAT = 1;
        private static final long DEFAULT_DELAY = 0;
        private static final boolean DEFAULT_RESET = false;
        private static final long DEFAULT_DURATION = 1000;

        private View target;
        private long delay = DEFAULT_DELAY;
        private long duration = DEFAULT_DURATION;
        private int repeat = DEFAULT_REPEAT;
        private boolean reset = DEFAULT_RESET;
        private BaseViewAnimator animator;
        private Interpolator interpolator;
        private List<Animator.AnimatorListener> listeners = new ArrayList<Animator.AnimatorListener>();
        private Builder(Techniques technique) {
            this.animator = technique.getAnimator();
        }

        private Builder(BaseViewAnimator animator) {
            this.animator = animator;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder interpolate(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder listen(AnimatorListenerAdapter listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder reset(boolean reset) {
            this.reset = reset;
            return this;
        }

        public Builder repeat(int count) {
            this.repeat = count;
            return this;
        }
        public YoYoString playOn(View target) {
            this.target = target;
            start();
            return new YoYoString(this.animator);
        }

        private void start() {
            animator.setTarget(target).setDuration(duration).setInterpolator(interpolator)
                    .setStartDelay(delay).setRest(reset).setRepeat(repeat);

            if (listeners.size() > 0) {
                animator.addAllListeners(listeners);
            }

            animator.start();
        }
    }

    public static class YoYoString {

        private BaseViewAnimator animator;

        private YoYoString(BaseViewAnimator animator) {
            this.animator = animator;
        }

        public boolean isStarted() {
            return animator.isStarted();
        }

        public boolean isRunning() {
            return animator.isRunning();
        }

        public void stop(boolean reset) {
            animator.cancel();

            if (reset) animator.reset();
        }
    }

}
