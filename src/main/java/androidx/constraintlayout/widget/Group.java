package androidx.constraintlayout.widget;
import r.android.view.View;
public class Group extends ConstraintHelper {
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
  public void updatePostLayout(  ConstraintLayout container){
    ConstraintLayout.LayoutParams params=(ConstraintLayout.LayoutParams)getLayoutParams();
    params.widget.setWidth(0);
    params.widget.setHeight(0);
  }
  public Group(){
    mUseViewMeasure=false;
  }
}
