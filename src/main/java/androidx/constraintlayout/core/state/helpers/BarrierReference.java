/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.constraintlayout.core.state.helpers;

import androidx.constraintlayout.core.state.ConstraintReference;
import androidx.constraintlayout.core.widgets.CoreBarrier;
import androidx.constraintlayout.core.widgets.HelperWidget;
import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;

public class BarrierReference extends HelperReference {

    private State.Direction mDirection;
    private int mMargin;
    private CoreBarrier mBarrierWidget;

    public BarrierReference(State state) {
        super(state, State.Helper.BARRIER);
    }

    public void setBarrierDirection(State.Direction barrierDirection) {
        mDirection = barrierDirection;
    }

    @Override
    public ConstraintReference margin(Object value) {
        margin(mState.convertDimension(value));
        return this;
    }

    public ConstraintReference margin(int value) {
        mMargin = value;
        return this;
    }

    @Override
    public HelperWidget getHelperWidget() {
        if (mBarrierWidget == null) {
            mBarrierWidget = new CoreBarrier();
        }
        return mBarrierWidget;
    }

    public void apply() {
        getHelperWidget();
        int direction = CoreBarrier.LEFT;
        switch (mDirection) {
            case LEFT:
            case START: {
                // TODO: handle RTL
            } break;
            case RIGHT:
            case END: {
                // TODO: handle RTL
                direction = CoreBarrier.RIGHT;
            } break;
            case TOP: {
                direction = CoreBarrier.TOP;
            } break;
            case BOTTOM: {
                direction = CoreBarrier.BOTTOM;
            }
        }
        mBarrierWidget.setBarrierType(direction);
        mBarrierWidget.setMargin(mMargin);
    }
}
