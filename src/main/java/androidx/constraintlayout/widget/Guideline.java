package androidx.constraintlayout.widget;
import r.android.view.View;
public class Guideline extends View {
  public void setVisibility(  int visibility){
  }
  protected void onMeasure(  int widthMeasureSpec,  int heightMeasureSpec){
    setMeasuredDimension(0,0);
  }
  public void setGuidelineBegin(  int margin){
    ConstraintLayout.LayoutParams params=(ConstraintLayout.LayoutParams)getLayoutParams();
    params.guideBegin=margin;
    setLayoutParams(params);
  }
  public void setGuidelineEnd(  int margin){
    ConstraintLayout.LayoutParams params=(ConstraintLayout.LayoutParams)getLayoutParams();
    params.guideEnd=margin;
    setLayoutParams(params);
  }
  public void setGuidelinePercent(  float ratio){
    ConstraintLayout.LayoutParams params=(ConstraintLayout.LayoutParams)getLayoutParams();
    params.guidePercent=ratio;
    setLayoutParams(params);
  }
  public Guideline(  r.android.content.Context context){
    super.setVisibility(View.GONE);
  }
  public Guideline(){
    super.setVisibility(View.GONE);
  }
}
