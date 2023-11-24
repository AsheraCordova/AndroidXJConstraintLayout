/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.constraintlayout.motion.widget;

import r.android.content.Context;
//import r.android.content.res.TypedArray;
import r.android.os.Build;

import androidx.constraintlayout.core.motion.utils.SplineSet;
import androidx.constraintlayout.motion.utils.ViewSpline;
import androidx.constraintlayout.widget.ConstraintAttribute;
//import androidx.constraintlayout.widget.R;

import r.android.util.AttributeSet;
import r.android.util.Log;
import r.android.util.SparseIntArray;
//import r.android.util.TypedValue;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Defines container for a key frame of for storing KeyAttributes.
 * KeyAttributes change post layout values of a view.
 *
 * @hide
 */

public class KeyAttributes extends Key {
    static final String NAME = "KeyAttribute";
    private static final String TAG = "KeyAttributes";
    private static final boolean DEBUG = false;
    private String mTransitionEasing;
    private int mCurveFit = -1;
    private boolean mVisibility = false;
    private float mAlpha = Float.NaN;
    private float mElevation = Float.NaN;
    private float mRotation = Float.NaN;
    private float mRotationX = Float.NaN;
    private float mRotationY = Float.NaN;
    private float mPivotX = Float.NaN;
    private float mPivotY = Float.NaN;
    private float mTransitionPathRotate = Float.NaN;
    private float mScaleX = Float.NaN;
    private float mScaleY = Float.NaN;
    private float mTranslationX = Float.NaN;
    private float mTranslationY = Float.NaN;
    private float mTranslationZ = Float.NaN;
    private float mProgress = Float.NaN;
    public static final int KEY_TYPE = 1;

    {
        mType = KEY_TYPE;
        mCustomConstraints = new HashMap<>();
    }

    public void load(Context context, AttributeSet attrs) {}

    /**
     * Gets the curve fit type this drives the interpolation
     *
     * @return
     */
    int getCurveFit() {
        return mCurveFit;
    }

    @Override
    public void getAttributeNames(HashSet<String> attributes) {

        if (!Float.isNaN(mAlpha)) {
            attributes.add(Key.ALPHA);
        }
        if (!Float.isNaN(mElevation)) {
            attributes.add(Key.ELEVATION);
        }
        if (!Float.isNaN(mRotation)) {
            attributes.add(Key.ROTATION);
        }
        if (!Float.isNaN(mRotationX)) {
            attributes.add(Key.ROTATION_X);
        }
        if (!Float.isNaN(mRotationY)) {
            attributes.add(Key.ROTATION_Y);
        }
        if (!Float.isNaN(mPivotX)) {
            attributes.add(Key.PIVOT_X);
        }
        if (!Float.isNaN(mPivotY)) {
            attributes.add(Key.PIVOT_Y);
        }
        if (!Float.isNaN(mTranslationX)) {
            attributes.add(Key.TRANSLATION_X);
        }
        if (!Float.isNaN(mTranslationY)) {
            attributes.add(Key.TRANSLATION_Y);
        }
        if (!Float.isNaN(mTranslationZ)) {
            attributes.add(Key.TRANSLATION_Z);
        }
        if (!Float.isNaN(mTransitionPathRotate)) {
            attributes.add(Key.TRANSITION_PATH_ROTATE);
        }
        if (!Float.isNaN(mScaleX)) {
            attributes.add(Key.SCALE_X);
        }
        if (!Float.isNaN(mScaleY)) {
            attributes.add(Key.SCALE_Y);
        }
        if (!Float.isNaN(mProgress)) {
            attributes.add(Key.PROGRESS);
        }
        if (mCustomConstraints.size() > 0) {
            for (String s : mCustomConstraints.keySet()) {
                attributes.add(Key.CUSTOM + "," + s);
            }
        }
    }

    public void setInterpolation(HashMap<String, Integer> interpolation) {
        if (mCurveFit == -1) {
            return;
        }
        if (!Float.isNaN(mAlpha)) {
            interpolation.put(Key.ALPHA, mCurveFit);
        }
        if (!Float.isNaN(mElevation)) {
            interpolation.put(Key.ELEVATION, mCurveFit);
        }
        if (!Float.isNaN(mRotation)) {
            interpolation.put(Key.ROTATION, mCurveFit);
        }
        if (!Float.isNaN(mRotationX)) {
            interpolation.put(Key.ROTATION_X, mCurveFit);
        }
        if (!Float.isNaN(mRotationY)) {
            interpolation.put(Key.ROTATION_Y, mCurveFit);
        }
        if (!Float.isNaN(mPivotX)) {
            interpolation.put(Key.PIVOT_X, mCurveFit);
        }
        if (!Float.isNaN(mPivotY)) {
            interpolation.put(Key.PIVOT_Y, mCurveFit);
        }
        if (!Float.isNaN(mTranslationX)) {
            interpolation.put(Key.TRANSLATION_X, mCurveFit);
        }
        if (!Float.isNaN(mTranslationY)) {
            interpolation.put(Key.TRANSLATION_Y, mCurveFit);
        }
        if (!Float.isNaN(mTranslationZ)) {
            interpolation.put(Key.TRANSLATION_Z, mCurveFit);
        }
        if (!Float.isNaN(mTransitionPathRotate)) {
            interpolation.put(Key.TRANSITION_PATH_ROTATE, mCurveFit);
        }
        if (!Float.isNaN(mScaleX)) {
            interpolation.put(Key.SCALE_X, mCurveFit);
        }
        if (!Float.isNaN(mScaleY)) {
            interpolation.put(Key.SCALE_Y, mCurveFit);
        }
        if (!Float.isNaN(mProgress)) {
            interpolation.put(Key.PROGRESS, mCurveFit);
        }
        if (mCustomConstraints.size() > 0) {
            for (String s : mCustomConstraints.keySet()) {
                interpolation.put(Key.CUSTOM + "," + s, mCurveFit);
            }
        }

    }

    @Override
    public void addValues(HashMap<String, ViewSpline> splines) {
        for (String s : splines.keySet()) {
            SplineSet splineSet = splines.get(s);
            if (splineSet == null) {
                continue;
            }
            if (s.startsWith(Key.CUSTOM)) {
                String cKey = s.substring(Key.CUSTOM.length() + 1);
                ConstraintAttribute cValue = mCustomConstraints.get(cKey);
                if (cValue != null) {
                    ((ViewSpline.CustomSet) splineSet).setPoint(mFramePosition, cValue);
                }
                continue;
            }
            switch (s) {
                case Key.ALPHA:
                    if (!Float.isNaN(mAlpha)) {
                        splineSet.setPoint(mFramePosition, mAlpha);
                    }
                    break;
                case Key.ELEVATION:
                    if (!Float.isNaN(mElevation)) {
                        splineSet.setPoint(mFramePosition, mElevation);
                    }
                    break;
                case Key.ROTATION:
                    if (!Float.isNaN(mRotation)) {
                        splineSet.setPoint(mFramePosition, mRotation);
                    }
                    break;
                case Key.ROTATION_X:
                    if (!Float.isNaN(mRotationX)) {
                        splineSet.setPoint(mFramePosition, mRotationX);
                    }
                    break;
                case Key.ROTATION_Y:
                    if (!Float.isNaN(mRotationY)) {
                        splineSet.setPoint(mFramePosition, mRotationY);
                    }
                    break;
                case Key.PIVOT_X:
                    if (!Float.isNaN(mRotationX)) {
                        splineSet.setPoint(mFramePosition, mPivotX);
                    }
                    break;
                case Key.PIVOT_Y:
                    if (!Float.isNaN(mRotationY)) {
                        splineSet.setPoint(mFramePosition, mPivotY);
                    }
                    break;
                case Key.TRANSITION_PATH_ROTATE:
                    if (!Float.isNaN(mTransitionPathRotate)) {
                        splineSet.setPoint(mFramePosition, mTransitionPathRotate);
                    }
                    break;
                case Key.SCALE_X:
                    if (!Float.isNaN(mScaleX)) {
                        splineSet.setPoint(mFramePosition, mScaleX);
                    }
                    break;
                case Key.SCALE_Y:
                    if (!Float.isNaN(mScaleY)) {
                        splineSet.setPoint(mFramePosition, mScaleY);
                    }
                    break;
                case Key.TRANSLATION_X:
                    if (!Float.isNaN(mTranslationX)) {
                        splineSet.setPoint(mFramePosition, mTranslationX);
                    }
                    break;
                case Key.TRANSLATION_Y:
                    if (!Float.isNaN(mTranslationY)) {
                        splineSet.setPoint(mFramePosition, mTranslationY);
                    }
                    break;
                case Key.TRANSLATION_Z:
                    if (!Float.isNaN(mTranslationZ)) {
                        splineSet.setPoint(mFramePosition, mTranslationZ);
                    }
                    break;
                case Key.PROGRESS:
                    if (!Float.isNaN(mProgress)) {
                        splineSet.setPoint(mFramePosition, mProgress);
                    }
                    break;
                default:
                    if (DEBUG) {
                        Log.v(TAG, "UNKNOWN addValues \"" + s + "\"");
                    }
            }
        }
    }

    @Override
    public void setValue(String tag, Object value) {
        switch (tag) {
            case ALPHA:
                mAlpha = toFloat(value);
                break;
            case CURVEFIT:
                mCurveFit = toInt(value);
                break;
            case ELEVATION:
                mElevation = toFloat(value);
                break;
            case MOTIONPROGRESS:
                mProgress = toFloat(value);
                break;
            case ROTATION:
                mRotation = toFloat(value);
                break;
            case ROTATION_X:
                mRotationX = toFloat(value);
                break;
            case ROTATION_Y:
                mRotationY = toFloat(value);
                break;
            case PIVOT_X:
                mPivotX = toFloat(value);
                break;
            case PIVOT_Y:
                mPivotY = toFloat(value);
                break;
            case SCALE_X:
                mScaleX = toFloat(value);
                break;
            case SCALE_Y:
                mScaleY = toFloat(value);
                break;
            case TRANSITIONEASING:
                mTransitionEasing = value.toString();
                break;
            case VISIBILITY:
                mVisibility = toBoolean(value);
                break;
            case TRANSITION_PATH_ROTATE:
                mTransitionPathRotate = toFloat(value);
                break;
            case TRANSLATION_X:
                mTranslationX = toFloat(value);
                break;
            case TRANSLATION_Y:
                mTranslationY = toFloat(value);
                break;
            case TRANSLATION_Z:
                mTranslationZ = toFloat(value);
                break;
        }
    }

    private static class Loader {} public Key copy(Key src) {
        super.copy(src);
        KeyAttributes k = (KeyAttributes) src;
        mCurveFit = k.mCurveFit;
        mVisibility = k.mVisibility;
        mAlpha = k.mAlpha;
        mElevation = k.mElevation;
        mRotation = k.mRotation;
        mRotationX = k.mRotationX;
        mRotationY = k.mRotationY;
        mPivotX = k.mPivotX;
        mPivotY = k.mPivotY;
        mTransitionPathRotate = k.mTransitionPathRotate;
        mScaleX = k.mScaleX;
        mScaleY = k.mScaleY;
        mTranslationX = k.mTranslationX;
        mTranslationY = k.mTranslationY;
        mTranslationZ = k.mTranslationZ;
        mProgress = k.mProgress;
        return this;
    }

    public Key clone() {
        return new KeyAttributes().copy(this);
    }
}
