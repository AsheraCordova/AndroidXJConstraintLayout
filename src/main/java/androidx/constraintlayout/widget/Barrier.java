package androidx.constraintlayout.widget;
import r.android.os.Build;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.HelperWidget;
import r.android.util.SparseArray;
import r.android.view.View;
public class Barrier extends ConstraintHelper {
  public static final int LEFT=androidx.constraintlayout.core.widgets.CoreBarrier.LEFT;
  public static final int TOP=androidx.constraintlayout.core.widgets.CoreBarrier.TOP;
  public static final int RIGHT=androidx.constraintlayout.core.widgets.CoreBarrier.RIGHT;
  public static final int BOTTOM=androidx.constraintlayout.core.widgets.CoreBarrier.BOTTOM;
  public static final int START=BOTTOM + 2;
  public static final int END=START + 1;
  private int mIndicatedType;
  private int mResolvedType;
  private androidx.constraintlayout.core.widgets.CoreBarrier mBarrier;
  public int getType(){
    return mIndicatedType;
  }
  public void setType(  int type){
    mIndicatedType=type;
  }
  private void updateType(  ConstraintWidget widget,  int type,  boolean isRtl){
    mResolvedType=type;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      if (mIndicatedType == START) {
        mResolvedType=LEFT;
      }
 else       if (mIndicatedType == END) {
        mResolvedType=RIGHT;
      }
    }
 else {
      if (isRtl) {
        if (mIndicatedType == START) {
          mResolvedType=RIGHT;
        }
 else         if (mIndicatedType == END) {
          mResolvedType=LEFT;
        }
      }
 else {
        if (mIndicatedType == START) {
          mResolvedType=LEFT;
        }
 else         if (mIndicatedType == END) {
          mResolvedType=RIGHT;
        }
      }
    }
    if (widget instanceof androidx.constraintlayout.core.widgets.CoreBarrier) {
      androidx.constraintlayout.core.widgets.CoreBarrier barrier=(androidx.constraintlayout.core.widgets.CoreBarrier)widget;
      barrier.setBarrierType(mResolvedType);
    }
  }
  public void resolveRtl(  ConstraintWidget widget,  boolean isRtl){
    updateType(widget,mIndicatedType,isRtl);
  }
  public void setAllowsGoneWidget(  boolean supportGone){
    mBarrier.setAllowsGoneWidget(supportGone);
  }
  public boolean allowsGoneWidget(){
    return mBarrier.getAllowsGoneWidget();
  }
  public boolean getAllowsGoneWidget(){
    return mBarrier.getAllowsGoneWidget();
  }
  public int getMargin(){
    return mBarrier.getMargin();
  }
  public void setMargin(  int margin){
    mBarrier.setMargin(margin);
  }
  public void loadParameters(  ConstraintSet.Constraint constraint,  HelperWidget child,  ConstraintLayout.LayoutParams layoutParams,  SparseArray<ConstraintWidget> mapIdToWidget){
    super.loadParameters(constraint,child,layoutParams,mapIdToWidget);
    if (child instanceof androidx.constraintlayout.core.widgets.CoreBarrier) {
      androidx.constraintlayout.core.widgets.CoreBarrier barrier=(androidx.constraintlayout.core.widgets.CoreBarrier)child;
      ConstraintWidgetContainer container=(ConstraintWidgetContainer)child.getParent();
      boolean isRtl=container.isRtl();
      updateType(barrier,constraint.layout.mBarrierDirection,isRtl);
      barrier.setAllowsGoneWidget(constraint.layout.mBarrierAllowsGoneWidgets);
      barrier.setMargin(constraint.layout.mBarrierMargin);
    }
  }
  public Barrier(  r.android.content.Context context){
    super.setVisibility(View.GONE);
    mBarrier=new androidx.constraintlayout.core.widgets.CoreBarrier();
    mHelperWidget=mBarrier;
    validateParams();
  }
  public Barrier(){
    super.setVisibility(View.GONE);
    mBarrier=new androidx.constraintlayout.core.widgets.CoreBarrier();
    mHelperWidget=mBarrier;
    validateParams();
  }
}
