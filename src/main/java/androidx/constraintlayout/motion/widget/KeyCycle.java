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

import androidx.constraintlayout.core.motion.utils.Oscillator;
import androidx.constraintlayout.core.motion.utils.SplineSet;
import androidx.constraintlayout.motion.utils.ViewOscillator;
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
 * Provide the passive data structure to get KeyPosition information form XML
 *
 * @hide
 */
public class KeyCycle extends Key {
    private static final String TAG = "KeyCycle";
    static final String NAME = "KeyCycle";
    public static final String WAVE_PERIOD = "wavePeriod";
    public static final String WAVE_OFFSET = "waveOffset";
    public static final String WAVE_PHASE = "wavePhase";
    public static final String WAVE_SHAPE = "waveShape";
    public static final int SHAPE_SIN_WAVE = Oscillator.SIN_WAVE;
    public static final int SHAPE_SQUARE_WAVE = Oscillator.SQUARE_WAVE;
    public static final int SHAPE_TRIANGLE_WAVE = Oscillator.TRIANGLE_WAVE;
    public static final int SHAPE_SAW_WAVE = Oscillator.SAW_WAVE;
    public static final int SHAPE_REVERSE_SAW_WAVE = Oscillator.REVERSE_SAW_WAVE;
    public static final int SHAPE_COS_WAVE = Oscillator.COS_WAVE;
    public static final int SHAPE_BOUNCE = Oscillator.BOUNCE;

    private String mTransitionEasing = null;
    private int mCurveFit = 0;
    public int mWaveShape = -1;
    private String mCustomWaveShape = null;
    public float mWavePeriod = Float.NaN;
    public float mWaveOffset = 0;
    public float mWavePhase = 0;
    private float mProgress = Float.NaN;
    public int mWaveVariesBy = -1;
    private float mAlpha = Float.NaN;
    private float mElevation = Float.NaN;
    private float mRotation = Float.NaN;
    private float mTransitionPathRotate = Float.NaN;
    private float mRotationX = Float.NaN;
    private float mRotationY = Float.NaN;
    private float mScaleX = Float.NaN;
    private float mScaleY = Float.NaN;
    private float mTranslationX = Float.NaN;
    private float mTranslationY = Float.NaN;
    private float mTranslationZ = Float.NaN;
    public static final int KEY_TYPE = 4;

    {
        mType = KEY_TYPE;
        mCustomConstraints = new HashMap<>();
    }

    public void load(Context context, AttributeSet attrs) {}

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
        if (!Float.isNaN(mScaleX)) {
            attributes.add(Key.SCALE_X);
        }
        if (!Float.isNaN(mScaleY)) {
            attributes.add(Key.SCALE_Y);
        }
        if (!Float.isNaN(mTransitionPathRotate)) {
            attributes.add(Key.TRANSITION_PATH_ROTATE);
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
        if (mCustomConstraints.size() > 0) {
            for (String s : mCustomConstraints.keySet()) {
                attributes.add(Key.CUSTOM + "," + s);
            }
        }
    }

    public void addCycleValues(HashMap<String, ViewOscillator> oscSet) {
        for (String key : oscSet.keySet()) {
            if (key.startsWith(Key.CUSTOM)) {
                String customKey = key.substring(Key.CUSTOM.length() + 1);
                ConstraintAttribute cValue = mCustomConstraints.get(customKey);
                if (cValue == null || cValue.getType() != ConstraintAttribute.AttributeType.FLOAT_TYPE) {
                    continue;
                }

                ViewOscillator osc = oscSet.get(key);
                if (osc == null) {
                    continue;
                }

                osc.setPoint(mFramePosition, mWaveShape, mCustomWaveShape, mWaveVariesBy, mWavePeriod, mWaveOffset, mWavePhase, cValue.getValueToInterpolate(), cValue);
                continue;
            }
            float value = getValue(key);
            if (Float.isNaN(value)) {
                continue;
            }

            ViewOscillator osc = oscSet.get(key);
            if (osc == null) {
                continue;
            }

            osc.setPoint(mFramePosition, mWaveShape, mCustomWaveShape, mWaveVariesBy, mWavePeriod, mWaveOffset, mWavePhase, value);
        }
    }

    public float getValue(String key) {
        switch (key) {
            case Key.ALPHA:
                return mAlpha;
            case Key.ELEVATION:
                return mElevation;
            case Key.ROTATION:
                return mRotation;
            case Key.ROTATION_X:
                return mRotationX;
            case Key.ROTATION_Y:
                return mRotationY;
            case Key.TRANSITION_PATH_ROTATE:
                return mTransitionPathRotate;
            case Key.SCALE_X:
                return mScaleX;
            case Key.SCALE_Y:
                return mScaleY;
            case Key.TRANSLATION_X:
                return mTranslationX;
            case Key.TRANSLATION_Y:
                return mTranslationY;
            case Key.TRANSLATION_Z:
                return mTranslationZ;
            case Key.WAVE_OFFSET:
                return mWaveOffset;
            case Key.WAVE_PHASE:
                return mWavePhase;
            case Key.PROGRESS:
                return mProgress;
            default:
                if (!key.startsWith("CUSTOM")) {
                    Log.v("WARNING! KeyCycle", "  UNKNOWN  " + key);
                }
                return Float.NaN;
        }
    }

    @Override
    public void addValues(HashMap<String, ViewSpline> splines) {
        CLDebug.logStack(TAG, "add " + splines.size() + " values", 2);
        for (String s : splines.keySet()) {
            SplineSet splineSet = splines.get(s);
            if (splineSet == null) {
                continue;
            }
            switch (s) {
                case Key.ALPHA:
                    splineSet.setPoint(mFramePosition, mAlpha);
                    break;
                case Key.ELEVATION:
                    splineSet.setPoint(mFramePosition, mElevation);
                    break;
                case Key.ROTATION:
                    splineSet.setPoint(mFramePosition, mRotation);
                    break;
                case Key.ROTATION_X:
                    splineSet.setPoint(mFramePosition, mRotationX);
                    break;
                case Key.ROTATION_Y:
                    splineSet.setPoint(mFramePosition, mRotationY);
                    break;
                case Key.TRANSITION_PATH_ROTATE:
                    splineSet.setPoint(mFramePosition, mTransitionPathRotate);
                    break;
                case Key.SCALE_X:
                    splineSet.setPoint(mFramePosition, mScaleX);
                    break;
                case Key.SCALE_Y:
                    splineSet.setPoint(mFramePosition, mScaleY);
                    break;
                case Key.TRANSLATION_X:
                    splineSet.setPoint(mFramePosition, mTranslationX);
                    break;
                case Key.TRANSLATION_Y:
                    splineSet.setPoint(mFramePosition, mTranslationY);
                    break;
                case Key.TRANSLATION_Z:
                    splineSet.setPoint(mFramePosition, mTranslationZ);
                    break;
                case Key.WAVE_OFFSET:
                    splineSet.setPoint(mFramePosition, mWaveOffset);
                    break;
                case Key.WAVE_PHASE:
                    splineSet.setPoint(mFramePosition, mWavePhase);
                    break;
                case Key.PROGRESS:
                    splineSet.setPoint(mFramePosition, mProgress);
                    break;
                default:
                    if (!s.startsWith("CUSTOM")) {
                        Log.v("WARNING KeyCycle", "  UNKNOWN  " + s);
                    }
            }
        }
    }

    private static class Loader {} public void setValue(String tag, Object value) {
        switch (tag) {
            case Key.ALPHA:
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
            case SCALE_X:
                mScaleX = toFloat(value);
                break;
            case SCALE_Y:
                mScaleY = toFloat(value);
                break;
            case TRANSITIONEASING:
                mTransitionEasing = value.toString();
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
            case WAVE_PERIOD:
                mWavePeriod = toFloat(value);
                break;
            case WAVE_OFFSET:
                mWaveOffset = toFloat(value);
                break;
            case WAVE_PHASE:
                mWavePhase = toFloat(value);
                break;
            case WAVE_SHAPE:
                if (value instanceof Integer) {
                    mWaveShape = toInt(value);
                } else {
                    mWaveShape = Oscillator.CUSTOM;
                    mCustomWaveShape = value.toString();
                }
                break;
        }
    }

    public Key copy(Key src) {
        super.copy(src);
        KeyCycle k = (KeyCycle) src;
        mTransitionEasing = k.mTransitionEasing;
        mCurveFit = k.mCurveFit;
        mWaveShape = k.mWaveShape;
        mCustomWaveShape = k.mCustomWaveShape;
        mWavePeriod = k.mWavePeriod;
        mWaveOffset = k.mWaveOffset;
        mWavePhase = k.mWavePhase;
        mProgress = k.mProgress;
        mWaveVariesBy = k.mWaveVariesBy;
        mAlpha = k.mAlpha;
        mElevation = k.mElevation;
        mRotation = k.mRotation;
        mTransitionPathRotate = k.mTransitionPathRotate;
        mRotationX = k.mRotationX;
        mRotationY = k.mRotationY;
        mScaleX = k.mScaleX;
        mScaleY = k.mScaleY;
        mTranslationX = k.mTranslationX;
        mTranslationY = k.mTranslationY;
        mTranslationZ = k.mTranslationZ;
        return this;
    }

    public Key clone() {
        return new KeyCycle().copy(this);
    }
}
