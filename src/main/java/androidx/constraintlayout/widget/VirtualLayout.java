package androidx.constraintlayout.widget;
import r.android.view.View;
public abstract class VirtualLayout extends ConstraintHelper {
  private boolean mApplyVisibilityOnAttach;
  private boolean mApplyElevationOnAttach;
  public void onMeasure(  androidx.constraintlayout.core.widgets.CoreVirtualLayout layout,  int widthMeasureSpec,  int heightMeasureSpec){
  }
  public void setVisibility(  int visibility){
    super.setVisibility(visibility);
    applyLayoutFeatures();
  }
  public void setElevation(  float elevation){
    super.setElevation(elevation);
    applyLayoutFeatures();
  }
  protected void applyLayoutFeaturesInConstraintSet(  ConstraintLayout container){
    applyLayoutFeatures(container);
  }
}
