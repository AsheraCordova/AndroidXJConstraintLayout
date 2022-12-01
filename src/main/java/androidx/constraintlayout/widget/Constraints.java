package androidx.constraintlayout.widget;
import r.android.view.View;
import r.android.view.ViewGroup;
public class Constraints extends ViewGroup {
  ConstraintSet myConstraintSet;
public static class LayoutParams extends ConstraintLayout.LayoutParams {
    public float alpha=1;
    public boolean applyElevation=false;
    public float elevation=0;
    public float rotation=0;
    public float rotationX=0;
    public float rotationY=0;
    public float scaleX=1;
    public float scaleY=1;
    public float transformPivotX=0;
    public float transformPivotY=0;
    public float translationX=0;
    public float translationY=0;
    public float translationZ=0;
    public LayoutParams(    int width,    int height){
      super(width,height);
    }
    public LayoutParams(    LayoutParams source){
      super(source);
    }
  }
  public ConstraintSet getConstraintSet(){
    if (myConstraintSet == null) {
      myConstraintSet=new ConstraintSet();
    }
    myConstraintSet.clone(this);
    return myConstraintSet;
  }
  protected void onLayout(  boolean changed,  int l,  int t,  int r,  int b){
  }
}
