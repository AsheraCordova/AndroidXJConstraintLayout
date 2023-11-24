/*
 * Copyright (C) 2020 The Android Open Source Project
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
//import r.android.content.res.XmlResourceParser;
import r.android.graphics.Rect;
import r.android.util.AttributeSet;
import r.android.util.Log;
//import r.android.util.TypedValue;
//import r.android.util.Xml;
import r.android.view.MotionEvent;
import r.android.view.View;
import r.android.view.ViewGroup;
import r.android.view.animation.AccelerateDecelerateInterpolator;
import r.android.view.animation.AccelerateInterpolator;
import r.android.view.animation.AnimationUtils;
import r.android.view.animation.AnticipateInterpolator;
import r.android.view.animation.BounceInterpolator;
import r.android.view.animation.DecelerateInterpolator;
import r.android.view.animation.Interpolator;
import r.android.view.animation.OvershootInterpolator;

import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
//import androidx.constraintlayout.widget.R;

//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides a support for <ViewTransition> tag
 * it Parses tag
 * it implement the transition
 * it will update ConstraintSet or sets
 * For asynchronous it will create and drive a MotionController.
 */
public class ViewTransition {
    private static String TAG = "ViewTransition";
    ConstraintSet set;
    public static final String VIEW_TRANSITION_TAG = "ViewTransition";
    public static final String KEY_FRAME_SET_TAG = "KeyFrameSet";
    public static final String CONSTRAINT_OVERRIDE = "ConstraintOverride";
    public static final String CUSTOM_ATTRIBUTE = "CustomAttribute";
    public static final String CUSTOM_METHOD = "CustomMethod";

    private static final int UNSET = -1;
    private int mId;
    // Transition can be up or down of manually fired
    public static final int ONSTATE_ACTION_DOWN = 1;
    public static final int ONSTATE_ACTION_UP = 2;
    public static final int ONSTATE_ACTION_DOWN_UP = 3;
    public static final int ONSTATE_SHARED_VALUE_SET = 4;
    public static final int ONSTATE_SHARED_VALUE_UNSET = 5;

    private int mOnStateTransition = UNSET;
    private boolean mDisabled = false;
    private int mPathMotionArc = 0;
    int mViewTransitionMode;
    static final int VIEWTRANSITIONMODE_CURRENTSTATE = 0;
    static final int VIEWTRANSITIONMODE_ALLSTATES = 1;
    static final int VIEWTRANSITIONMODE_NOSTATE = 2;
    KeyFrames mKeyFrames;
    ConstraintSet.Constraint mConstraintDelta;
    private int mDuration = UNSET;
    private int mUpDuration = UNSET;

    private int mTargetId;
    private String mTargetString;

    // interpolator code
    private static final int SPLINE_STRING = -1;
    private static final int INTERPOLATOR_REFERENCE_ID = -2;
    private int mDefaultInterpolator = 0;
    private String mDefaultInterpolatorString = null;
    private int mDefaultInterpolatorID = -1;
    static final int EASE_IN_OUT = 0;
    static final int EASE_IN = 1;
    static final int EASE_OUT = 2;
    static final int LINEAR = 3;
    static final int BOUNCE = 4;
    static final int OVERSHOOT = 5;
    static final int ANTICIPATE = 6;

    Context mContext;
    private int mSetsTag = UNSET;
    private int mClearsTag = UNSET;
    private int mIfTagSet = UNSET;
    private int mIfTagNotSet = UNSET;

    // shared value management. mSharedValueId is the key we are watching,
    // mSharedValueCurrent the current value for that key, and mSharedValueTarget
    // is the target we are waiting for to trigger.
    private int mSharedValueTarget = UNSET;
    private int mSharedValueID = UNSET;
    private int mSharedValueCurrent = UNSET;

    public int getSharedValueCurrent() {
        return mSharedValueCurrent;
    }

    public void setSharedValueCurrent(int sharedValueCurrent) {
        this.mSharedValueCurrent = sharedValueCurrent;
    }

    /**
     * Gets the type of transition to listen to.
     *
     * @return ONSTATE_TRANSITION_*
     */
    public int getStateTransition() {
        return mOnStateTransition;
    }

    /**
     * Sets the type of transition to listen to.
     *
     * @param stateTransition
     */
    public void setStateTransition(int stateTransition) {
        this.mOnStateTransition = stateTransition;
    }

    /**
     * Gets the SharedValue it will be listening for.
     *
     * @return
     */
    public int getSharedValue() {
        return mSharedValueTarget;
    }

    /**
     * sets the SharedValue it will be listening for.
     */
    public void setSharedValue(int sharedValue) {
        this.mSharedValueTarget = sharedValue;
    }

    /**
     * Gets the ID of the SharedValue it will be listening for.
     *
     * @return the id of the shared value
     */
    public int getSharedValueID() {
        return mSharedValueID;
    }

    /**
     * sets the ID of the SharedValue it will be listening for.
     */
    public void setSharedValueID(int sharedValueID) {
        this.mSharedValueID = sharedValueID;
    }

    public String toString() {
        return "ViewTransition(" + CLDebug.getName(mContext, mId) + ")";
    }

    Interpolator getInterpolator(Context context) {
        switch (mDefaultInterpolator) {
            case SPLINE_STRING:
                final Easing easing = Easing.getInterpolator(mDefaultInterpolatorString);
                return new Interpolator() {
                    @Override
                    public float getInterpolation(float v) {
                        return (float) easing.get(v);
                    }
                };
            case INTERPOLATOR_REFERENCE_ID:
                return AnimationUtils.loadInterpolator(context,
                        mDefaultInterpolatorID);
            case EASE_IN_OUT:
                return new AccelerateDecelerateInterpolator();
            case EASE_IN:
                return new AccelerateInterpolator();
            case EASE_OUT:
                return new DecelerateInterpolator();
            case LINEAR:
                return null;
            case ANTICIPATE:
                return new AnticipateInterpolator();
            case OVERSHOOT:
                return new OvershootInterpolator();
            case BOUNCE:
                return new BounceInterpolator();
        }
        return null;
    }

    void applyIndependentTransition(ViewTransitionController controller, MotionLayout motionLayout, View view) {
        MotionController motionController = new MotionController(view);
        motionController.setBothStates(view);
        mKeyFrames.addAllFrames(motionController);
        motionController.setup(motionLayout.getWidth(), motionLayout.getHeight(), mDuration, System.nanoTime());
        new Animate(controller, motionController,
                mDuration, mUpDuration, mOnStateTransition,
                getInterpolator(motionLayout.getContext()), mSetsTag, mClearsTag);
    }

    static class Animate {
        private final int mSetsTag;
        private final int mClearsTag;
        long mStart;
        MotionController mMC;
        int mDuration;
        int mUpDuration;
        KeyCache mCache = new KeyCache();
        ViewTransitionController mVtController;
        Interpolator mInterpolator;
        boolean reverse = false;
        float mPosition;
        float mDpositionDt;
        long mLastRender;
        Rect mTempRec = new Rect();
        boolean hold_at_100 = false;

        Animate(ViewTransitionController controller,
                MotionController motionController,
                int duration, int upDuration, int mode,
                Interpolator interpolator, int setTag, int clearTag) {
            mVtController = controller;
            mMC = motionController;
            mDuration = duration;
            mUpDuration = upDuration;
            mStart = System.nanoTime();
            mLastRender = mStart;
            mVtController.addAnimation(this);
            mInterpolator = interpolator;
            mSetsTag = setTag;
            mClearsTag = clearTag;
            if (mode == ONSTATE_ACTION_DOWN_UP) {
                hold_at_100 = true;
            }
            mDpositionDt = (duration == 0) ? Float.MAX_VALUE : 1f / duration;
            mutate();
        }

        void reverse(boolean dir) {
            reverse = dir;
            if (reverse && mUpDuration != UNSET) {
                mDpositionDt = (mUpDuration == 0) ? Float.MAX_VALUE : 1f / mUpDuration;
            }
            mVtController.invalidate();
            mLastRender = System.nanoTime();
        }

        void mutate() {
            if (reverse) {
                mutateReverse();
            } else {
                mutateForward();
            }
        }

        void mutateReverse() {
            long current = System.nanoTime();
            long elapse = current - mLastRender;
            mLastRender = current;

            mPosition -= ((float) (elapse * 1E-6)) * mDpositionDt;
            if (mPosition < 0.0f) {
                mPosition = 0.0f;
            }

            float ipos = (mInterpolator == null) ? mPosition : mInterpolator.getInterpolation(mPosition);
            boolean repaint = mMC.interpolate(mMC.mView, ipos, current, mCache);

            if (mPosition <= 0) {
                if (mSetsTag != UNSET) {
                    mMC.getView().setTag(mSetsTag, System.nanoTime());
                }
                if (mClearsTag != UNSET) {
                    mMC.getView().setTag(mClearsTag, null);
                }
                mVtController.removeAnimation(this);
            }
            if (mPosition > 0f || repaint) {
                mVtController.invalidate();
            }
        }

        void mutateForward() {

            long current = System.nanoTime();
            long elapse = current - mLastRender;
            mLastRender = current;

            mPosition += ((float) (elapse * 1E-6)) * mDpositionDt;
            if (mPosition >= 1.0f) {
                mPosition = 1.0f;
            }

            float ipos = (mInterpolator == null) ? mPosition : mInterpolator.getInterpolation(mPosition);
            boolean repaint = mMC.interpolate(mMC.mView, ipos, current, mCache);


            if (mPosition >= 1) {
                if (mSetsTag != UNSET) {
                    mMC.getView().setTag(mSetsTag, System.nanoTime());
                }
                if (mClearsTag != UNSET) {
                    mMC.getView().setTag(mClearsTag, null);
                }
                if (!hold_at_100) {
                    mVtController.removeAnimation(this);
                }
            }
            if (mPosition < 1f || repaint) {
                mVtController.invalidate();
            }
        }

        public void reactTo(int action, float x, float y) {
            switch (action) {
                case MotionEvent.ACTION_UP:
                    if (!reverse) {
                        reverse(true);
                    }
                    return;
                case MotionEvent.ACTION_MOVE:
                    View view = mMC.getView();
                    view.getHitRect(mTempRec);
                    if (!mTempRec.contains((int) x, (int) y)) {
                        if (!reverse)
                            reverse(true);
                    }
            }
        }
    }

    void applyTransition(ViewTransitionController controller,
                         MotionLayout layout,
                         int fromId,
                         ConstraintSet current,
                         View... views) {
        if (mDisabled) {
            return;
        }
        if (mViewTransitionMode == VIEWTRANSITIONMODE_NOSTATE) {
            applyIndependentTransition(controller, layout, views[0]);
            return;
        }
        if (mViewTransitionMode == VIEWTRANSITIONMODE_ALLSTATES) {
            int[] ids = layout.getConstraintSetIds();
            for (int i = 0; i < ids.length; i++) {
                int id = ids[i];
                if (id == fromId) {
                    continue;
                }
                ConstraintSet cSet = layout.getConstraintSet(id);
                for (View view : views) {
                    ConstraintSet.Constraint constraint = cSet.getConstraint(view.getId());
                    if (mConstraintDelta != null) {
                        mConstraintDelta.applyDelta(constraint);
                        constraint.mCustomConstraints.putAll(mConstraintDelta.mCustomConstraints);
                    }
                }
            }
        }

        ConstraintSet transformedState = new ConstraintSet();
        transformedState.clone(current);
        for (View view : views) {
            ConstraintSet.Constraint constraint = transformedState.getConstraint(view.getId());
            if (mConstraintDelta != null) {
                mConstraintDelta.applyDelta(constraint);
                constraint.mCustomConstraints.putAll(mConstraintDelta.mCustomConstraints);
            }
        }

        layout.updateState(fromId, transformedState);
        layout.updateState(r.android.R.id.view_transition, current);
        layout.setState(r.android.R.id.view_transition, -1, -1);
        MotionScene.Transition tmpTransition = new MotionScene.Transition(-1, layout.mScene, r.android.R.id.view_transition, fromId);
        for (View view : views) {
            updateTransition(tmpTransition, view);
        }
        layout.setTransition(tmpTransition);
        layout.transitionToEnd(() -> {
            if (mSetsTag != UNSET) {
                for (View view : views) {
                    view.setTag(mSetsTag, System.nanoTime());
                }
            }
            if (mClearsTag != UNSET) {
                for (View view : views) {
                    view.setTag(mClearsTag, null);
                }
            }
        });
    }

    private void updateTransition(MotionScene.Transition transition, View view) {
        if (mDuration != -1) {
            transition.setDuration(mDuration);
        }
        transition.setPathMotionArc(mPathMotionArc);
        transition.setInterpolatorInfo(mDefaultInterpolator, mDefaultInterpolatorString, mDefaultInterpolatorID);
        int id = view.getId();
        if (mKeyFrames != null) {
            ArrayList<Key> keys = mKeyFrames.getKeyFramesForView(KeyFrames.UNSET);
            KeyFrames keyFrames = new KeyFrames();
            for (Key key : keys) {
                keyFrames.addKey(key.clone().setViewId(id));
            }

            transition.addKeyFrame(keyFrames);
        }
    }

    int getId() {
        return mId;
    }

    void setId(int id) {
        this.mId = id;
    }

    boolean matchesView(View view) {
        if (view == null) {
            return false;
        }
        if (mTargetId == -1 && mTargetString == null) {
            return false;
        }
        if (!checkTags(view)) {
            return false;
        }
        if (view.getId() == mTargetId) {
            return true;
        }
        if (mTargetString == null) {
            return false;
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof ConstraintLayout.LayoutParams) {
            String tag = ((ConstraintLayout.LayoutParams) (view.getLayoutParams())).constraintTag;
            if (tag != null && tag.matches(mTargetString)) {
                return true;
            }
        }
        return false;
    }

    boolean supports(int action) {
        if (mOnStateTransition == ONSTATE_ACTION_DOWN) {
            return action == MotionEvent.ACTION_DOWN;
        }
        if (mOnStateTransition == ONSTATE_ACTION_UP) {
            return action == MotionEvent.ACTION_UP;
        }
        if (mOnStateTransition == ONSTATE_ACTION_DOWN_UP) {
            return action == MotionEvent.ACTION_DOWN;
        }
        return false;
    }

    boolean isEnabled() {
        return !mDisabled;
    }

    void setEnabled(boolean enable) {
        this.mDisabled = !enable;
    }

    boolean checkTags(View view) {

        boolean set = (mIfTagSet == UNSET) ? true : (null != view.getTag(mIfTagSet));
        boolean notSet = (mIfTagNotSet == UNSET) ? true : null == view.getTag(mIfTagNotSet);
        return set && notSet;
    }
}
