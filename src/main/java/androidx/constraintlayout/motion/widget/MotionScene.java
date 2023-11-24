package androidx.constraintlayout.motion.widget;
import r.android.content.Context;
import r.android.graphics.RectF;
import r.android.util.Log;
import r.android.util.SparseArray;
import r.android.util.SparseIntArray;
import r.android.view.MotionEvent;
import r.android.view.View;
import r.android.view.animation.AccelerateDecelerateInterpolator;
import r.android.view.animation.AccelerateInterpolator;
import r.android.view.animation.AnimationUtils;
import r.android.view.animation.AnticipateInterpolator;
import r.android.view.animation.BounceInterpolator;
import r.android.view.animation.DecelerateInterpolator;
import r.android.view.animation.Interpolator;
import r.android.view.animation.OvershootInterpolator;
import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.CLStateSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MotionScene {
  private static final String TAG="MotionScene";
  private static final boolean DEBUG=false;
  private static final int MIN_DURATION=8;
  final static int TRANSITION_BACKWARD=0;
  final static int TRANSITION_FORWARD=1;
  private static final int SPLINE_STRING=-1;
  private static final int INTERPOLATOR_REFERENCE_ID=-2;
  public static final int UNSET=-1;
  private final MotionLayout mMotionLayout;
  CLStateSet mStateSet=null;
  Transition mCurrentTransition=null;
  private boolean mDisableAutoTransition=false;
  private ArrayList<Transition> mTransitionList=new ArrayList<>();
  private Transition mDefaultTransition=null;
  private ArrayList<Transition> mAbstractTransitionList=new ArrayList<>();
  private SparseArray<ConstraintSet> mConstraintSetMap=new SparseArray<>();
  private SparseIntArray mDeriveMap=new SparseIntArray();
  private boolean DEBUG_DESKTOP=false;
  public int mDefaultDuration=400;
  public int mLayoutDuringTransition=0;
  public static final int LAYOUT_IGNORE_REQUEST=0;
  public static final int LAYOUT_HONOR_REQUEST=1;
  private MotionEvent mLastTouchDown;
  private boolean mIgnoreTouch=false;
  private boolean mMotionOutsideRegion=false;
  private MotionLayout.MotionTracker mVelocityTracker;
  private boolean mRtl;
  final ViewTransitionController mViewTransitionController;
  void setTransition(  int beginId,  int endId){
    int start=beginId;
    int end=endId;
    if (mStateSet != null) {
      int tmp=mStateSet.stateGetConstraintID(beginId,-1,-1);
      if (tmp != -1) {
        start=tmp;
      }
      tmp=mStateSet.stateGetConstraintID(endId,-1,-1);
      if (tmp != -1) {
        end=tmp;
      }
    }
    if (DEBUG) {
      Log.v(TAG,CLDebug.getLocation() + " setTransition " + CLDebug.getName(mMotionLayout.getContext(),beginId)+ " -> "+ CLDebug.getName(mMotionLayout.getContext(),endId));
    }
    if (mCurrentTransition != null) {
      if (mCurrentTransition.mConstraintSetEnd == endId && mCurrentTransition.mConstraintSetStart == beginId) {
        return;
      }
    }
    for (    Transition transition : mTransitionList) {
      if ((transition.mConstraintSetEnd == end && transition.mConstraintSetStart == start) || (transition.mConstraintSetEnd == endId && transition.mConstraintSetStart == beginId)) {
        if (DEBUG) {
          Log.v(TAG,CLDebug.getLocation() + " found transition  " + CLDebug.getName(mMotionLayout.getContext(),beginId)+ " -> "+ CLDebug.getName(mMotionLayout.getContext(),endId));
        }
        mCurrentTransition=transition;
        if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
          mCurrentTransition.mTouchResponse.setRTL(mRtl);
        }
        return;
      }
    }
    Transition matchTransition=mDefaultTransition;
    for (    Transition transition : mAbstractTransitionList) {
      if (transition.mConstraintSetEnd == endId) {
        matchTransition=transition;
      }
    }
    Transition t=new Transition(this,matchTransition);
    t.mConstraintSetStart=start;
    t.mConstraintSetEnd=end;
    if (start != UNSET) {
      mTransitionList.add(t);
    }
    mCurrentTransition=t;
  }
  public void addTransition(  Transition transition){
    int index=getIndex(transition);
    if (index == -1) {
      mTransitionList.add(transition);
    }
 else {
      mTransitionList.set(index,transition);
    }
  }
  public void removeTransition(  Transition transition){
    int index=getIndex(transition);
    if (index != -1) {
      mTransitionList.remove(index);
    }
  }
  private int getIndex(  Transition transition){
    int id=transition.mId;
    if (id == UNSET) {
      throw new IllegalArgumentException("The transition must have an id");
    }
    int index=0;
    for (; index < mTransitionList.size(); index++) {
      if (mTransitionList.get(index).mId == id) {
        return index;
      }
    }
    return -1;
  }
  public boolean validateLayout(  MotionLayout layout){
    return (layout == mMotionLayout && layout.mScene == this);
  }
  public void setTransition(  Transition transition){
    mCurrentTransition=transition;
    if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
      mCurrentTransition.mTouchResponse.setRTL(mRtl);
    }
  }
  private int getRealID(  int stateId){
    if (mStateSet != null) {
      int tmp=mStateSet.stateGetConstraintID(stateId,-1,-1);
      if (tmp != -1) {
        return tmp;
      }
    }
    return stateId;
  }
  public List<Transition> getTransitionsWithState(  int stateId){
    stateId=getRealID(stateId);
    ArrayList<Transition> ret=new ArrayList<>();
    for (    Transition transition : mTransitionList) {
      if (transition.mConstraintSetStart == stateId || transition.mConstraintSetEnd == stateId) {
        ret.add(transition);
      }
    }
    return ret;
  }
  public void addOnClickListeners(  MotionLayout motionLayout,  int currentState){
    for (    Transition transition : mTransitionList) {
      if (transition.mOnClicks.size() > 0) {
        for (        Transition.TransitionOnClick onClick : transition.mOnClicks) {
          onClick.removeOnClickListeners(motionLayout);
        }
      }
    }
    for (    Transition transition : mAbstractTransitionList) {
      if (transition.mOnClicks.size() > 0) {
        for (        Transition.TransitionOnClick onClick : transition.mOnClicks) {
          onClick.removeOnClickListeners(motionLayout);
        }
      }
    }
    for (    Transition transition : mTransitionList) {
      if (transition.mOnClicks.size() > 0) {
        for (        Transition.TransitionOnClick onClick : transition.mOnClicks) {
          onClick.addOnClickListeners(motionLayout,currentState,transition);
        }
      }
    }
    for (    Transition transition : mAbstractTransitionList) {
      if (transition.mOnClicks.size() > 0) {
        for (        Transition.TransitionOnClick onClick : transition.mOnClicks) {
          onClick.addOnClickListeners(motionLayout,currentState,transition);
        }
      }
    }
  }
  public Transition bestTransitionFor(  int currentState,  float dx,  float dy,  MotionEvent lastTouchDown){
    List<Transition> candidates=null;
    if (currentState != -1) {
      candidates=getTransitionsWithState(currentState);
      float max=0;
      Transition best=null;
      RectF cache=new RectF();
      for (      Transition transition : candidates) {
        if (transition.mDisable) {
          continue;
        }
        if (transition.mTouchResponse != null) {
          transition.mTouchResponse.setRTL(mRtl);
          RectF region=transition.mTouchResponse.getTouchRegion(mMotionLayout,cache);
          if (region != null && lastTouchDown != null && (!region.contains(lastTouchDown.getX(),lastTouchDown.getY()))) {
            continue;
          }
          region=transition.mTouchResponse.getLimitBoundsTo(mMotionLayout,cache);
          if (region != null && lastTouchDown != null && (!region.contains(lastTouchDown.getX(),lastTouchDown.getY()))) {
            continue;
          }
          float val=transition.mTouchResponse.dot(dx,dy);
          if (transition.mTouchResponse.mIsRotateMode && lastTouchDown != null) {
            float startX=lastTouchDown.getX() - transition.mTouchResponse.mRotateCenterX;
            float startY=lastTouchDown.getY() - transition.mTouchResponse.mRotateCenterY;
            float endX=dx + startX;
            float endY=dy + startY;
            double endAngle=Math.atan2(endY,endX);
            double startAngle=Math.atan2(startX,startY);
            val=(float)(endAngle - startAngle) * 10;
          }
          if (transition.mConstraintSetEnd == currentState) {
            val*=-1;
          }
 else {
            val*=1.1f;
          }
          if (val > max) {
            max=val;
            best=transition;
          }
        }
      }
      if (DEBUG) {
        if (best != null) {
          Log.v(TAG,CLDebug.getLocation() + "  ### BEST ----- " + best.debugString(mMotionLayout.getContext())+ " ----");
        }
 else {
          Log.v(TAG,CLDebug.getLocation() + "  ### BEST ----- " + null+ " ----");
        }
      }
      return best;
    }
    return mCurrentTransition;
  }
  public ArrayList<Transition> getDefinedTransitions(){
    return mTransitionList;
  }
  public Transition getTransitionById(  int id){
    for (    Transition transition : mTransitionList) {
      if (transition.mId == id) {
        return transition;
      }
    }
    return null;
  }
  public int[] getConstraintSetIds(){
    int[] ids=new int[mConstraintSetMap.size()];
    for (int i=0; i < ids.length; i++) {
      ids[i]=mConstraintSetMap.keyAt(i);
    }
    return ids;
  }
  boolean autoTransition(  MotionLayout motionLayout,  int currentState){
    if (isProcessingTouch()) {
      return false;
    }
    if (mDisableAutoTransition) {
      return false;
    }
    for (    Transition transition : mTransitionList) {
      if (transition.mAutoTransition == Transition.AUTO_NONE) {
        continue;
      }
      if (mCurrentTransition == transition && mCurrentTransition.isTransitionFlag(Transition.TRANSITION_FLAG_INTRA_AUTO)) {
        continue;
      }
      if (currentState == transition.mConstraintSetStart && (transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_END || transition.mAutoTransition == Transition.AUTO_JUMP_TO_END)) {
        motionLayout.setState(MotionLayout.TransitionState.FINISHED);
        motionLayout.setTransition(transition);
        if (transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_END) {
          motionLayout.transitionToEnd();
          motionLayout.setState(MotionLayout.TransitionState.SETUP);
          motionLayout.setState(MotionLayout.TransitionState.MOVING);
        }
 else {
          motionLayout.setProgress(1);
          motionLayout.evaluate(true);
          motionLayout.setState(MotionLayout.TransitionState.SETUP);
          motionLayout.setState(MotionLayout.TransitionState.MOVING);
          motionLayout.setState(MotionLayout.TransitionState.FINISHED);
          motionLayout.onNewStateAttachHandlers();
        }
        return true;
      }
      if (currentState == transition.mConstraintSetEnd && (transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_START || transition.mAutoTransition == Transition.AUTO_JUMP_TO_START)) {
        motionLayout.setState(MotionLayout.TransitionState.FINISHED);
        motionLayout.setTransition(transition);
        if (transition.mAutoTransition == Transition.AUTO_ANIMATE_TO_START) {
          motionLayout.transitionToStart();
          motionLayout.setState(MotionLayout.TransitionState.SETUP);
          motionLayout.setState(MotionLayout.TransitionState.MOVING);
        }
 else {
          motionLayout.setProgress(0);
          motionLayout.evaluate(true);
          motionLayout.setState(MotionLayout.TransitionState.SETUP);
          motionLayout.setState(MotionLayout.TransitionState.MOVING);
          motionLayout.setState(MotionLayout.TransitionState.FINISHED);
          motionLayout.onNewStateAttachHandlers();
        }
        return true;
      }
    }
    return false;
  }
  private boolean isProcessingTouch(){
    return (mVelocityTracker != null);
  }
  public void viewTransition(  int id,  View... view){
    mViewTransitionController.viewTransition(id,view);
  }
public static class Transition {
    public int mId=UNSET;
    private boolean mIsAbstract=false;
    public int mConstraintSetEnd=-1;
    public int mConstraintSetStart=-1;
    public int mDefaultInterpolator=0;
    private String mDefaultInterpolatorString=null;
    public int mDefaultInterpolatorID=-1;
    public int mDuration=400;
    public float mStagger=0.0f;
    private final MotionScene mMotionScene;
    private ArrayList<KeyFrames> mKeyFramesList=new ArrayList<>();
    private TouchResponse mTouchResponse=null;
    private ArrayList<TransitionOnClick> mOnClicks=new ArrayList<>();
    public int mAutoTransition=0;
    public static final int AUTO_NONE=0;
    public static final int AUTO_JUMP_TO_START=1;
    public static final int AUTO_JUMP_TO_END=2;
    public static final int AUTO_ANIMATE_TO_START=3;
    public static final int AUTO_ANIMATE_TO_END=4;
    private boolean mDisable=false;
    public int mPathMotionArc=UNSET;
    public int mLayoutDuringTransition=0;
    public int mTransitionFlags=0;
    final static int TRANSITION_FLAG_FIRST_DRAW=1;
    final static int TRANSITION_FLAG_INTRA_AUTO=2;
    public void setOnSwipe(    OnSwipe onSwipe){
      mTouchResponse=(onSwipe == null) ? null : new TouchResponse(mMotionScene.mMotionLayout,onSwipe);
    }
    public void addOnClick(    int id,    int action){
      for (      TransitionOnClick onClick : mOnClicks) {
        if (onClick.mTargetId == id) {
          onClick.mMode=action;
          return;
        }
      }
      TransitionOnClick click=new TransitionOnClick(this,id,action);
      mOnClicks.add(click);
    }
    public void removeOnClick(    int id){
      TransitionOnClick toRemove=null;
      for (      TransitionOnClick onClick : mOnClicks) {
        if (onClick.mTargetId == id) {
          toRemove=onClick;
          break;
        }
      }
      if (toRemove != null) {
        mOnClicks.remove(toRemove);
      }
    }
    public int getLayoutDuringTransition(){
      return mLayoutDuringTransition;
    }
    public void setLayoutDuringTransition(    int mode){
      mLayoutDuringTransition=mode;
    }
    public void setAutoTransition(    int type){
      mAutoTransition=type;
    }
    public int getAutoTransition(){
      return mAutoTransition;
    }
    public int getId(){
      return mId;
    }
    public int getEndConstraintSetId(){
      return mConstraintSetEnd;
    }
    public int getStartConstraintSetId(){
      return mConstraintSetStart;
    }
    public void setDuration(    int duration){
      this.mDuration=Math.max(duration,MIN_DURATION);
    }
    public int getDuration(){
      return mDuration;
    }
    public float getStagger(){
      return mStagger;
    }
    public List<KeyFrames> getKeyFrameList(){
      return mKeyFramesList;
    }
    public void addKeyFrame(    KeyFrames keyFrames){
      mKeyFramesList.add(keyFrames);
    }
    public List<TransitionOnClick> getOnClickList(){
      return mOnClicks;
    }
    public TouchResponse getTouchResponse(){
      return mTouchResponse;
    }
    public void setStagger(    float stagger){
      mStagger=stagger;
    }
    public void setPathMotionArc(    int arcMode){
      mPathMotionArc=arcMode;
    }
    public int getPathMotionArc(){
      return mPathMotionArc;
    }
    public boolean isEnabled(){
      return !mDisable;
    }
    public void setEnable(    boolean enable){
      setEnabled(enable);
    }
    public void setEnabled(    boolean enable){
      mDisable=!enable;
    }
    public String debugString(    Context context){
      String ret;
      if (mConstraintSetStart == UNSET) {
        ret="null";
      }
 else {
        ret=context.getResources().getResourceEntryName(mConstraintSetStart);
      }
      if (mConstraintSetEnd == UNSET) {
        ret+=" -> " + "null";
      }
 else {
        ret+=" -> " + context.getResources().getResourceEntryName(mConstraintSetEnd);
      }
      return ret;
    }
    public boolean isTransitionFlag(    int flag){
      return 0 != (mTransitionFlags & flag);
    }
    public void setTransitionFlag(    int flag){
      mTransitionFlags=flag;
    }
    public void setOnTouchUp(    int touchUpMode){
      TouchResponse touchResponse=getTouchResponse();
      if (touchResponse != null) {
        touchResponse.setTouchUpMode(touchUpMode);
      }
    }
public static class TransitionOnClick implements View.OnClickListener {
      private final Transition mTransition;
      int mTargetId=UNSET;
      int mMode=0x11;
      public static final int ANIM_TO_END=0x0001;
      public static final int ANIM_TOGGLE=0x0011;
      public static final int ANIM_TO_START=0x0010;
      public static final int JUMP_TO_END=0x100;
      public static final int JUMP_TO_START=0x1000;
      public TransitionOnClick(      Transition transition,      int id,      int action){
        mTransition=transition;
        mTargetId=id;
        mMode=action;
      }
      public void addOnClickListeners(      MotionLayout motionLayout,      int currentState,      Transition transition){
        View v=mTargetId == UNSET ? motionLayout : motionLayout.findViewById(mTargetId);
        if (v == null) {
          Log.e(TAG,"OnClick could not find id " + mTargetId);
          return;
        }
        int start=transition.mConstraintSetStart;
        int end=transition.mConstraintSetEnd;
        if (start == UNSET) {
          v.setMyAttribute("onClick",this);
          return;
        }
        boolean listen=((mMode & ANIM_TO_END) != 0) && currentState == start;
        listen|=((mMode & JUMP_TO_END) != 0) && currentState == start;
        listen|=((mMode & ANIM_TO_END) != 0) && currentState == start;
        listen|=((mMode & ANIM_TO_START) != 0) && currentState == end;
        listen|=((mMode & JUMP_TO_START) != 0) && currentState == end;
        if (true || listen) {
          v.setMyAttribute("onClick",this);
        }
      }
      public void removeOnClickListeners(      MotionLayout motionLayout){
        if (mTargetId == UNSET) {
          return;
        }
        View v=motionLayout.findViewById(mTargetId);
        if (v == null) {
          Log.e(TAG," (*)  could not find id " + mTargetId);
          return;
        }
        v.setMyAttribute("onClick",null);
      }
      boolean isTransitionViable(      Transition current,      MotionLayout tl){
        if (mTransition == current) {
          return true;
        }
        int dest=mTransition.mConstraintSetEnd;
        int from=mTransition.mConstraintSetStart;
        if (from == UNSET) {
          return tl.mCurrentState != dest;
        }
        return (tl.mCurrentState == from) || (tl.mCurrentState == dest);
      }
      public void onClick(      View view){
        MotionLayout tl=mTransition.mMotionScene.mMotionLayout;
        if (!tl.isInteractionEnabled()) {
          return;
        }
        if (mTransition.mConstraintSetStart == UNSET) {
          int currentState=tl.getCurrentState();
          if (currentState == UNSET) {
            tl.transitionToState(mTransition.mConstraintSetEnd);
            return;
          }
          Transition t=new Transition(mTransition.mMotionScene,mTransition);
          t.mConstraintSetStart=currentState;
          t.mConstraintSetEnd=mTransition.mConstraintSetEnd;
          tl.setTransition(t);
          tl.transitionToEnd();
          return;
        }
        Transition current=mTransition.mMotionScene.mCurrentTransition;
        boolean forward=((mMode & ANIM_TO_END) != 0 || (mMode & JUMP_TO_END) != 0);
        boolean backward=((mMode & ANIM_TO_START) != 0 || (mMode & JUMP_TO_START) != 0);
        boolean bidirectional=forward && backward;
        if (bidirectional) {
          if (mTransition.mMotionScene.mCurrentTransition != mTransition) {
            tl.setTransition(mTransition);
          }
          if (tl.getCurrentState() == tl.getEndState() || tl.getProgress() > 0.5f) {
            forward=false;
          }
 else {
            backward=false;
          }
        }
        if (isTransitionViable(current,tl)) {
          if (forward && (mMode & ANIM_TO_END) != 0) {
            tl.setTransition(mTransition);
            tl.transitionToEnd();
          }
 else           if (backward && (mMode & ANIM_TO_START) != 0) {
            tl.setTransition(mTransition);
            tl.transitionToStart();
          }
 else           if (forward && (mMode & JUMP_TO_END) != 0) {
            tl.setTransition(mTransition);
            tl.setProgress(1);
          }
 else           if (backward && (mMode & JUMP_TO_START) != 0) {
            tl.setTransition(mTransition);
            tl.setProgress(0);
          }
        }
      }
    }
    Transition(    MotionScene motionScene,    Transition global){
      mMotionScene=motionScene;
      mDuration=motionScene.mDefaultDuration;
      if (global != null) {
        mPathMotionArc=global.mPathMotionArc;
        mDefaultInterpolator=global.mDefaultInterpolator;
        mDefaultInterpolatorString=global.mDefaultInterpolatorString;
        mDefaultInterpolatorID=global.mDefaultInterpolatorID;
        mDuration=global.mDuration;
        mKeyFramesList=global.mKeyFramesList;
        mStagger=global.mStagger;
        mLayoutDuringTransition=global.mLayoutDuringTransition;
      }
    }
    public Transition(    int id,    MotionScene motionScene,    int constraintSetStartId,    int constraintSetEndId){
      mId=id;
      mMotionScene=motionScene;
      mConstraintSetStart=constraintSetStartId;
      mConstraintSetEnd=constraintSetEndId;
      mDuration=motionScene.mDefaultDuration;
      mLayoutDuringTransition=motionScene.mLayoutDuringTransition;
    }
    public void setInterpolatorInfo(    int interpolator,    String interpolatorString,    int interpolatorID){
      mDefaultInterpolator=interpolator;
      mDefaultInterpolatorString=interpolatorString;
      mDefaultInterpolatorID=interpolatorID;
    }
    }public ConstraintSet getConstraintSet(  Context context,  String id){
    if (DEBUG_DESKTOP) {
      System.out.println("id " + id);
      System.out.println("size " + mConstraintSetMap.size());
    }
    for (int i=0; i < mConstraintSetMap.size(); i++) {
      int key=mConstraintSetMap.keyAt(i);
      String IdAsString=context.getResources().getResourceName(key);
      if (DEBUG_DESKTOP) {
        System.out.println("Id for <" + i + "> is <"+ IdAsString+ "> looking for <"+ id+ ">");
      }
      if (id.equals(IdAsString)) {
        return mConstraintSetMap.get(key);
      }
    }
    return null;
  }
  ConstraintSet getConstraintSet(  int id){
    return getConstraintSet(id,-1,-1);
  }
  ConstraintSet getConstraintSet(  int id,  int width,  int height){
    if (DEBUG_DESKTOP) {
      System.out.println("id " + id);
      System.out.println("size " + mConstraintSetMap.size());
    }
    if (mStateSet != null) {
      int cid=mStateSet.stateGetConstraintID(id,width,height);
      if (cid != -1) {
        id=cid;
      }
    }
    if (mConstraintSetMap.get(id) == null) {
      Log.e(TAG,"Warning could not find ConstraintSet id/" + CLDebug.getName(mMotionLayout.getContext(),id) + " In MotionScene");
      return mConstraintSetMap.get(mConstraintSetMap.keyAt(0));
    }
    return mConstraintSetMap.get(id);
  }
  public void setConstraintSet(  int id,  ConstraintSet set){
    mConstraintSetMap.put(id,set);
  }
  public void getKeyFrames(  MotionController motionController){
    if (mCurrentTransition == null) {
      if (mDefaultTransition != null) {
        for (        KeyFrames keyFrames : mDefaultTransition.mKeyFramesList) {
          keyFrames.addFrames(motionController);
        }
      }
      return;
    }
    for (    KeyFrames keyFrames : mCurrentTransition.mKeyFramesList) {
      keyFrames.addFrames(motionController);
    }
  }
  boolean supportTouch(){
    for (    Transition transition : mTransitionList) {
      if (transition.mTouchResponse != null) {
        return true;
      }
    }
    return mCurrentTransition != null && mCurrentTransition.mTouchResponse != null;
  }
  float mLastTouchX, mLastTouchY;
  void processTouchEvent(  MotionEvent event,  int currentState,  MotionLayout motionLayout){
    if (DEBUG) {
      Log.v(TAG,CLDebug.getLocation() + " processTouchEvent");
    }
    RectF cache=new RectF();
    if (mVelocityTracker == null) {
      mVelocityTracker=mMotionLayout.obtainVelocityTracker();
    }
    mVelocityTracker.addMovement(event);
    if (DEBUG) {
      float time=(event.getEventTime() % 100000) / 1000f;
      float x=event.getRawX();
      float y=event.getRawY();
      Log.v(TAG," " + time + "  processTouchEvent "+ "state="+ CLDebug.getState(motionLayout,currentState)+ "  "+ CLDebug.getActionType(event)+ " "+ x+ ", "+ y+ " \t "+ motionLayout.getProgress());
    }
    if (currentState != -1) {
      RectF region;
switch (event.getAction()) {
case MotionEvent.ACTION_DOWN:
        mLastTouchX=event.getRawX();
      mLastTouchY=event.getRawY();
    mLastTouchDown=event;
  mIgnoreTouch=false;
if (mCurrentTransition.mTouchResponse != null) {
  region=mCurrentTransition.mTouchResponse.getLimitBoundsTo(mMotionLayout,cache);
  if (region != null && !region.contains(mLastTouchDown.getX(),mLastTouchDown.getY())) {
    mLastTouchDown=null;
    mIgnoreTouch=true;
    return;
  }
  region=mCurrentTransition.mTouchResponse.getTouchRegion(mMotionLayout,cache);
  if (region != null && (!region.contains(mLastTouchDown.getX(),mLastTouchDown.getY()))) {
    mMotionOutsideRegion=true;
  }
 else {
    mMotionOutsideRegion=false;
  }
  mCurrentTransition.mTouchResponse.setDown(mLastTouchX,mLastTouchY);
}
if (DEBUG) {
Log.v(TAG,"----- ACTION_DOWN " + mLastTouchX + ","+ mLastTouchY);
}
return;
case MotionEvent.ACTION_MOVE:
if (mIgnoreTouch) {
break;
}
float dy=event.getRawY() - mLastTouchY;
float dx=event.getRawX() - mLastTouchX;
if (DEBUG) {
Log.v(TAG,"----- ACTION_MOVE " + dx + ","+ dy);
}
if (dx == 0.0 && dy == 0.0 || mLastTouchDown == null) {
return;
}
Transition transition=bestTransitionFor(currentState,dx,dy,mLastTouchDown);
if (DEBUG) {
Log.v(TAG,CLDebug.getLocation() + " best Transition For " + dx+ ","+ dy+ " "+ ((transition == null) ? null : transition.debugString(mMotionLayout.getContext())));
}
if (transition != null) {
motionLayout.setTransition(transition);
region=mCurrentTransition.mTouchResponse.getTouchRegion(mMotionLayout,cache);
mMotionOutsideRegion=region != null && (!region.contains(mLastTouchDown.getX(),mLastTouchDown.getY()));
mCurrentTransition.mTouchResponse.setUpTouchEvent(mLastTouchX,mLastTouchY);
}
}
}
if (mIgnoreTouch) {
return;
}
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null && !mMotionOutsideRegion) {
mCurrentTransition.mTouchResponse.processTouchEvent(event,mVelocityTracker,currentState,this);
}
mLastTouchX=event.getRawX();
mLastTouchY=event.getRawY();
if (event.getAction() == MotionEvent.ACTION_UP) {
if (mVelocityTracker != null) {
mVelocityTracker.recycle();
mVelocityTracker=null;
if (motionLayout.mCurrentState != UNSET) {
autoTransition(motionLayout,motionLayout.mCurrentState);
}
}
}
}
int getStartId(){
if (mCurrentTransition == null) {
return -1;
}
return mCurrentTransition.mConstraintSetStart;
}
int getEndId(){
if (mCurrentTransition == null) {
return -1;
}
return mCurrentTransition.mConstraintSetEnd;
}
static final int EASE_IN_OUT=0;
static final int EASE_IN=1;
static final int EASE_OUT=2;
static final int LINEAR=3;
static final int BOUNCE=4;
static final int OVERSHOOT=5;
static final int ANTICIPATE=6;
public Interpolator getInterpolator(){
switch (mCurrentTransition.mDefaultInterpolator) {
case SPLINE_STRING:
final Easing easing=Easing.getInterpolator(mCurrentTransition.mDefaultInterpolatorString);
return new Interpolator(){
public float getInterpolation(float v){
return (float)easing.get(v);
}
}
;
case INTERPOLATOR_REFERENCE_ID:
return AnimationUtils.loadInterpolator(mMotionLayout.getContext(),mCurrentTransition.mDefaultInterpolatorID);
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
public int getDuration(){
if (mCurrentTransition != null) {
return mCurrentTransition.mDuration;
}
return mDefaultDuration;
}
public int gatPathMotionArc(){
return (mCurrentTransition != null) ? mCurrentTransition.mPathMotionArc : UNSET;
}
public float getStaggered(){
if (mCurrentTransition != null) {
return mCurrentTransition.mStagger;
}
return 0;
}
float getMaxAcceleration(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getMaxAcceleration();
}
return 0;
}
float getMaxVelocity(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getMaxVelocity();
}
return 0;
}
float getSpringStiffiness(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getSpringStiffness();
}
return 0;
}
float getSpringMass(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getSpringMass();
}
return 0;
}
float getSpringDamping(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getSpringDamping();
}
return 0;
}
float getSpringStopThreshold(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getSpringStopThreshold();
}
return 0;
}
int getSpringBoundary(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getSpringBoundary();
}
return 0;
}
int getAutoCompleteMode(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
return mCurrentTransition.mTouchResponse.getAutoCompleteMode();
}
return 0;
}
void setupTouch(){
if (mCurrentTransition != null && mCurrentTransition.mTouchResponse != null) {
mCurrentTransition.mTouchResponse.setupTouch();
}
}
void readFallback(MotionLayout motionLayout){
for (int i=0; i < mConstraintSetMap.size(); i++) {
int key=mConstraintSetMap.keyAt(i);
if (hasCycleDependency(key)) {
Log.e(TAG,"Cannot be derived from yourself");
return;
}
readConstraintChain(key,motionLayout);
}
}
private boolean hasCycleDependency(int key){
int derived=mDeriveMap.get(key);
int len=mDeriveMap.size();
while (derived > 0) {
if (derived == key) {
return true;
}
if (len-- < 0) {
return true;
}
derived=mDeriveMap.get(derived);
}
return false;
}
private void readConstraintChain(int key,MotionLayout motionLayout){
ConstraintSet cs=mConstraintSetMap.get(key);
cs.derivedState=cs.mIdString;
int derivedFromId=mDeriveMap.get(key);
if (derivedFromId > 0) {
readConstraintChain(derivedFromId,motionLayout);
ConstraintSet derivedFrom=mConstraintSetMap.get(derivedFromId);
if (derivedFrom == null) {
Log.e(TAG,"ERROR! invalid deriveConstraintsFrom: @id/" + CLDebug.getName(mMotionLayout.getContext(),derivedFromId));
return;
}
cs.derivedState+="/" + derivedFrom.derivedState;
cs.readFallback(derivedFrom);
}
 else {
cs.derivedState+="  layout";
cs.readFallback(motionLayout);
}
cs.applyDeltaFrom(cs);
}
public MotionScene(MotionLayout layout){
mMotionLayout=layout;
mViewTransitionController=new ViewTransitionController(layout);
}
public void putDerivedId(int id,int derivedId){
mDeriveMap.put(id,derivedId);
}
public void copyDerivedContraints(ConstraintSet cs,int key,MotionLayout motionLayout){
cs.derivedState=cs.mIdString;
int derivedFromId=mDeriveMap.get(key);
if (derivedFromId > 0) {
ConstraintSet derivedFrom=mConstraintSetMap.get(derivedFromId);
if (derivedFrom == null) {
Log.e(TAG,"ERROR! invalid deriveConstraintsFrom: @id/" + CLDebug.getName(mMotionLayout.getContext(),derivedFromId));
return;
}
cs.derivedState+="/" + derivedFrom.derivedState;
cs.readFallback(derivedFrom);
}
}
}
