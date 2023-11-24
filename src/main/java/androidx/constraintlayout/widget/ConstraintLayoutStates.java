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

package androidx.constraintlayout.widget;

import r.android.content.Context;
import r.android.content.res.Resources;
//import r.android.content.res.TypedArray;
//import r.android.content.res.XmlResourceParser;
import r.android.util.AttributeSet;
import r.android.util.Log;
import r.android.util.SparseArray;
//import r.android.util.Xml;

//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class ConstraintLayoutStates {
    public static final String TAG = "ConstraintLayoutStates";
    private static final boolean DEBUG = false;
    private final ConstraintLayout mConstraintLayout;
    ConstraintSet mDefaultConstraintSet;
    int mCurrentStateId = -1; // default
    int mCurrentConstraintNumber = -1; // default
    private SparseArray<State> mStateList = new SparseArray<>();
    private SparseArray<ConstraintSet> mConstraintSetMap = new SparseArray<>();
    private ConstraintsChangedListener mConstraintsChangedListener = null;

    ConstraintLayoutStates(Context context, ConstraintLayout layout, int resourceID) {
        mConstraintLayout = layout;
        //load(context, resourceID);
    }

    /**
     * Return true if it needs to change
     * @param id
     * @param width
     * @param height
     * @return
     */
    public boolean needsToChange(int id, float width, float height) {
        if (mCurrentStateId != id) {
            return true;
        }

        State state = (id == -1) ? mStateList.valueAt(0) : mStateList.get(mCurrentStateId);

        if (mCurrentConstraintNumber != -1) {
            if (state.mVariants.get(mCurrentConstraintNumber).match(width, height)) {
                return false;
            }
        }

        if (mCurrentConstraintNumber == state.findMatch(width, height)) {
            return false;
        }
        return true;
    }

    /**
     * updateConstraints for the view with the id and width and height
     * @param id
     * @param width
     * @param height
     */
    public void updateConstraints(int id, float width, float height) {
        if (mCurrentStateId == id) {
            State state;
            if (id == -1) {
                state = mStateList.valueAt(0); // id not being used take the first
            } else {
                state = mStateList.get(mCurrentStateId);

            }
            if (mCurrentConstraintNumber != -1) {
                if (state.mVariants.get(mCurrentConstraintNumber).match(width, height)) {
                    return;
                }
            }
            int match = state.findMatch(width, height);
            if (mCurrentConstraintNumber == match) {
                return;
            }

            ConstraintSet constraintSet = (match == -1) ? mDefaultConstraintSet :
                    state.mVariants.get(match).mConstraintSet;
            int cid = (match == -1) ? state.mConstraintID :
                    state.mVariants.get(match).mConstraintID;
            if (constraintSet == null) {
                return;
            }
            mCurrentConstraintNumber = match;
            if (mConstraintsChangedListener != null) {
                mConstraintsChangedListener.preLayoutChange(-1, cid);
            }
            constraintSet.applyTo(mConstraintLayout);
            if (mConstraintsChangedListener != null) {
                mConstraintsChangedListener.postLayoutChange(-1, cid);
            }

        } else {
            mCurrentStateId = id;
            State state = mStateList.get(mCurrentStateId);
            int match = state.findMatch(width, height);
            ConstraintSet constraintSet = (match == -1) ? state.mConstraintSet :
                    state.mVariants.get(match).mConstraintSet;
            int cid = (match == -1) ? state.mConstraintID :
                    state.mVariants.get(match).mConstraintID;

            if (constraintSet == null) {
                Log.v(TAG, "NO Constraint set found ! id=" + id
                        + ", dim =" + width + ", " + height);
                return;
            }
            mCurrentConstraintNumber = match;
            if (mConstraintsChangedListener != null) {
                mConstraintsChangedListener.preLayoutChange(id, cid);
            }
            constraintSet.applyTo(mConstraintLayout);
            if (mConstraintsChangedListener != null) {
                mConstraintsChangedListener.postLayoutChange(id, cid);
            }
        }

    }

    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
    }

    /////////////////////////////////////////////////////////////////////////
    //      This represents one state
    /////////////////////////////////////////////////////////////////////////
    static class State {
        int mId;
        ArrayList<Variant> mVariants = new ArrayList<>();
        int mConstraintID = -1;
        ConstraintSet mConstraintSet;

        void add(Variant size) {
            mVariants.add(size);
        }

        public int findMatch(float width, float height) {
            for (int i = 0; i < mVariants.size(); i++) {
                if (mVariants.get(i).match(width, height)) {
                    return i;
                }
            }
            return -1;
        }
    }

    static class Variant {
        int mId;
        float mMinWidth = Float.NaN;
        float mMinHeight = Float.NaN;
        float mMaxWidth = Float.NaN;
        float mMaxHeight = Float.NaN;
        int mConstraintID = -1;
        ConstraintSet mConstraintSet;

        boolean match(float widthDp, float heightDp) {
            if (DEBUG) {
                Log.v(TAG, "width = " + (int) widthDp
                        + " < " + mMinWidth + " && " + (int) widthDp + " > " + mMaxWidth
                        + " height = " + (int) heightDp
                        + " < " + mMinHeight + " && " + (int) heightDp + " > " + mMaxHeight);
            }
            if (!Float.isNaN(mMinWidth)) {
                if (widthDp < mMinWidth) return false;
            }
            if (!Float.isNaN(mMinHeight)) {
                if (heightDp < mMinHeight) return false;
            }
            if (!Float.isNaN(mMaxWidth)) {
                if (widthDp > mMaxWidth) return false;
            }
            if (!Float.isNaN(mMaxHeight)) {
                if (heightDp > mMaxHeight) return false;
            }
            return true;

        }
    }

    /**
     * Load a constraint set from a constraintSet.xml file
     *
     * @param context    the context for the inflation
     * @param resourceId mId of xml file in res/xml/
     */
    }
