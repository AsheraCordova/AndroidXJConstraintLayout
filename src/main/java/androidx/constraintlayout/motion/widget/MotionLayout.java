package androidx.constraintlayout.motion.widget;
import r.android.graphics.Rect;
import r.android.os.Build;
import r.android.os.Bundle;
import r.android.util.Log;
import r.android.util.SparseArray;
import r.android.util.SparseBooleanArray;
import r.android.util.SparseIntArray;
import r.android.view.MotionEvent;
import r.android.view.View;
import r.android.view.ViewGroup;
import r.android.view.animation.Interpolator;
import r.android.widget.TextView;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets. CoreFlow;
import androidx.constraintlayout.core.widgets.Helper;
import androidx.constraintlayout.motion.utils.StopLogic;
import androidx.constraintlayout.core.motion.utils.ViewState;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import static r.android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.motion.widget.MotionScene.Transition.TRANSITION_FLAG_FIRST_DRAW;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
import static androidx.constraintlayout.widget.ConstraintSet.UNSET;
public class MotionLayout extends ConstraintLayout {
  public static final int TOUCH_UP_COMPLETE=0;
  public static final int TOUCH_UP_COMPLETE_TO_START=1;
  public static final int TOUCH_UP_COMPLETE_TO_END=2;
  public static final int TOUCH_UP_STOP=3;
  public static final int TOUCH_UP_DECELERATE=4;
  public static final int TOUCH_UP_DECELERATE_AND_COMPLETE=5;
  public static final int TOUCH_UP_NEVER_TO_START=6;
  public static final int TOUCH_UP_NEVER_TO_END=7;
  static final String TAG="MotionLayout";
  private final static boolean DEBUG=false;
  public static boolean IS_IN_EDIT_MODE;
  MotionScene mScene;
  Interpolator mInterpolator;
  Interpolator mProgressInterpolator=null;
  float mLastVelocity=0;
  private int mBeginState=UNSET;
  int mCurrentState=UNSET;
  private int mEndState=UNSET;
  private int mLastWidthMeasureSpec=0;
  private int mLastHeightMeasureSpec=0;
  private boolean mInteractionEnabled=true;
  HashMap<View,MotionController> mFrameArrayList=new HashMap<>();
  private long mAnimationStartTime=0;
  private float mTransitionDuration=1f;
  float mTransitionPosition=0.0f;
  float mTransitionLastPosition=0.0f;
  private long mTransitionLastTime;
  float mTransitionGoalPosition=0.0f;
  private boolean mTransitionInstantly;
  boolean mInTransition=false;
  boolean mIndirectTransition=false;
  private TransitionListener mTransitionListener;
  private float lastPos;
  private float lastY;
  public static final int DEBUG_SHOW_NONE=0;
  public static final int DEBUG_SHOW_PROGRESS=1;
  public static final int DEBUG_SHOW_PATH=2;
  int mDebugPath=0;
  static final int MAX_KEY_FRAMES=50;
  private boolean mTemporalInterpolator=false;
  private StopLogic mStopLogic=new StopLogic();
  private DecelerateInterpolator mDecelerateLogic=new DecelerateInterpolator();
  boolean firstDown=true;
  int mOldWidth;
  int mOldHeight;
  int mLastLayoutWidth;
  int mLastLayoutHeight;
  boolean mUndergoingMotion=false;
  float mScrollTargetDX;
  float mScrollTargetDY;
  float mScrollTargetDT;
  private boolean mKeepAnimating=false;
  private ArrayList<MotionHelper> mOnShowHelpers=null;
  private ArrayList<MotionHelper> mOnHideHelpers=null;
  private ArrayList<MotionHelper> mDecoratorsHelpers=null;
  private CopyOnWriteArrayList<TransitionListener> mTransitionListeners=null;
  private int mFrames=0;
  private float mLastFps=0;
  private int mListenerState=0;
  private float mListenerPosition=0.0f;
  boolean mIsAnimating=false;
  public final static int VELOCITY_POST_LAYOUT=0;
  public final static int VELOCITY_LAYOUT=1;
  public final static int VELOCITY_STATIC_POST_LAYOUT=2;
  public final static int VELOCITY_STATIC_LAYOUT=3;
  protected boolean mMeasureDuringTransition=false;
  int mStartWrapWidth;
  int mStartWrapHeight;
  int mEndWrapWidth;
  int mEndWrapHeight;
  int mWidthMeasureMode;
  int mHeightMeasureMode;
  float mPostInterpolationPosition;
  private KeyCache mKeyCache=new KeyCache();
  private boolean mInLayout=false;
  private StateCache mStateCache;
  private Runnable mOnComplete=null;
  private int[] mScheduledTransitionTo=null;
  int mScheduledTransitions=0;
  private boolean mInRotation=false;
  int mRotatMode=0;
  HashMap<View,ViewState> mPreRotate=new HashMap<>();
  private int mPreRotateWidth;
  private int mPreRotateHeight;
  private int mPreviouseRotation;
  Rect mTempRect=new Rect();
  private boolean mDelayedApply=false;
  MotionController getMotionController(  int mTouchAnchorId){
    return mFrameArrayList.get(findViewById(mTouchAnchorId));
  }
  enum TransitionState {  UNDEFINED,   SETUP,   MOVING,   FINISHED}
  TransitionState mTransitionState=TransitionState.UNDEFINED;
  private static final float EPSILON=0.00001f;
  protected long getNanoTime(){
    return System.nanoTime();
  }
protected interface MotionTracker {
    public void recycle();
    public void clear();
    public void addMovement(    MotionEvent event);
    public void computeCurrentVelocity(    int units);
    public void computeCurrentVelocity(    int units,    float maxVelocity);
    public float getXVelocity();
    public float getYVelocity();
    public float getXVelocity(    int id);
    public float getYVelocity(    int id);
  }
  void setState(  TransitionState newState){
    if (DEBUG) {
      CLDebug.logStack(TAG,mTransitionState + " -> " + newState+ " "+ CLDebug.getName(getContext(),mCurrentState),2);
    }
    if (newState == TransitionState.FINISHED && mCurrentState == UNSET) {
      return;
    }
    TransitionState oldState=mTransitionState;
    mTransitionState=newState;
    if (oldState == TransitionState.MOVING && newState == TransitionState.MOVING) {
      fireTransitionChange();
    }
switch (oldState) {
case UNDEFINED:
case SETUP:
      if (newState == TransitionState.MOVING) {
        fireTransitionChange();
      }
    if (newState == TransitionState.FINISHED) {
      fireTransitionCompleted();
    }
  break;
case MOVING:
if (newState == TransitionState.FINISHED) {
  fireTransitionCompleted();
}
break;
case FINISHED:
break;
}
}
public void setTransition(int beginId,int endId){
if (!isAttachedToWindow()) {
if (mStateCache == null) {
mStateCache=new StateCache();
}
mStateCache.setStartState(beginId);
mStateCache.setEndState(endId);
return;
}
if (mScene != null) {
mBeginState=beginId;
mEndState=endId;
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " setTransition " + CLDebug.getName(getContext(),beginId)+ " -> "+ CLDebug.getName(getContext(),endId));
}
mScene.setTransition(beginId,endId);
mModel.initFrom(mLayoutWidget,mScene.getConstraintSet(beginId),mScene.getConstraintSet(endId));
rebuildScene();
mTransitionLastPosition=0;
transitionToStart();
}
}
public void setTransition(int transitionId){
if (mScene != null) {
MotionScene.Transition transition=getTransition(transitionId);
int current=mCurrentState;
mBeginState=transition.getStartConstraintSetId();
mEndState=transition.getEndConstraintSetId();
if (!isAttachedToWindow()) {
if (mStateCache == null) {
mStateCache=new StateCache();
}
mStateCache.setStartState(mBeginState);
mStateCache.setEndState(mEndState);
return;
}
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " setTransition " + CLDebug.getName(getContext(),mBeginState)+ " -> "+ CLDebug.getName(getContext(),mEndState)+ "   current="+ CLDebug.getName(getContext(),mCurrentState));
}
float pos=Float.NaN;
if (mCurrentState == mBeginState) {
pos=0;
}
 else if (mCurrentState == mEndState) {
pos=1;
}
mScene.setTransition(transition);
mModel.initFrom(mLayoutWidget,mScene.getConstraintSet(mBeginState),mScene.getConstraintSet(mEndState));
rebuildScene();
if (mTransitionLastPosition != pos) {
if (pos == 0) {
endTrigger(true);
mScene.getConstraintSet(mBeginState).applyTo(this);
}
 else if (pos == 1) {
endTrigger(false);
mScene.getConstraintSet(mEndState).applyTo(this);
}
}
mTransitionLastPosition=Float.isNaN(pos) ? 0 : pos;
if (Float.isNaN(pos)) {
Log.v(TAG,CLDebug.getLocation() + " transitionToStart ");
transitionToStart();
}
 else {
setProgress(pos);
}
}
}
protected void setTransition(MotionScene.Transition transition){
mScene.setTransition(transition);
setState(TransitionState.SETUP);
if (mCurrentState == mScene.getEndId()) {
mTransitionLastPosition=1.0f;
mTransitionPosition=1.0f;
mTransitionGoalPosition=1;
}
 else {
mTransitionLastPosition=0;
mTransitionPosition=0f;
mTransitionGoalPosition=0;
}
mTransitionLastTime=(transition.isTransitionFlag(TRANSITION_FLAG_FIRST_DRAW)) ? -1 : getNanoTime();
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + "  new mTransitionLastPosition = " + mTransitionLastPosition+ "");
Log.v(TAG,CLDebug.getLocation() + " setTransition was " + CLDebug.getName(getContext(),mBeginState)+ " -> "+ CLDebug.getName(getContext(),mEndState));
}
int newBeginState=mScene.getStartId();
int newEndState=mScene.getEndId();
if (newBeginState == mBeginState && newEndState == mEndState) {
return;
}
mBeginState=newBeginState;
mEndState=newEndState;
mScene.setTransition(mBeginState,mEndState);
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " setTransition now " + CLDebug.getName(getContext(),mBeginState)+ " -> "+ CLDebug.getName(getContext(),mEndState));
}
mModel.initFrom(mLayoutWidget,mScene.getConstraintSet(mBeginState),mScene.getConstraintSet(mEndState));
mModel.setMeasuredId(mBeginState,mEndState);
mModel.reEvaluateState();
rebuildScene();
}
public void setState(int id,int screenWidth,int screenHeight){
setState(TransitionState.SETUP);
mCurrentState=id;
mBeginState=UNSET;
mEndState=UNSET;
if (mConstraintLayoutSpec != null) {
mConstraintLayoutSpec.updateConstraints(id,screenWidth,screenHeight);
}
 else if (mScene != null) {
mScene.getConstraintSet(id).applyTo(this);
}
}
public void setProgress(float pos,float velocity){
if (!isAttachedToWindow()) {
if (mStateCache == null) {
mStateCache=new StateCache();
}
mStateCache.setProgress(pos);
mStateCache.setVelocity(velocity);
return;
}
setProgress(pos);
setState(TransitionState.MOVING);
mLastVelocity=velocity;
animateTo(1);
}
class StateCache {
float mProgress=Float.NaN;
float mVelocity=Float.NaN;
int startState=UNSET;
int endState=UNSET;
final String KeyProgress="motion.progress";
final String KeyVelocity="motion.velocity";
final String KeyStartState="motion.StartState";
final String KeyEndState="motion.EndState";
void apply(){
if (this.startState != UNSET || this.endState != UNSET) {
if (this.startState == UNSET) {
transitionToState(endState);
}
 else if (this.endState == UNSET) {
setState(this.startState,-1,-1);
}
 else {
setTransition(startState,endState);
}
setState(TransitionState.SETUP);
}
if (Float.isNaN(this.mVelocity)) {
if (Float.isNaN(this.mProgress)) {
return;
}
MotionLayout.this.setProgress(this.mProgress);
return;
}
MotionLayout.this.setProgress(this.mProgress,mVelocity);
this.mProgress=Float.NaN;
this.mVelocity=Float.NaN;
this.startState=UNSET;
this.endState=UNSET;
}
public Bundle getTransitionState(){
Bundle bundle=new Bundle();
bundle.putFloat(KeyProgress,this.mProgress);
bundle.putFloat(KeyVelocity,this.mVelocity);
bundle.putInt(KeyStartState,this.startState);
bundle.putInt(KeyEndState,this.endState);
return bundle;
}
public void setTransitionState(Bundle bundle){
this.mProgress=bundle.getFloat(KeyProgress);
this.mVelocity=bundle.getFloat(KeyVelocity);
this.startState=bundle.getInt(KeyStartState);
this.endState=bundle.getInt(KeyEndState);
}
public void setProgress(float progress){
this.mProgress=progress;
}
public void setEndState(int endState){
this.endState=endState;
}
public void setVelocity(float mVelocity){
this.mVelocity=mVelocity;
}
public void setStartState(int startState){
this.startState=startState;
}
public void recordState(){
endState=MotionLayout.this.mEndState;
startState=MotionLayout.this.mBeginState;
mVelocity=MotionLayout.this.getVelocity();
mProgress=MotionLayout.this.getProgress();
}
}
public void setProgress(float pos){
if (pos < 0.0f || pos > 1.0f) {
Log.w(TAG,"Warning! Progress is defined for values between 0.0 and 1.0 inclusive");
}
if (!isAttachedToWindow()) {
if (mStateCache == null) {
mStateCache=new StateCache();
}
mStateCache.setProgress(pos);
return;
}
if (DEBUG) {
String str=getContext().getResources().getResourceName(mBeginState) + " -> ";
str+=getContext().getResources().getResourceName(mEndState) + ":" + getProgress();
Log.v(TAG,CLDebug.getLocation() + " > " + str);
CLDebug.logStack(TAG," Progress = " + pos,3);
}
if (pos <= 0f) {
if (mTransitionLastPosition == 1.0f && mCurrentState == mEndState) {
setState(TransitionState.MOVING);
}
mCurrentState=mBeginState;
if (mTransitionLastPosition == 0.0f) {
setState(TransitionState.FINISHED);
}
}
 else if (pos >= 1.0f) {
if (mTransitionLastPosition == 0.0f && mCurrentState == mBeginState) {
setState(TransitionState.MOVING);
}
mCurrentState=mEndState;
if (mTransitionLastPosition == 1.0f) {
setState(TransitionState.FINISHED);
}
}
 else {
mCurrentState=UNSET;
setState(TransitionState.MOVING);
}
if (mScene == null) {
return;
}
mTransitionInstantly=true;
mTransitionGoalPosition=pos;
mTransitionPosition=pos;
mTransitionLastTime=-1;
mAnimationStartTime=-1;
mInterpolator=null;
mInTransition=true;
invalidate();
}
private void setupMotionViews(){
int n=getChildCount();
mModel.build();
mInTransition=true;
SparseArray<MotionController> controllers=new SparseArray<>();
for (int i=0; i < n; i++) {
View child=getChildAt(i);
controllers.put(child.getId(),mFrameArrayList.get(child));
}
int layoutWidth=getWidth();
int layoutHeight=getHeight();
int arc=mScene.gatPathMotionArc();
if (arc != UNSET) {
for (int i=0; i < n; i++) {
MotionController motionController=mFrameArrayList.get(getChildAt(i));
if (motionController != null) {
motionController.setPathMotionArc(arc);
}
}
}
SparseBooleanArray sparseBooleanArray=new SparseBooleanArray();
int[] depends=new int[mFrameArrayList.size()];
int count=0;
for (int i=0; i < n; i++) {
View view=getChildAt(i);
MotionController motionController=mFrameArrayList.get(view);
if (motionController.getAnimateRelativeTo() != UNSET) {
sparseBooleanArray.put(motionController.getAnimateRelativeTo(),true);
depends[count++]=motionController.getAnimateRelativeTo();
}
}
if (mDecoratorsHelpers != null) {
for (int i=0; i < count; i++) {
MotionController motionController=mFrameArrayList.get(findViewById(depends[i]));
if (motionController == null) {
continue;
}
mScene.getKeyFrames(motionController);
}
for (MotionHelper mDecoratorsHelper : mDecoratorsHelpers) {
mDecoratorsHelper.onPreSetup(this,mFrameArrayList);
}
for (int i=0; i < count; i++) {
MotionController motionController=mFrameArrayList.get(findViewById(depends[i]));
if (motionController == null) {
continue;
}
motionController.setup(layoutWidth,layoutHeight,mTransitionDuration,getNanoTime());
}
}
 else {
for (int i=0; i < count; i++) {
MotionController motionController=mFrameArrayList.get(findViewById(depends[i]));
if (motionController == null) {
continue;
}
mScene.getKeyFrames(motionController);
motionController.setup(layoutWidth,layoutHeight,mTransitionDuration,getNanoTime());
}
}
for (int i=0; i < n; i++) {
View v=getChildAt(i);
MotionController motionController=mFrameArrayList.get(v);
if (sparseBooleanArray.get(v.getId())) {
continue;
}
if (motionController != null) {
mScene.getKeyFrames(motionController);
motionController.setup(layoutWidth,layoutHeight,mTransitionDuration,getNanoTime());
}
}
float stagger=mScene.getStaggered();
if (stagger != 0.0f) {
boolean flip=stagger < 0.0;
boolean useMotionStagger=false;
stagger=Math.abs(stagger);
float min=Float.MAX_VALUE, max=-Float.MAX_VALUE;
for (int i=0; i < n; i++) {
MotionController f=mFrameArrayList.get(getChildAt(i));
if (!Float.isNaN(f.mMotionStagger)) {
useMotionStagger=true;
break;
}
float x=f.getFinalX();
float y=f.getFinalY();
float mdist=(flip) ? (y - x) : (y + x);
min=Math.min(min,mdist);
max=Math.max(max,mdist);
}
if (useMotionStagger) {
min=Float.MAX_VALUE;
max=-Float.MAX_VALUE;
for (int i=0; i < n; i++) {
MotionController f=mFrameArrayList.get(getChildAt(i));
if (!Float.isNaN(f.mMotionStagger)) {
min=Math.min(min,f.mMotionStagger);
max=Math.max(max,f.mMotionStagger);
}
}
for (int i=0; i < n; i++) {
MotionController f=mFrameArrayList.get(getChildAt(i));
if (!Float.isNaN(f.mMotionStagger)) {
f.mStaggerScale=1 / (1 - stagger);
if (flip) {
  f.mStaggerOffset=stagger - stagger * ((max - f.mMotionStagger) / (max - min));
}
 else {
  f.mStaggerOffset=stagger - stagger * (f.mMotionStagger - min) / (max - min);
}
}
}
}
 else {
for (int i=0; i < n; i++) {
MotionController f=mFrameArrayList.get(getChildAt(i));
float x=f.getFinalX();
float y=f.getFinalY();
float mdist=(flip) ? (y - x) : (y + x);
f.mStaggerScale=1 / (1 - stagger);
f.mStaggerOffset=stagger - stagger * (mdist - (min)) / (max - (min));
}
}
}
}
public void touchAnimateTo(int touchUpMode,float position,float currentVelocity){
if (DEBUG) {
Log.v(TAG," " + CLDebug.getLocation() + " touchAnimateTo "+ position+ "   "+ currentVelocity);
}
if (mScene == null) {
return;
}
if (mTransitionLastPosition == position) {
return;
}
mTemporalInterpolator=true;
mAnimationStartTime=getNanoTime();
mTransitionDuration=mScene.getDuration() / 1000f;
mTransitionGoalPosition=position;
mInTransition=true;
switch (touchUpMode) {
case TOUCH_UP_COMPLETE:
case TOUCH_UP_NEVER_TO_START:
case TOUCH_UP_NEVER_TO_END:
case TOUCH_UP_COMPLETE_TO_START:
case TOUCH_UP_COMPLETE_TO_END:
{
if (touchUpMode == TOUCH_UP_COMPLETE_TO_START || touchUpMode == TOUCH_UP_NEVER_TO_END) {
position=0;
}
 else if (touchUpMode == TOUCH_UP_COMPLETE_TO_END || touchUpMode == TOUCH_UP_NEVER_TO_START) {
position=1;
}
if (mScene.getAutoCompleteMode() == TouchResponse.COMPLETE_MODE_CONTINUOUS_VELOCITY) {
mStopLogic.config(mTransitionLastPosition,position,currentVelocity,mTransitionDuration,mScene.getMaxAcceleration(),mScene.getMaxVelocity());
}
 else {
mStopLogic.springConfig(mTransitionLastPosition,position,currentVelocity,mScene.getSpringMass(),mScene.getSpringStiffiness(),mScene.getSpringDamping(),mScene.getSpringStopThreshold(),mScene.getSpringBoundary());
}
int currentState=mCurrentState;
mTransitionGoalPosition=position;
mCurrentState=currentState;
mInterpolator=mStopLogic;
}
break;
case TOUCH_UP_STOP:
{
}
break;
case TOUCH_UP_DECELERATE:
{
mDecelerateLogic.config(currentVelocity,mTransitionLastPosition,mScene.getMaxAcceleration());
mInterpolator=mDecelerateLogic;
}
break;
case TOUCH_UP_DECELERATE_AND_COMPLETE:
{
if (willJump(currentVelocity,mTransitionLastPosition,mScene.getMaxAcceleration())) {
mDecelerateLogic.config(currentVelocity,mTransitionLastPosition,mScene.getMaxAcceleration());
mInterpolator=mDecelerateLogic;
}
 else {
mStopLogic.config(mTransitionLastPosition,position,currentVelocity,mTransitionDuration,mScene.getMaxAcceleration(),mScene.getMaxVelocity());
mLastVelocity=0;
int currentState=mCurrentState;
mTransitionGoalPosition=position;
mCurrentState=currentState;
mInterpolator=mStopLogic;
}
}
break;
}
mTransitionInstantly=false;
mAnimationStartTime=getNanoTime();
invalidate();
}
private static boolean willJump(float velocity,float position,float maxAcceleration){
if (velocity > 0) {
float time=velocity / maxAcceleration;
float pos=velocity * time - (maxAcceleration * time * time) / 2;
return (position + pos > 1);
}
 else {
float time=-velocity / maxAcceleration;
float pos=velocity * time + (maxAcceleration * time * time) / 2;
return (position + pos < 0);
}
}
class DecelerateInterpolator extends MotionInterpolator {
float initalV=0;
float currentP=0;
float maxA;
public void config(float velocity,float position,float maxAcceleration){
initalV=velocity;
currentP=position;
maxA=maxAcceleration;
}
public float getInterpolation(float time){
if (initalV > 0) {
if (initalV / maxA < time) {
time=initalV / maxA;
}
mLastVelocity=initalV - maxA * time;
float pos=initalV * time - (maxA * time * time) / 2;
return pos + currentP;
}
 else {
if (-initalV / maxA < time) {
time=-initalV / maxA;
}
mLastVelocity=initalV + maxA * time;
float pos=initalV * time + (maxA * time * time) / 2;
return pos + currentP;
}
}
public float getVelocity(){
return mLastVelocity;
}
}
void animateTo(float position){
if (DEBUG) {
Log.v(TAG," " + CLDebug.getLocation() + " ... animateTo("+ position+ ") last:"+ mTransitionLastPosition);
}
if (mScene == null) {
return;
}
if (mTransitionLastPosition != mTransitionPosition && mTransitionInstantly) {
mTransitionLastPosition=mTransitionPosition;
}
if (mTransitionLastPosition == position) {
return;
}
mTemporalInterpolator=false;
float currentPosition=mTransitionLastPosition;
mTransitionGoalPosition=position;
mTransitionDuration=mScene.getDuration() / 1000f;
setProgress(mTransitionGoalPosition);
mInterpolator=null;
mProgressInterpolator=mScene.getInterpolator();
mTransitionInstantly=false;
mAnimationStartTime=getNanoTime();
mInTransition=true;
mTransitionPosition=currentPosition;
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " mTransitionLastPosition = " + mTransitionLastPosition+ " currentPosition ="+ currentPosition);
}
mTransitionLastPosition=currentPosition;
invalidate();
}
private void computeCurrentPositions(){
final int n=getChildCount();
for (int i=0; i < n; i++) {
View v=getChildAt(i);
MotionController frame=mFrameArrayList.get(v);
if (frame == null) {
continue;
}
frame.setStartCurrentState(v);
}
}
public void transitionToStart(){
animateTo(0.0f);
}
public void transitionToEnd(){
animateTo(1.0f);
mOnComplete=null;
}
public void transitionToEnd(Runnable onComplete){
animateTo(1.0f);
mOnComplete=onComplete;
}
public void transitionToState(int id){
if (!isAttachedToWindow()) {
if (mStateCache == null) {
mStateCache=new StateCache();
}
mStateCache.setEndState(id);
return;
}
transitionToState(id,-1,-1);
}
public void transitionToState(int id,int duration){
if (!isAttachedToWindow()) {
if (mStateCache == null) {
mStateCache=new StateCache();
}
mStateCache.setEndState(id);
return;
}
transitionToState(id,-1,-1,duration);
}
public void transitionToState(int id,int screenWidth,int screenHeight){
transitionToState(id,screenWidth,screenHeight,-1);
}
public void transitionToState(int id,int screenWidth,int screenHeight,int duration){
if (DEBUG && mScene.mStateSet == null) {
Log.v(TAG,CLDebug.getLocation() + " mStateSet = null");
}
if (mScene != null && mScene.mStateSet != null) {
int tmp_id=mScene.mStateSet.convertToConstraintSet(mCurrentState,id,screenWidth,screenHeight);
if (tmp_id != -1) {
if (DEBUG) {
Log.v(TAG," got state  " + CLDebug.getLocation() + " lookup("+ CLDebug.getName(getContext(),id)+ screenWidth+ " , "+ screenHeight+ " ) =  "+ CLDebug.getName(getContext(),tmp_id));
}
id=tmp_id;
}
}
if (mCurrentState == id) {
return;
}
if (mBeginState == id) {
animateTo(0.0f);
if (duration > 0) {
mTransitionDuration=duration / 1000f;
}
return;
}
if (mEndState == id) {
animateTo(1.0f);
if (duration > 0) {
mTransitionDuration=duration / 1000f;
}
return;
}
mEndState=id;
if (mCurrentState != UNSET) {
if (DEBUG) {
Log.v(TAG," transitionToState " + CLDebug.getLocation() + " current  = "+ CLDebug.getName(getContext(),mCurrentState)+ " to "+ CLDebug.getName(getContext(),mEndState));
CLDebug.logStack(TAG," transitionToState  ",4);
Log.v(TAG,"-------------------------------------------");
}
setTransition(mCurrentState,id);
animateTo(1.0f);
mTransitionLastPosition=0;
transitionToEnd();
if (duration > 0) {
mTransitionDuration=duration / 1000f;
}
return;
}
if (DEBUG) {
Log.v(TAG,"setTransition  unknown -> " + CLDebug.getName(getContext(),id));
}
mTemporalInterpolator=false;
mTransitionGoalPosition=1;
mTransitionPosition=0;
mTransitionLastPosition=0;
mTransitionLastTime=getNanoTime();
mAnimationStartTime=getNanoTime();
mTransitionInstantly=false;
mInterpolator=null;
if (duration == -1) {
mTransitionDuration=mScene.getDuration() / 1000f;
}
mBeginState=UNSET;
mScene.setTransition(mBeginState,mEndState);
SparseArray<MotionController> controllers=new SparseArray<>();
if (duration == 0) {
mTransitionDuration=mScene.getDuration() / 1000f;
}
 else if (duration > 0) {
mTransitionDuration=duration / 1000f;
}
int n=getChildCount();
mFrameArrayList.clear();
for (int i=0; i < n; i++) {
View v=getChildAt(i);
MotionController f=new MotionController(v);
mFrameArrayList.put(v,f);
controllers.put(v.getId(),mFrameArrayList.get(v));
}
mInTransition=true;
mModel.initFrom(mLayoutWidget,null,mScene.getConstraintSet(id));
rebuildScene();
mModel.build();
computeCurrentPositions();
int layoutWidth=getWidth();
int layoutHeight=getHeight();
if (mDecoratorsHelpers != null) {
for (int i=0; i < n; i++) {
MotionController motionController=mFrameArrayList.get(getChildAt(i));
if (motionController == null) {
continue;
}
mScene.getKeyFrames(motionController);
}
for (MotionHelper mDecoratorsHelper : mDecoratorsHelpers) {
mDecoratorsHelper.onPreSetup(this,mFrameArrayList);
}
for (int i=0; i < n; i++) {
MotionController motionController=mFrameArrayList.get(getChildAt(i));
if (motionController == null) {
continue;
}
motionController.setup(layoutWidth,layoutHeight,mTransitionDuration,getNanoTime());
}
}
 else {
for (int i=0; i < n; i++) {
MotionController motionController=mFrameArrayList.get(getChildAt(i));
if (motionController == null) {
continue;
}
mScene.getKeyFrames(motionController);
motionController.setup(layoutWidth,layoutHeight,mTransitionDuration,getNanoTime());
}
}
float stagger=mScene.getStaggered();
if (stagger != 0.0f) {
float min=Float.MAX_VALUE, max=-Float.MAX_VALUE;
for (int i=0; i < n; i++) {
MotionController f=mFrameArrayList.get(getChildAt(i));
float x=f.getFinalX();
float y=f.getFinalY();
min=Math.min(min,y + x);
max=Math.max(max,y + x);
}
for (int i=0; i < n; i++) {
MotionController f=mFrameArrayList.get(getChildAt(i));
float x=f.getFinalX();
float y=f.getFinalY();
f.mStaggerScale=1 / (1 - stagger);
f.mStaggerOffset=stagger - stagger * (x + y - (min)) / (max - (min));
}
}
mTransitionPosition=0;
mTransitionLastPosition=0;
mInTransition=true;
invalidate();
}
public float getVelocity(){
return mLastVelocity;
}
class Model {
ConstraintWidgetContainer mLayoutStart=new ConstraintWidgetContainer();
ConstraintWidgetContainer mLayoutEnd=new ConstraintWidgetContainer();
ConstraintSet mStart=null;
ConstraintSet mEnd=null;
int mStartId;
int mEndId;
void copy(ConstraintWidgetContainer src,ConstraintWidgetContainer dest){
ArrayList<ConstraintWidget> children=src.getChildren();
HashMap<ConstraintWidget,ConstraintWidget> map=new HashMap<>();
map.put(src,dest);
dest.getChildren().clear();
dest.copy(src,map);
for (ConstraintWidget child_s : children) {
ConstraintWidget child_d;
if (child_s instanceof androidx.constraintlayout.core.widgets.CoreBarrier) {
child_d=new androidx.constraintlayout.core.widgets.CoreBarrier();
}
 else if (child_s instanceof androidx.constraintlayout.core.widgets.CoreGuideline) {
child_d=new androidx.constraintlayout.core.widgets.CoreGuideline();
}
 else if (child_s instanceof  CoreFlow) {
child_d=new  CoreFlow();
}
 else if (child_s instanceof androidx.constraintlayout.core.widgets.Helper) {
child_d=new androidx.constraintlayout.core.widgets.HelperWidget();
}
 else {
child_d=new ConstraintWidget();
}
dest.add(child_d);
map.put(child_s,child_d);
}
for (ConstraintWidget child_s : children) {
map.get(child_s).copy(child_s,map);
}
}
void initFrom(ConstraintWidgetContainer baseLayout,ConstraintSet start,ConstraintSet end){
mStart=start;
mEnd=end;
mLayoutStart=new ConstraintWidgetContainer();
mLayoutEnd=new ConstraintWidgetContainer();
mLayoutStart.setMeasurer(mLayoutWidget.getMeasurer());
mLayoutEnd.setMeasurer(mLayoutWidget.getMeasurer());
mLayoutStart.removeAllChildren();
mLayoutEnd.removeAllChildren();
copy(mLayoutWidget,mLayoutStart);
copy(mLayoutWidget,mLayoutEnd);
if (mTransitionLastPosition > 0.5) {
if (start != null) {
setupConstraintWidget(mLayoutStart,start);
}
setupConstraintWidget(mLayoutEnd,end);
}
 else {
setupConstraintWidget(mLayoutEnd,end);
if (start != null) {
setupConstraintWidget(mLayoutStart,start);
}
}
if (DEBUG) {
Log.v(TAG,"> mLayoutStart.updateHierarchy " + CLDebug.getLocation());
}
mLayoutStart.setRtl(isRtl());
mLayoutStart.updateHierarchy();
if (DEBUG) {
for (ConstraintWidget child : mLayoutStart.getChildren()) {
View view=(View)child.getCompanionWidget();
debugWidget(">>>>>>>  " + CLDebug.getName(view),child);
}
Log.v(TAG,"> mLayoutEnd.updateHierarchy " + CLDebug.getLocation());
Log.v(TAG,"> mLayoutEnd.updateHierarchy  " + CLDebug.getLocation() + "  == isRtl()="+ isRtl());
}
mLayoutEnd.setRtl(isRtl());
mLayoutEnd.updateHierarchy();
if (DEBUG) {
for (ConstraintWidget child : mLayoutEnd.getChildren()) {
View view=(View)child.getCompanionWidget();
debugWidget(">>>>>>>  " + CLDebug.getName(view),child);
}
}
ViewGroup.LayoutParams layoutParams=getLayoutParams();
if (layoutParams != null) {
if (layoutParams.width == WRAP_CONTENT) {
mLayoutStart.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
mLayoutEnd.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
}
if (layoutParams.height == WRAP_CONTENT) {
mLayoutStart.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
mLayoutEnd.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
}
}
}
private void setupConstraintWidget(ConstraintWidgetContainer base,ConstraintSet cSet){
SparseArray<ConstraintWidget> mapIdToWidget=new SparseArray<>();
Constraints.LayoutParams layoutParams=new Constraints.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
mapIdToWidget.clear();
mapIdToWidget.put(PARENT_ID,base);
mapIdToWidget.put(getId(),base);
if (cSet != null && cSet.mRotate != 0) {
resolveSystem(mLayoutEnd,getOptimizationLevel(),MeasureSpec.makeMeasureSpec(getHeight(),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(getWidth(),MeasureSpec.EXACTLY));
}
for (ConstraintWidget child : base.getChildren()) {
View view=(View)child.getCompanionWidget();
mapIdToWidget.put(view.getId(),child);
}
for (ConstraintWidget child : base.getChildren()) {
View view=(View)child.getCompanionWidget();
cSet.applyToLayoutParams(view.getId(),layoutParams);
child.setWidth(cSet.getWidth(view.getId()));
child.setHeight(cSet.getHeight(view.getId()));
if (view instanceof ConstraintHelper) {
cSet.applyToHelper((ConstraintHelper)view,child,layoutParams,mapIdToWidget);
if (view instanceof Barrier) {
((Barrier)view).validateParams();
if (DEBUG) {
Log.v(TAG,">>>>>>>>>> Barrier " + (CLDebug.getName(getContext(),((Barrier)view).getReferencedIds())));
}
}
}
if (DEBUG) {
debugLayoutParam(">>>>>>>  " + CLDebug.getName(view),layoutParams);
}
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
layoutParams.resolveLayoutDirection(getLayoutDirection());
}
 else {
layoutParams.resolveLayoutDirection(ViewCompat.LAYOUT_DIRECTION_LTR);
}
applyConstraintsFromLayoutParams(false,view,child,layoutParams,mapIdToWidget);
if (cSet.getVisibilityMode(view.getId()) == ConstraintSet.VISIBILITY_MODE_IGNORE) {
child.setVisibility(view.getVisibility());
}
 else {
child.setVisibility(cSet.getVisibility(view.getId()));
}
}
for (ConstraintWidget child : base.getChildren()) {
if (child instanceof androidx.constraintlayout.core.widgets.CoreVirtualLayout) {
ConstraintHelper view=(ConstraintHelper)child.getCompanionWidget();
Helper helper=(Helper)child;
view.updatePreLayout(base,helper,mapIdToWidget);
androidx.constraintlayout.core.widgets.CoreVirtualLayout virtualLayout=(androidx.constraintlayout.core.widgets.CoreVirtualLayout)helper;
virtualLayout.captureWidgets();
}
}
}
ConstraintWidget getWidget(ConstraintWidgetContainer container,View view){
if (container.getCompanionWidget() == view) {
return container;
}
ArrayList<ConstraintWidget> children=container.getChildren();
final int count=children.size();
for (int i=0; i < count; i++) {
ConstraintWidget widget=children.get(i);
if (widget.getCompanionWidget() == view) {
return widget;
}
}
return null;
}
private void debugLayoutParam(String str,LayoutParams params){
String a=" ";
a+=params.startToStart != UNSET ? "SS" : "__";
a+=params.startToEnd != UNSET ? "|SE" : "|__";
a+=params.endToStart != UNSET ? "|ES" : "|__";
a+=params.endToEnd != UNSET ? "|EE" : "|__";
a+=params.leftToLeft != UNSET ? "|LL" : "|__";
a+=params.leftToRight != UNSET ? "|LR" : "|__";
a+=params.rightToLeft != UNSET ? "|RL" : "|__";
a+=params.rightToRight != UNSET ? "|RR" : "|__";
a+=params.topToTop != UNSET ? "|TT" : "|__";
a+=params.topToBottom != UNSET ? "|TB" : "|__";
a+=params.bottomToTop != UNSET ? "|BT" : "|__";
a+=params.bottomToBottom != UNSET ? "|BB" : "|__";
Log.v(TAG,str + a);
}
private void debugWidget(String str,ConstraintWidget child){
String a=" ";
a+=child.mTop.mTarget != null ? ("T" + (child.mTop.mTarget.mType == ConstraintAnchor.Type.TOP ? "T" : "B")) : "__";
a+=child.mBottom.mTarget != null ? ("B" + (child.mBottom.mTarget.mType == ConstraintAnchor.Type.TOP ? "T" : "B")) : "__";
a+=child.mLeft.mTarget != null ? ("L" + (child.mLeft.mTarget.mType == ConstraintAnchor.Type.LEFT ? "L" : "R")) : "__";
a+=child.mRight.mTarget != null ? ("R" + (child.mRight.mTarget.mType == ConstraintAnchor.Type.LEFT ? "L" : "R")) : "__";
Log.v(TAG,str + a + " ---  "+ child);
}
private void debugLayout(String title,ConstraintWidgetContainer c){
View v=(View)c.getCompanionWidget();
String cName=title + " " + CLDebug.getName(v);
Log.v(TAG,cName + "  ========= " + c);
int count=c.getChildren().size();
for (int i=0; i < count; i++) {
String str=cName + "[" + i+ "] ";
ConstraintWidget child=c.getChildren().get(i);
String a="";
a+=child.mTop.mTarget != null ? "T" : "_";
a+=child.mBottom.mTarget != null ? "B" : "_";
a+=child.mLeft.mTarget != null ? "L" : "_";
a+=child.mRight.mTarget != null ? "R" : "_";
v=(View)child.getCompanionWidget();
String name=CLDebug.getName(v);
if (v instanceof TextView) {
name+="(" + ((TextView)v).getText() + ")";
}
Log.v(TAG,str + "  " + name+ " "+ child+ " "+ a);
}
Log.v(TAG,cName + " done. ");
}
public void reEvaluateState(){
measure(mLastWidthMeasureSpec,mLastHeightMeasureSpec);
setupMotionViews();
}
public void measure(int widthMeasureSpec,int heightMeasureSpec){
int widthMode=MeasureSpec.getMode(widthMeasureSpec);
int heightMode=MeasureSpec.getMode(heightMeasureSpec);
mWidthMeasureMode=widthMode;
mHeightMeasureMode=heightMode;
int optimisationLevel=getOptimizationLevel();
if (mCurrentState == getStartState()) {
resolveSystem(mLayoutEnd,optimisationLevel,(mEnd == null || mEnd.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mEnd == null || mEnd.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
if (mStart != null) {
resolveSystem(mLayoutStart,optimisationLevel,(mStart == null || mStart.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mStart == null || mStart.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
}
}
 else {
if (mStart != null) {
resolveSystem(mLayoutStart,optimisationLevel,(mStart == null || mStart.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mStart == null || mStart.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
}
resolveSystem(mLayoutEnd,optimisationLevel,(mEnd == null || mEnd.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mEnd == null || mEnd.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
}
boolean recompute_start_end_size=true;
if (getParent() instanceof MotionLayout && widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
recompute_start_end_size=false;
}
if (recompute_start_end_size) {
mWidthMeasureMode=widthMode;
mHeightMeasureMode=heightMode;
if (mCurrentState == getStartState()) {
resolveSystem(mLayoutEnd,optimisationLevel,(mEnd == null || mEnd.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mEnd == null || mEnd.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
if (mStart != null) {
resolveSystem(mLayoutStart,optimisationLevel,(mStart == null || mStart.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mStart == null || mStart.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
}
}
 else {
if (mStart != null) {
resolveSystem(mLayoutStart,optimisationLevel,(mStart == null || mStart.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mStart == null || mStart.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
}
resolveSystem(mLayoutEnd,optimisationLevel,(mEnd == null || mEnd.mRotate == 0) ? widthMeasureSpec : heightMeasureSpec,(mEnd == null || mEnd.mRotate == 0) ? heightMeasureSpec : widthMeasureSpec);
}
mStartWrapWidth=mLayoutStart.getWidth();
mStartWrapHeight=mLayoutStart.getHeight();
mEndWrapWidth=mLayoutEnd.getWidth();
mEndWrapHeight=mLayoutEnd.getHeight();
mMeasureDuringTransition=((mStartWrapWidth != mEndWrapWidth) || (mStartWrapHeight != mEndWrapHeight));
}
int width=mStartWrapWidth;
int height=mStartWrapHeight;
if (mWidthMeasureMode == MeasureSpec.AT_MOST || mWidthMeasureMode == MeasureSpec.UNSPECIFIED) {
width=(int)(mStartWrapWidth + mPostInterpolationPosition * (mEndWrapWidth - mStartWrapWidth));
}
if (mHeightMeasureMode == MeasureSpec.AT_MOST || mHeightMeasureMode == MeasureSpec.UNSPECIFIED) {
height=(int)(mStartWrapHeight + mPostInterpolationPosition * (mEndWrapHeight - mStartWrapHeight));
}
boolean isWidthMeasuredTooSmall=mLayoutStart.isWidthMeasuredTooSmall() || mLayoutEnd.isWidthMeasuredTooSmall();
boolean isHeightMeasuredTooSmall=mLayoutStart.isHeightMeasuredTooSmall() || mLayoutEnd.isHeightMeasuredTooSmall();
resolveMeasuredDimension(widthMeasureSpec,heightMeasureSpec,width,height,isWidthMeasuredTooSmall,isHeightMeasuredTooSmall);
if (DEBUG) {
CLDebug.logStack(TAG,">>>>>>>>",3);
debugLayout(">>>>>>> measure str ",mLayoutStart);
debugLayout(">>>>>>> measure end ",mLayoutEnd);
}
}
public void build(){
final int n=getChildCount();
mFrameArrayList.clear();
SparseArray<MotionController> controllers=new SparseArray<>();
int[] ids=new int[n];
for (int i=0; i < n; i++) {
View v=getChildAt(i);
MotionController motionController=new MotionController(v);
controllers.put(ids[i]=v.getId(),motionController);
mFrameArrayList.put(v,motionController);
}
for (int i=0; i < n; i++) {
View v=getChildAt(i);
MotionController motionController=mFrameArrayList.get(v);
if (motionController == null) {
continue;
}
if (mStart != null) {
ConstraintWidget startWidget=getWidget(mLayoutStart,v);
if (startWidget != null) {
motionController.setStartState(toRect(startWidget),mStart,getWidth(),getHeight());
}
 else {
if (mDebugPath != 0) {
Log.e(TAG,CLDebug.getLocation() + "no widget for  " + CLDebug.getName(v)+ " ("+ v.getClass().getName()+ ")");
}
}
}
 else {
if (mInRotation) {
motionController.setStartState(mPreRotate.get(v),v,mRotatMode,mPreRotateWidth,mPreRotateHeight);
}
}
if (mEnd != null) {
ConstraintWidget endWidget=getWidget(mLayoutEnd,v);
if (endWidget != null) {
motionController.setEndState(toRect(endWidget),mEnd,getWidth(),getHeight());
}
 else {
if (mDebugPath != 0) {
Log.e(TAG,CLDebug.getLocation() + "no widget for  " + CLDebug.getName(v)+ " ("+ v.getClass().getName()+ ")");
}
}
}
}
for (int i=0; i < n; i++) {
MotionController controller=controllers.get(ids[i]);
int relativeToId=controller.getAnimateRelativeTo();
if (relativeToId != UNSET) {
controller.setupRelative(controllers.get(relativeToId));
}
}
}
public void setMeasuredId(int startId,int endId){
mStartId=startId;
mEndId=endId;
}
public boolean isNotConfiguredWith(int startId,int endId){
return startId != mStartId || endId != mEndId;
}
}
private Rect toRect(ConstraintWidget cw){
mTempRect.top=cw.getY();
mTempRect.left=cw.getX();
mTempRect.right=cw.getWidth() + mTempRect.left;
mTempRect.bottom=cw.getHeight() + mTempRect.top;
return mTempRect;
}
Model mModel=new Model();
public void requestLayout(){
if (!(mMeasureDuringTransition)) {
if (mCurrentState == UNSET && mScene != null && mScene.mCurrentTransition != null && mScene.mCurrentTransition.getLayoutDuringTransition() == MotionScene.LAYOUT_IGNORE_REQUEST) {
return;
}
}
super.requestLayout();
}
protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
if (DEBUG) {
Log.v(TAG,"onMeasure " + CLDebug.getLocation());
}
if (mScene == null) {
super.onMeasure(widthMeasureSpec,heightMeasureSpec);
return;
}
boolean recalc=(mLastWidthMeasureSpec != widthMeasureSpec || mLastHeightMeasureSpec != heightMeasureSpec);
if (mNeedsFireTransitionCompleted) {
mNeedsFireTransitionCompleted=false;
onNewStateAttachHandlers();
processTransitionCompleted();
recalc=true;
}
if (mDirtyHierarchy) {
recalc=true;
}
mLastWidthMeasureSpec=widthMeasureSpec;
mLastHeightMeasureSpec=heightMeasureSpec;
int startId=mScene.getStartId();
int endId=mScene.getEndId();
boolean setMeasure=true;
if ((recalc || mModel.isNotConfiguredWith(startId,endId)) && mBeginState != UNSET) {
super.onMeasure(widthMeasureSpec,heightMeasureSpec);
mModel.initFrom(mLayoutWidget,mScene.getConstraintSet(startId),mScene.getConstraintSet(endId));
mModel.reEvaluateState();
mModel.setMeasuredId(startId,endId);
setMeasure=false;
}
 else if (recalc) {
super.onMeasure(widthMeasureSpec,heightMeasureSpec);
}
if (mMeasureDuringTransition || setMeasure) {
int heightPadding=getPaddingTop() + getPaddingBottom();
int widthPadding=getPaddingLeft() + getPaddingRight();
int androidLayoutWidth=mLayoutWidget.getWidth() + widthPadding;
int androidLayoutHeight=mLayoutWidget.getHeight() + heightPadding;
if (mWidthMeasureMode == MeasureSpec.AT_MOST || mWidthMeasureMode == MeasureSpec.UNSPECIFIED) {
androidLayoutWidth=(int)(mStartWrapWidth + mPostInterpolationPosition * (mEndWrapWidth - mStartWrapWidth));
requestLayout();
}
if (mHeightMeasureMode == MeasureSpec.AT_MOST || mHeightMeasureMode == MeasureSpec.UNSPECIFIED) {
androidLayoutHeight=(int)(mStartWrapHeight + mPostInterpolationPosition * (mEndWrapHeight - mStartWrapHeight));
requestLayout();
}
setMeasuredDimension(androidLayoutWidth,androidLayoutHeight);
}
if (!reduceFlicker) {setRedraw(false);evaluateLayout();setRedraw(true);}
}
private void evaluateLayout(){
float dir=Math.signum(mTransitionGoalPosition - mTransitionLastPosition);
long currentTime=getNanoTime();
float deltaPos=0f;
if (!(mInterpolator instanceof StopLogic)) {
deltaPos=dir * (currentTime - mTransitionLastTime) * 1E-9f / mTransitionDuration;
}
float position=mTransitionLastPosition + deltaPos;
boolean done=false;
if (mTransitionInstantly) {
position=mTransitionGoalPosition;
}
if ((dir > 0 && position >= mTransitionGoalPosition) || (dir <= 0 && position <= mTransitionGoalPosition)) {
position=mTransitionGoalPosition;
done=true;
}
if (mInterpolator != null && !done) {
if (mTemporalInterpolator) {
float time=(currentTime - mAnimationStartTime) * 1E-9f;
position=mInterpolator.getInterpolation(time);
}
 else {
position=mInterpolator.getInterpolation(position);
}
}
if ((dir > 0 && position >= mTransitionGoalPosition) || (dir <= 0 && position <= mTransitionGoalPosition)) {
position=mTransitionGoalPosition;
}
mPostInterpolationPosition=position;
int n=getChildCount();
long time=getNanoTime();
float interPos=mProgressInterpolator == null ? position : mProgressInterpolator.getInterpolation(position);
for (int i=0; i < n; i++) {
final View child=getChildAt(i);
final MotionController frame=mFrameArrayList.get(child);
if (frame != null) {
frame.interpolate(child,interPos,time,mKeyCache);
}
}
if (mMeasureDuringTransition) {
requestLayout();
}
}
void endTrigger(boolean start){if (reduceFlicker) {evaluateLayout();}
int n=getChildCount();
for (int i=0; i < n; i++) {
final View child=getChildAt(i);
final MotionController frame=mFrameArrayList.get(child);
if (frame != null) {
frame.endTrigger(start);
}
}
}
void evaluate(boolean force){
if (mTransitionLastTime == -1) {
mTransitionLastTime=getNanoTime();
}
if (mTransitionLastPosition > 0.0f && mTransitionLastPosition < 1.0f) {
mCurrentState=UNSET;
}
boolean newState=false;
if (mKeepAnimating || mInTransition && (force || mTransitionGoalPosition != mTransitionLastPosition)) {
float dir=Math.signum(mTransitionGoalPosition - mTransitionLastPosition);
long currentTime=getNanoTime();
float deltaPos=0f;
if (!(mInterpolator instanceof MotionInterpolator)) {
deltaPos=dir * (currentTime - mTransitionLastTime) * 1E-9f / mTransitionDuration;
}
float position=mTransitionLastPosition + deltaPos;
boolean done=false;
if (mTransitionInstantly) {
position=mTransitionGoalPosition;
}
if ((dir > 0 && position >= mTransitionGoalPosition) || (dir <= 0 && position <= mTransitionGoalPosition)) {
position=mTransitionGoalPosition;
mInTransition=false;
done=true;
}
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " mTransitionLastPosition = " + mTransitionLastPosition+ " position = "+ position);
}
mTransitionLastPosition=position;
mTransitionPosition=position;
mTransitionLastTime=currentTime;
int NOT_STOP_LOGIC=0;
int STOP_LOGIC_CONTINUE=1;
int STOP_LOGIC_STOP=2;
int stopLogicDone=NOT_STOP_LOGIC;
if (mInterpolator != null && !done) {
if (mTemporalInterpolator) {
float time=(currentTime - mAnimationStartTime) * 1E-9f;
position=mInterpolator.getInterpolation(time);
if (mInterpolator == mStopLogic) {
boolean dp=mStopLogic.isStopped();
stopLogicDone=(dp) ? STOP_LOGIC_STOP : STOP_LOGIC_CONTINUE;
}
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " mTransitionLastPosition = " + mTransitionLastPosition+ " position = "+ position);
}
mTransitionLastPosition=position;
mTransitionLastTime=currentTime;
if (mInterpolator instanceof MotionInterpolator) {
float lastVelocity=((MotionInterpolator)mInterpolator).getVelocity();
mLastVelocity=lastVelocity;
if (Math.abs(lastVelocity) * mTransitionDuration <= EPSILON && stopLogicDone == STOP_LOGIC_STOP) {
mInTransition=false;
}
if (lastVelocity > 0 && position >= 1.0f) {
mTransitionLastPosition=position=1.0f;
mInTransition=false;
}
if (lastVelocity < 0 && position <= 0) {
mTransitionLastPosition=position=0.0f;
mInTransition=false;
}
}
}
 else {
float p2=position;
position=mInterpolator.getInterpolation(position);
if (mInterpolator instanceof MotionInterpolator) {
mLastVelocity=((MotionInterpolator)mInterpolator).getVelocity();
}
 else {
p2=mInterpolator.getInterpolation(p2 + deltaPos);
mLastVelocity=dir * (p2 - position) / deltaPos;
}
}
}
 else {
mLastVelocity=deltaPos;
}
if (Math.abs(mLastVelocity) > EPSILON) {
setState(TransitionState.MOVING);
}
if (stopLogicDone != STOP_LOGIC_CONTINUE) {
if ((dir > 0 && position >= mTransitionGoalPosition) || (dir <= 0 && position <= mTransitionGoalPosition)) {
position=mTransitionGoalPosition;
mInTransition=false;
}
if (position >= 1.0f || position <= 0.0f) {
mInTransition=false;
setState(TransitionState.FINISHED);
}
}
int n=getChildCount();
mKeepAnimating=false;
long time=getNanoTime();
if (DEBUG) {
Log.v(TAG,"LAYOUT frame.interpolate at " + position);
}
mPostInterpolationPosition=position;
float interPos=mProgressInterpolator == null ? position : mProgressInterpolator.getInterpolation(position);
if (mProgressInterpolator != null) {
mLastVelocity=mProgressInterpolator.getInterpolation(position + dir / mTransitionDuration);
mLastVelocity-=mProgressInterpolator.getInterpolation(position);
}
for (int i=0; i < n; i++) {
final View child=getChildAt(i);
final MotionController frame=mFrameArrayList.get(child);
if (frame != null) {
mKeepAnimating|=frame.interpolate(child,interPos,time,mKeyCache);
}
}
if (DEBUG) {
Log.v(TAG," interpolate " + CLDebug.getLocation() + " "+ CLDebug.getName(this)+ " "+ CLDebug.getName(getContext(),mBeginState)+ " "+ position);
}
boolean end=((dir > 0 && position >= mTransitionGoalPosition) || (dir <= 0 && position <= mTransitionGoalPosition));
if (!mKeepAnimating && !mInTransition && end) {
setState(TransitionState.FINISHED);
}
if (mMeasureDuringTransition) {
requestLayout();
}
mKeepAnimating|=!end;
if (position <= 0 && mBeginState != UNSET) {
if (mCurrentState != mBeginState) {
newState=true;
mCurrentState=mBeginState;
ConstraintSet set=mScene.getConstraintSet(mBeginState);
set.applyCustomAttributes(this);
setState(TransitionState.FINISHED);
}
}
if (position >= 1.0) {
if (DEBUG) {
Log.v(TAG,CLDebug.getLoc() + " ============= setting  to end " + CLDebug.getName(getContext(),mEndState)+ "  "+ position);
}
if (mCurrentState != mEndState) {
newState=true;
mCurrentState=mEndState;
ConstraintSet set=mScene.getConstraintSet(mEndState);
set.applyCustomAttributes(this);
setState(TransitionState.FINISHED);
}
}
if (mKeepAnimating || mInTransition) {
invalidate();
}
 else {
if ((dir > 0 && position == 1) || (dir < 0 && position == 0)) {
setState(TransitionState.FINISHED);
}
}
if (!mKeepAnimating && !mInTransition && ((dir > 0 && position == 1) || (dir < 0 && position == 0))) {
onNewStateAttachHandlers();
}
}
if (mTransitionLastPosition >= 1.0f) {
if (mCurrentState != mEndState) {
newState=true;
}
mCurrentState=mEndState;
}
 else if (mTransitionLastPosition <= 0.0f) {
if (mCurrentState != mBeginState) {
newState=true;
}
mCurrentState=mBeginState;
}
mNeedsFireTransitionCompleted|=newState;
if (newState && !mInLayout) {
requestLayout();
}
mTransitionPosition=mTransitionLastPosition;
}
private boolean mNeedsFireTransitionCompleted=false;
protected void onLayout(boolean changed,int left,int top,int right,int bottom){
mInLayout=true;
try {
if (DEBUG) {
Log.v(TAG," onLayout " + getProgress() + "  "+ CLDebug.getLocation());
}
if (mScene == null) {
super.onLayout(changed,left,top,right,bottom);
return;
}
int w=right - left;
int h=bottom - top;
if (mLastLayoutWidth != w || mLastLayoutHeight != h) {
rebuildScene();
evaluate(true);
if (DEBUG) {
Log.v(TAG," onLayout  rebuildScene  " + CLDebug.getLocation());
}
}
mLastLayoutWidth=w;
mLastLayoutHeight=h;
mOldWidth=w;
mOldHeight=h;
}
  finally {
mInLayout=false;
}
}
public boolean onTouchEvent(MotionEvent event){
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " onTouchEvent = " + mTransitionLastPosition);
}
if (mScene != null && mInteractionEnabled && mScene.supportTouch()) {
MotionScene.Transition currentTransition=mScene.mCurrentTransition;
if (currentTransition != null && !currentTransition.isEnabled()) {
return false;
}
mScene.processTouchEvent(event,getCurrentState(),this);
return true;
}
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " mTransitionLastPosition = " + mTransitionLastPosition);
}
return false;
}
void onNewStateAttachHandlers(){
if (mScene == null) {
return;
}
if (mScene.autoTransition(this,mCurrentState)) {
requestLayout();
return;
}
if (mCurrentState != UNSET) {
mScene.addOnClickListeners(this,mCurrentState);
}
if (mScene.supportTouch()) {
mScene.setupTouch();
}
}
public int getCurrentState(){
return mCurrentState;
}
public float getProgress(){
return mTransitionLastPosition;
}
void getAnchorDpDt(int mTouchAnchorId,float pos,float locationX,float locationY,float[] mAnchorDpDt){
View v;
MotionController f=mFrameArrayList.get(v=getViewById(mTouchAnchorId));
if (DEBUG) {
Log.v(TAG," getAnchorDpDt " + CLDebug.getName(v) + " "+ CLDebug.getLocation());
}
if (f != null) {
f.getDpDt(pos,locationX,locationY,mAnchorDpDt);
float y=v.getY();
float deltaPos=pos - lastPos;
float deltaY=y - lastY;
float dydp=(deltaPos != 0.0f) ? deltaY / deltaPos : Float.NaN;
if (DEBUG) {
Log.v(TAG," getAnchorDpDt " + CLDebug.getName(v) + " "+ CLDebug.getLocation()+ " "+ Arrays.toString(mAnchorDpDt));
}
lastPos=pos;
lastY=y;
}
 else {
String idName=(v == null) ? "" + mTouchAnchorId : v.getContext().getResources().getResourceName(mTouchAnchorId);
Log.w(TAG,"WARNING could not find view id " + idName);
}
}
public interface TransitionListener {
public void onTransitionStarted(MotionLayout motionLayout,int startId,int endId);
void onTransitionChange(MotionLayout motionLayout,int startId,int endId,float progress);
void onTransitionCompleted(MotionLayout motionLayout,int currentId);
void onTransitionTrigger(MotionLayout motionLayout,int triggerId,boolean positive,float progress);
}
public void fireTrigger(int triggerId,boolean positive,float progress){
if (mTransitionListener != null) {
mTransitionListener.onTransitionTrigger(this,triggerId,positive,progress);
}
if (mTransitionListeners != null) {
for (TransitionListener listeners : mTransitionListeners) {
listeners.onTransitionTrigger(this,triggerId,positive,progress);
}
}
}
private void fireTransitionChange(){
if (mTransitionListener != null || (mTransitionListeners != null && !mTransitionListeners.isEmpty())) {
if (mListenerPosition != mTransitionPosition) {
if (mListenerState != UNSET) {
if (mTransitionListener != null) {
mTransitionListener.onTransitionStarted(this,mBeginState,mEndState);
}
if (mTransitionListeners != null) {
for (TransitionListener listeners : mTransitionListeners) {
listeners.onTransitionStarted(this,mBeginState,mEndState);
}
}
mIsAnimating=true;
}
mListenerState=UNSET;
mListenerPosition=mTransitionPosition;
if (mTransitionListener != null) {
mTransitionListener.onTransitionChange(this,mBeginState,mEndState,mTransitionPosition);
}
if (mTransitionListeners != null) {
for (TransitionListener listeners : mTransitionListeners) {
listeners.onTransitionChange(this,mBeginState,mEndState,mTransitionPosition);
}
}
mIsAnimating=true;
}
}
}
ArrayList<Integer> mTransitionCompleted=new ArrayList<>();
protected void fireTransitionCompleted(){
if (mTransitionListener != null || (mTransitionListeners != null && !mTransitionListeners.isEmpty())) {
if (mListenerState == UNSET) {
mListenerState=mCurrentState;
int lastState=UNSET;
if (!mTransitionCompleted.isEmpty()) {
lastState=mTransitionCompleted.get(mTransitionCompleted.size() - 1);
}
if (lastState != mCurrentState && mCurrentState != -1) {
mTransitionCompleted.add(mCurrentState);
}
}
}
processTransitionCompleted();
if (mOnComplete != null) {
mOnComplete.run();
}
if (mScheduledTransitionTo != null && mScheduledTransitions > 0) {
transitionToState(mScheduledTransitionTo[0]);
System.arraycopy(mScheduledTransitionTo,1,mScheduledTransitionTo,0,mScheduledTransitionTo.length - 1);
mScheduledTransitions--;
}
}
private void processTransitionCompleted(){
if (mTransitionListener == null && (mTransitionListeners == null || mTransitionListeners.isEmpty())) {
return;
}
mIsAnimating=false;
for (Integer state : mTransitionCompleted) {
if (mTransitionListener != null) {
mTransitionListener.onTransitionCompleted(this,state);
}
if (mTransitionListeners != null) {
for (TransitionListener listeners : mTransitionListeners) {
listeners.onTransitionCompleted(this,state);
}
}
}
mTransitionCompleted.clear();
}
public void onViewAdded(View view){
super.onViewAdded(view);
if (view instanceof MotionHelper) {
MotionHelper helper=(MotionHelper)view;
if (mTransitionListeners == null) {
mTransitionListeners=new CopyOnWriteArrayList<>();
}
mTransitionListeners.add(helper);
if (helper.isUsedOnShow()) {
if (mOnShowHelpers == null) {
mOnShowHelpers=new ArrayList<>();
}
mOnShowHelpers.add(helper);
}
if (helper.isUseOnHide()) {
if (mOnHideHelpers == null) {
mOnHideHelpers=new ArrayList<>();
}
mOnHideHelpers.add(helper);
}
if (helper.isDecorator()) {
if (mDecoratorsHelpers == null) {
mDecoratorsHelpers=new ArrayList<>();
}
mDecoratorsHelpers.add(helper);
}
}
}
public void onViewRemoved(View view){
super.onViewRemoved(view);
if (mOnShowHelpers != null) {
mOnShowHelpers.remove(view);
}
if (mOnHideHelpers != null) {
mOnHideHelpers.remove(view);
}
}
public void setOnShow(float progress){
if (mOnShowHelpers != null) {
final int count=mOnShowHelpers.size();
for (int i=0; i < count; i++) {
MotionHelper helper=mOnShowHelpers.get(i);
helper.setProgress(progress);
}
}
}
public void setOnHide(float progress){
if (mOnHideHelpers != null) {
final int count=mOnHideHelpers.size();
for (int i=0; i < count; i++) {
MotionHelper helper=mOnHideHelpers.get(i);
helper.setProgress(progress);
}
}
}
public int[] getConstraintSetIds(){
if (mScene == null) {
return null;
}
return mScene.getConstraintSetIds();
}
public ConstraintSet getConstraintSet(int id){
if (mScene == null) {
return null;
}
return mScene.getConstraintSet(id);
}
public void rebuildScene(){
mModel.reEvaluateState();
invalidate();
}
public void updateState(int stateId,ConstraintSet set){
if (mScene != null) {
mScene.setConstraintSet(stateId,set);
}
updateState();
if (mCurrentState == stateId) {
set.applyTo(this);
}
}
public void updateState(){
mModel.initFrom(mLayoutWidget,mScene.getConstraintSet(mBeginState),mScene.getConstraintSet(mEndState));
rebuildScene();
}
public ArrayList<MotionScene.Transition> getDefinedTransitions(){
if (mScene == null) {
return null;
}
return mScene.getDefinedTransitions();
}
public int getStartState(){
return mBeginState;
}
public int getEndState(){
return mEndState;
}
public void setTransitionDuration(int milliseconds){
if (mScene == null) {
Log.e(TAG,"MotionScene not defined");
return;
}
mScene.setDuration(milliseconds);
}
public MotionScene.Transition getTransition(int id){
return mScene.getTransitionById(id);
}
public boolean isInteractionEnabled(){
return mInteractionEnabled;
}
public void viewTransition(int viewTransitionId,View... view){
if (mScene != null) {
mScene.viewTransition(viewTransitionId,view);
}
 else {
Log.e(TAG," no motionScene");
}
}
private boolean reduceFlicker;
public void initMotionScene(){
mScene=new MotionScene(this);
}
@Override public void invalidate(){
super.invalidate();
com.ashera.widget.PluginInvoker.enqueueTaskForEventLoop(this::run,System.currentTimeMillis());
}
private void run(){
if (mLayoutWidget != null) {
try {
setRedraw(false);
evaluate(false);
setRedraw(true);
}
 catch (Exception e) {
}
}
}
public MotionScene getScene(){
return mScene;
}
public void setReduceFlicker(boolean reduceFlicker){
this.reduceFlicker=reduceFlicker;
}
public void postInit(){
if (mCurrentState == UNSET && mScene != null) {
mCurrentState=mScene.getStartId();
mBeginState=mScene.getStartId();
mEndState=mScene.getEndId();
}
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || isAttachedToWindow()) {
if (mScene != null) {
ConstraintSet cSet=mScene.getConstraintSet(mCurrentState);
if (mDecoratorsHelpers != null) {
for (MotionHelper mh : mDecoratorsHelpers) {
mh.onFinishedMotionScene(this);
}
}
if (cSet != null) {
cSet.applyTo(this);
}
mBeginState=mCurrentState;
}
onNewStateAttachHandlers();
if (mStateCache != null) {
if (mDelayedApply) {
post(new Runnable(){
@Override public void run(){
mStateCache.apply();
}
}
);
}
 else {
mStateCache.apply();
}
}
 else {
if (mScene != null && mScene.mCurrentTransition != null) {
if (mScene.mCurrentTransition.getAutoTransition() == MotionScene.Transition.AUTO_ANIMATE_TO_END) {
transitionToEnd();
setState(TransitionState.SETUP);
setState(TransitionState.MOVING);
}
}
}
}
}
public final static class MyTracker implements androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker {
@Override public void recycle(){
}
@Override public void clear(){
}
@Override public void addMovement(MotionEvent event){
}
@Override public void computeCurrentVelocity(int units){
}
@Override public void computeCurrentVelocity(int units,float maxVelocity){
}
@Override public float getXVelocity(){
return 0;
}
@Override public float getYVelocity(){
return 0;
}
@Override public float getXVelocity(int id){
return 0;
}
@Override public float getYVelocity(int id){
return 0;
}
}
private androidx.constraintlayout.motion.widget.MotionLayout.MyTracker myTracker=new androidx.constraintlayout.motion.widget.MotionLayout.MyTracker();
public androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker obtainVelocityTracker(){
return myTracker;
}
}
