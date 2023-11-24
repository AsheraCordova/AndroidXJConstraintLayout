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
import r.android.graphics.RectF;

import androidx.constraintlayout.motion.utils.ViewSpline;
import androidx.constraintlayout.widget.ConstraintAttribute;
//import androidx.constraintlayout.widget.R;

import r.android.util.AttributeSet;
import r.android.util.Log;
import r.android.util.SparseIntArray;
//import r.android.util.TypedValue;
import r.android.view.View;
import r.android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 * Defines container for a key frame of for storing KeyAttributes.
 * KeyAttributes change post layout values of a view.
 *
 * @hide
 */

public class KeyTrigger extends Key {
    static final String NAME = "KeyTrigger";
    private static final String TAG = "KeyTrigger";
    public static final String VIEW_TRANSITION_ON_CROSS = "viewTransitionOnCross";
    public static final String VIEW_TRANSITION_ON_POSITIVE_CROSS = "viewTransitionOnPositiveCross";
    public static final String VIEW_TRANSITION_ON_NEGATIVE_CROSS = "viewTransitionOnNegativeCross";
    public static final String POST_LAYOUT = "postLayout";
    public static final String TRIGGER_SLACK = "triggerSlack";
    public static final String TRIGGER_COLLISION_VIEW = "triggerCollisionView";
    public static final String TRIGGER_COLLISION_ID = "triggerCollisionId";
    public static final String TRIGGER_ID = "triggerID";
    public static final String POSITIVE_CROSS = "positiveCross";
    public static final String NEGATIVE_CROSS = "negativeCross";
    public static final String TRIGGER_RECEIVER = "triggerReceiver";
    public static final String CROSS = "CROSS";

    private int mCurveFit = -1;
    private String mCross = null;
    private int mTriggerReceiver = UNSET;
    private String mNegativeCross = null;
    private String mPositiveCross = null;
    private int mTriggerID = UNSET;
    private int mTriggerCollisionId = UNSET;
    private View mTriggerCollisionView = null;
    float mTriggerSlack = .1f;
    private boolean mFireCrossReset = true;
    private boolean mFireNegativeReset = true;
    private boolean mFirePositiveReset = true;
    public float mFireThreshold = Float.NaN;
    private float mFireLastPos;
    private boolean mPostLayout = false;
    int mViewTransitionOnNegativeCross = UNSET;
    int mViewTransitionOnPositiveCross = UNSET;
    int mViewTransitionOnCross = UNSET;

    RectF mCollisionRect = new RectF();
    RectF mTargetRect = new RectF();
    HashMap<String, Method> mMethodHashMap = new HashMap<>();
    public static final int KEY_TYPE = 5;

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
    }

    @Override
    public void addValues(HashMap<String, ViewSpline> splines) {
    }

    @Override
    public void setValue(String tag, Object value) {
        switch (tag) {
            case CROSS:
                mCross = value.toString();
                break;
            case TRIGGER_RECEIVER:
                mTriggerReceiver = toInt(value);
                break;
            case NEGATIVE_CROSS:
                mNegativeCross = value.toString();
                break;
            case POSITIVE_CROSS:
                mPositiveCross = value.toString();
                break;
            case TRIGGER_ID:
                mTriggerID = toInt(value);
                break;
            case TRIGGER_COLLISION_ID:
                mTriggerCollisionId = toInt(value);
                break;
            case TRIGGER_COLLISION_VIEW:
                mTriggerCollisionView = (View) value;
                break;
            case TRIGGER_SLACK:
                mTriggerSlack = toFloat(value);
                break;
            case POST_LAYOUT:
                mPostLayout = toBoolean(value);
                break;
            case VIEW_TRANSITION_ON_NEGATIVE_CROSS:
                mViewTransitionOnNegativeCross = toInt(value);
                break;
            case VIEW_TRANSITION_ON_POSITIVE_CROSS:
                mViewTransitionOnPositiveCross = toInt(value);
                break;
            case VIEW_TRANSITION_ON_CROSS:
                mViewTransitionOnCross = toInt(value);
                break;

        }
    }

    private void setUpRect(RectF rect, View child, boolean postLayout) {
        rect.top = child.getTop();
        rect.bottom = child.getBottom();
        rect.left = child.getLeft();
        rect.right = child.getRight();
        if (postLayout) {
            //child.getMatrix().mapRect(rect);
        }
    }

    public void conditionallyFire(float pos, View child) {
        boolean fireCross = false;
        boolean fireNegative = false;
        boolean firePositive = false;

        if (mTriggerCollisionId != UNSET) {
            if (mTriggerCollisionView == null) {
                mTriggerCollisionView = ((ViewGroup) child.getParent()).findViewById(mTriggerCollisionId);
            }

            setUpRect(mCollisionRect, mTriggerCollisionView, mPostLayout);
            setUpRect(mTargetRect, child, mPostLayout);
            boolean in = mCollisionRect.intersect(mTargetRect);
            // TODO scale by mTriggerSlack
            if (in) {
                if (mFireCrossReset) {
                    fireCross = true;
                    mFireCrossReset = false;
                }
                if (mFirePositiveReset) {
                    firePositive = true;
                    mFirePositiveReset = false;
                }
                mFireNegativeReset = true;
            } else {
                if (!mFireCrossReset) {
                    fireCross = true;
                    mFireCrossReset = true;
                }
                if (mFireNegativeReset) {
                    fireNegative = true;
                    mFireNegativeReset = false;
                }
                mFirePositiveReset = true;
            }

        } else {

            // Check for crossing
            if (mFireCrossReset) {

                float offset = pos - mFireThreshold;
                float lastOffset = mFireLastPos - mFireThreshold;

                if (offset * lastOffset < 0) { // just crossed the threshold
                    fireCross = true;
                    mFireCrossReset = false;
                }
            } else {
                if (Math.abs(pos - mFireThreshold) > mTriggerSlack) {
                    mFireCrossReset = true;
                }
            }

            // Check for negative crossing
            if (mFireNegativeReset) {
                float offset = pos - mFireThreshold;
                float lastOffset = mFireLastPos - mFireThreshold;
                if (offset * lastOffset < 0 && offset < 0) { // just crossed the threshold
                    fireNegative = true;
                    mFireNegativeReset = false;
                }
            } else {
                if (Math.abs(pos - mFireThreshold) > mTriggerSlack) {
                    mFireNegativeReset = true;
                }
            }
            // Check for positive crossing
            if (mFirePositiveReset) {
                float offset = pos - mFireThreshold;
                float lastOffset = mFireLastPos - mFireThreshold;
                if (offset * lastOffset < 0 && offset > 0) { // just crossed the threshold
                    firePositive = true;
                    mFirePositiveReset = false;
                }
            } else {
                if (Math.abs(pos - mFireThreshold) > mTriggerSlack) {
                    mFirePositiveReset = true;
                }
            }
        }
        mFireLastPos = pos;

        if (fireNegative || fireCross || firePositive) {
            ((MotionLayout) child.getParent()).fireTrigger(mTriggerID, firePositive, pos);
        }
        View call = (mTriggerReceiver == UNSET) ? child : ((MotionLayout) child.getParent()).findViewById(mTriggerReceiver);

        if (fireNegative) {
            if (mNegativeCross != null) {
                fire(mNegativeCross, call);
            }
            if (mViewTransitionOnNegativeCross != UNSET) {
                ((MotionLayout) child.getParent()).viewTransition(mViewTransitionOnNegativeCross, call);
            }
        }
        if (firePositive) {
            if (mPositiveCross != null) {
                fire(mPositiveCross, call);
            }
            if (mViewTransitionOnPositiveCross != UNSET) {
                ((MotionLayout) child.getParent()).viewTransition(mViewTransitionOnPositiveCross, call);
            }
        }
        if (fireCross) {
            if (mCross != null) {
                fire(mCross, call);
            }
            if (mViewTransitionOnCross != UNSET) {
                ((MotionLayout) child.getParent()).viewTransition(mViewTransitionOnCross, call);
            }
        }

    }

    private void fire(String str, View call) {
        if (str == null) {
            return;
        }
        if (!str.startsWith(".")) {switch (str) {case "state0":call.state0();break;case "state1":call.state1();break;case "state2":call.state2();break;case "state3":call.state3();break;case "state4":call.state4();break;case "stateYes":call.stateYes();break;case "stateNo":call.stateNo();break;default:break;}return;}if (str.startsWith(".")) {
            fireCustom(str, call);
            return;
        }
        Method method = null;
        if (mMethodHashMap.containsKey(str)) {
            method = mMethodHashMap.get(str);
            if (method == null) { // we looked up and did not find
                return;
            }
        }
        if (method == null) {
            try {
                method = call.getClass().getMethod(str);
                mMethodHashMap.put(str, method);
            } catch (NoSuchMethodException e) {
                mMethodHashMap.put(str, null); // record that we could not get this method
                Log.e(TAG, "Could not find method \"" + str + "\"" + "on class "
                        + call.getClass().getSimpleName() + " " + CLDebug.getName(call));
                return;
            }
        }
        try {
            method.invoke(call);
        } catch (Exception e) {
            Log.e(TAG, "Exception in call \"" + mCross + "\"" + "on class "
                    + call.getClass().getSimpleName() + " " + CLDebug.getName(call));
        }
    }

    private void fireCustom(String str, View view) {
        boolean callAll = str.length() == 1;
        if (!callAll) {
            str = str.substring(1).toLowerCase(Locale.ROOT);
        }
        for (String name : mCustomConstraints.keySet()) {
            String lowerCase = name.toLowerCase(Locale.ROOT);
            if (callAll || lowerCase.matches(str)) {
                ConstraintAttribute custom = mCustomConstraints.get(name);
                if (custom != null) {
                    custom.applyCustom(view);
                }
            }
        }
    }

    private static class Loader {} public Key copy(Key src) {
        super.copy(src);
        KeyTrigger k = (KeyTrigger) src;
        mCurveFit = k.mCurveFit;
        mCross = k.mCross;
        mTriggerReceiver = k.mTriggerReceiver;
        mNegativeCross = k.mNegativeCross;
        mPositiveCross = k.mPositiveCross;
        mTriggerID = k.mTriggerID;
        mTriggerCollisionId = k.mTriggerCollisionId;
        mTriggerCollisionView = k.mTriggerCollisionView;
        mTriggerSlack = k.mTriggerSlack;
        mFireCrossReset = k.mFireCrossReset;
        mFireNegativeReset = k.mFireNegativeReset;
        mFirePositiveReset = k.mFirePositiveReset;
        mFireThreshold = k.mFireThreshold;
        mFireLastPos = k.mFireLastPos;
        mPostLayout = k.mPostLayout;
        mCollisionRect = k.mCollisionRect;
        mTargetRect = k.mTargetRect;
        mMethodHashMap = k.mMethodHashMap;
        return this;
    }

    public Key clone() {
        return new KeyTrigger().copy(this);
    }
}
