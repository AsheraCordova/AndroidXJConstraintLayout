package androidx.constraintlayout.helper.widget;
import r.android.os.Build;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import r.android.R;
import androidx.constraintlayout.widget.VirtualLayout;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.HelperWidget;
import r.android.util.SparseArray;
public class Flow extends VirtualLayout {
  private static final String TAG="Flow";
  private androidx.constraintlayout.core.widgets.CoreFlow mFlow;
  public static final int HORIZONTAL=androidx.constraintlayout.core.widgets.CoreFlow.HORIZONTAL;
  public static final int VERTICAL=androidx.constraintlayout.core.widgets.CoreFlow.VERTICAL;
  public static final int WRAP_NONE=androidx.constraintlayout.core.widgets.CoreFlow.WRAP_NONE;
  public static final int WRAP_CHAIN=androidx.constraintlayout.core.widgets.CoreFlow.WRAP_CHAIN;
  public static final int WRAP_ALIGNED=androidx.constraintlayout.core.widgets.CoreFlow.WRAP_ALIGNED;
  public static final int CHAIN_SPREAD=ConstraintWidget.CHAIN_SPREAD;
  public static final int CHAIN_SPREAD_INSIDE=ConstraintWidget.CHAIN_SPREAD_INSIDE;
  public static final int CHAIN_PACKED=ConstraintWidget.CHAIN_PACKED;
  public static final int HORIZONTAL_ALIGN_START=androidx.constraintlayout.core.widgets.CoreFlow.HORIZONTAL_ALIGN_START;
  public static final int HORIZONTAL_ALIGN_END=androidx.constraintlayout.core.widgets.CoreFlow.HORIZONTAL_ALIGN_END;
  public static final int HORIZONTAL_ALIGN_CENTER=androidx.constraintlayout.core.widgets.CoreFlow.HORIZONTAL_ALIGN_CENTER;
  public static final int VERTICAL_ALIGN_TOP=androidx.constraintlayout.core.widgets.CoreFlow.VERTICAL_ALIGN_TOP;
  public static final int VERTICAL_ALIGN_BOTTOM=androidx.constraintlayout.core.widgets.CoreFlow.VERTICAL_ALIGN_BOTTOM;
  public static final int VERTICAL_ALIGN_CENTER=androidx.constraintlayout.core.widgets.CoreFlow.VERTICAL_ALIGN_CENTER;
  public static final int VERTICAL_ALIGN_BASELINE=androidx.constraintlayout.core.widgets.CoreFlow.VERTICAL_ALIGN_BASELINE;
  public void resolveRtl(  ConstraintWidget widget,  boolean isRtl){
    mFlow.applyRtl(isRtl);
  }
  protected void onMeasure(  int widthMeasureSpec,  int heightMeasureSpec){
    onMeasure(mFlow,widthMeasureSpec,heightMeasureSpec);
  }
  public void onMeasure(  androidx.constraintlayout.core.widgets.CoreVirtualLayout layout,  int widthMeasureSpec,  int heightMeasureSpec){
    int widthMode=MeasureSpec.getMode(widthMeasureSpec);
    int widthSize=MeasureSpec.getSize(widthMeasureSpec);
    int heightMode=MeasureSpec.getMode(heightMeasureSpec);
    int heightSize=MeasureSpec.getSize(heightMeasureSpec);
    if (layout != null) {
      layout.measure(widthMode,widthSize,heightMode,heightSize);
      setMeasuredDimension(layout.getMeasuredWidth(),layout.getMeasuredHeight());
    }
 else {
      setMeasuredDimension(0,0);
    }
  }
  public void loadParameters(  ConstraintSet.Constraint constraint,  HelperWidget child,  ConstraintLayout.LayoutParams layoutParams,  SparseArray<ConstraintWidget> mapIdToWidget){
    super.loadParameters(constraint,child,layoutParams,mapIdToWidget);
    if (child instanceof androidx.constraintlayout.core.widgets.CoreFlow) {
      androidx.constraintlayout.core.widgets.CoreFlow flow=(androidx.constraintlayout.core.widgets.CoreFlow)child;
      if (layoutParams.orientation != -1) {
        flow.setOrientation(layoutParams.orientation);
      }
    }
  }
  public void setOrientation(  int orientation){
    mFlow.setOrientation(orientation);
    requestLayout();
  }
  public void setPadding(  int padding){
    mFlow.setPadding(padding);
    requestLayout();
  }
  public void setPaddingLeft(  int paddingLeft){
    mFlow.setPaddingLeft(paddingLeft);
    requestLayout();
  }
  public void setPaddingTop(  int paddingTop){
    mFlow.setPaddingTop(paddingTop);
    requestLayout();
  }
  public void setPaddingRight(  int paddingRight){
    mFlow.setPaddingRight(paddingRight);
    requestLayout();
  }
  public void setPaddingBottom(  int paddingBottom){
    mFlow.setPaddingBottom(paddingBottom);
    requestLayout();
  }
  public void setWrapMode(  int mode){
    mFlow.setWrapMode(mode);
    requestLayout();
  }
  public void setHorizontalStyle(  int style){
    mFlow.setHorizontalStyle(style);
    requestLayout();
  }
  public void setVerticalStyle(  int style){
    mFlow.setVerticalStyle(style);
    requestLayout();
  }
  public void setHorizontalBias(  float bias){
    mFlow.setHorizontalBias(bias);
    requestLayout();
  }
  public void setVerticalBias(  float bias){
    mFlow.setVerticalBias(bias);
    requestLayout();
  }
  public void setFirstHorizontalStyle(  int style){
    mFlow.setFirstHorizontalStyle(style);
    requestLayout();
  }
  public void setFirstVerticalStyle(  int style){
    mFlow.setFirstVerticalStyle(style);
    requestLayout();
  }
  public void setFirstHorizontalBias(  float bias){
    mFlow.setFirstHorizontalBias(bias);
    requestLayout();
  }
  public void setFirstVerticalBias(  float bias){
    mFlow.setFirstVerticalBias(bias);
    requestLayout();
  }
  public void setHorizontalAlign(  int align){
    mFlow.setHorizontalAlign(align);
    requestLayout();
  }
  public void setVerticalAlign(  int align){
    mFlow.setVerticalAlign(align);
    requestLayout();
  }
  public void setHorizontalGap(  int gap){
    mFlow.setHorizontalGap(gap);
    requestLayout();
  }
  public void setVerticalGap(  int gap){
    mFlow.setVerticalGap(gap);
    requestLayout();
  }
  public void setMaxElementsWrap(  int max){
    mFlow.setMaxElementsWrap(max);
    requestLayout();
  }
  public Flow(){
    mFlow=new androidx.constraintlayout.core.widgets.CoreFlow();
    mHelperWidget=mFlow;
    validateParams();
  }
}
