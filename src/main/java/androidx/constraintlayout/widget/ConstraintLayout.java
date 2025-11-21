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
 * Copyright (C) 2015 The Android Open Source Project
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
import r.android.content.pm.ApplicationInfo;
import r.android.content.res.Resources;
import r.android.os.Build;
import androidx.constraintlayout.core.LinearSystem;
import androidx.constraintlayout.core.Metrics;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.CoreGuideline;
import androidx.constraintlayout.core.widgets.Optimizer;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.ViewCompat;
import r.android.util.SparseArray;
import r.android.view.View;
import r.android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.*;
import static r.android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static r.android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
public class ConstraintLayout extends ViewGroup {
  public static final String VERSION="ConstraintLayout-2.1.0";
  private static final String TAG="ConstraintLayout";
  private static final boolean USE_CONSTRAINTS_HELPER=true;
  private static final boolean DEBUG=LinearSystem.FULL_DEBUG;
  private static final boolean DEBUG_DRAW_CONSTRAINTS=false;
  private static final boolean MEASURE=false;
  private static final boolean OPTIMIZE_HEIGHT_CHANGE=false;
  SparseArray<View> mChildrenByIds=new SparseArray<>();
  private ArrayList<ConstraintHelper> mConstraintHelpers=new ArrayList<>(4);
  protected ConstraintWidgetContainer mLayoutWidget=new ConstraintWidgetContainer();
  private int mMinWidth=0;
  private int mMinHeight=0;
  private int mMaxWidth=Integer.MAX_VALUE;
  private int mMaxHeight=Integer.MAX_VALUE;
  protected boolean mDirtyHierarchy=true;
  private int mOptimizationLevel=Optimizer.OPTIMIZATION_STANDARD;
  private ConstraintSet mConstraintSet=null;
  protected ConstraintLayoutStates mConstraintLayoutSpec=null;
  private int mConstraintSetId=-1;
  private HashMap<String,Integer> mDesignIds=new HashMap<>();
  private int mLastMeasureWidth=-1;
  private int mLastMeasureHeight=-1;
  int mLastMeasureWidthSize=-1;
  int mLastMeasureHeightSize=-1;
  int mLastMeasureWidthMode=MeasureSpec.UNSPECIFIED;
  int mLastMeasureHeightMode=MeasureSpec.UNSPECIFIED;
  private SparseArray<ConstraintWidget> mTempMapIdToWidget=new SparseArray<>();
  public final static int DESIGN_INFO_ID=0;
  private Metrics mMetrics;
  private static SharedValues sSharedValues=null;
  public static SharedValues getSharedValues(){
    if (sSharedValues == null) {
      sSharedValues=new SharedValues();
    }
    return sSharedValues;
  }
  public void setDesignInformation(  int type,  Object value1,  Object value2){
    if (type == DESIGN_INFO_ID && value1 instanceof String && value2 instanceof Integer) {
      if (mDesignIds == null) {
        mDesignIds=new HashMap<>();
      }
      String name=(String)value1;
      int index=name.indexOf("/");
      if (index != -1) {
        name=name.substring(index + 1);
      }
      int id=(Integer)value2;
      mDesignIds.put(name,id);
    }
  }
  public Object getDesignInformation(  int type,  Object value){
    if (type == DESIGN_INFO_ID && value instanceof String) {
      String name=(String)value;
      if (mDesignIds != null && mDesignIds.containsKey(name)) {
        return mDesignIds.get(name);
      }
    }
    return null;
  }
@com.google.j2objc.annotations.WeakOuter class Measurer implements BasicMeasure.Measurer {
    @com.google.j2objc.annotations.Weak ConstraintLayout layout;
    int paddingTop;
    int paddingBottom;
    int paddingWidth;
    int paddingHeight;
    int layoutWidthSpec;
    int layoutHeightSpec;
    public void captureLayoutInfo(    int widthSpec,    int heightSpec,    int top,    int bottom,    int width,    int height){
      paddingTop=top;
      paddingBottom=bottom;
      paddingWidth=width;
      paddingHeight=height;
      layoutWidthSpec=widthSpec;
      layoutHeightSpec=heightSpec;
    }
    public Measurer(    ConstraintLayout l){
      layout=l;
    }
    public final void measure(    ConstraintWidget widget,    BasicMeasure.Measure measure){
      if (widget == null) {
        return;
      }
      if (widget.getVisibility() == GONE && !widget.isInPlaceholder()) {
        measure.measuredWidth=0;
        measure.measuredHeight=0;
        measure.measuredBaseline=0;
        return;
      }
      if (widget.getParent() == null) {
        return;
      }
      long startMeasure;
      long endMeasure;
      if (MEASURE) {
        startMeasure=System.nanoTime();
      }
      ConstraintWidget.DimensionBehaviour horizontalBehavior=measure.horizontalBehavior;
      ConstraintWidget.DimensionBehaviour verticalBehavior=measure.verticalBehavior;
      int horizontalDimension=measure.horizontalDimension;
      int verticalDimension=measure.verticalDimension;
      int horizontalSpec=0;
      int verticalSpec=0;
      int heightPadding=paddingTop + paddingBottom;
      int widthPadding=paddingWidth;
      View child=(View)widget.getCompanionWidget();
switch (horizontalBehavior) {
case FIXED:
{
          horizontalSpec=MeasureSpec.makeMeasureSpec(horizontalDimension,MeasureSpec.EXACTLY);
        }
      break;
case WRAP_CONTENT:
{
      horizontalSpec=getChildMeasureSpec(layoutWidthSpec,widthPadding,WRAP_CONTENT);
    }
  break;
case MATCH_PARENT:
{
  horizontalSpec=getChildMeasureSpec(layoutWidthSpec,widthPadding + widget.getHorizontalMargin(),LayoutParams.MATCH_PARENT);
}
break;
case MATCH_CONSTRAINT:
{
horizontalSpec=getChildMeasureSpec(layoutWidthSpec,widthPadding,WRAP_CONTENT);
boolean shouldDoWrap=widget.mMatchConstraintDefaultWidth == MATCH_CONSTRAINT_WRAP;
if (measure.measureStrategy == BasicMeasure.Measure.TRY_GIVEN_DIMENSIONS || measure.measureStrategy == BasicMeasure.Measure.USE_GIVEN_DIMENSIONS) {
boolean otherDimensionStable=child.getMeasuredHeight() == widget.getHeight();
boolean useCurrent=measure.measureStrategy == BasicMeasure.Measure.USE_GIVEN_DIMENSIONS || !shouldDoWrap || (shouldDoWrap && otherDimensionStable) || (child instanceof Placeholder) || (widget.isResolvedHorizontally());
if (useCurrent) {
  horizontalSpec=MeasureSpec.makeMeasureSpec(widget.getWidth(),MeasureSpec.EXACTLY);
}
}
}
break;
}
switch (verticalBehavior) {
case FIXED:
{
verticalSpec=MeasureSpec.makeMeasureSpec(verticalDimension,MeasureSpec.EXACTLY);
}
break;
case WRAP_CONTENT:
{
verticalSpec=getChildMeasureSpec(layoutHeightSpec,heightPadding,WRAP_CONTENT);
}
break;
case MATCH_PARENT:
{
verticalSpec=getChildMeasureSpec(layoutHeightSpec,heightPadding + widget.getVerticalMargin(),LayoutParams.MATCH_PARENT);
}
break;
case MATCH_CONSTRAINT:
{
verticalSpec=getChildMeasureSpec(layoutHeightSpec,heightPadding,WRAP_CONTENT);
boolean shouldDoWrap=widget.mMatchConstraintDefaultHeight == MATCH_CONSTRAINT_WRAP;
if (measure.measureStrategy == BasicMeasure.Measure.TRY_GIVEN_DIMENSIONS || measure.measureStrategy == BasicMeasure.Measure.USE_GIVEN_DIMENSIONS) {
boolean otherDimensionStable=child.getMeasuredWidth() == widget.getWidth();
boolean useCurrent=measure.measureStrategy == BasicMeasure.Measure.USE_GIVEN_DIMENSIONS || !shouldDoWrap || (shouldDoWrap && otherDimensionStable) || (child instanceof Placeholder) || (widget.isResolvedVertically());
if (useCurrent) {
verticalSpec=MeasureSpec.makeMeasureSpec(widget.getHeight(),MeasureSpec.EXACTLY);
}
}
}
break;
}
ConstraintWidgetContainer container=(ConstraintWidgetContainer)widget.getParent();
if (container != null && Optimizer.enabled(mOptimizationLevel,Optimizer.OPTIMIZATION_CACHE_MEASURES)) {
if (child.getMeasuredWidth() == widget.getWidth() && child.getMeasuredWidth() < container.getWidth() && child.getMeasuredHeight() == widget.getHeight() && child.getMeasuredHeight() < container.getHeight() && child.getBaseline() == widget.getBaselineDistance() && !widget.isMeasureRequested()) {
boolean similar=isSimilarSpec(widget.getLastHorizontalMeasureSpec(),horizontalSpec,widget.getWidth()) && isSimilarSpec(widget.getLastVerticalMeasureSpec(),verticalSpec,widget.getHeight());
if (similar) {
measure.measuredWidth=widget.getWidth();
measure.measuredHeight=widget.getHeight();
measure.measuredBaseline=widget.getBaselineDistance();
if (DEBUG) {
System.out.println("SKIPPED " + widget);
}
return;
}
}
}
boolean horizontalMatchConstraints=(horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
boolean verticalMatchConstraints=(verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
boolean verticalDimensionKnown=(verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT || verticalBehavior == ConstraintWidget.DimensionBehaviour.FIXED);
boolean horizontalDimensionKnown=(horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT || horizontalBehavior == ConstraintWidget.DimensionBehaviour.FIXED);
boolean horizontalUseRatio=horizontalMatchConstraints && widget.mDimensionRatio > 0;
boolean verticalUseRatio=verticalMatchConstraints && widget.mDimensionRatio > 0;
if (child == null) {
return;
}
LayoutParams params=(LayoutParams)child.getLayoutParams();
int width=0;
int height=0;
int baseline=0;
if ((measure.measureStrategy == BasicMeasure.Measure.TRY_GIVEN_DIMENSIONS || measure.measureStrategy == BasicMeasure.Measure.USE_GIVEN_DIMENSIONS) || !(horizontalMatchConstraints && widget.mMatchConstraintDefaultWidth == MATCH_CONSTRAINT_SPREAD && verticalMatchConstraints && widget.mMatchConstraintDefaultHeight == MATCH_CONSTRAINT_SPREAD)) {
if (child instanceof VirtualLayout && widget instanceof androidx.constraintlayout.core.widgets.CoreVirtualLayout) {
androidx.constraintlayout.core.widgets.CoreVirtualLayout layout=(androidx.constraintlayout.core.widgets.CoreVirtualLayout)widget;
((VirtualLayout)child).onMeasure(layout,horizontalSpec,verticalSpec);
}
 else {
child.measure(horizontalSpec,verticalSpec);
}
widget.setLastMeasureSpec(horizontalSpec,verticalSpec);
int w=child.getMeasuredWidth();
int h=child.getMeasuredHeight();
baseline=child.getBaseline();
width=w;
height=h;
if (DEBUG) {
String measurement=MeasureSpec.toString(horizontalSpec) + " x " + MeasureSpec.toString(verticalSpec)+ " => "+ width+ " x "+ height;
System.out.println("    (M) measure " + " (" + widget.getDebugName() + ") : "+ measurement);
}
if (widget.mMatchConstraintMinWidth > 0) {
width=Math.max(widget.mMatchConstraintMinWidth,width);
}
if (widget.mMatchConstraintMaxWidth > 0) {
width=Math.min(widget.mMatchConstraintMaxWidth,width);
}
if (widget.mMatchConstraintMinHeight > 0) {
height=Math.max(widget.mMatchConstraintMinHeight,height);
}
if (widget.mMatchConstraintMaxHeight > 0) {
height=Math.min(widget.mMatchConstraintMaxHeight,height);
}
boolean optimizeDirect=Optimizer.enabled(mOptimizationLevel,Optimizer.OPTIMIZATION_DIRECT);
if (!optimizeDirect) {
if (horizontalUseRatio && verticalDimensionKnown) {
float ratio=widget.mDimensionRatio;
width=(int)(0.5f + height * ratio);
}
 else if (verticalUseRatio && horizontalDimensionKnown) {
float ratio=widget.mDimensionRatio;
height=(int)(0.5f + width / ratio);
}
}
if (w != width || h != height) {
if (w != width) {
horizontalSpec=MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
}
if (h != height) {
verticalSpec=MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
}
child.measure(horizontalSpec,verticalSpec);
widget.setLastMeasureSpec(horizontalSpec,verticalSpec);
width=child.getMeasuredWidth();
height=child.getMeasuredHeight();
baseline=child.getBaseline();
if (DEBUG) {
String measurement2=MeasureSpec.toString(horizontalSpec) + " x " + MeasureSpec.toString(verticalSpec)+ " => "+ width+ " x "+ height;
System.out.println("measure (b) " + widget.getDebugName() + " : "+ measurement2);
}
}
}
boolean hasBaseline=baseline != -1;
measure.measuredNeedsSolverPass=(width != measure.horizontalDimension) || (height != measure.verticalDimension);
if (params.needsBaseline) {
hasBaseline=true;
}
if (hasBaseline && baseline != -1 && widget.getBaselineDistance() != baseline) {
measure.measuredNeedsSolverPass=true;
}
measure.measuredWidth=width;
measure.measuredHeight=height;
measure.measuredHasBaseline=hasBaseline;
measure.measuredBaseline=baseline;
if (MEASURE) {
endMeasure=System.nanoTime();
if (mMetrics != null) {
mMetrics.measuresWidgetsDuration+=(endMeasure - startMeasure);
}
}
}
private boolean isSimilarSpec(int lastMeasureSpec,int spec,int widgetSize){
if (lastMeasureSpec == spec) {
return true;
}
int lastMode=MeasureSpec.getMode(lastMeasureSpec);
int lastSize=MeasureSpec.getSize(lastMeasureSpec);
int mode=MeasureSpec.getMode(spec);
int size=MeasureSpec.getSize(spec);
if (mode == MeasureSpec.EXACTLY && (lastMode == MeasureSpec.AT_MOST || lastMode == MeasureSpec.UNSPECIFIED) && widgetSize == size) {
return true;
}
return false;
}
public final void didMeasures(){
final int widgetsCount=layout.getChildCount();
for (int i=0; i < widgetsCount; i++) {
final View child=layout.getChildAt(i);
if (child instanceof Placeholder) {
((Placeholder)child).updatePostMeasure(layout);
}
}
final int helperCount=layout.mConstraintHelpers.size();
if (helperCount > 0) {
for (int i=0; i < helperCount; i++) {
ConstraintHelper helper=layout.mConstraintHelpers.get(i);
helper.updatePostMeasure(layout);
}
}
}
}
Measurer mMeasurer=new Measurer(this);
public void onViewAdded(View view){
super.onViewAdded(view);
ConstraintWidget widget=getViewWidget(view);
if (view instanceof androidx.constraintlayout.widget.Guideline) {
if (!(widget instanceof CoreGuideline)) {
LayoutParams layoutParams=(LayoutParams)view.getLayoutParams();
layoutParams.widget=new CoreGuideline();
layoutParams.isGuideline=true;
((CoreGuideline)layoutParams.widget).setOrientation(layoutParams.orientation);
}
}
if (view instanceof ConstraintHelper) {
ConstraintHelper helper=(ConstraintHelper)view;
helper.validateParams();
LayoutParams layoutParams=(LayoutParams)view.getLayoutParams();
layoutParams.isHelper=true;
if (!mConstraintHelpers.contains(helper)) {
mConstraintHelpers.add(helper);
}
}
mChildrenByIds.put(view.getId(),view);
mDirtyHierarchy=true;
}
public void onViewRemoved(View view){
super.onViewRemoved(view);
mChildrenByIds.remove(view.getId());
ConstraintWidget widget=getViewWidget(view);
mLayoutWidget.remove(widget);
mConstraintHelpers.remove(view);
mDirtyHierarchy=true;
}
public void setMinWidth(int value){
if (value == mMinWidth) {
return;
}
mMinWidth=value;
requestLayout();
}
public void setMinHeight(int value){
if (value == mMinHeight) {
return;
}
mMinHeight=value;
requestLayout();
}
public int getMinWidth(){
return mMinWidth;
}
public int getMinHeight(){
return mMinHeight;
}
public void setMaxWidth(int value){
if (value == mMaxWidth) {
return;
}
mMaxWidth=value;
requestLayout();
}
public void setMaxHeight(int value){
if (value == mMaxHeight) {
return;
}
mMaxHeight=value;
requestLayout();
}
public int getMaxWidth(){
return mMaxWidth;
}
public int getMaxHeight(){
return mMaxHeight;
}
private boolean updateHierarchy(){
final int count=getChildCount();
boolean recompute=false;
for (int i=0; i < count; i++) {
final View child=getChildAt(i);
if (child.isLayoutRequested()) {
recompute=true;
break;
}
}
if (recompute) {
setChildrenConstraints();
}
return recompute;
}
private void setChildrenConstraints(){
final boolean isInEditMode=DEBUG || isInEditMode();
final int count=getChildCount();
for (int i=0; i < count; i++) {
View child=getChildAt(i);
ConstraintWidget widget=getViewWidget(child);
if (widget == null) {
continue;
}
widget.reset();
}
if (isInEditMode) {
for (int i=0; i < count; i++) {
final View view=getChildAt(i);
try {
String IdAsString=getResources().getResourceName(view.getId());
setDesignInformation(DESIGN_INFO_ID,IdAsString,view.getId());
int slashIndex=IdAsString.indexOf('/');
if (slashIndex != -1) {
IdAsString=IdAsString.substring(slashIndex + 1);
}
getTargetWidget(view.getId()).setDebugName(IdAsString);
}
 catch (Resources.NotFoundException e) {
}
}
}
 else if (DEBUG) {
mLayoutWidget.setDebugName("root");
for (int i=0; i < count; i++) {
final View view=getChildAt(i);
try {
String IdAsString=getResources().getResourceName(view.getId());
setDesignInformation(DESIGN_INFO_ID,IdAsString,view.getId());
int slashIndex=IdAsString.indexOf('/');
if (slashIndex != -1) {
IdAsString=IdAsString.substring(slashIndex + 1);
}
getTargetWidget(view.getId()).setDebugName(IdAsString);
}
 catch (Resources.NotFoundException e) {
}
}
}
if (USE_CONSTRAINTS_HELPER && mConstraintSetId != -1) {
for (int i=0; i < count; i++) {
final View child=getChildAt(i);
if (child.getId() == mConstraintSetId && child instanceof Constraints) {
mConstraintSet=((Constraints)child).getConstraintSet();
}
}
}
if (mConstraintSet != null) {
mConstraintSet.applyToInternal(this,true);
}
mLayoutWidget.removeAllChildren();
final int helperCount=mConstraintHelpers.size();
if (helperCount > 0) {
for (int i=0; i < helperCount; i++) {
ConstraintHelper helper=mConstraintHelpers.get(i);
helper.updatePreLayout(this);
}
}
for (int i=0; i < count; i++) {
View child=getChildAt(i);
if (child instanceof Placeholder) {
((Placeholder)child).updatePreLayout(this);
}
}
mTempMapIdToWidget.clear();
mTempMapIdToWidget.put(PARENT_ID,mLayoutWidget);
mTempMapIdToWidget.put(getId(),mLayoutWidget);
for (int i=0; i < count; i++) {
final View child=getChildAt(i);
ConstraintWidget widget=getViewWidget(child);
mTempMapIdToWidget.put(child.getId(),widget);
}
for (int i=0; i < count; i++) {
final View child=getChildAt(i);
ConstraintWidget widget=getViewWidget(child);
if (widget == null) {
continue;
}
final LayoutParams layoutParams=(LayoutParams)child.getLayoutParams();
mLayoutWidget.add(widget);
applyConstraintsFromLayoutParams(isInEditMode,child,widget,layoutParams,mTempMapIdToWidget);
}
}
protected void applyConstraintsFromLayoutParams(boolean isInEditMode,View child,ConstraintWidget widget,LayoutParams layoutParams,SparseArray<ConstraintWidget> idToWidget){
layoutParams.validate();
layoutParams.helped=false;
widget.setVisibility(child.getVisibility());
if (layoutParams.isInPlaceholder) {
widget.setInPlaceholder(true);
widget.setVisibility(View.GONE);
}
widget.setCompanionWidget(child);
if (child instanceof ConstraintHelper) {
ConstraintHelper helper=(ConstraintHelper)child;
helper.resolveRtl(widget,mLayoutWidget.isRtl());
}
if (layoutParams.isGuideline) {
CoreGuideline guideline=(CoreGuideline)widget;
int resolvedGuideBegin=layoutParams.resolvedGuideBegin;
int resolvedGuideEnd=layoutParams.resolvedGuideEnd;
float resolvedGuidePercent=layoutParams.resolvedGuidePercent;
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
resolvedGuideBegin=layoutParams.guideBegin;
resolvedGuideEnd=layoutParams.guideEnd;
resolvedGuidePercent=layoutParams.guidePercent;
}
if (resolvedGuidePercent != UNSET) {
guideline.setGuidePercent(resolvedGuidePercent);
}
 else if (resolvedGuideBegin != UNSET) {
guideline.setGuideBegin(resolvedGuideBegin);
}
 else if (resolvedGuideEnd != UNSET) {
guideline.setGuideEnd(resolvedGuideEnd);
}
}
 else {
int resolvedLeftToLeft=layoutParams.resolvedLeftToLeft;
int resolvedLeftToRight=layoutParams.resolvedLeftToRight;
int resolvedRightToLeft=layoutParams.resolvedRightToLeft;
int resolvedRightToRight=layoutParams.resolvedRightToRight;
int resolveGoneLeftMargin=layoutParams.resolveGoneLeftMargin;
int resolveGoneRightMargin=layoutParams.resolveGoneRightMargin;
float resolvedHorizontalBias=layoutParams.resolvedHorizontalBias;
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
resolvedLeftToLeft=layoutParams.leftToLeft;
resolvedLeftToRight=layoutParams.leftToRight;
resolvedRightToLeft=layoutParams.rightToLeft;
resolvedRightToRight=layoutParams.rightToRight;
resolveGoneLeftMargin=layoutParams.goneLeftMargin;
resolveGoneRightMargin=layoutParams.goneRightMargin;
resolvedHorizontalBias=layoutParams.horizontalBias;
if (resolvedLeftToLeft == UNSET && resolvedLeftToRight == UNSET) {
if (layoutParams.startToStart != UNSET) {
resolvedLeftToLeft=layoutParams.startToStart;
}
 else if (layoutParams.startToEnd != UNSET) {
resolvedLeftToRight=layoutParams.startToEnd;
}
}
if (resolvedRightToLeft == UNSET && resolvedRightToRight == UNSET) {
if (layoutParams.endToStart != UNSET) {
resolvedRightToLeft=layoutParams.endToStart;
}
 else if (layoutParams.endToEnd != UNSET) {
resolvedRightToRight=layoutParams.endToEnd;
}
}
}
if (layoutParams.circleConstraint != UNSET) {
ConstraintWidget target=idToWidget.get(layoutParams.circleConstraint);
if (target != null) {
widget.connectCircularConstraint(target,layoutParams.circleAngle,layoutParams.circleRadius);
}
}
 else {
if (resolvedLeftToLeft != UNSET) {
ConstraintWidget target=idToWidget.get(resolvedLeftToLeft);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.LEFT,target,ConstraintAnchor.Type.LEFT,layoutParams.leftMargin,resolveGoneLeftMargin);
}
}
 else if (resolvedLeftToRight != UNSET) {
ConstraintWidget target=idToWidget.get(resolvedLeftToRight);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.LEFT,target,ConstraintAnchor.Type.RIGHT,layoutParams.leftMargin,resolveGoneLeftMargin);
}
}
if (resolvedRightToLeft != UNSET) {
ConstraintWidget target=idToWidget.get(resolvedRightToLeft);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.RIGHT,target,ConstraintAnchor.Type.LEFT,layoutParams.rightMargin,resolveGoneRightMargin);
}
}
 else if (resolvedRightToRight != UNSET) {
ConstraintWidget target=idToWidget.get(resolvedRightToRight);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.RIGHT,target,ConstraintAnchor.Type.RIGHT,layoutParams.rightMargin,resolveGoneRightMargin);
}
}
if (layoutParams.topToTop != UNSET) {
ConstraintWidget target=idToWidget.get(layoutParams.topToTop);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.TOP,target,ConstraintAnchor.Type.TOP,layoutParams.topMargin,layoutParams.goneTopMargin);
}
}
 else if (layoutParams.topToBottom != UNSET) {
ConstraintWidget target=idToWidget.get(layoutParams.topToBottom);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.TOP,target,ConstraintAnchor.Type.BOTTOM,layoutParams.topMargin,layoutParams.goneTopMargin);
}
}
if (layoutParams.bottomToTop != UNSET) {
ConstraintWidget target=idToWidget.get(layoutParams.bottomToTop);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.BOTTOM,target,ConstraintAnchor.Type.TOP,layoutParams.bottomMargin,layoutParams.goneBottomMargin);
}
}
 else if (layoutParams.bottomToBottom != UNSET) {
ConstraintWidget target=idToWidget.get(layoutParams.bottomToBottom);
if (target != null) {
widget.immediateConnect(ConstraintAnchor.Type.BOTTOM,target,ConstraintAnchor.Type.BOTTOM,layoutParams.bottomMargin,layoutParams.goneBottomMargin);
}
}
if (layoutParams.baselineToBaseline != UNSET) {
setWidgetBaseline(widget,layoutParams,idToWidget,layoutParams.baselineToBaseline,ConstraintAnchor.Type.BASELINE);
}
 else if (layoutParams.baselineToTop != UNSET) {
setWidgetBaseline(widget,layoutParams,idToWidget,layoutParams.baselineToTop,ConstraintAnchor.Type.TOP);
}
 else if (layoutParams.baselineToBottom != UNSET) {
setWidgetBaseline(widget,layoutParams,idToWidget,layoutParams.baselineToBottom,ConstraintAnchor.Type.BOTTOM);
}
if (resolvedHorizontalBias >= 0) {
widget.setHorizontalBiasPercent(resolvedHorizontalBias);
}
if (layoutParams.verticalBias >= 0) {
widget.setVerticalBiasPercent(layoutParams.verticalBias);
}
}
if (isInEditMode && ((layoutParams.editorAbsoluteX != UNSET) || (layoutParams.editorAbsoluteY != UNSET))) {
widget.setOrigin(layoutParams.editorAbsoluteX,layoutParams.editorAbsoluteY);
}
if (!layoutParams.horizontalDimensionFixed) {
if (layoutParams.width == MATCH_PARENT) {
if (layoutParams.constrainedWidth) {
widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
}
 else {
widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
}
widget.getAnchor(ConstraintAnchor.Type.LEFT).mMargin=layoutParams.leftMargin;
widget.getAnchor(ConstraintAnchor.Type.RIGHT).mMargin=layoutParams.rightMargin;
}
 else {
widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
widget.setWidth(0);
}
}
 else {
widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
widget.setWidth(layoutParams.width);
if (layoutParams.width == WRAP_CONTENT) {
widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
}
}
if (!layoutParams.verticalDimensionFixed) {
if (layoutParams.height == MATCH_PARENT) {
if (layoutParams.constrainedHeight) {
widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
}
 else {
widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
}
widget.getAnchor(ConstraintAnchor.Type.TOP).mMargin=layoutParams.topMargin;
widget.getAnchor(ConstraintAnchor.Type.BOTTOM).mMargin=layoutParams.bottomMargin;
}
 else {
widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
widget.setHeight(0);
}
}
 else {
widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
widget.setHeight(layoutParams.height);
if (layoutParams.height == WRAP_CONTENT) {
widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
}
}
widget.setDimensionRatio(layoutParams.dimensionRatio);
widget.setHorizontalWeight(layoutParams.horizontalWeight);
widget.setVerticalWeight(layoutParams.verticalWeight);
widget.setHorizontalChainStyle(layoutParams.horizontalChainStyle);
widget.setVerticalChainStyle(layoutParams.verticalChainStyle);
widget.setWrapBehaviorInParent(layoutParams.wrapBehaviorInParent);
widget.setHorizontalMatchStyle(layoutParams.matchConstraintDefaultWidth,layoutParams.matchConstraintMinWidth,layoutParams.matchConstraintMaxWidth,layoutParams.matchConstraintPercentWidth);
widget.setVerticalMatchStyle(layoutParams.matchConstraintDefaultHeight,layoutParams.matchConstraintMinHeight,layoutParams.matchConstraintMaxHeight,layoutParams.matchConstraintPercentHeight);
}
}
private void setWidgetBaseline(ConstraintWidget widget,LayoutParams layoutParams,SparseArray<ConstraintWidget> idToWidget,int baselineTarget,ConstraintAnchor.Type type){
View view=mChildrenByIds.get(baselineTarget);
ConstraintWidget target=idToWidget.get(baselineTarget);
if (target != null && view != null && view.getLayoutParams() instanceof LayoutParams) {
layoutParams.needsBaseline=true;
if (type == ConstraintAnchor.Type.BASELINE) {
LayoutParams targetParams=(LayoutParams)view.getLayoutParams();
targetParams.needsBaseline=true;
targetParams.widget.setHasBaseline(true);
}
ConstraintAnchor baseline=widget.getAnchor(ConstraintAnchor.Type.BASELINE);
ConstraintAnchor targetAnchor=target.getAnchor(type);
baseline.connect(targetAnchor,layoutParams.baselineMargin,layoutParams.goneBaselineMargin,true);
widget.setHasBaseline(true);
widget.getAnchor(ConstraintAnchor.Type.TOP).reset();
widget.getAnchor(ConstraintAnchor.Type.BOTTOM).reset();
}
}
private final ConstraintWidget getTargetWidget(int id){
if (id == LayoutParams.PARENT_ID) {
return mLayoutWidget;
}
 else {
View view=mChildrenByIds.get(id);
if (view == null) {
view=findViewById(id);
if (view != null && view != this && view.getParent() == this) {
onViewAdded(view);
}
}
if (view == this) {
return mLayoutWidget;
}
return view == null ? null : ((LayoutParams)view.getLayoutParams()).widget;
}
}
public final ConstraintWidget getViewWidget(View view){
if (view == this) {
return mLayoutWidget;
}
if (view != null) {
if (view.getLayoutParams() instanceof LayoutParams) {
return ((LayoutParams)view.getLayoutParams()).widget;
}
view.setLayoutParams(generateLayoutParams(view.getLayoutParams()));
if (view.getLayoutParams() instanceof LayoutParams) {
return ((LayoutParams)view.getLayoutParams()).widget;
}
}
return null;
}
private int mOnMeasureWidthMeasureSpec=0;
private int mOnMeasureHeightMeasureSpec=0;
protected void resolveSystem(ConstraintWidgetContainer layout,int optimizationLevel,int widthMeasureSpec,int heightMeasureSpec){
int widthMode=MeasureSpec.getMode(widthMeasureSpec);
int widthSize=MeasureSpec.getSize(widthMeasureSpec);
int heightMode=MeasureSpec.getMode(heightMeasureSpec);
int heightSize=MeasureSpec.getSize(heightMeasureSpec);
int paddingY=Math.max(0,getPaddingTop());
int paddingBottom=Math.max(0,getPaddingBottom());
int paddingHeight=paddingY + paddingBottom;
int paddingWidth=getPaddingWidth();
int paddingX;
mMeasurer.captureLayoutInfo(widthMeasureSpec,heightMeasureSpec,paddingY,paddingBottom,paddingWidth,paddingHeight);
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
int paddingStart=Math.max(0,getPaddingStart());
int paddingEnd=Math.max(0,getPaddingEnd());
if (paddingStart > 0 || paddingEnd > 0) {
if (isRtl()) {
paddingX=paddingEnd;
}
 else {
paddingX=paddingStart;
}
}
 else {
paddingX=Math.max(0,getPaddingLeft());
}
}
 else {
paddingX=Math.max(0,getPaddingLeft());
}
widthSize-=paddingWidth;
heightSize-=paddingHeight;
setSelfDimensionBehaviour(layout,widthMode,widthSize,heightMode,heightSize);
layout.measure(optimizationLevel,widthMode,widthSize,heightMode,heightSize,mLastMeasureWidth,mLastMeasureHeight,paddingX,paddingY);
}
protected void resolveMeasuredDimension(int widthMeasureSpec,int heightMeasureSpec,int measuredWidth,int measuredHeight,boolean isWidthMeasuredTooSmall,boolean isHeightMeasuredTooSmall){
int childState=0;
int heightPadding=mMeasurer.paddingHeight;
int widthPadding=mMeasurer.paddingWidth;
int androidLayoutWidth=measuredWidth + widthPadding;
int androidLayoutHeight=measuredHeight + heightPadding;
int resolvedWidthSize=resolveSizeAndState(androidLayoutWidth,widthMeasureSpec,childState);
int resolvedHeightSize=resolveSizeAndState(androidLayoutHeight,heightMeasureSpec,childState << MEASURED_HEIGHT_STATE_SHIFT);
resolvedWidthSize&=MEASURED_SIZE_MASK;
resolvedHeightSize&=MEASURED_SIZE_MASK;
resolvedWidthSize=Math.min(mMaxWidth,resolvedWidthSize);
resolvedHeightSize=Math.min(mMaxHeight,resolvedHeightSize);
if (isWidthMeasuredTooSmall) {
resolvedWidthSize|=MEASURED_STATE_TOO_SMALL;
}
if (isHeightMeasuredTooSmall) {
resolvedHeightSize|=MEASURED_STATE_TOO_SMALL;
}
setMeasuredDimension(resolvedWidthSize,resolvedHeightSize);
mLastMeasureWidth=resolvedWidthSize;
mLastMeasureHeight=resolvedHeightSize;
}
protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
long time=0;
if (DEBUG) {
time=System.currentTimeMillis();
}
boolean sameSpecsAsPreviousMeasure=(mOnMeasureWidthMeasureSpec == widthMeasureSpec && mOnMeasureHeightMeasureSpec == heightMeasureSpec);
sameSpecsAsPreviousMeasure=false;
if (!mDirtyHierarchy && !sameSpecsAsPreviousMeasure) {
final int count=getChildCount();
for (int i=0; i < count; i++) {
final View child=getChildAt(i);
if (child.isLayoutRequested()) {
if (DEBUG) {
System.out.println("### CHILD " + child + " REQUESTED LAYOUT, FORCE DIRTY HIERARCHY");
}
mDirtyHierarchy=true;
break;
}
}
}
if (!mDirtyHierarchy) {
if (sameSpecsAsPreviousMeasure) {
resolveMeasuredDimension(widthMeasureSpec,heightMeasureSpec,mLayoutWidget.getWidth(),mLayoutWidget.getHeight(),mLayoutWidget.isWidthMeasuredTooSmall(),mLayoutWidget.isHeightMeasuredTooSmall());
return;
}
if (OPTIMIZE_HEIGHT_CHANGE && mOnMeasureWidthMeasureSpec == widthMeasureSpec && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST && MeasureSpec.getMode(mOnMeasureHeightMeasureSpec) == MeasureSpec.AT_MOST) {
int newSize=MeasureSpec.getSize(heightMeasureSpec);
if (DEBUG) {
System.out.println("### COMPATIBLE REQ " + newSize + " >= ? "+ mLayoutWidget.getHeight());
}
if (newSize >= mLayoutWidget.getHeight() && !mLayoutWidget.isHeightMeasuredTooSmall()) {
mOnMeasureWidthMeasureSpec=widthMeasureSpec;
mOnMeasureHeightMeasureSpec=heightMeasureSpec;
resolveMeasuredDimension(widthMeasureSpec,heightMeasureSpec,mLayoutWidget.getWidth(),mLayoutWidget.getHeight(),mLayoutWidget.isWidthMeasuredTooSmall(),mLayoutWidget.isHeightMeasuredTooSmall());
return;
}
}
}
mOnMeasureWidthMeasureSpec=widthMeasureSpec;
mOnMeasureHeightMeasureSpec=heightMeasureSpec;
if (DEBUG) {
System.out.println("### ON MEASURE " + mDirtyHierarchy + " of "+ mLayoutWidget.getDebugName()+ " onMeasure width: "+ MeasureSpec.toString(widthMeasureSpec)+ " height: "+ MeasureSpec.toString(heightMeasureSpec)+ this);
}
mLayoutWidget.setRtl(isRtl());
if (mDirtyHierarchy) {
mDirtyHierarchy=false;
if (updateHierarchy()) {
mLayoutWidget.updateHierarchy();
}
}
resolveSystem(mLayoutWidget,mOptimizationLevel,widthMeasureSpec,heightMeasureSpec);
resolveMeasuredDimension(widthMeasureSpec,heightMeasureSpec,mLayoutWidget.getWidth(),mLayoutWidget.getHeight(),mLayoutWidget.isWidthMeasuredTooSmall(),mLayoutWidget.isHeightMeasuredTooSmall());
if (DEBUG) {
time=System.currentTimeMillis() - time;
System.out.println(mLayoutWidget.getDebugName() + " (" + getChildCount()+ ") DONE onMeasure width: "+ MeasureSpec.toString(widthMeasureSpec)+ " height: "+ MeasureSpec.toString(heightMeasureSpec)+ " => "+ mLastMeasureWidth+ " x "+ mLastMeasureHeight+ " lasted "+ time);
}
}
protected boolean isRtl(){
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
boolean isRtlSupported=(getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_SUPPORTS_RTL) != 0;
return isRtlSupported && (View.LAYOUT_DIRECTION_RTL == getLayoutDirection());
}
return false;
}
private int getPaddingWidth(){
int widthPadding=Math.max(0,getPaddingLeft()) + Math.max(0,getPaddingRight());
int rtlPadding=0;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
rtlPadding=Math.max(0,getPaddingStart()) + Math.max(0,getPaddingEnd());
}
if (rtlPadding > 0) {
widthPadding=rtlPadding;
}
return widthPadding;
}
protected void setSelfDimensionBehaviour(ConstraintWidgetContainer layout,int widthMode,int widthSize,int heightMode,int heightSize){
int heightPadding=mMeasurer.paddingHeight;
int widthPadding=mMeasurer.paddingWidth;
ConstraintWidget.DimensionBehaviour widthBehaviour=ConstraintWidget.DimensionBehaviour.FIXED;
ConstraintWidget.DimensionBehaviour heightBehaviour=ConstraintWidget.DimensionBehaviour.FIXED;
int desiredWidth=0;
int desiredHeight=0;
final int childCount=getChildCount();
switch (widthMode) {
case MeasureSpec.AT_MOST:
{
widthBehaviour=ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
desiredWidth=widthSize;
if (childCount == 0) {
desiredWidth=Math.max(0,mMinWidth);
}
}
break;
case MeasureSpec.UNSPECIFIED:
{
widthBehaviour=ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
if (childCount == 0) {
desiredWidth=Math.max(0,mMinWidth);
}
}
break;
case MeasureSpec.EXACTLY:
{
desiredWidth=Math.min(mMaxWidth - widthPadding,widthSize);
}
}
switch (heightMode) {
case MeasureSpec.AT_MOST:
{
heightBehaviour=ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
desiredHeight=heightSize;
if (childCount == 0) {
desiredHeight=Math.max(0,mMinHeight);
}
}
break;
case MeasureSpec.UNSPECIFIED:
{
heightBehaviour=ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
if (childCount == 0) {
desiredHeight=Math.max(0,mMinHeight);
}
}
break;
case MeasureSpec.EXACTLY:
{
desiredHeight=Math.min(mMaxHeight - heightPadding,heightSize);
}
}
if (desiredWidth != layout.getWidth() || desiredHeight != layout.getHeight()) {
layout.invalidateMeasures();
}
layout.setX(0);
layout.setY(0);
layout.setMaxWidth(mMaxWidth - widthPadding);
layout.setMaxHeight(mMaxHeight - heightPadding);
layout.setMinWidth(0);
layout.setMinHeight(0);
layout.setHorizontalDimensionBehaviour(widthBehaviour);
layout.setWidth(desiredWidth);
layout.setVerticalDimensionBehaviour(heightBehaviour);
layout.setHeight(desiredHeight);
layout.setMinWidth(mMinWidth - widthPadding);
layout.setMinHeight(mMinHeight - heightPadding);
}
protected void onLayout(boolean changed,int left,int top,int right,int bottom){
if (DEBUG) {
System.out.println(mLayoutWidget.getDebugName() + " onLayout changed: " + changed+ " left: "+ left+ " top: "+ top+ " right: "+ right+ " bottom: "+ bottom+ " ("+ (right - left)+ " x "+ (bottom - top)+ ")");
}
final int widgetsCount=getChildCount();
final boolean isInEditMode=isInEditMode();
for (int i=0; i < widgetsCount; i++) {
final View child=getChildAt(i);
LayoutParams params=(LayoutParams)child.getLayoutParams();
ConstraintWidget widget=params.widget;
if (child.getVisibility() == GONE && !params.isGuideline && !params.isHelper && !params.isVirtualGroup && !isInEditMode) {
continue;
}
if (params.isInPlaceholder) {
continue;
}
int l=widget.getX();
int t=widget.getY();
int r=l + widget.getWidth();
int b=t + widget.getHeight();
if (DEBUG) {
if (child.getVisibility() != View.GONE && (child.getMeasuredWidth() != widget.getWidth() || child.getMeasuredHeight() != widget.getHeight())) {
int deltaX=Math.abs(child.getMeasuredWidth() - widget.getWidth());
int deltaY=Math.abs(child.getMeasuredHeight() - widget.getHeight());
if (deltaX > 1 || deltaY > 1) {
System.out.println("child " + child + " measuredWidth "+ child.getMeasuredWidth()+ " vs "+ widget.getWidth()+ " x measureHeight "+ child.getMeasuredHeight()+ " vs "+ widget.getHeight());
}
}
}
child.layout(l,t,r,b);
if (child instanceof Placeholder) {
Placeholder holder=(Placeholder)child;
View content=holder.getContent();
if (content != null) {
content.setVisibility(VISIBLE);
content.layout(l,t,r,b);
}
}
}
final int helperCount=mConstraintHelpers.size();
if (helperCount > 0) {
for (int i=0; i < helperCount; i++) {
ConstraintHelper helper=mConstraintHelpers.get(i);
helper.updatePostLayout(this);
}
}
}
public void setOptimizationLevel(int level){
mOptimizationLevel=level;
mLayoutWidget.setOptimizationLevel(level);
}
public int getOptimizationLevel(){
return mLayoutWidget.getOptimizationLevel();
}
protected LayoutParams generateDefaultLayoutParams(){
return new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
}
public void setConstraintSet(ConstraintSet set){
mConstraintSet=set;
}
public View getViewById(int id){
return mChildrenByIds.get(id);
}
public static class LayoutParams extends ViewGroup.MarginLayoutParams {
public static final int MATCH_CONSTRAINT=0;
public static final int PARENT_ID=0;
public static final int UNSET=-1;
public static final int GONE_UNSET=Integer.MIN_VALUE;
public static final int HORIZONTAL=ConstraintWidget.HORIZONTAL;
public static final int VERTICAL=ConstraintWidget.VERTICAL;
public static final int LEFT=1;
public static final int RIGHT=2;
public static final int TOP=3;
public static final int BOTTOM=4;
public static final int BASELINE=5;
public static final int START=6;
public static final int END=7;
public static final int CIRCLE=8;
public static final int MATCH_CONSTRAINT_WRAP=ConstraintWidget.MATCH_CONSTRAINT_WRAP;
public static final int MATCH_CONSTRAINT_SPREAD=ConstraintWidget.MATCH_CONSTRAINT_SPREAD;
public static final int MATCH_CONSTRAINT_PERCENT=ConstraintWidget.MATCH_CONSTRAINT_PERCENT;
public static final int CHAIN_SPREAD=ConstraintWidget.CHAIN_SPREAD;
public static final int CHAIN_SPREAD_INSIDE=ConstraintWidget.CHAIN_SPREAD_INSIDE;
public static final int CHAIN_PACKED=ConstraintWidget.CHAIN_PACKED;
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
public int circleConstraint=UNSET;
public int circleRadius=0;
public float circleAngle=0;
public int startToEnd=UNSET;
public int startToStart=UNSET;
public int endToStart=UNSET;
public int endToEnd=UNSET;
public int goneLeftMargin=GONE_UNSET;
public int goneTopMargin=GONE_UNSET;
public int goneRightMargin=GONE_UNSET;
public int goneBottomMargin=GONE_UNSET;
public int goneStartMargin=GONE_UNSET;
public int goneEndMargin=GONE_UNSET;
public int goneBaselineMargin=GONE_UNSET;
public int baselineMargin=0;
boolean widthSet=true;
boolean heightSet=true;
public float horizontalBias=0.5f;
public float verticalBias=0.5f;
public String dimensionRatio=null;
float dimensionRatioValue=0;
int dimensionRatioSide=VERTICAL;
public float horizontalWeight=UNSET;
public float verticalWeight=UNSET;
public int horizontalChainStyle=CHAIN_SPREAD;
public int verticalChainStyle=CHAIN_SPREAD;
public int matchConstraintDefaultWidth=MATCH_CONSTRAINT_SPREAD;
public int matchConstraintDefaultHeight=MATCH_CONSTRAINT_SPREAD;
public int matchConstraintMinWidth=0;
public int matchConstraintMinHeight=0;
public int matchConstraintMaxWidth=0;
public int matchConstraintMaxHeight=0;
public float matchConstraintPercentWidth=1;
public float matchConstraintPercentHeight=1;
public int editorAbsoluteX=UNSET;
public int editorAbsoluteY=UNSET;
public int orientation=UNSET;
public boolean constrainedWidth=false;
public boolean constrainedHeight=false;
public String constraintTag=null;
public static final int WRAP_BEHAVIOR_INCLUDED=ConstraintWidget.WRAP_BEHAVIOR_INCLUDED;
public static final int WRAP_BEHAVIOR_HORIZONTAL_ONLY=ConstraintWidget.WRAP_BEHAVIOR_HORIZONTAL_ONLY;
public static final int WRAP_BEHAVIOR_VERTICAL_ONLY=ConstraintWidget.WRAP_BEHAVIOR_VERTICAL_ONLY;
public static final int WRAP_BEHAVIOR_SKIPPED=ConstraintWidget.WRAP_BEHAVIOR_SKIPPED;
public int wrapBehaviorInParent=WRAP_BEHAVIOR_INCLUDED;
boolean horizontalDimensionFixed=true;
boolean verticalDimensionFixed=true;
boolean needsBaseline=false;
boolean isGuideline=false;
boolean isHelper=false;
boolean isInPlaceholder=false;
boolean isVirtualGroup=false;
int resolvedLeftToLeft=UNSET;
int resolvedLeftToRight=UNSET;
int resolvedRightToLeft=UNSET;
int resolvedRightToRight=UNSET;
int resolveGoneLeftMargin=GONE_UNSET;
int resolveGoneRightMargin=GONE_UNSET;
float resolvedHorizontalBias=0.5f;
int resolvedGuideBegin;
int resolvedGuideEnd;
float resolvedGuidePercent;
ConstraintWidget widget=new ConstraintWidget();
public boolean helped=false;
public LayoutParams(LayoutParams source){
super(source);
this.guideBegin=source.guideBegin;
this.guideEnd=source.guideEnd;
this.guidePercent=source.guidePercent;
this.leftToLeft=source.leftToLeft;
this.leftToRight=source.leftToRight;
this.rightToLeft=source.rightToLeft;
this.rightToRight=source.rightToRight;
this.topToTop=source.topToTop;
this.topToBottom=source.topToBottom;
this.bottomToTop=source.bottomToTop;
this.bottomToBottom=source.bottomToBottom;
this.baselineToBaseline=source.baselineToBaseline;
this.baselineToTop=source.baselineToTop;
this.baselineToBottom=source.baselineToBottom;
this.circleConstraint=source.circleConstraint;
this.circleRadius=source.circleRadius;
this.circleAngle=source.circleAngle;
this.startToEnd=source.startToEnd;
this.startToStart=source.startToStart;
this.endToStart=source.endToStart;
this.endToEnd=source.endToEnd;
this.goneLeftMargin=source.goneLeftMargin;
this.goneTopMargin=source.goneTopMargin;
this.goneRightMargin=source.goneRightMargin;
this.goneBottomMargin=source.goneBottomMargin;
this.goneStartMargin=source.goneStartMargin;
this.goneEndMargin=source.goneEndMargin;
this.goneBaselineMargin=source.goneBaselineMargin;
this.baselineMargin=source.baselineMargin;
this.horizontalBias=source.horizontalBias;
this.verticalBias=source.verticalBias;
this.dimensionRatio=source.dimensionRatio;
this.dimensionRatioValue=source.dimensionRatioValue;
this.dimensionRatioSide=source.dimensionRatioSide;
this.horizontalWeight=source.horizontalWeight;
this.verticalWeight=source.verticalWeight;
this.horizontalChainStyle=source.horizontalChainStyle;
this.verticalChainStyle=source.verticalChainStyle;
this.constrainedWidth=source.constrainedWidth;
this.constrainedHeight=source.constrainedHeight;
this.matchConstraintDefaultWidth=source.matchConstraintDefaultWidth;
this.matchConstraintDefaultHeight=source.matchConstraintDefaultHeight;
this.matchConstraintMinWidth=source.matchConstraintMinWidth;
this.matchConstraintMaxWidth=source.matchConstraintMaxWidth;
this.matchConstraintMinHeight=source.matchConstraintMinHeight;
this.matchConstraintMaxHeight=source.matchConstraintMaxHeight;
this.matchConstraintPercentWidth=source.matchConstraintPercentWidth;
this.matchConstraintPercentHeight=source.matchConstraintPercentHeight;
this.editorAbsoluteX=source.editorAbsoluteX;
this.editorAbsoluteY=source.editorAbsoluteY;
this.orientation=source.orientation;
this.horizontalDimensionFixed=source.horizontalDimensionFixed;
this.verticalDimensionFixed=source.verticalDimensionFixed;
this.needsBaseline=source.needsBaseline;
this.isGuideline=source.isGuideline;
this.resolvedLeftToLeft=source.resolvedLeftToLeft;
this.resolvedLeftToRight=source.resolvedLeftToRight;
this.resolvedRightToLeft=source.resolvedRightToLeft;
this.resolvedRightToRight=source.resolvedRightToRight;
this.resolveGoneLeftMargin=source.resolveGoneLeftMargin;
this.resolveGoneRightMargin=source.resolveGoneRightMargin;
this.resolvedHorizontalBias=source.resolvedHorizontalBias;
this.constraintTag=source.constraintTag;
this.wrapBehaviorInParent=source.wrapBehaviorInParent;
this.widget=source.widget;
this.widthSet=source.widthSet;
this.heightSet=source.heightSet;
}
public void validate(){
isGuideline=false;
horizontalDimensionFixed=true;
verticalDimensionFixed=true;
if (width == WRAP_CONTENT && constrainedWidth) {
horizontalDimensionFixed=false;
if (matchConstraintDefaultWidth == MATCH_CONSTRAINT_SPREAD) {
matchConstraintDefaultWidth=MATCH_CONSTRAINT_WRAP;
}
}
if (height == WRAP_CONTENT && constrainedHeight) {
verticalDimensionFixed=false;
if (matchConstraintDefaultHeight == MATCH_CONSTRAINT_SPREAD) {
matchConstraintDefaultHeight=MATCH_CONSTRAINT_WRAP;
}
}
if (width == MATCH_CONSTRAINT || width == MATCH_PARENT) {
horizontalDimensionFixed=false;
if (width == MATCH_CONSTRAINT && matchConstraintDefaultWidth == MATCH_CONSTRAINT_WRAP) {
width=WRAP_CONTENT;
constrainedWidth=true;
}
}
if (height == MATCH_CONSTRAINT || height == MATCH_PARENT) {
verticalDimensionFixed=false;
if (height == MATCH_CONSTRAINT && matchConstraintDefaultHeight == MATCH_CONSTRAINT_WRAP) {
height=WRAP_CONTENT;
constrainedHeight=true;
}
}
if (guidePercent != UNSET || guideBegin != UNSET || guideEnd != UNSET) {
isGuideline=true;
horizontalDimensionFixed=true;
verticalDimensionFixed=true;
if (!(widget instanceof CoreGuideline)) {
widget=new CoreGuideline();
}
((CoreGuideline)widget).setOrientation(orientation);
}
}
public LayoutParams(int width,int height){
super(width,height);
}
public LayoutParams(ViewGroup.LayoutParams source){
super(source);
}
public void resolveLayoutDirection(int layoutDirection){
int originalLeftMargin=leftMargin;
int originalRightMargin=rightMargin;
boolean isRtl=false;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
super.resolveLayoutDirection(layoutDirection);
isRtl=(View.LAYOUT_DIRECTION_RTL == getLayoutDirection());
}
resolvedRightToLeft=UNSET;
resolvedRightToRight=UNSET;
resolvedLeftToLeft=UNSET;
resolvedLeftToRight=UNSET;
resolveGoneLeftMargin=UNSET;
resolveGoneRightMargin=UNSET;
resolveGoneLeftMargin=goneLeftMargin;
resolveGoneRightMargin=goneRightMargin;
resolvedHorizontalBias=horizontalBias;
resolvedGuideBegin=guideBegin;
resolvedGuideEnd=guideEnd;
resolvedGuidePercent=guidePercent;
if (isRtl) {
boolean startEndDefined=false;
if (startToEnd != UNSET) {
resolvedRightToLeft=startToEnd;
startEndDefined=true;
}
 else if (startToStart != UNSET) {
resolvedRightToRight=startToStart;
startEndDefined=true;
}
if (endToStart != UNSET) {
resolvedLeftToRight=endToStart;
startEndDefined=true;
}
if (endToEnd != UNSET) {
resolvedLeftToLeft=endToEnd;
startEndDefined=true;
}
if (goneStartMargin != GONE_UNSET) {
resolveGoneRightMargin=goneStartMargin;
}
if (goneEndMargin != GONE_UNSET) {
resolveGoneLeftMargin=goneEndMargin;
}
if (startEndDefined) {
resolvedHorizontalBias=1 - horizontalBias;
}
if (isGuideline && orientation == CoreGuideline.VERTICAL) {
if (guidePercent != UNSET) {
resolvedGuidePercent=1 - guidePercent;
resolvedGuideBegin=UNSET;
resolvedGuideEnd=UNSET;
}
 else if (guideBegin != UNSET) {
resolvedGuideEnd=guideBegin;
resolvedGuideBegin=UNSET;
resolvedGuidePercent=UNSET;
}
 else if (guideEnd != UNSET) {
resolvedGuideBegin=guideEnd;
resolvedGuideEnd=UNSET;
resolvedGuidePercent=UNSET;
}
}
}
 else {
if (startToEnd != UNSET) {
resolvedLeftToRight=startToEnd;
}
if (startToStart != UNSET) {
resolvedLeftToLeft=startToStart;
}
if (endToStart != UNSET) {
resolvedRightToLeft=endToStart;
}
if (endToEnd != UNSET) {
resolvedRightToRight=endToEnd;
}
if (goneStartMargin != GONE_UNSET) {
resolveGoneLeftMargin=goneStartMargin;
}
if (goneEndMargin != GONE_UNSET) {
resolveGoneRightMargin=goneEndMargin;
}
}
if (endToStart == UNSET && endToEnd == UNSET && startToStart == UNSET && startToEnd == UNSET) {
if (rightToLeft != UNSET) {
resolvedRightToLeft=rightToLeft;
if (rightMargin <= 0 && originalRightMargin > 0) {
rightMargin=originalRightMargin;
}
}
 else if (rightToRight != UNSET) {
resolvedRightToRight=rightToRight;
if (rightMargin <= 0 && originalRightMargin > 0) {
rightMargin=originalRightMargin;
}
}
if (leftToLeft != UNSET) {
resolvedLeftToLeft=leftToLeft;
if (leftMargin <= 0 && originalLeftMargin > 0) {
leftMargin=originalLeftMargin;
}
}
 else if (leftToRight != UNSET) {
resolvedLeftToRight=leftToRight;
if (leftMargin <= 0 && originalLeftMargin > 0) {
leftMargin=originalLeftMargin;
}
}
}
}
public String getConstraintTag(){
return constraintTag;
}
}
public void requestLayout(){
markHierarchyDirty();
super.requestLayout();
}
private void markHierarchyDirty(){
mDirtyHierarchy=true;
mLastMeasureWidth=-1;
mLastMeasureHeight=-1;
mLastMeasureWidthSize=-1;
mLastMeasureHeightSize=-1;
mLastMeasureWidthMode=MeasureSpec.UNSPECIFIED;
mLastMeasureHeightMode=MeasureSpec.UNSPECIFIED;
}
public ConstraintLayout(){
mLayoutWidget.setCompanionWidget(this);
mLayoutWidget.setMeasurer(mMeasurer);
mChildrenByIds.put(getId(),this);
mConstraintSet=null;
mLayoutWidget.setOptimizationLevel(mOptimizationLevel);
}
public void release(){
for (ConstraintWidget constraintWidget : mLayoutWidget.getChildren()) {
constraintWidget.reset();
constraintWidget.resetAnchors();
}
mLayoutWidget.resetAnchors();
mLayoutWidget.release();
mConstraintHelpers=null;
mLayoutWidget=null;
mChildrenByIds=null;
mMeasurer=null;
}
}
