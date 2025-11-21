//start - license
/*
 * Copyright (c) 2025 Ashera Cordova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
//end - license
package androidx.constraintlayout.motion.widget;

import r.android.content.Context;
//import r.android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.constraintlayout.widget.R;

import r.android.graphics.Canvas;
import r.android.util.AttributeSet;
import r.android.view.View;
import r.android.view.ViewGroup;

import java.util.HashMap;

/**
 * @hide
 */
public class MotionHelper extends ConstraintHelper implements MotionHelperInterface {

    private boolean mUseOnShow = false;
    private boolean mUseOnHide = false;
    private float mProgress;
    protected View[] views;

    public boolean isUsedOnShow() {
        return mUseOnShow;
    }

   /**
     *
     * @return
     * @hide
     */
    @Override
    public boolean isUseOnHide() {
        return mUseOnHide;
    }

    @Override
    public float getProgress() {
        return mProgress;
    }

    @Override
    public void setProgress(float progress) {
        mProgress = progress;
        if (this.mCount > 0) {
            this.views = this.getViews((ConstraintLayout)this.getParent());

            for(int i = 0; i < this.mCount; ++i) {
                View view = this.views[i];
                this.setProgress(view, progress);
            }
        } else {
            ViewGroup group = (ViewGroup)this.getParent();
            int count = group.getChildCount();

            for(int i = 0; i < count; ++i) {
                View view = group.getChildAt(i);
                if (view instanceof MotionHelper) {
                    continue;
                }
                this.setProgress(view, progress);
            }
        }
    }

   /**
     *
     * @param view
     * @param progress
     * @hide
     */
    public void setProgress(View view, float progress) {

    }

    @Override
    public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
    }

    @Override
    public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
     }

    @Override
    public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
    }

    @Override
    public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

    }

    @Override
    public boolean isDecorator() {
        return false;
    }

    @Override
    public void onPreDraw(Canvas canvas) {

    }
    @Override
    public void onFinishedMotionScene(MotionLayout motionLayout) {

    }

    @Override
    public void onPostDraw(Canvas canvas) {

    }

    @Override
    public void onPreSetup(MotionLayout motionLayout, HashMap<View, MotionController> controllerMap) {

    }

}
