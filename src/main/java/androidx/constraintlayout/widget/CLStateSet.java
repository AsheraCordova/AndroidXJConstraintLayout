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

public class CLStateSet {
    public static final String TAG = "ConstraintLayoutStates";
    private static final boolean DEBUG = false;
    int mDefaultState = -1;

    int mCurrentStateId = -1; // default
    int mCurrentConstraintNumber = -1; // default
    private SparseArray<State> mStateList = new SparseArray<>();
    @SuppressWarnings("unused")
    private ConstraintsChangedListener mConstraintsChangedListener = null;

    /**
     * Parse a CLStateSet
     * @param context
     * @param parser
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
     * listen for changes in constraintSet
     * @param constraintsChangedListener
     */
    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
    }

    /**
     * Get the constraint id for a state
     * @param id
     * @param width
     * @param height
     * @return
     */
    public int stateGetConstraintID(int id, int width, int height) {
        return updateConstraints(-1, id, width, height);
    }

    /**
     * converts a state to a constraintSet
     *
     * @param currentConstrainSettId
     * @param stateId
     * @param width
     * @param height
     * @return
     */
    public int convertToConstraintSet(int currentConstrainSettId,
                                      int stateId,
                                      float width,
                                      float height) {
        State state = mStateList.get(stateId);
        if (state == null) {
            return stateId;
        }
        if (width == -1 || height == -1) {            // for the case without width/height matching
            if (state.mConstraintID == currentConstrainSettId) {
                return currentConstrainSettId;
            }
            for (Variant mVariant : state.mVariants) {
                if (currentConstrainSettId == mVariant.mConstraintID) {
                    return currentConstrainSettId;
                }
            }
            return state.mConstraintID;
        } else {
            Variant match = null;
            for (Variant mVariant : state.mVariants) {
                if (mVariant.match(width, height)) {
                    if (currentConstrainSettId == mVariant.mConstraintID) {
                        return currentConstrainSettId;
                    }
                    match = mVariant;
                }
            }
            if (match != null) {
                return match.mConstraintID;
            }

            return state.mConstraintID;
        }
    }

    /**
     * Update the Constraints
     * @param currentId
     * @param id
     * @param width
     * @param height
     * @return
     */
    public int updateConstraints(int currentId, int id, float width, float height) {
        if (currentId == id) {
            State state;
            if (id == -1) {
                state = mStateList.valueAt(0); // id not being used take the first
            } else {
                state = mStateList.get(mCurrentStateId);

            }
            if (state == null) {
                return -1;
            }
            if (mCurrentConstraintNumber != -1) {
                if (state.mVariants.get(currentId).match(width, height)) {
                    return currentId;
                }
            }
            int match = state.findMatch(width, height);
            if (currentId == match) {
                return currentId;
            }

            return (match == -1) ? state.mConstraintID : state.mVariants.get(match).mConstraintID;

        } else  {
            State state = mStateList.get(id);
            if (state == null) {
                return  -1;
            }
            int match = state.findMatch(width, height);
            return (match == -1) ? state.mConstraintID :  state.mVariants.get(match).mConstraintID;
        }

    }

    /////////////////////////////////////////////////////////////////////////
    //      This represents one state
    /////////////////////////////////////////////////////////////////////////
    static class State {
        int mId;
        ArrayList<Variant> mVariants = new ArrayList<>();
        int mConstraintID = -1;
        boolean mIsLayout = false;

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
        boolean mIsLayout = false;

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

}
