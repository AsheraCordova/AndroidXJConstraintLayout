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
package androidx.constraintlayout.helper.widget;
import r.android.util.Log;
import r.android.view.View;
import androidx.constraintlayout.motion.widget.MotionHelper;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintSet;
import java.util.ArrayList;
public class Carousel extends MotionHelper {
  private static final boolean DEBUG=false;
  private static final String TAG="Carousel";
  private Adapter mAdapter=null;
  private final ArrayList<View> mList=new ArrayList<>();
  private int mPreviousIndex=0;
  private int mIndex=0;
  private MotionLayout mMotionLayout;
  private int firstViewReference=-1;
  private boolean infiniteCarousel=false;
  private int backwardTransition=-1;
  private int forwardTransition=-1;
  private int previousState=-1;
  private int nextState=-1;
  private float dampening=0.9f;
  private int startIndex=0;
  private int emptyViewBehavior=INVISIBLE;
  public static final int TOUCH_UP_IMMEDIATE_STOP=1;
  public static final int TOUCH_UP_CARRY_ON=2;
  private int touchUpMode=TOUCH_UP_IMMEDIATE_STOP;
  private float velocityThreshold=2f;
  private int mTargetIndex=-1;
  private int mAnimateTargetDelay=200;
public interface Adapter {
    int count();
    void populate(    View view,    int index);
    void onNewItem(    int index);
  }
  public void setAdapter(  Adapter adapter){
    mAdapter=adapter;
  }
  public int getCount(){
    if (mAdapter != null) {
      return mAdapter.count();
    }
    return 0;
  }
  public int getCurrentIndex(){
    return mIndex;
  }
  public void transitionToIndex(  int index,  int delay){
    mTargetIndex=Math.max(0,Math.min(getCount() - 1,index));
    mAnimateTargetDelay=Math.max(0,delay);
    mMotionLayout.setTransitionDuration(mAnimateTargetDelay);
    if (index < mIndex) {
      mMotionLayout.transitionToState(previousState,mAnimateTargetDelay);
    }
 else {
      mMotionLayout.transitionToState(nextState,mAnimateTargetDelay);
    }
  }
  public void jumpToIndex(  int index){
    mIndex=Math.max(0,Math.min(getCount() - 1,index));
    refresh();
  }
  public void refresh(){
    final int count=mList.size();
    for (int i=0; i < count; i++) {
      View view=mList.get(i);
      if (mAdapter.count() == 0) {
        updateViewVisibility(view,emptyViewBehavior);
      }
 else {
        updateViewVisibility(view,VISIBLE);
      }
    }
    mMotionLayout.rebuildScene();
    updateItems();mMotionLayout.remeasure();
  }
  public void onTransitionChange(  MotionLayout motionLayout,  int startId,  int endId,  float progress){
    if (DEBUG) {
      System.out.println("onTransitionChange from " + startId + " to "+ endId+ " progress "+ progress);
    }
    mLastStartId=startId;
  }
  int mLastStartId=-1;
  public void onTransitionCompleted(  MotionLayout motionLayout,  int currentId){
    mPreviousIndex=mIndex;
    if (currentId == nextState) {
      mIndex++;
    }
 else     if (currentId == previousState) {
      mIndex--;
    }
    if (infiniteCarousel) {
      if (mIndex >= mAdapter.count()) {
        mIndex=0;
      }
      if (mIndex < 0) {
        mIndex=mAdapter.count() - 1;
      }
    }
 else {
      if (mIndex >= mAdapter.count()) {
        mIndex=mAdapter.count() - 1;
      }
      if (mIndex < 0) {
        mIndex=0;
      }
    }
    if (mPreviousIndex != mIndex) {
      mMotionLayout.post(mUpdateRunnable);
    }
  }
  private void enableAllTransitions(  boolean enable){
    ArrayList<MotionScene.Transition> transitions=mMotionLayout.getDefinedTransitions();
    for (    MotionScene.Transition transition : transitions) {
      transition.setEnabled(enable);
    }
  }
  private boolean enableTransition(  int transitionID,  boolean enable){
    if (transitionID == -1) {
      return false;
    }
    if (mMotionLayout == null) {
      return false;
    }
    MotionScene.Transition transition=mMotionLayout.getTransition(transitionID);
    if (transition == null) {
      return false;
    }
    if (enable == transition.isEnabled()) {
      return false;
    }
    transition.setEnabled(enable);
    return true;
  }
  Runnable mUpdateRunnable=new Runnable(){
    public void run(){
      mMotionLayout.setRedraw(false);mMotionLayout.setProgress(0);
      updateItems();mMotionLayout.remeasure();
      mAdapter.onNewItem(mIndex);mMotionLayout.setRedraw(true);
      float velocity=mMotionLayout.getVelocity();
      if (touchUpMode == TOUCH_UP_CARRY_ON && velocity > velocityThreshold && mIndex < mAdapter.count() - 1) {
        final float v=velocity * dampening;
        if (mIndex == 0 && mPreviousIndex > mIndex) {
          return;
        }
        if (mIndex == mAdapter.count() - 1 && mPreviousIndex < mIndex) {
          return;
        }
        mMotionLayout.post(new Runnable(){
          public void run(){
            mMotionLayout.touchAnimateTo(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE,1,v);
          }
        }
);
      }
    }
  }
;
  public void onAttachedToWindow(){
    super.onAttachedToWindow();
    MotionLayout container=null;
    if (getParent() instanceof MotionLayout) {
      container=(MotionLayout)getParent();
    }
 else {
      return;
    }
    mList.clear();for (int i=0; i < mCount; i++) {
      int id=mIds[i];
      View view=container.getViewById(id);
      if (firstViewReference == id) {
        startIndex=i;
      }
      mList.add(view);
    }
    mMotionLayout=container;
    if (touchUpMode == TOUCH_UP_CARRY_ON) {
      MotionScene.Transition forward=mMotionLayout.getTransition(forwardTransition);
      if (forward != null) {
        forward.setOnTouchUp(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE);
      }
      MotionScene.Transition backward=mMotionLayout.getTransition(backwardTransition);
      if (backward != null) {
        backward.setOnTouchUp(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE);
      }
    }
    updateItems();mMotionLayout.remeasure();
  }
  private boolean updateViewVisibility(  View view,  int visibility){
    if (mMotionLayout == null) {
      return false;
    }
    boolean needsMotionSceneRebuild=false;
    int[] constraintSets=mMotionLayout.getConstraintSetIds();
    for (int i=0; i < constraintSets.length; i++) {
      needsMotionSceneRebuild|=updateViewVisibility(constraintSets[i],view,visibility);
    }
    return needsMotionSceneRebuild;
  }
  private boolean updateViewVisibility(  int constraintSetId,  View view,  int visibility){
    ConstraintSet constraintSet=mMotionLayout.getConstraintSet(constraintSetId);
    if (constraintSet == null) {
      return false;
    }
    ConstraintSet.Constraint constraint=constraintSet.getConstraint(view.getId());
    if (constraint == null) {
      return false;
    }
    constraint.propertySet.mVisibilityMode=ConstraintSet.VISIBILITY_MODE_IGNORE;
    view.setVisibility(visibility);
    return true;
  }
  public void updateItems(){
    if (mAdapter == null) {
      return;
    }
    if (mMotionLayout == null) {
      return;
    }
    if (mAdapter.count() == 0) {
      return;
    }
    if (DEBUG) {
      System.out.println("Update items, index: " + mIndex);
    }
    final int viewCount=mList.size();
    for (int i=0; i < viewCount; i++) {
      View view=mList.get(i);
      int index=mIndex + i - startIndex;
      if (infiniteCarousel) {
        if (index < 0) {
          if (emptyViewBehavior != View.INVISIBLE) {
            updateViewVisibility(view,emptyViewBehavior);
          }
 else {
            updateViewVisibility(view,VISIBLE);
          }
          if (index % mAdapter.count() == 0) {
            mAdapter.populate(view,0);
          }
 else {
            mAdapter.populate(view,mAdapter.count() + (index % mAdapter.count()));
          }
        }
 else         if (index >= mAdapter.count()) {
          if (index == mAdapter.count()) {
            index=0;
          }
 else           if (index > mAdapter.count()) {
            index=index % mAdapter.count();
          }
          if (emptyViewBehavior != View.INVISIBLE) {
            updateViewVisibility(view,emptyViewBehavior);
          }
 else {
            updateViewVisibility(view,VISIBLE);
          }
          mAdapter.populate(view,index);
        }
 else {
          updateViewVisibility(view,VISIBLE);
          mAdapter.populate(view,index);
        }
      }
 else {
        if (index < 0) {
          updateViewVisibility(view,emptyViewBehavior);
        }
 else         if (index >= mAdapter.count()) {
          updateViewVisibility(view,emptyViewBehavior);
        }
 else {
          updateViewVisibility(view,VISIBLE);
          mAdapter.populate(view,index);
        }
      }
    }
    if (mTargetIndex != -1 && mTargetIndex != mIndex) {
      mMotionLayout.post(() -> {
        mMotionLayout.setTransitionDuration(mAnimateTargetDelay);
        if (mTargetIndex < mIndex) {
          mMotionLayout.transitionToState(previousState,mAnimateTargetDelay);
        }
 else {
          mMotionLayout.transitionToState(nextState,mAnimateTargetDelay);
        }
      }
);
    }
 else     if (mTargetIndex == mIndex) {
      mTargetIndex=-1;
    }
    if (backwardTransition == -1 || forwardTransition == -1) {
      Log.w(TAG,"No backward or forward transitions defined for Carousel!");
      return;
    }
    if (infiniteCarousel) {
      return;
    }
    final int count=mAdapter.count();
    if (mIndex == 0) {
      enableTransition(backwardTransition,false);
    }
 else {
      enableTransition(backwardTransition,true);
      mMotionLayout.setTransition(backwardTransition);
    }
    if (mIndex == count - 1) {
      enableTransition(forwardTransition,false);
    }
 else {
      enableTransition(forwardTransition,true);
      mMotionLayout.setTransition(forwardTransition);
    }
  }
  public int getBackwardTransition(){
    return backwardTransition;
  }
  public void setBackwardTransition(  int backwardTransition){
    this.backwardTransition=backwardTransition;
  }
  public int getForwardTransition(){
    return forwardTransition;
  }
  public void setForwardTransition(  int forwardTransition){
    this.forwardTransition=forwardTransition;
  }
  public int getPreviousState(){
    return previousState;
  }
  public void setPreviousState(  int previousState){
    this.previousState=previousState;
  }
  public int getNextState(){
    return nextState;
  }
  public void setNextState(  int nextState){
    this.nextState=nextState;
  }
  public float getDampening(){
    return dampening;
  }
  public void setDampening(  float dampening){
    this.dampening=dampening;
  }
  public int getEmptyViewBehavior(){
    return emptyViewBehavior;
  }
  public void setEmptyViewBehavior(  int emptyViewBehavior){
    this.emptyViewBehavior=emptyViewBehavior;
  }
  public int getTouchUpMode(){
    return touchUpMode;
  }
  public void setTouchUpMode(  int touchUpMode){
    this.touchUpMode=touchUpMode;
  }
  public float getVelocityThreshold(){
    return velocityThreshold;
  }
  public void setVelocityThreshold(  float velocityThreshold){
    this.velocityThreshold=velocityThreshold;
  }
  public int getFirstViewReference(){
    return firstViewReference;
  }
  public void setFirstViewReference(  int firstViewReference){
    this.firstViewReference=firstViewReference;
  }
  public boolean isInfiniteCarousel(){
    return infiniteCarousel;
  }
  public void setInfiniteCarousel(  boolean infiniteCarousel){
    this.infiniteCarousel=infiniteCarousel;
  }
}
