package androidx.constraintlayout.widget;
import r.android.content.Context;
import r.android.content.res.Resources;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Helper;
import androidx.constraintlayout.core.widgets.HelperWidget;
import r.android.util.Log;
import r.android.util.SparseArray;
import r.android.view.View;
import r.android.view.ViewGroup;
import r.android.view.ViewParent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
public abstract class ConstraintHelper extends View {
  protected int[] mIds=new int[32];
  protected int mCount;
  protected Context myContext;
  protected Helper mHelperWidget;
  protected boolean mUseViewMeasure=false;
  protected String mReferenceIds;
  private View[] mViews=null;
  protected HashMap<Integer,String> mMap=new HashMap<>();
  public void addView(  View view){
    if (view == this) {
      return;
    }
    if (view.getId() == -1) {
      Log.e("ConstraintHelper","Views added to a ConstraintHelper need to have an id");
      return;
    }
    if (view.getParent() == null) {
      Log.e("ConstraintHelper","Views added to a ConstraintHelper need to have a parent");
      return;
    }
    mReferenceIds=null;
    addRscID(view.getId());
    requestLayout();
  }
  public int removeView(  View view){
    int index=-1;
    int id=view.getId();
    if (id == -1) {
      return index;
    }
    mReferenceIds=null;
    for (int i=0; i < mCount; i++) {
      if (mIds[i] == id) {
        index=i;
        for (int j=i; j < mCount - 1; j++) {
          mIds[j]=mIds[j + 1];
        }
        mIds[mCount - 1]=0;
        mCount--;
        break;
      }
    }
    requestLayout();
    return index;
  }
  public int[] getReferencedIds(){
    return Arrays.copyOf(mIds,mCount);
  }
  public void setReferencedIds(  int[] ids){
    mReferenceIds=null;
    mCount=0;
    for (int i=0; i < ids.length; i++) {
      addRscID(ids[i]);
    }
  }
  private void addRscID(  int id){
    if (id == getId()) {
      return;
    }
    if (mCount + 1 > mIds.length) {
      mIds=Arrays.copyOf(mIds,mIds.length * 2);
    }
    mIds[mCount]=id;
    mCount++;
  }
  protected void onMeasure(  int widthMeasureSpec,  int heightMeasureSpec){
    if (mUseViewMeasure) {
      super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
 else {
      setMeasuredDimension(0,0);
    }
  }
  public void validateParams(){
    if (mHelperWidget == null) {
      return;
    }
    ViewGroup.LayoutParams params=getLayoutParams();
    if (params instanceof ConstraintLayout.LayoutParams) {
      ConstraintLayout.LayoutParams layoutParams=(ConstraintLayout.LayoutParams)params;
      layoutParams.widget=(ConstraintWidget)mHelperWidget;
    }
  }
  private void addID(  String idString){
    if (idString == null || idString.length() == 0) {
      return;
    }
    if (myContext == null) {
      return;
    }
    idString=idString.trim();
    ConstraintLayout parent=null;
    if (getParent() instanceof ConstraintLayout) {
      parent=(ConstraintLayout)getParent();
    }
    int rscId=findId(idString);
    if (rscId != 0) {
      mMap.put(rscId,idString);
      addRscID(rscId);
    }
 else {
      Log.w("ConstraintHelper","Could not find id of \"" + idString + "\"");
    }
  }
  private int findId(  String referenceId){
    ConstraintLayout parent=null;
    if (getParent() instanceof ConstraintLayout) {
      parent=(ConstraintLayout)getParent();
    }
    int rscId=0;
    if (isInEditMode() && parent != null) {
      Object value=parent.getDesignInformation(0,referenceId);
      if (value instanceof Integer) {
        rscId=(Integer)value;
      }
    }
    if (rscId == 0 && parent != null) {
      rscId=findId(parent,referenceId);
    }
    if (rscId == 0) {
      try {
        Class res=r.android.R.id.class;
        //Field field=res.getField(referenceId);
        //rscId=field.getInt(null);
      }
 catch (      Exception e) {
      }
    }
    if (rscId == 0) {
      rscId=myContext.getResources().getIdentifier(referenceId,"id",myContext.getPackageName());
    }
    return rscId;
  }
  private int findId(  ConstraintLayout container,  String idString){
    if (idString == null || container == null) {
      return 0;
    }
    Resources resources=myContext.getResources();
    if (resources == null) {
      return 0;
    }
    final int count=container.getChildCount();
    for (int j=0; j < count; j++) {
      View child=container.getChildAt(j);
      if (child.getId() != -1) {
        String res=null;
        try {
          res=resources.getResourceEntryName(child.getId());
        }
 catch (        r.android.content.res.Resources.NotFoundException e) {
        }
        if (idString.equals(res)) {
          return child.getId();
        }
      }
    }
    return 0;
  }
  protected void setIds(  String idList){
    mReferenceIds=idList;
    if (idList == null) {
      return;
    }
    int begin=0;
    mCount=0;
    while (true) {
      int end=idList.indexOf(',',begin);
      if (end == -1) {
        addID(idList.substring(begin));
        break;
      }
      addID(idList.substring(begin,end));
      begin=end + 1;
    }
  }
  protected void applyLayoutFeatures(  ConstraintLayout container){
    int visibility=getVisibility();
    float elevation=0;
    if (r.android.os.Build.VERSION.SDK_INT >= r.android.os.Build.VERSION_CODES.LOLLIPOP) {
      elevation=getElevation();
    }
    for (int i=0; i < mCount; i++) {
      int id=mIds[i];
      View view=container.getViewById(id);
      if (view != null) {
        view.setVisibility(visibility);
        if (elevation > 0 && r.android.os.Build.VERSION.SDK_INT >= r.android.os.Build.VERSION_CODES.LOLLIPOP) {
          view.setTranslationZ(view.getTranslationZ() + elevation);
        }
      }
    }
  }
  protected void applyLayoutFeatures(){
    ViewParent parent=getParent();
    if (parent != null && parent instanceof ConstraintLayout) {
      applyLayoutFeatures((ConstraintLayout)parent);
    }
  }
  protected void applyLayoutFeaturesInConstraintSet(  ConstraintLayout container){
  }
  public void updatePreLayout(  ConstraintLayout container){
    if (isInEditMode()) {
      setIds(mReferenceIds);
    }
    if (mHelperWidget == null) {
      return;
    }
    mHelperWidget.removeAllIds();
    for (int i=0; i < mCount; i++) {
      int id=mIds[i];
      View view=container.getViewById(id);
      if (view == null) {
        String candidate=mMap.get(id);
        int foundId=findId(container,candidate);
        if (foundId != 0) {
          mIds[i]=foundId;
          mMap.put(foundId,candidate);
          view=container.getViewById(foundId);
        }
      }
      if (view != null) {
        mHelperWidget.add(container.getViewWidget(view));
      }
    }
    mHelperWidget.updateConstraints(container.mLayoutWidget);
  }
  public void updatePreLayout(  ConstraintWidgetContainer container,  Helper helper,  SparseArray<ConstraintWidget> map){
    helper.removeAllIds();
    for (int i=0; i < mCount; i++) {
      int id=mIds[i];
      helper.add(map.get(id));
    }
  }
  protected View[] getViews(  ConstraintLayout layout){
    if (mViews == null || mViews.length != mCount) {
      mViews=new View[mCount];
    }
    for (int i=0; i < mCount; i++) {
      int id=mIds[i];
      mViews[i]=layout.getViewById(id);
    }
    return mViews;
  }
  public void updatePostLayout(  ConstraintLayout container){
  }
  public void updatePostMeasure(  ConstraintLayout container){
  }
  public void loadParameters(  ConstraintSet.Constraint constraint,  HelperWidget child,  ConstraintLayout.LayoutParams layoutParams,  SparseArray<ConstraintWidget> mapIdToWidget){
    if (constraint.layout.mReferenceIds != null) {
      setReferencedIds(constraint.layout.mReferenceIds);
    }
 else     if (constraint.layout.mReferenceIdString != null && constraint.layout.mReferenceIdString.length() > 0) {
      constraint.layout.mReferenceIds=convertReferenceString(this,constraint.layout.mReferenceIdString);
    }
    child.removeAllIds();
    if (constraint.layout.mReferenceIds != null) {
      for (int i=0; i < constraint.layout.mReferenceIds.length; i++) {
        int id=constraint.layout.mReferenceIds[i];
        ConstraintWidget widget=mapIdToWidget.get(id);
        if (widget != null) {
          child.add(widget);
        }
      }
    }
  }
  private int[] convertReferenceString(  View view,  String referenceIdString){
    String[] split=referenceIdString.split(",");
    Context context=view.getContext();
    int[] rscIds=new int[split.length];
    int count=0;
    for (int i=0; i < split.length; i++) {
      String idString=split[i];
      idString=idString.trim();
      int id=findId(idString);
      if (id != 0) {
        rscIds[count++]=id;
      }
    }
    if (count != split.length) {
      rscIds=Arrays.copyOf(rscIds,count);
    }
    return rscIds;
  }
  public void resolveRtl(  ConstraintWidget widget,  boolean isRtl){
  }
  public void setTag(  int key,  Object tag){
    super.setTag(key,tag);
    if (tag == null && mReferenceIds == null) {
      addRscID(key);
    }
  }
  public boolean containsId(  final int id){
    boolean result=false;
    for (    int i : mIds) {
      if (i == id) {
        result=true;
        break;
      }
    }
    return result;
  }
  public int indexFromId(  final int id){
    int index=-1;
    for (    int i : mIds) {
      index++;
      if (i == id) {
        return index;
      }
    }
    return index;
  }
}
