package androidx.constraintlayout.widget;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import r.android.view.View;
public class Placeholder extends View {
  private int mContentId=-1;
  private View mContent=null;
  private int mEmptyVisibility=View.INVISIBLE;
  public void setEmptyVisibility(  int visibility){
    mEmptyVisibility=visibility;
  }
  public int getEmptyVisibility(){
    return mEmptyVisibility;
  }
  public View getContent(){
    return mContent;
  }
  public void updatePreLayout(  ConstraintLayout container){
    if (mContentId == -1) {
      if (!isInEditMode()) {
        setVisibility(mEmptyVisibility);
      }
    }
    mContent=container.findViewById(mContentId);
    if (mContent != null) {
      ConstraintLayout.LayoutParams layoutParamsContent=(ConstraintLayout.LayoutParams)mContent.getLayoutParams();
      layoutParamsContent.isInPlaceholder=true;
      mContent.setVisibility(View.VISIBLE);
      setVisibility(View.VISIBLE);
    }
  }
  public void setContentId(  int id){
    if (mContentId == id) {
      return;
    }
    if (mContent != null) {
      mContent.setVisibility(VISIBLE);
      ConstraintLayout.LayoutParams layoutParamsContent=(ConstraintLayout.LayoutParams)mContent.getLayoutParams();
      layoutParamsContent.isInPlaceholder=false;
      mContent=null;
    }
    mContentId=id;
    if (id != ConstraintLayout.LayoutParams.UNSET) {
      View v=((View)getParent()).findViewById(id);
      if (v != null) {
        v.setVisibility(GONE);
      }
    }
  }
  public void updatePostMeasure(  ConstraintLayout container){
    if (mContent == null) {
      return;
    }
    ConstraintLayout.LayoutParams layoutParams=(ConstraintLayout.LayoutParams)getLayoutParams();
    ConstraintLayout.LayoutParams layoutParamsContent=(ConstraintLayout.LayoutParams)mContent.getLayoutParams();
    layoutParamsContent.widget.setVisibility(View.VISIBLE);
    if (layoutParams.widget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.FIXED) {
      layoutParams.widget.setWidth(layoutParamsContent.widget.getWidth());
    }
    if (layoutParams.widget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.FIXED) {
      layoutParams.widget.setHeight(layoutParamsContent.widget.getHeight());
    }
    layoutParamsContent.widget.setVisibility(View.GONE);
  }
  public Placeholder(){
    super.setVisibility(mEmptyVisibility);
    mContentId=-1;
  }
}
