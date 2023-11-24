package androidx.constraintlayout.widget;
import r.android.content.Context;
import r.android.os.Build;
import r.android.os.Build.VERSION_CODES;
import r.android.util.Log;
import r.android.util.SparseArray;
import r.android.view.View;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.HelperWidget;
import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.motion.widget.CLDebug;
import androidx.constraintlayout.widget.ConstraintAttribute.AttributeType;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
public class ConstraintSet {
  private static final String TAG="ConstraintSet";
  private static final String ERROR_MESSAGE="XML parser error must be within a Constraint ";
  private static final int INTERNAL_MATCH_PARENT=-1;
  private static final int INTERNAL_WRAP_CONTENT=-2;
  private static final int INTERNAL_MATCH_CONSTRAINT=-3;
  private static final int INTERNAL_WRAP_CONTENT_CONSTRAINED=-4;
  private boolean mValidate;
  public String mIdString;
  public String derivedState="";
  public static final int ROTATE_NONE=0;
  public static final int ROTATE_PORTRATE_OF_RIGHT=1;
  public static final int ROTATE_PORTRATE_OF_LEFT=2;
  public static final int ROTATE_RIGHT_OF_PORTRATE=3;
  public static final int ROTATE_LEFT_OF_PORTRATE=4;
  public int mRotate=0;
  private HashMap<String,ConstraintAttribute> mSavedAttributes=new HashMap<>();
  private boolean mForceId=true;
  public static final int UNSET=LayoutParams.UNSET;
  public static final int MATCH_CONSTRAINT=ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
  public static final int WRAP_CONTENT=ConstraintLayout.LayoutParams.WRAP_CONTENT;
  public static final int MATCH_CONSTRAINT_WRAP=ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_WRAP;
  public static final int MATCH_CONSTRAINT_SPREAD=ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD;
  public static final int MATCH_CONSTRAINT_PERCENT=ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
  public static final int PARENT_ID=ConstraintLayout.LayoutParams.PARENT_ID;
  public static final int HORIZONTAL=ConstraintLayout.LayoutParams.HORIZONTAL;
  public static final int VERTICAL=ConstraintLayout.LayoutParams.VERTICAL;
  public static final int HORIZONTAL_GUIDELINE=0;
  public static final int VERTICAL_GUIDELINE=1;
  public static final int VISIBLE=View.VISIBLE;
  public static final int INVISIBLE=View.INVISIBLE;
  public static final int GONE=View.GONE;
  public static final int LEFT=ConstraintLayout.LayoutParams.LEFT;
  public static final int RIGHT=ConstraintLayout.LayoutParams.RIGHT;
  public static final int TOP=ConstraintLayout.LayoutParams.TOP;
  public static final int BOTTOM=ConstraintLayout.LayoutParams.BOTTOM;
  public static final int BASELINE=ConstraintLayout.LayoutParams.BASELINE;
  public static final int START=ConstraintLayout.LayoutParams.START;
  public static final int END=ConstraintLayout.LayoutParams.END;
  public static final int CIRCLE_REFERENCE=ConstraintLayout.LayoutParams.CIRCLE;
  public static final int CHAIN_SPREAD=ConstraintLayout.LayoutParams.CHAIN_SPREAD;
  public static final int CHAIN_SPREAD_INSIDE=ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;
  public static final int VISIBILITY_MODE_NORMAL=0;
  public static final int VISIBILITY_MODE_IGNORE=1;
  public static final int CHAIN_PACKED=ConstraintLayout.LayoutParams.CHAIN_PACKED;
  private static final boolean DEBUG=false;
  private static final int BARRIER_TYPE=1;
  private HashMap<Integer,Constraint> mConstraints=new HashMap<Integer,Constraint>();
  private static final int BASELINE_TO_BASELINE=1;
  private static final int BOTTOM_MARGIN=2;
  private static final int BOTTOM_TO_BOTTOM=3;
  private static final int BOTTOM_TO_TOP=4;
  private static final int DIMENSION_RATIO=5;
  private static final int EDITOR_ABSOLUTE_X=6;
  private static final int EDITOR_ABSOLUTE_Y=7;
  private static final int END_MARGIN=8;
  private static final int END_TO_END=9;
  private static final int END_TO_START=10;
  private static final int GONE_BOTTOM_MARGIN=11;
  private static final int GONE_END_MARGIN=12;
  private static final int GONE_LEFT_MARGIN=13;
  private static final int GONE_RIGHT_MARGIN=14;
  private static final int GONE_START_MARGIN=15;
  private static final int GONE_TOP_MARGIN=16;
  private static final int GUIDE_BEGIN=17;
  private static final int GUIDE_END=18;
  private static final int GUIDE_PERCENT=19;
  private static final int HORIZONTAL_BIAS=20;
  private static final int LAYOUT_HEIGHT=21;
  private static final int LAYOUT_VISIBILITY=22;
  private static final int LAYOUT_WIDTH=23;
  private static final int LEFT_MARGIN=24;
  private static final int LEFT_TO_LEFT=25;
  private static final int LEFT_TO_RIGHT=26;
  private static final int ORIENTATION=27;
  private static final int RIGHT_MARGIN=28;
  private static final int RIGHT_TO_LEFT=29;
  private static final int RIGHT_TO_RIGHT=30;
  private static final int START_MARGIN=31;
  private static final int START_TO_END=32;
  private static final int START_TO_START=33;
  private static final int TOP_MARGIN=34;
  private static final int TOP_TO_BOTTOM=35;
  private static final int TOP_TO_TOP=36;
  private static final int VERTICAL_BIAS=37;
  private static final int VIEW_ID=38;
  private static final int HORIZONTAL_WEIGHT=39;
  private static final int VERTICAL_WEIGHT=40;
  private static final int HORIZONTAL_STYLE=41;
  private static final int VERTICAL_STYLE=42;
  private static final int ALPHA=43;
  private static final int ELEVATION=44;
  private static final int ROTATION_X=45;
  private static final int ROTATION_Y=46;
  private static final int SCALE_X=47;
  private static final int SCALE_Y=48;
  private static final int TRANSFORM_PIVOT_X=49;
  private static final int TRANSFORM_PIVOT_Y=50;
  private static final int TRANSLATION_X=51;
  private static final int TRANSLATION_Y=52;
  private static final int TRANSLATION_Z=53;
  private static final int WIDTH_DEFAULT=54;
  private static final int HEIGHT_DEFAULT=55;
  private static final int WIDTH_MAX=56;
  private static final int HEIGHT_MAX=57;
  private static final int WIDTH_MIN=58;
  private static final int HEIGHT_MIN=59;
  private static final int ROTATION=60;
  private static final int CIRCLE=61;
  private static final int CIRCLE_RADIUS=62;
  private static final int CIRCLE_ANGLE=63;
  private static final int ANIMATE_RELATIVE_TO=64;
  private static final int TRANSITION_EASING=65;
  private static final int DRAW_PATH=66;
  private static final int TRANSITION_PATH_ROTATE=67;
  private static final int PROGRESS=68;
  private static final int WIDTH_PERCENT=69;
  private static final int HEIGHT_PERCENT=70;
  private static final int CHAIN_USE_RTL=71;
  private static final int BARRIER_DIRECTION=72;
  private static final int BARRIER_MARGIN=73;
  private static final int CONSTRAINT_REFERENCED_IDS=74;
  private static final int BARRIER_ALLOWS_GONE_WIDGETS=75;
  private static final int PATH_MOTION_ARC=76;
  private static final int CONSTRAINT_TAG=77;
  private static final int VISIBILITY_MODE=78;
  private static final int MOTION_STAGGER=79;
  private static final int CONSTRAINED_WIDTH=80;
  private static final int CONSTRAINED_HEIGHT=81;
  private static final int ANIMATE_CIRCLE_ANGLE_TO=82;
  private static final int TRANSFORM_PIVOT_TARGET=83;
  private static final int QUANTIZE_MOTION_STEPS=84;
  private static final int QUANTIZE_MOTION_PHASE=85;
  private static final int QUANTIZE_MOTION_INTERPOLATOR=86;
  private static final int UNUSED=87;
  private static final int QUANTIZE_MOTION_INTERPOLATOR_TYPE=88;
  private static final int QUANTIZE_MOTION_INTERPOLATOR_ID=89;
  private static final int QUANTIZE_MOTION_INTERPOLATOR_STR=90;
  private static final int BASELINE_TO_TOP=91;
  private static final int BASELINE_TO_BOTTOM=92;
  private static final int BASELINE_MARGIN=93;
  private static final int GONE_BASELINE_MARGIN=94;
  private static final int LAYOUT_CONSTRAINT_WIDTH=95;
  private static final int LAYOUT_CONSTRAINT_HEIGHT=96;
  private static final int LAYOUT_WRAP_BEHAVIOR=97;
  private static final int MOTION_TARGET=98;
  private static final String KEY_WEIGHT="weight";
  private static final String KEY_RATIO="ratio";
  private static final String KEY_PERCENT_PARENT="parent";
  public Constraint getParameters(  int mId){
    return get(mId);
  }
  public void readFallback(  ConstraintSet set){
    for (    Integer key : set.mConstraints.keySet()) {
      int id=key;
      Constraint parent=set.mConstraints.get(key);
      if (!mConstraints.containsKey(id)) {
        mConstraints.put(id,new Constraint());
      }
      Constraint constraint=mConstraints.get(id);
      if (constraint == null) {
        continue;
      }
      if (!constraint.layout.mApply) {
        constraint.layout.copyFrom(parent.layout);
      }
      if (!constraint.propertySet.mApply) {
        constraint.propertySet.copyFrom(parent.propertySet);
      }
      if (!constraint.transform.mApply) {
        constraint.transform.copyFrom(parent.transform);
      }
      if (!constraint.motion.mApply) {
        constraint.motion.copyFrom(parent.motion);
      }
      for (      String s : parent.mCustomConstraints.keySet()) {
        if (!constraint.mCustomConstraints.containsKey(s)) {
          constraint.mCustomConstraints.put(s,parent.mCustomConstraints.get(s));
        }
      }
    }
  }
  public void readFallback(  ConstraintLayout constraintLayout){
    int count=constraintLayout.getChildCount();
    for (int i=0; i < count; i++) {
      View view=constraintLayout.getChildAt(i);
      ConstraintLayout.LayoutParams param=(ConstraintLayout.LayoutParams)view.getLayoutParams();
      int id=view.getId();
      if (mForceId && id == -1) {
        throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
      }
      if (!mConstraints.containsKey(id)) {
        mConstraints.put(id,new Constraint());
      }
      Constraint constraint=mConstraints.get(id);
      if (constraint == null) {
        continue;
      }
      if (!constraint.layout.mApply) {
        constraint.fillFrom(id,param);
        if (view instanceof ConstraintHelper) {
          constraint.layout.mReferenceIds=((ConstraintHelper)view).getReferencedIds();
          if (view instanceof Barrier) {
            Barrier barrier=(Barrier)view;
            constraint.layout.mBarrierAllowsGoneWidgets=barrier.getAllowsGoneWidget();
            constraint.layout.mBarrierDirection=barrier.getType();
            constraint.layout.mBarrierMargin=barrier.getMargin();
          }
        }
        constraint.layout.mApply=true;
      }
      if (!constraint.propertySet.mApply) {
        constraint.propertySet.visibility=view.getVisibility();
        constraint.propertySet.alpha=view.getAlpha();
        constraint.propertySet.mApply=true;
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        if (!constraint.transform.mApply) {
          constraint.transform.mApply=true;
          constraint.transform.rotation=view.getRotation();
          constraint.transform.rotationX=view.getRotationX();
          constraint.transform.rotationY=view.getRotationY();
          constraint.transform.scaleX=view.getScaleX();
          constraint.transform.scaleY=view.getScaleY();
          float pivotX=view.getPivotX();
          float pivotY=view.getPivotY();
          if (pivotX != 0.0 || pivotY != 0.0) {
            constraint.transform.transformPivotX=pivotX;
            constraint.transform.transformPivotY=pivotY;
          }
          constraint.transform.translationX=view.getTranslationX();
          constraint.transform.translationY=view.getTranslationY();
          if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            constraint.transform.translationZ=view.getTranslationZ();
            if (constraint.transform.applyElevation) {
              constraint.transform.elevation=view.getElevation();
            }
          }
        }
      }
    }
  }
  public void applyDeltaFrom(  ConstraintSet cs){
    for (    Constraint from : cs.mConstraints.values()) {
      if (from.mDelta != null) {
        if (from.mTargetString != null) {
          int count=0;
          for (          int key : mConstraints.keySet()) {
            Constraint potential=getConstraint(key);
            if (potential.layout.mConstraintTag != null) {
              if (from.mTargetString.matches(potential.layout.mConstraintTag)) {
                from.mDelta.applyDelta(potential);
                potential.mCustomConstraints.putAll((HashMap)from.mCustomConstraints.clone());
              }
            }
          }
        }
 else {
          Constraint constraint=getConstraint(from.mViewId);
          from.mDelta.applyDelta(constraint);
        }
      }
    }
  }
public static class Layout {
    public boolean mIsGuideline=false;
    public boolean mApply=false;
    public boolean mOverride=false;
    public int mWidth;
    public int mHeight;
    public static final int UNSET=ConstraintSet.UNSET;
    public static final int UNSET_GONE_MARGIN=Integer.MIN_VALUE;
    public int guideBegin=UNSET;
    public int guideEnd=UNSET;
    public float guidePercent=UNSET;
    public int leftToLeft=UNSET;
    public int leftToRight=UNSET;
    public int rightToLeft=UNSET;
    public int rightToRight=UNSET;
    public int topToTop=UNSET;
    public int topToBottom=UNSET;
    public int bottomToTop=UNSET;
    public int bottomToBottom=UNSET;
    public int baselineToBaseline=UNSET;
    public int baselineToTop=UNSET;
    public int baselineToBottom=UNSET;
    public int startToEnd=UNSET;
    public int startToStart=UNSET;
    public int endToStart=UNSET;
    public int endToEnd=UNSET;
    public float horizontalBias=0.5f;
    public float verticalBias=0.5f;
    public String dimensionRatio=null;
    public int circleConstraint=UNSET;
    public int circleRadius=0;
    public float circleAngle=0;
    public int editorAbsoluteX=UNSET;
    public int editorAbsoluteY=UNSET;
    public int orientation=UNSET;
    public int leftMargin=0;
    public int rightMargin=0;
    public int topMargin=0;
    public int bottomMargin=0;
    public int endMargin=0;
    public int startMargin=0;
    public int baselineMargin=0;
    public int goneLeftMargin=UNSET_GONE_MARGIN;
    public int goneTopMargin=UNSET_GONE_MARGIN;
    public int goneRightMargin=UNSET_GONE_MARGIN;
    public int goneBottomMargin=UNSET_GONE_MARGIN;
    public int goneEndMargin=UNSET_GONE_MARGIN;
    public int goneStartMargin=UNSET_GONE_MARGIN;
    public int goneBaselineMargin=UNSET_GONE_MARGIN;
    public float verticalWeight=UNSET;
    public float horizontalWeight=UNSET;
    public int horizontalChainStyle=CHAIN_SPREAD;
    public int verticalChainStyle=CHAIN_SPREAD;
    public int widthDefault=ConstraintWidget.MATCH_CONSTRAINT_SPREAD;
    public int heightDefault=ConstraintWidget.MATCH_CONSTRAINT_SPREAD;
    public int widthMax=UNSET;
    public int heightMax=UNSET;
    public int widthMin=UNSET;
    public int heightMin=UNSET;
    public float widthPercent=1;
    public float heightPercent=1;
    public int mBarrierDirection=UNSET;
    public int mBarrierMargin=0;
    public int mHelperType=UNSET;
    public int[] mReferenceIds;
    public String mReferenceIdString;
    public String mConstraintTag;
    public boolean constrainedWidth=false;
    public boolean constrainedHeight=false;
    public boolean mBarrierAllowsGoneWidgets=true;
    public int mWrapBehavior=ConstraintWidget.WRAP_BEHAVIOR_INCLUDED;
    public void copyFrom(    Layout src){
      mIsGuideline=src.mIsGuideline;
      mWidth=src.mWidth;
      mApply=src.mApply;
      mHeight=src.mHeight;
      guideBegin=src.guideBegin;
      guideEnd=src.guideEnd;
      guidePercent=src.guidePercent;
      leftToLeft=src.leftToLeft;
      leftToRight=src.leftToRight;
      rightToLeft=src.rightToLeft;
      rightToRight=src.rightToRight;
      topToTop=src.topToTop;
      topToBottom=src.topToBottom;
      bottomToTop=src.bottomToTop;
      bottomToBottom=src.bottomToBottom;
      baselineToBaseline=src.baselineToBaseline;
      baselineToTop=src.baselineToTop;
      baselineToBottom=src.baselineToBottom;
      startToEnd=src.startToEnd;
      startToStart=src.startToStart;
      endToStart=src.endToStart;
      endToEnd=src.endToEnd;
      horizontalBias=src.horizontalBias;
      verticalBias=src.verticalBias;
      dimensionRatio=src.dimensionRatio;
      circleConstraint=src.circleConstraint;
      circleRadius=src.circleRadius;
      circleAngle=src.circleAngle;
      editorAbsoluteX=src.editorAbsoluteX;
      editorAbsoluteY=src.editorAbsoluteY;
      orientation=src.orientation;
      leftMargin=src.leftMargin;
      rightMargin=src.rightMargin;
      topMargin=src.topMargin;
      bottomMargin=src.bottomMargin;
      endMargin=src.endMargin;
      startMargin=src.startMargin;
      baselineMargin=src.baselineMargin;
      goneLeftMargin=src.goneLeftMargin;
      goneTopMargin=src.goneTopMargin;
      goneRightMargin=src.goneRightMargin;
      goneBottomMargin=src.goneBottomMargin;
      goneEndMargin=src.goneEndMargin;
      goneStartMargin=src.goneStartMargin;
      goneBaselineMargin=src.goneBaselineMargin;
      verticalWeight=src.verticalWeight;
      horizontalWeight=src.horizontalWeight;
      horizontalChainStyle=src.horizontalChainStyle;
      verticalChainStyle=src.verticalChainStyle;
      widthDefault=src.widthDefault;
      heightDefault=src.heightDefault;
      widthMax=src.widthMax;
      heightMax=src.heightMax;
      widthMin=src.widthMin;
      heightMin=src.heightMin;
      widthPercent=src.widthPercent;
      heightPercent=src.heightPercent;
      mBarrierDirection=src.mBarrierDirection;
      mBarrierMargin=src.mBarrierMargin;
      mHelperType=src.mHelperType;
      mConstraintTag=src.mConstraintTag;
      if (src.mReferenceIds != null) {
        mReferenceIds=Arrays.copyOf(src.mReferenceIds,src.mReferenceIds.length);
      }
 else {
        mReferenceIds=null;
      }
      mReferenceIdString=src.mReferenceIdString;
      constrainedWidth=src.constrainedWidth;
      constrainedHeight=src.constrainedHeight;
      mBarrierAllowsGoneWidgets=src.mBarrierAllowsGoneWidgets;
      mWrapBehavior=src.mWrapBehavior;
    }
    private static final int BASELINE_TO_BASELINE=1;
    private static final int BOTTOM_MARGIN=2;
    private static final int BOTTOM_TO_BOTTOM=3;
    private static final int BOTTOM_TO_TOP=4;
    private static final int DIMENSION_RATIO=5;
    private static final int EDITOR_ABSOLUTE_X=6;
    private static final int EDITOR_ABSOLUTE_Y=7;
    private static final int END_MARGIN=8;
    private static final int END_TO_END=9;
    private static final int END_TO_START=10;
    private static final int GONE_BOTTOM_MARGIN=11;
    private static final int GONE_END_MARGIN=12;
    private static final int GONE_LEFT_MARGIN=13;
    private static final int GONE_RIGHT_MARGIN=14;
    private static final int GONE_START_MARGIN=15;
    private static final int GONE_TOP_MARGIN=16;
    private static final int GUIDE_BEGIN=17;
    private static final int GUIDE_END=18;
    private static final int GUIDE_PERCENT=19;
    private static final int HORIZONTAL_BIAS=20;
    private static final int LAYOUT_HEIGHT=21;
    private static final int LAYOUT_WIDTH=22;
    private static final int LEFT_MARGIN=23;
    private static final int LEFT_TO_LEFT=24;
    private static final int LEFT_TO_RIGHT=25;
    private static final int ORIENTATION=26;
    private static final int RIGHT_MARGIN=27;
    private static final int RIGHT_TO_LEFT=28;
    private static final int RIGHT_TO_RIGHT=29;
    private static final int START_MARGIN=30;
    private static final int START_TO_END=31;
    private static final int START_TO_START=32;
    private static final int TOP_MARGIN=33;
    private static final int TOP_TO_BOTTOM=34;
    private static final int TOP_TO_TOP=35;
    private static final int VERTICAL_BIAS=36;
    private static final int HORIZONTAL_WEIGHT=37;
    private static final int VERTICAL_WEIGHT=38;
    private static final int HORIZONTAL_STYLE=39;
    private static final int VERTICAL_STYLE=40;
    private static final int LAYOUT_CONSTRAINT_WIDTH=41;
    private static final int LAYOUT_CONSTRAINT_HEIGHT=42;
    private static final int CIRCLE=61;
    private static final int CIRCLE_RADIUS=62;
    private static final int CIRCLE_ANGLE=63;
    private static final int WIDTH_PERCENT=69;
    private static final int HEIGHT_PERCENT=70;
    private static final int CHAIN_USE_RTL=71;
    private static final int BARRIER_DIRECTION=72;
    private static final int BARRIER_MARGIN=73;
    private static final int CONSTRAINT_REFERENCED_IDS=74;
    private static final int BARRIER_ALLOWS_GONE_WIDGETS=75;
    private static final int UNUSED=76;
  }
public static class Transform {
    public boolean mApply=false;
    public float rotation=0;
    public float rotationX=0;
    public float rotationY=0;
    public float scaleX=1;
    public float scaleY=1;
    public float transformPivotX=Float.NaN;
    public float transformPivotY=Float.NaN;
    public int transformPivotTarget=UNSET;
    public float translationX=0;
    public float translationY=0;
    public float translationZ=0;
    public boolean applyElevation=false;
    public float elevation=0;
    public void copyFrom(    Transform src){
      mApply=src.mApply;
      rotation=src.rotation;
      rotationX=src.rotationX;
      rotationY=src.rotationY;
      scaleX=src.scaleX;
      scaleY=src.scaleY;
      transformPivotX=src.transformPivotX;
      transformPivotY=src.transformPivotY;
      transformPivotTarget=src.transformPivotTarget;
      translationX=src.translationX;
      translationY=src.translationY;
      translationZ=src.translationZ;
      applyElevation=src.applyElevation;
      elevation=src.elevation;
    }
    private static final int ROTATION=1;
    private static final int ROTATION_X=2;
    private static final int ROTATION_Y=3;
    private static final int SCALE_X=4;
    private static final int SCALE_Y=5;
    private static final int TRANSFORM_PIVOT_X=6;
    private static final int TRANSFORM_PIVOT_Y=7;
    private static final int TRANSLATION_X=8;
    private static final int TRANSLATION_Y=9;
    private static final int TRANSLATION_Z=10;
    private static final int ELEVATION=11;
    private static final int TRANSFORM_PIVOT_TARGET=12;
  }
public static class PropertySet {
    public boolean mApply=false;
    public int visibility=View.VISIBLE;
    public int mVisibilityMode=VISIBILITY_MODE_NORMAL;
    public float alpha=1;
    public float mProgress=Float.NaN;
    public void copyFrom(    PropertySet src){
      mApply=src.mApply;
      visibility=src.visibility;
      alpha=src.alpha;
      mProgress=src.mProgress;
      mVisibilityMode=src.mVisibilityMode;
    }
  }
public static class Motion {
    public boolean mApply=false;
    public int mAnimateRelativeTo=Layout.UNSET;
    public int mAnimateCircleAngleTo=0;
    public String mTransitionEasing=null;
    public int mPathMotionArc=Layout.UNSET;
    public int mDrawPath=0;
    public float mMotionStagger=Float.NaN;
    public int mPolarRelativeTo=Layout.UNSET;
    public float mPathRotate=Float.NaN;
    public float mQuantizeMotionPhase=Float.NaN;
    public int mQuantizeMotionSteps=Layout.UNSET;
    public String mQuantizeInterpolatorString=null;
    public int mQuantizeInterpolatorType=INTERPOLATOR_UNDEFINED;
    public int mQuantizeInterpolatorID=-1;
    private static final int INTERPOLATOR_REFERENCE_ID=-2;
    private static final int SPLINE_STRING=-1;
    private static final int INTERPOLATOR_UNDEFINED=-3;
    public void copyFrom(    Motion src){
      mApply=src.mApply;
      mAnimateRelativeTo=src.mAnimateRelativeTo;
      mTransitionEasing=src.mTransitionEasing;
      mPathMotionArc=src.mPathMotionArc;
      mDrawPath=src.mDrawPath;
      mPathRotate=src.mPathRotate;
      mMotionStagger=src.mMotionStagger;
      mPolarRelativeTo=src.mPolarRelativeTo;
    }
    private static final int TRANSITION_PATH_ROTATE=1;
    private static final int PATH_MOTION_ARC=2;
    private static final int TRANSITION_EASING=3;
    private static final int MOTION_DRAW_PATH=4;
    private static final int ANIMATE_RELATIVE_TO=5;
    private static final int ANIMATE_CIRCLE_ANGLE_TO=6;
    private static final int MOTION_STAGGER=7;
    private static final int QUANTIZE_MOTION_STEPS=8;
    private static final int QUANTIZE_MOTION_PHASE=9;
    private static final int QUANTIZE_MOTION_INTERPOLATOR=10;
  }
public static class Constraint {
    int mViewId;
    String mTargetString;
    public final PropertySet propertySet=new PropertySet();
    public final Motion motion=new Motion();
    public final Layout layout=new Layout();
    public final Transform transform=new Transform();
    public HashMap<String,ConstraintAttribute> mCustomConstraints=new HashMap<>();
    Delta mDelta;
static class Delta {
      private static final int INITIAL_BOOLEAN=4;
      private static final int INITIAL_INT=10;
      private static final int INITIAL_FLOAT=10;
      private static final int INITIAL_STRING=5;
      int[] mTypeInt=new int[INITIAL_INT];
      int[] mValueInt=new int[INITIAL_INT];
      int mCountInt=0;
      int[] mTypeFloat=new int[INITIAL_FLOAT];
      float[] mValueFloat=new float[INITIAL_FLOAT];
      int mCountFloat=0;
      int[] mTypeString=new int[INITIAL_STRING];
      String[] mValueString=new String[INITIAL_STRING];
      int mCountString=0;
      int[] mTypeBoolean=new int[INITIAL_BOOLEAN];
      boolean[] mValueBoolean=new boolean[INITIAL_BOOLEAN];
      int mCountBoolean=0;
      void applyDelta(      Constraint c){
        for (int i=0; i < mCountInt; i++) {
          setDeltaValue(c,mTypeInt[i],mValueInt[i]);
        }
        for (int i=0; i < mCountFloat; i++) {
          setDeltaValue(c,mTypeFloat[i],mValueFloat[i]);
        }
        for (int i=0; i < mCountString; i++) {
          setDeltaValue(c,mTypeString[i],mValueString[i]);
        }
        for (int i=0; i < mCountBoolean; i++) {
          setDeltaValue(c,mTypeBoolean[i],mValueBoolean[i]);
        }
      }
    }
    public void applyDelta(    Constraint c){
      if (mDelta != null) {
        mDelta.applyDelta(c);
      }
    }
    public Constraint clone(){
      Constraint clone=new Constraint();
      clone.layout.copyFrom(layout);
      clone.motion.copyFrom(motion);
      clone.propertySet.copyFrom(propertySet);
      clone.transform.copyFrom(transform);
      clone.mViewId=mViewId;
      clone.mDelta=mDelta;
      return clone;
    }
    public void fillFromConstraints(    ConstraintHelper helper,    int viewId,    Constraints.LayoutParams param){
      fillFromConstraints(viewId,param);
      if (helper instanceof Barrier) {
        layout.mHelperType=BARRIER_TYPE;
        Barrier barrier=(Barrier)helper;
        layout.mBarrierDirection=barrier.getType();
        layout.mReferenceIds=barrier.getReferencedIds();
        layout.mBarrierMargin=barrier.getMargin();
      }
    }
    public void fillFromConstraints(    int viewId,    Constraints.LayoutParams param){
      fillFrom(viewId,param);
      propertySet.alpha=param.alpha;
      transform.rotation=param.rotation;
      transform.rotationX=param.rotationX;
      transform.rotationY=param.rotationY;
      transform.scaleX=param.scaleX;
      transform.scaleY=param.scaleY;
      transform.transformPivotX=param.transformPivotX;
      transform.transformPivotY=param.transformPivotY;
      transform.translationX=param.translationX;
      transform.translationY=param.translationY;
      transform.translationZ=param.translationZ;
      transform.elevation=param.elevation;
      transform.applyElevation=param.applyElevation;
    }
    private void fillFrom(    int viewId,    ConstraintLayout.LayoutParams param){
      mViewId=viewId;
      layout.leftToLeft=param.leftToLeft;
      layout.leftToRight=param.leftToRight;
      layout.rightToLeft=param.rightToLeft;
      layout.rightToRight=param.rightToRight;
      layout.topToTop=param.topToTop;
      layout.topToBottom=param.topToBottom;
      layout.bottomToTop=param.bottomToTop;
      layout.bottomToBottom=param.bottomToBottom;
      layout.baselineToBaseline=param.baselineToBaseline;
      layout.baselineToTop=param.baselineToTop;
      layout.baselineToBottom=param.baselineToBottom;
      layout.startToEnd=param.startToEnd;
      layout.startToStart=param.startToStart;
      layout.endToStart=param.endToStart;
      layout.endToEnd=param.endToEnd;
      layout.horizontalBias=param.horizontalBias;
      layout.verticalBias=param.verticalBias;
      layout.dimensionRatio=param.dimensionRatio;
      layout.circleConstraint=param.circleConstraint;
      layout.circleRadius=param.circleRadius;
      layout.circleAngle=param.circleAngle;
      layout.editorAbsoluteX=param.editorAbsoluteX;
      layout.editorAbsoluteY=param.editorAbsoluteY;
      layout.orientation=param.orientation;
      layout.guidePercent=param.guidePercent;
      layout.guideBegin=param.guideBegin;
      layout.guideEnd=param.guideEnd;
      layout.mWidth=param.width;
      layout.mHeight=param.height;
      layout.leftMargin=param.leftMargin;
      layout.rightMargin=param.rightMargin;
      layout.topMargin=param.topMargin;
      layout.bottomMargin=param.bottomMargin;
      layout.baselineMargin=param.baselineMargin;
      layout.verticalWeight=param.verticalWeight;
      layout.horizontalWeight=param.horizontalWeight;
      layout.verticalChainStyle=param.verticalChainStyle;
      layout.horizontalChainStyle=param.horizontalChainStyle;
      layout.constrainedWidth=param.constrainedWidth;
      layout.constrainedHeight=param.constrainedHeight;
      layout.widthDefault=param.matchConstraintDefaultWidth;
      layout.heightDefault=param.matchConstraintDefaultHeight;
      layout.widthMax=param.matchConstraintMaxWidth;
      layout.heightMax=param.matchConstraintMaxHeight;
      layout.widthMin=param.matchConstraintMinWidth;
      layout.heightMin=param.matchConstraintMinHeight;
      layout.widthPercent=param.matchConstraintPercentWidth;
      layout.heightPercent=param.matchConstraintPercentHeight;
      layout.mConstraintTag=param.constraintTag;
      layout.goneTopMargin=param.goneTopMargin;
      layout.goneBottomMargin=param.goneBottomMargin;
      layout.goneLeftMargin=param.goneLeftMargin;
      layout.goneRightMargin=param.goneRightMargin;
      layout.goneStartMargin=param.goneStartMargin;
      layout.goneEndMargin=param.goneEndMargin;
      layout.goneBaselineMargin=param.goneBaselineMargin;
      layout.mWrapBehavior=param.wrapBehaviorInParent;
      int currentApiVersion=r.android.os.Build.VERSION.SDK_INT;
      if (currentApiVersion >= r.android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
        layout.endMargin=param.getMarginEnd();
        layout.startMargin=param.getMarginStart();
      }
    }
    public void applyTo(    ConstraintLayout.LayoutParams param){
      param.leftToLeft=layout.leftToLeft;
      param.leftToRight=layout.leftToRight;
      param.rightToLeft=layout.rightToLeft;
      param.rightToRight=layout.rightToRight;
      param.topToTop=layout.topToTop;
      param.topToBottom=layout.topToBottom;
      param.bottomToTop=layout.bottomToTop;
      param.bottomToBottom=layout.bottomToBottom;
      param.baselineToBaseline=layout.baselineToBaseline;
      param.baselineToTop=layout.baselineToTop;
      param.baselineToBottom=layout.baselineToBottom;
      param.startToEnd=layout.startToEnd;
      param.startToStart=layout.startToStart;
      param.endToStart=layout.endToStart;
      param.endToEnd=layout.endToEnd;
      param.leftMargin=layout.leftMargin;
      param.rightMargin=layout.rightMargin;
      param.topMargin=layout.topMargin;
      param.bottomMargin=layout.bottomMargin;
      param.goneStartMargin=layout.goneStartMargin;
      param.goneEndMargin=layout.goneEndMargin;
      param.goneTopMargin=layout.goneTopMargin;
      param.goneBottomMargin=layout.goneBottomMargin;
      param.horizontalBias=layout.horizontalBias;
      param.verticalBias=layout.verticalBias;
      param.circleConstraint=layout.circleConstraint;
      param.circleRadius=layout.circleRadius;
      param.circleAngle=layout.circleAngle;
      param.dimensionRatio=layout.dimensionRatio;
      param.editorAbsoluteX=layout.editorAbsoluteX;
      param.editorAbsoluteY=layout.editorAbsoluteY;
      param.verticalWeight=layout.verticalWeight;
      param.horizontalWeight=layout.horizontalWeight;
      param.verticalChainStyle=layout.verticalChainStyle;
      param.horizontalChainStyle=layout.horizontalChainStyle;
      param.constrainedWidth=layout.constrainedWidth;
      param.constrainedHeight=layout.constrainedHeight;
      param.matchConstraintDefaultWidth=layout.widthDefault;
      param.matchConstraintDefaultHeight=layout.heightDefault;
      param.matchConstraintMaxWidth=layout.widthMax;
      param.matchConstraintMaxHeight=layout.heightMax;
      param.matchConstraintMinWidth=layout.widthMin;
      param.matchConstraintMinHeight=layout.heightMin;
      param.matchConstraintPercentWidth=layout.widthPercent;
      param.matchConstraintPercentHeight=layout.heightPercent;
      param.orientation=layout.orientation;
      param.guidePercent=layout.guidePercent;
      param.guideBegin=layout.guideBegin;
      param.guideEnd=layout.guideEnd;
      param.width=layout.mWidth;
      param.height=layout.mHeight;
      if (layout.mConstraintTag != null) {
        param.constraintTag=layout.mConstraintTag;
      }
      param.wrapBehaviorInParent=layout.mWrapBehavior;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        param.setMarginStart(layout.startMargin);
        param.setMarginEnd(layout.endMargin);
      }
      param.validate();
    }
  }
  public void clone(  ConstraintSet set){
    mConstraints.clear();
    for (    Integer key : set.mConstraints.keySet()) {
      Constraint constraint=set.mConstraints.get(key);
      if (constraint == null) {
        continue;
      }
      mConstraints.put(key,constraint.clone());
    }
  }
  public void clone(  ConstraintLayout constraintLayout){
    int count=constraintLayout.getChildCount();
    mConstraints.clear();
    for (int i=0; i < count; i++) {
      View view=constraintLayout.getChildAt(i);
      ConstraintLayout.LayoutParams param=(ConstraintLayout.LayoutParams)view.getLayoutParams();
      int id=view.getId();
      if (mForceId && id == -1) {
        throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
      }
      if (!mConstraints.containsKey(id)) {
        mConstraints.put(id,new Constraint());
      }
      Constraint constraint=mConstraints.get(id);
      if (constraint == null) {
        continue;
      }
      constraint.mCustomConstraints=ConstraintAttribute.extractAttributes(mSavedAttributes,view);
      constraint.fillFrom(id,param);
      constraint.propertySet.visibility=view.getVisibility();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        constraint.propertySet.alpha=view.getAlpha();
        constraint.transform.rotation=view.getRotation();
        constraint.transform.rotationX=view.getRotationX();
        constraint.transform.rotationY=view.getRotationY();
        constraint.transform.scaleX=view.getScaleX();
        constraint.transform.scaleY=view.getScaleY();
        float pivotX=view.getPivotX();
        float pivotY=view.getPivotY();
        if (pivotX != 0.0 || pivotY != 0.0) {
          constraint.transform.transformPivotX=pivotX;
          constraint.transform.transformPivotY=pivotY;
        }
        constraint.transform.translationX=view.getTranslationX();
        constraint.transform.translationY=view.getTranslationY();
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
          constraint.transform.translationZ=view.getTranslationZ();
          if (constraint.transform.applyElevation) {
            constraint.transform.elevation=view.getElevation();
          }
        }
      }
      if (view instanceof Barrier) {
        Barrier barrier=((Barrier)view);
        constraint.layout.mBarrierAllowsGoneWidgets=barrier.getAllowsGoneWidget();
        constraint.layout.mReferenceIds=barrier.getReferencedIds();
        constraint.layout.mBarrierDirection=barrier.getType();
        constraint.layout.mBarrierMargin=barrier.getMargin();
      }
    }
  }
  public void clone(  Constraints constraints){
    int count=constraints.getChildCount();
    mConstraints.clear();
    for (int i=0; i < count; i++) {
      View view=constraints.getChildAt(i);
      Constraints.LayoutParams param=(Constraints.LayoutParams)view.getLayoutParams();
      int id=view.getId();
      if (mForceId && id == -1) {
        throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
      }
      if (!mConstraints.containsKey(id)) {
        mConstraints.put(id,new Constraint());
      }
      Constraint constraint=mConstraints.get(id);
      if (constraint == null) {
        continue;
      }
      if (view instanceof ConstraintHelper) {
        ConstraintHelper helper=(ConstraintHelper)view;
        constraint.fillFromConstraints(helper,id,param);
      }
      constraint.fillFromConstraints(id,param);
    }
  }
  public void applyTo(  ConstraintLayout constraintLayout){
    applyToInternal(constraintLayout,true);
    constraintLayout.setConstraintSet(null);
    constraintLayout.requestLayout();
  }
  public void applyCustomAttributes(  ConstraintLayout constraintLayout){
    int count=constraintLayout.getChildCount();
    for (int i=0; i < count; i++) {
      View view=constraintLayout.getChildAt(i);
      int id=view.getId();
      if (!mConstraints.containsKey(id)) {
        Log.w(TAG,"id unknown " + CLDebug.getName(view));
        continue;
      }
      if (mForceId && id == -1) {
        throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
      }
      if (mConstraints.containsKey(id)) {
        Constraint constraint=mConstraints.get(id);
        if (constraint == null) {
          continue;
        }
        ConstraintAttribute.setAttributes(view,constraint.mCustomConstraints);
      }
    }
  }
  public void applyToHelper(  ConstraintHelper helper,  ConstraintWidget child,  LayoutParams layoutParams,  SparseArray<ConstraintWidget> mapIdToWidget){
    int id=helper.getId();
    if (mConstraints.containsKey(id)) {
      Constraint constraint=mConstraints.get(id);
      if (constraint != null && child instanceof HelperWidget) {
        HelperWidget helperWidget=(HelperWidget)child;
        helper.loadParameters(constraint,helperWidget,layoutParams,mapIdToWidget);
      }
    }
  }
  public void applyToLayoutParams(  int id,  ConstraintLayout.LayoutParams layoutParams){
    if (mConstraints.containsKey(id)) {
      Constraint constraint=mConstraints.get(id);
      if (constraint != null) {
        constraint.applyTo(layoutParams);
      }
    }
  }
  void applyToInternal(  ConstraintLayout constraintLayout,  boolean applyPostLayout){
    int count=constraintLayout.getChildCount();
    HashSet<Integer> used=new HashSet<Integer>(mConstraints.keySet());
    for (int i=0; i < count; i++) {
      View view=constraintLayout.getChildAt(i);
      int id=view.getId();
      if (!mConstraints.containsKey(id)) {
        Log.w(TAG,"id unknown " + CLDebug.getName(view));
        continue;
      }
      if (mForceId && id == -1) {
        throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
      }
      if (id == -1) {
        continue;
      }
      if (mConstraints.containsKey(id)) {
        used.remove(id);
        Constraint constraint=mConstraints.get(id);
        if (constraint == null) {
          continue;
        }
        if (view instanceof Barrier) {
          constraint.layout.mHelperType=BARRIER_TYPE;
          Barrier barrier=(Barrier)view;
          barrier.setId(id);
          barrier.setType(constraint.layout.mBarrierDirection);
          barrier.setMargin(constraint.layout.mBarrierMargin);
          barrier.setAllowsGoneWidget(constraint.layout.mBarrierAllowsGoneWidgets);
          if (constraint.layout.mReferenceIds != null) {
            barrier.setReferencedIds(constraint.layout.mReferenceIds);
          }
 else           if (constraint.layout.mReferenceIdString != null) {
            constraint.layout.mReferenceIds=convertReferenceString(barrier,constraint.layout.mReferenceIdString);
            barrier.setReferencedIds(constraint.layout.mReferenceIds);
          }
        }
        ConstraintLayout.LayoutParams param=(ConstraintLayout.LayoutParams)view.getLayoutParams();
        param.validate();
        constraint.applyTo(param);
        if (applyPostLayout) {
          ConstraintAttribute.setAttributes(view,constraint.mCustomConstraints);
        }
        view.setLayoutParams(param);
        if (constraint.propertySet.mVisibilityMode == VISIBILITY_MODE_NORMAL) {
          view.setVisibility(constraint.propertySet.visibility);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          view.setAlpha(constraint.propertySet.alpha);
          view.setRotation(constraint.transform.rotation);
          view.setRotationX(constraint.transform.rotationX);
          view.setRotationY(constraint.transform.rotationY);
          view.setScaleX(constraint.transform.scaleX);
          view.setScaleY(constraint.transform.scaleY);
          if (constraint.transform.transformPivotTarget != UNSET) {
            View layout=(View)view.getParent();
            View center=layout.findViewById(constraint.transform.transformPivotTarget);
            if (center != null) {
              float cy=(center.getTop() + center.getBottom()) / 2.0f;
              float cx=(center.getLeft() + center.getRight()) / 2.0f;
              if (view.getRight() - view.getLeft() > 0 && view.getBottom() - view.getTop() > 0) {
                float px=(cx - view.getLeft());
                float py=(cy - view.getTop());
                view.setPivotX(px);
                view.setPivotY(py);
              }
            }
          }
 else {
            if (!Float.isNaN(constraint.transform.transformPivotX)) {
              view.setPivotX(constraint.transform.transformPivotX);
            }
            if (!Float.isNaN(constraint.transform.transformPivotY)) {
              view.setPivotY(constraint.transform.transformPivotY);
            }
          }
          view.setTranslationX(constraint.transform.translationX);
          view.setTranslationY(constraint.transform.translationY);
          if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            view.setTranslationZ(constraint.transform.translationZ);
            if (constraint.transform.applyElevation) {
              view.setElevation(constraint.transform.elevation);
            }
          }
        }
      }
 else {
        Log.v(TAG,"WARNING NO CONSTRAINTS for view " + id);
      }
    }
    for (    Integer id : used) {
      Constraint constraint=mConstraints.get(id);
      if (constraint == null) {
        continue;
      }
      if (constraint.layout.mHelperType == BARRIER_TYPE) {
        Barrier barrier=new Barrier(constraintLayout.getContext());
        barrier.setId(id);
        if (constraint.layout.mReferenceIds != null) {
          barrier.setReferencedIds(constraint.layout.mReferenceIds);
        }
 else         if (constraint.layout.mReferenceIdString != null) {
          constraint.layout.mReferenceIds=convertReferenceString(barrier,constraint.layout.mReferenceIdString);
          barrier.setReferencedIds(constraint.layout.mReferenceIds);
        }
        barrier.setType(constraint.layout.mBarrierDirection);
        barrier.setMargin(constraint.layout.mBarrierMargin);
        LayoutParams param=constraintLayout.generateDefaultLayoutParams();
        barrier.validateParams();
        constraint.applyTo(param);
        constraintLayout.addView(barrier,param);
      }
      if (constraint.layout.mIsGuideline) {
        Guideline g=new Guideline(constraintLayout.getContext());
        g.setId(id);
        ConstraintLayout.LayoutParams param=constraintLayout.generateDefaultLayoutParams();
        constraint.applyTo(param);
        constraintLayout.addView(g,param);
      }
    }
    for (int i=0; i < count; i++) {
      View view=constraintLayout.getChildAt(i);
      if (view instanceof ConstraintHelper) {
        ConstraintHelper constraintHelper=(ConstraintHelper)view;
        constraintHelper.applyLayoutFeaturesInConstraintSet(constraintLayout);
      }
    }
  }
  public void center(  int centerID,  int firstID,  int firstSide,  int firstMargin,  int secondId,  int secondSide,  int secondMargin,  float bias){
    if (firstMargin < 0) {
      throw new IllegalArgumentException("margin must be > 0");
    }
    if (secondMargin < 0) {
      throw new IllegalArgumentException("margin must be > 0");
    }
    if (bias <= 0 || bias > 1) {
      throw new IllegalArgumentException("bias must be between 0 and 1 inclusive");
    }
    if (firstSide == LEFT || firstSide == RIGHT) {
      connect(centerID,LEFT,firstID,firstSide,firstMargin);
      connect(centerID,RIGHT,secondId,secondSide,secondMargin);
      Constraint constraint=mConstraints.get(centerID);
      if (constraint != null) {
        constraint.layout.horizontalBias=bias;
      }
    }
 else     if (firstSide == START || firstSide == END) {
      connect(centerID,START,firstID,firstSide,firstMargin);
      connect(centerID,END,secondId,secondSide,secondMargin);
      Constraint constraint=mConstraints.get(centerID);
      if (constraint != null) {
        constraint.layout.horizontalBias=bias;
      }
    }
 else {
      connect(centerID,TOP,firstID,firstSide,firstMargin);
      connect(centerID,BOTTOM,secondId,secondSide,secondMargin);
      Constraint constraint=mConstraints.get(centerID);
      if (constraint != null) {
        constraint.layout.verticalBias=bias;
      }
    }
  }
  public void centerHorizontally(  int centerID,  int leftId,  int leftSide,  int leftMargin,  int rightId,  int rightSide,  int rightMargin,  float bias){
    connect(centerID,LEFT,leftId,leftSide,leftMargin);
    connect(centerID,RIGHT,rightId,rightSide,rightMargin);
    Constraint constraint=mConstraints.get(centerID);
    if (constraint != null) {
      constraint.layout.horizontalBias=bias;
    }
  }
  public void centerHorizontallyRtl(  int centerID,  int startId,  int startSide,  int startMargin,  int endId,  int endSide,  int endMargin,  float bias){
    connect(centerID,START,startId,startSide,startMargin);
    connect(centerID,END,endId,endSide,endMargin);
    Constraint constraint=mConstraints.get(centerID);
    if (constraint != null) {
      constraint.layout.horizontalBias=bias;
    }
  }
  public void centerVertically(  int centerID,  int topId,  int topSide,  int topMargin,  int bottomId,  int bottomSide,  int bottomMargin,  float bias){
    connect(centerID,TOP,topId,topSide,topMargin);
    connect(centerID,BOTTOM,bottomId,bottomSide,bottomMargin);
    Constraint constraint=mConstraints.get(centerID);
    if (constraint != null) {
      constraint.layout.verticalBias=bias;
    }
  }
  public void createVerticalChain(  int topId,  int topSide,  int bottomId,  int bottomSide,  int[] chainIds,  float[] weights,  int style){
    if (chainIds.length < 2) {
      throw new IllegalArgumentException("must have 2 or more widgets in a chain");
    }
    if (weights != null && weights.length != chainIds.length) {
      throw new IllegalArgumentException("must have 2 or more widgets in a chain");
    }
    if (weights != null) {
      get(chainIds[0]).layout.verticalWeight=weights[0];
    }
    get(chainIds[0]).layout.verticalChainStyle=style;
    connect(chainIds[0],TOP,topId,topSide,0);
    for (int i=1; i < chainIds.length; i++) {
      int chainId=chainIds[i];
      connect(chainIds[i],TOP,chainIds[i - 1],BOTTOM,0);
      connect(chainIds[i - 1],BOTTOM,chainIds[i],TOP,0);
      if (weights != null) {
        get(chainIds[i]).layout.verticalWeight=weights[i];
      }
    }
    connect(chainIds[chainIds.length - 1],BOTTOM,bottomId,bottomSide,0);
  }
  public void createHorizontalChain(  int leftId,  int leftSide,  int rightId,  int rightSide,  int[] chainIds,  float[] weights,  int style){
    createHorizontalChain(leftId,leftSide,rightId,rightSide,chainIds,weights,style,LEFT,RIGHT);
  }
  public void createHorizontalChainRtl(  int startId,  int startSide,  int endId,  int endSide,  int[] chainIds,  float[] weights,  int style){
    createHorizontalChain(startId,startSide,endId,endSide,chainIds,weights,style,START,END);
  }
  private void createHorizontalChain(  int leftId,  int leftSide,  int rightId,  int rightSide,  int[] chainIds,  float[] weights,  int style,  int left,  int right){
    if (chainIds.length < 2) {
      throw new IllegalArgumentException("must have 2 or more widgets in a chain");
    }
    if (weights != null && weights.length != chainIds.length) {
      throw new IllegalArgumentException("must have 2 or more widgets in a chain");
    }
    if (weights != null) {
      get(chainIds[0]).layout.horizontalWeight=weights[0];
    }
    get(chainIds[0]).layout.horizontalChainStyle=style;
    connect(chainIds[0],left,leftId,leftSide,UNSET);
    for (int i=1; i < chainIds.length; i++) {
      int chainId=chainIds[i];
      connect(chainIds[i],left,chainIds[i - 1],right,UNSET);
      connect(chainIds[i - 1],right,chainIds[i],left,UNSET);
      if (weights != null) {
        get(chainIds[i]).layout.horizontalWeight=weights[i];
      }
    }
    connect(chainIds[chainIds.length - 1],right,rightId,rightSide,UNSET);
  }
  public void connect(  int startID,  int startSide,  int endID,  int endSide,  int margin){
    if (!mConstraints.containsKey(startID)) {
      mConstraints.put(startID,new Constraint());
    }
    Constraint constraint=mConstraints.get(startID);
    if (constraint == null) {
      return;
    }
switch (startSide) {
case LEFT:
      if (endSide == LEFT) {
        constraint.layout.leftToLeft=endID;
        constraint.layout.leftToRight=Layout.UNSET;
      }
 else       if (endSide == RIGHT) {
        constraint.layout.leftToRight=endID;
        constraint.layout.leftToLeft=Layout.UNSET;
      }
 else {
        throw new IllegalArgumentException("Left to " + sideToString(endSide) + " undefined");
      }
    constraint.layout.leftMargin=margin;
  break;
case RIGHT:
if (endSide == LEFT) {
  constraint.layout.rightToLeft=endID;
  constraint.layout.rightToRight=Layout.UNSET;
}
 else if (endSide == RIGHT) {
  constraint.layout.rightToRight=endID;
  constraint.layout.rightToLeft=Layout.UNSET;
}
 else {
  throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
constraint.layout.rightMargin=margin;
break;
case TOP:
if (endSide == TOP) {
constraint.layout.topToTop=endID;
constraint.layout.topToBottom=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else if (endSide == BOTTOM) {
constraint.layout.topToBottom=endID;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
constraint.layout.topMargin=margin;
break;
case BOTTOM:
if (endSide == BOTTOM) {
constraint.layout.bottomToBottom=endID;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else if (endSide == TOP) {
constraint.layout.bottomToTop=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
constraint.layout.bottomMargin=margin;
break;
case BASELINE:
if (endSide == BASELINE) {
constraint.layout.baselineToBaseline=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topToBottom=Layout.UNSET;
}
 else if (endSide == TOP) {
constraint.layout.baselineToTop=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topToBottom=Layout.UNSET;
}
 else if (endSide == BOTTOM) {
constraint.layout.baselineToBottom=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topToBottom=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
case START:
if (endSide == START) {
constraint.layout.startToStart=endID;
constraint.layout.startToEnd=Layout.UNSET;
}
 else if (endSide == END) {
constraint.layout.startToEnd=endID;
constraint.layout.startToStart=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
constraint.layout.startMargin=margin;
break;
case END:
if (endSide == END) {
constraint.layout.endToEnd=endID;
constraint.layout.endToStart=Layout.UNSET;
}
 else if (endSide == START) {
constraint.layout.endToStart=endID;
constraint.layout.endToEnd=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
constraint.layout.endMargin=margin;
break;
default :
throw new IllegalArgumentException(sideToString(startSide) + " to " + sideToString(endSide)+ " unknown");
}
}
public void connect(int startID,int startSide,int endID,int endSide){
if (!mConstraints.containsKey(startID)) {
mConstraints.put(startID,new Constraint());
}
Constraint constraint=mConstraints.get(startID);
if (constraint == null) {
return;
}
switch (startSide) {
case LEFT:
if (endSide == LEFT) {
constraint.layout.leftToLeft=endID;
constraint.layout.leftToRight=Layout.UNSET;
}
 else if (endSide == RIGHT) {
constraint.layout.leftToRight=endID;
constraint.layout.leftToLeft=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("left to " + sideToString(endSide) + " undefined");
}
break;
case RIGHT:
if (endSide == LEFT) {
constraint.layout.rightToLeft=endID;
constraint.layout.rightToRight=Layout.UNSET;
}
 else if (endSide == RIGHT) {
constraint.layout.rightToRight=endID;
constraint.layout.rightToLeft=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
case TOP:
if (endSide == TOP) {
constraint.layout.topToTop=endID;
constraint.layout.topToBottom=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else if (endSide == BOTTOM) {
constraint.layout.topToBottom=endID;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
case BOTTOM:
if (endSide == BOTTOM) {
constraint.layout.bottomToBottom=endID;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else if (endSide == TOP) {
constraint.layout.bottomToTop=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
case BASELINE:
if (endSide == BASELINE) {
constraint.layout.baselineToBaseline=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topToBottom=Layout.UNSET;
}
 else if (endSide == TOP) {
constraint.layout.baselineToTop=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topToBottom=Layout.UNSET;
}
 else if (endSide == BOTTOM) {
constraint.layout.baselineToBottom=endID;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topToBottom=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
case START:
if (endSide == START) {
constraint.layout.startToStart=endID;
constraint.layout.startToEnd=Layout.UNSET;
}
 else if (endSide == END) {
constraint.layout.startToEnd=endID;
constraint.layout.startToStart=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
case END:
if (endSide == END) {
constraint.layout.endToEnd=endID;
constraint.layout.endToStart=Layout.UNSET;
}
 else if (endSide == START) {
constraint.layout.endToStart=endID;
constraint.layout.endToEnd=Layout.UNSET;
}
 else {
throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
}
break;
default :
throw new IllegalArgumentException(sideToString(startSide) + " to " + sideToString(endSide)+ " unknown");
}
}
public void centerHorizontally(int viewId,int toView){
if (toView == PARENT_ID) {
center(viewId,PARENT_ID,ConstraintSet.LEFT,0,PARENT_ID,ConstraintSet.RIGHT,0,0.5f);
}
 else {
center(viewId,toView,ConstraintSet.RIGHT,0,toView,ConstraintSet.LEFT,0,0.5f);
}
}
public void centerHorizontallyRtl(int viewId,int toView){
if (toView == PARENT_ID) {
center(viewId,PARENT_ID,ConstraintSet.START,0,PARENT_ID,ConstraintSet.END,0,0.5f);
}
 else {
center(viewId,toView,ConstraintSet.END,0,toView,ConstraintSet.START,0,0.5f);
}
}
public void centerVertically(int viewId,int toView){
if (toView == PARENT_ID) {
center(viewId,PARENT_ID,ConstraintSet.TOP,0,PARENT_ID,ConstraintSet.BOTTOM,0,0.5f);
}
 else {
center(viewId,toView,ConstraintSet.BOTTOM,0,toView,ConstraintSet.TOP,0,0.5f);
}
}
public void clear(int viewId){
mConstraints.remove(viewId);
}
public void clear(int viewId,int anchor){
if (mConstraints.containsKey(viewId)) {
Constraint constraint=mConstraints.get(viewId);
if (constraint == null) {
return;
}
switch (anchor) {
case LEFT:
constraint.layout.leftToRight=Layout.UNSET;
constraint.layout.leftToLeft=Layout.UNSET;
constraint.layout.leftMargin=Layout.UNSET;
constraint.layout.goneLeftMargin=Layout.UNSET_GONE_MARGIN;
break;
case RIGHT:
constraint.layout.rightToRight=Layout.UNSET;
constraint.layout.rightToLeft=Layout.UNSET;
constraint.layout.rightMargin=Layout.UNSET;
constraint.layout.goneRightMargin=Layout.UNSET_GONE_MARGIN;
break;
case TOP:
constraint.layout.topToBottom=Layout.UNSET;
constraint.layout.topToTop=Layout.UNSET;
constraint.layout.topMargin=0;
constraint.layout.goneTopMargin=Layout.UNSET_GONE_MARGIN;
break;
case BOTTOM:
constraint.layout.bottomToTop=Layout.UNSET;
constraint.layout.bottomToBottom=Layout.UNSET;
constraint.layout.bottomMargin=0;
constraint.layout.goneBottomMargin=Layout.UNSET_GONE_MARGIN;
break;
case BASELINE:
constraint.layout.baselineToBaseline=Layout.UNSET;
constraint.layout.baselineToTop=Layout.UNSET;
constraint.layout.baselineToBottom=Layout.UNSET;
constraint.layout.baselineMargin=0;
constraint.layout.goneBaselineMargin=Layout.UNSET_GONE_MARGIN;
break;
case START:
constraint.layout.startToEnd=Layout.UNSET;
constraint.layout.startToStart=Layout.UNSET;
constraint.layout.startMargin=0;
constraint.layout.goneStartMargin=Layout.UNSET_GONE_MARGIN;
break;
case END:
constraint.layout.endToStart=Layout.UNSET;
constraint.layout.endToEnd=Layout.UNSET;
constraint.layout.endMargin=0;
constraint.layout.goneEndMargin=Layout.UNSET_GONE_MARGIN;
break;
case CIRCLE_REFERENCE:
constraint.layout.circleAngle=Layout.UNSET;
constraint.layout.circleRadius=Layout.UNSET;
constraint.layout.circleConstraint=Layout.UNSET;
break;
default :
throw new IllegalArgumentException("unknown constraint");
}
}
}
public void setMargin(int viewId,int anchor,int value){
Constraint constraint=get(viewId);
switch (anchor) {
case LEFT:
constraint.layout.leftMargin=value;
break;
case RIGHT:
constraint.layout.rightMargin=value;
break;
case TOP:
constraint.layout.topMargin=value;
break;
case BOTTOM:
constraint.layout.bottomMargin=value;
break;
case BASELINE:
constraint.layout.baselineMargin=value;
break;
case START:
constraint.layout.startMargin=value;
break;
case END:
constraint.layout.endMargin=value;
break;
default :
throw new IllegalArgumentException("unknown constraint");
}
}
public void setGoneMargin(int viewId,int anchor,int value){
Constraint constraint=get(viewId);
switch (anchor) {
case LEFT:
constraint.layout.goneLeftMargin=value;
break;
case RIGHT:
constraint.layout.goneRightMargin=value;
break;
case TOP:
constraint.layout.goneTopMargin=value;
break;
case BOTTOM:
constraint.layout.goneBottomMargin=value;
break;
case BASELINE:
constraint.layout.goneBaselineMargin=value;
break;
case START:
constraint.layout.goneStartMargin=value;
break;
case END:
constraint.layout.goneEndMargin=value;
break;
default :
throw new IllegalArgumentException("unknown constraint");
}
}
public void setHorizontalBias(int viewId,float bias){
get(viewId).layout.horizontalBias=bias;
}
public void setVerticalBias(int viewId,float bias){
get(viewId).layout.verticalBias=bias;
}
public void setDimensionRatio(int viewId,String ratio){
get(viewId).layout.dimensionRatio=ratio;
}
public void setVisibility(int viewId,int visibility){
get(viewId).propertySet.visibility=visibility;
}
public int getVisibilityMode(int viewId){
return get(viewId).propertySet.mVisibilityMode;
}
public int getVisibility(int viewId){
return get(viewId).propertySet.visibility;
}
public int getHeight(int viewId){
return get(viewId).layout.mHeight;
}
public int getWidth(int viewId){
return get(viewId).layout.mWidth;
}
public void setAlpha(int viewId,float alpha){
get(viewId).propertySet.alpha=alpha;
}
public boolean getApplyElevation(int viewId){
return get(viewId).transform.applyElevation;
}
public void setApplyElevation(int viewId,boolean apply){
if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
get(viewId).transform.applyElevation=apply;
}
}
public void setElevation(int viewId,float elevation){
if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
get(viewId).transform.elevation=elevation;
get(viewId).transform.applyElevation=true;
}
}
public void setRotation(int viewId,float rotation){
get(viewId).transform.rotation=rotation;
}
public void setRotationX(int viewId,float rotationX){
get(viewId).transform.rotationX=rotationX;
}
public void setRotationY(int viewId,float rotationY){
get(viewId).transform.rotationY=rotationY;
}
public void setScaleX(int viewId,float scaleX){
get(viewId).transform.scaleX=scaleX;
}
public void setScaleY(int viewId,float scaleY){
get(viewId).transform.scaleY=scaleY;
}
public void setTransformPivotX(int viewId,float transformPivotX){
get(viewId).transform.transformPivotX=transformPivotX;
}
public void setTransformPivotY(int viewId,float transformPivotY){
get(viewId).transform.transformPivotY=transformPivotY;
}
public void setTransformPivot(int viewId,float transformPivotX,float transformPivotY){
Constraint constraint=get(viewId);
constraint.transform.transformPivotY=transformPivotY;
constraint.transform.transformPivotX=transformPivotX;
}
public void setTranslationX(int viewId,float translationX){
get(viewId).transform.translationX=translationX;
}
public void setTranslationY(int viewId,float translationY){
get(viewId).transform.translationY=translationY;
}
public void setTranslation(int viewId,float translationX,float translationY){
Constraint constraint=get(viewId);
constraint.transform.translationX=translationX;
constraint.transform.translationY=translationY;
}
public void setTranslationZ(int viewId,float translationZ){
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
get(viewId).transform.translationZ=translationZ;
}
}
public void constrainHeight(int viewId,int height){
get(viewId).layout.mHeight=height;
}
public void constrainWidth(int viewId,int width){
get(viewId).layout.mWidth=width;
}
public void constrainCircle(int viewId,int id,int radius,float angle){
Constraint constraint=get(viewId);
constraint.layout.circleConstraint=id;
constraint.layout.circleRadius=radius;
constraint.layout.circleAngle=angle;
}
public void constrainMaxHeight(int viewId,int height){
get(viewId).layout.heightMax=height;
}
public void constrainMaxWidth(int viewId,int width){
get(viewId).layout.widthMax=width;
}
public void constrainMinHeight(int viewId,int height){
get(viewId).layout.heightMin=height;
}
public void constrainMinWidth(int viewId,int width){
get(viewId).layout.widthMin=width;
}
public void constrainPercentWidth(int viewId,float percent){
get(viewId).layout.widthPercent=percent;
}
public void constrainPercentHeight(int viewId,float percent){
get(viewId).layout.heightPercent=percent;
}
public void constrainDefaultHeight(int viewId,int height){
get(viewId).layout.heightDefault=height;
}
public void constrainDefaultWidth(int viewId,int width){
get(viewId).layout.widthDefault=width;
}
public void setHorizontalWeight(int viewId,float weight){
get(viewId).layout.horizontalWeight=weight;
}
public void setVerticalWeight(int viewId,float weight){
get(viewId).layout.verticalWeight=weight;
}
public void setHorizontalChainStyle(int viewId,int chainStyle){
get(viewId).layout.horizontalChainStyle=chainStyle;
}
public void setVerticalChainStyle(int viewId,int chainStyle){
get(viewId).layout.verticalChainStyle=chainStyle;
}
public void addToHorizontalChain(int viewId,int leftId,int rightId){
connect(viewId,LEFT,leftId,(leftId == PARENT_ID) ? LEFT : RIGHT,0);
connect(viewId,RIGHT,rightId,(rightId == PARENT_ID) ? RIGHT : LEFT,0);
if (leftId != PARENT_ID) {
connect(leftId,RIGHT,viewId,LEFT,0);
}
if (rightId != PARENT_ID) {
connect(rightId,LEFT,viewId,RIGHT,0);
}
}
public void addToHorizontalChainRTL(int viewId,int leftId,int rightId){
connect(viewId,START,leftId,(leftId == PARENT_ID) ? START : END,0);
connect(viewId,END,rightId,(rightId == PARENT_ID) ? END : START,0);
if (leftId != PARENT_ID) {
connect(leftId,END,viewId,START,0);
}
if (rightId != PARENT_ID) {
connect(rightId,START,viewId,END,0);
}
}
public void addToVerticalChain(int viewId,int topId,int bottomId){
connect(viewId,TOP,topId,(topId == PARENT_ID) ? TOP : BOTTOM,0);
connect(viewId,BOTTOM,bottomId,(bottomId == PARENT_ID) ? BOTTOM : TOP,0);
if (topId != PARENT_ID) {
connect(topId,BOTTOM,viewId,TOP,0);
}
if (bottomId != PARENT_ID) {
connect(bottomId,TOP,viewId,BOTTOM,0);
}
}
public void removeFromVerticalChain(int viewId){
if (mConstraints.containsKey(viewId)) {
Constraint constraint=mConstraints.get(viewId);
if (constraint == null) {
return;
}
int topId=constraint.layout.topToBottom;
int bottomId=constraint.layout.bottomToTop;
if (topId != Layout.UNSET || bottomId != Layout.UNSET) {
if (topId != Layout.UNSET && bottomId != Layout.UNSET) {
connect(topId,BOTTOM,bottomId,TOP,0);
connect(bottomId,TOP,topId,BOTTOM,0);
}
 else if (constraint.layout.bottomToBottom != Layout.UNSET) {
connect(topId,BOTTOM,constraint.layout.bottomToBottom,BOTTOM,0);
}
 else if (constraint.layout.topToTop != Layout.UNSET) {
connect(bottomId,TOP,constraint.layout.topToTop,TOP,0);
}
}
}
clear(viewId,TOP);
clear(viewId,BOTTOM);
}
public void removeFromHorizontalChain(int viewId){
if (mConstraints.containsKey(viewId)) {
Constraint constraint=mConstraints.get(viewId);
if (constraint == null) {
return;
}
int leftId=constraint.layout.leftToRight;
int rightId=constraint.layout.rightToLeft;
if (leftId != Layout.UNSET || rightId != Layout.UNSET) {
if (leftId != Layout.UNSET && rightId != Layout.UNSET) {
connect(leftId,RIGHT,rightId,LEFT,0);
connect(rightId,LEFT,leftId,RIGHT,0);
}
 else if (constraint.layout.rightToRight != Layout.UNSET) {
connect(leftId,RIGHT,constraint.layout.rightToRight,RIGHT,0);
}
 else if (constraint.layout.leftToLeft != Layout.UNSET) {
connect(rightId,LEFT,constraint.layout.leftToLeft,LEFT,0);
}
clear(viewId,LEFT);
clear(viewId,RIGHT);
}
 else {
int startId=constraint.layout.startToEnd;
int endId=constraint.layout.endToStart;
if (startId != Layout.UNSET || endId != Layout.UNSET) {
if (startId != Layout.UNSET && endId != Layout.UNSET) {
connect(startId,END,endId,START,0);
connect(endId,START,leftId,END,0);
}
 else if (endId != Layout.UNSET) {
if (constraint.layout.rightToRight != Layout.UNSET) {
connect(leftId,END,constraint.layout.rightToRight,END,0);
}
 else if (constraint.layout.leftToLeft != Layout.UNSET) {
connect(endId,START,constraint.layout.leftToLeft,START,0);
}
}
}
clear(viewId,START);
clear(viewId,END);
}
}
}
public void create(int guidelineID,int orientation){
Constraint constraint=get(guidelineID);
constraint.layout.mIsGuideline=true;
constraint.layout.orientation=orientation;
}
public void createBarrier(int id,int direction,int margin,int... referenced){
Constraint constraint=get(id);
constraint.layout.mHelperType=BARRIER_TYPE;
constraint.layout.mBarrierDirection=direction;
constraint.layout.mBarrierMargin=margin;
constraint.layout.mIsGuideline=false;
constraint.layout.mReferenceIds=referenced;
}
public void setGuidelineBegin(int guidelineID,int margin){
get(guidelineID).layout.guideBegin=margin;
get(guidelineID).layout.guideEnd=Layout.UNSET;
get(guidelineID).layout.guidePercent=Layout.UNSET;
}
public void setGuidelineEnd(int guidelineID,int margin){
get(guidelineID).layout.guideEnd=margin;
get(guidelineID).layout.guideBegin=Layout.UNSET;
get(guidelineID).layout.guidePercent=Layout.UNSET;
}
public void setGuidelinePercent(int guidelineID,float ratio){
get(guidelineID).layout.guidePercent=ratio;
get(guidelineID).layout.guideEnd=Layout.UNSET;
get(guidelineID).layout.guideBegin=Layout.UNSET;
}
public void setBarrierType(int id,int type){
Constraint constraint=get(id);
constraint.layout.mHelperType=type;
}
public Constraint get(int id){
if (!mConstraints.containsKey(id)) {
mConstraints.put(id,new Constraint());
}
return mConstraints.get(id);
}
private String sideToString(int side){
switch (side) {
case LEFT:
return "left";
case RIGHT:
return "right";
case TOP:
return "top";
case BOTTOM:
return "bottom";
case BASELINE:
return "baseline";
case START:
return "start";
case END:
return "end";
}
return "undefined";
}
private static void setDeltaValue(Constraint c,int type,float value){
switch (type) {
case GUIDE_PERCENT:
c.layout.guidePercent=value;
break;
case CIRCLE_ANGLE:
c.layout.circleAngle=value;
break;
case HORIZONTAL_BIAS:
c.layout.horizontalBias=value;
break;
case VERTICAL_BIAS:
c.layout.verticalBias=value;
break;
case ALPHA:
c.propertySet.alpha=value;
break;
case ELEVATION:
c.transform.elevation=value;
c.transform.applyElevation=true;
break;
case ROTATION:
c.transform.rotation=value;
break;
case ROTATION_X:
c.transform.rotationX=value;
break;
case ROTATION_Y:
c.transform.rotationY=value;
break;
case SCALE_X:
c.transform.scaleX=value;
break;
case SCALE_Y:
c.transform.scaleY=value;
break;
case TRANSFORM_PIVOT_X:
c.transform.transformPivotX=value;
break;
case TRANSFORM_PIVOT_Y:
c.transform.transformPivotY=value;
break;
case TRANSLATION_X:
c.transform.translationX=value;
break;
case TRANSLATION_Y:
c.transform.translationY=value;
break;
case TRANSLATION_Z:
c.transform.translationZ=value;
break;
case VERTICAL_WEIGHT:
c.layout.verticalWeight=value;
break;
case HORIZONTAL_WEIGHT:
c.layout.horizontalWeight=value;
break;
case WIDTH_PERCENT:
c.layout.widthPercent=value;
break;
case HEIGHT_PERCENT:
c.layout.heightPercent=value;
break;
case PROGRESS:
c.propertySet.mProgress=value;
break;
case TRANSITION_PATH_ROTATE:
c.motion.mPathRotate=value;
break;
case MOTION_STAGGER:
c.motion.mMotionStagger=value;
break;
case QUANTIZE_MOTION_PHASE:
c.motion.mQuantizeMotionPhase=value;
break;
case UNUSED:
break;
default :
Log.w(TAG,"Unknown attribute 0x");
}
}
private static void setDeltaValue(Constraint c,int type,int value){
switch (type) {
case EDITOR_ABSOLUTE_X:
c.layout.editorAbsoluteX=value;
break;
case EDITOR_ABSOLUTE_Y:
c.layout.editorAbsoluteY=value;
break;
case LAYOUT_WRAP_BEHAVIOR:
c.layout.mWrapBehavior=value;
break;
case GUIDE_BEGIN:
c.layout.guideBegin=value;
break;
case GUIDE_END:
c.layout.guideEnd=value;
break;
case ORIENTATION:
c.layout.orientation=value;
break;
case CIRCLE:
c.layout.circleConstraint=value;
break;
case CIRCLE_RADIUS:
c.layout.circleRadius=value;
break;
case GONE_LEFT_MARGIN:
c.layout.goneLeftMargin=value;
break;
case GONE_TOP_MARGIN:
c.layout.goneTopMargin=value;
break;
case GONE_RIGHT_MARGIN:
c.layout.goneRightMargin=value;
break;
case GONE_BOTTOM_MARGIN:
c.layout.goneBottomMargin=value;
break;
case GONE_START_MARGIN:
c.layout.goneStartMargin=value;
break;
case GONE_END_MARGIN:
c.layout.goneEndMargin=value;
break;
case GONE_BASELINE_MARGIN:
c.layout.goneBaselineMargin=value;
break;
case LEFT_MARGIN:
c.layout.leftMargin=value;
break;
case RIGHT_MARGIN:
c.layout.rightMargin=value;
break;
case START_MARGIN:
c.layout.startMargin=value;
break;
case END_MARGIN:
c.layout.endMargin=value;
break;
case TOP_MARGIN:
c.layout.topMargin=value;
break;
case BOTTOM_MARGIN:
c.layout.bottomMargin=value;
break;
case BASELINE_MARGIN:
c.layout.baselineMargin=value;
break;
case LAYOUT_WIDTH:
c.layout.mWidth=value;
break;
case LAYOUT_HEIGHT:
c.layout.mHeight=value;
break;
case WIDTH_DEFAULT:
c.layout.widthDefault=value;
break;
case HEIGHT_DEFAULT:
c.layout.heightDefault=value;
break;
case HEIGHT_MAX:
c.layout.heightMax=value;
break;
case WIDTH_MAX:
c.layout.widthMax=value;
break;
case HEIGHT_MIN:
c.layout.heightMin=value;
break;
case WIDTH_MIN:
c.layout.widthMin=value;
break;
case LAYOUT_VISIBILITY:
c.propertySet.visibility=value;
break;
case VISIBILITY_MODE:
c.propertySet.mVisibilityMode=value;
break;
case TRANSFORM_PIVOT_TARGET:
c.transform.transformPivotTarget=value;
break;
case VERTICAL_STYLE:
c.layout.verticalChainStyle=value;
break;
case HORIZONTAL_STYLE:
c.layout.horizontalChainStyle=value;
break;
case VIEW_ID:
c.mViewId=value;
break;
case ANIMATE_RELATIVE_TO:
c.motion.mAnimateRelativeTo=value;
break;
case ANIMATE_CIRCLE_ANGLE_TO:
c.motion.mAnimateCircleAngleTo=value;
break;
case PATH_MOTION_ARC:
c.motion.mPathMotionArc=value;
break;
case QUANTIZE_MOTION_STEPS:
c.motion.mQuantizeMotionSteps=value;
break;
case QUANTIZE_MOTION_INTERPOLATOR_TYPE:
c.motion.mQuantizeInterpolatorType=value;
break;
case QUANTIZE_MOTION_INTERPOLATOR_ID:
c.motion.mQuantizeInterpolatorID=value;
break;
case DRAW_PATH:
c.motion.mDrawPath=value;
break;
case BARRIER_DIRECTION:
c.layout.mBarrierDirection=value;
break;
case BARRIER_MARGIN:
c.layout.mBarrierMargin=value;
break;
case UNUSED:
break;
default :
Log.w(TAG,"Unknown attribute 0x");
}
}
private static void setDeltaValue(Constraint c,int type,String value){
switch (type) {
case DIMENSION_RATIO:
c.layout.dimensionRatio=value;
break;
case TRANSITION_EASING:
c.motion.mTransitionEasing=value;
break;
case QUANTIZE_MOTION_INTERPOLATOR_STR:
c.motion.mQuantizeInterpolatorString=value;
break;
case CONSTRAINT_REFERENCED_IDS:
c.layout.mReferenceIdString=value;
break;
case CONSTRAINT_TAG:
c.layout.mConstraintTag=value;
break;
case UNUSED:
break;
default :
Log.w(TAG,"Unknown attribute 0x");
}
}
private static void setDeltaValue(Constraint c,int type,boolean value){
switch (type) {
case CONSTRAINED_WIDTH:
c.layout.constrainedWidth=value;
break;
case CONSTRAINED_HEIGHT:
c.layout.constrainedHeight=value;
break;
case ELEVATION:
c.transform.applyElevation=value;
break;
case BARRIER_ALLOWS_GONE_WIDGETS:
c.layout.mBarrierAllowsGoneWidgets=value;
break;
case UNUSED:
break;
default :
Log.w(TAG,"Unknown attribute 0x");
}
}
private int[] convertReferenceString(View view,String referenceIdString){
String[] split=referenceIdString.split(",");
Context context=view.getContext();
int[] tags=new int[split.length];
int count=0;
for (int i=0; i < split.length; i++) {
String idString=split[i];
idString=idString.trim();
int tag=0;
try {
Class res=r.android.R.id.class;
//Field field=res.getField(idString);
//tag=field.getInt(null);
}
 catch (Exception e) {
}
if (tag == 0) {
tag=context.getResources().getIdentifier(idString,"id",context.getPackageName());
}
if (tag == 0 && view.isInEditMode() && view.getParent() instanceof ConstraintLayout) {
ConstraintLayout constraintLayout=(ConstraintLayout)view.getParent();
Object value=constraintLayout.getDesignInformation(0,idString);
if (value != null && value instanceof Integer) {
tag=(Integer)value;
}
}
tags[count++]=tag;
}
if (count != split.length) {
tags=Arrays.copyOf(tags,count);
}
return tags;
}
public Constraint getConstraint(int id){
if (mConstraints.containsKey(id)) {
return mConstraints.get(id);
}
return null;
}
public boolean isForceId(){
return mForceId;
}
public void setForceId(boolean forceId){
this.mForceId=forceId;
}
public void setValidateOnParse(boolean validate){
mValidate=validate;
}
}
