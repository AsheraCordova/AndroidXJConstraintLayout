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
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.constraintlayout.helper.widget;
import r.android.util.Log;
import r.android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import r.android.R;
import androidx.constraintlayout.widget.VirtualLayout;
import java.util.Arrays;
public class CircularFlow extends VirtualLayout {
  private static final String TAG="CircularFlow";
  @com.google.j2objc.annotations.Weak ConstraintLayout mContainer;
  int mViewCenter;
  private static int DEFAULT_RADIUS=0;
  private static float DEFAULT_ANGLE=0F;
  private float[] mAngles;
  private int[] mRadius;
  private int mCountRadius;
  private int mCountAngle;
  private String mReferenceAngles;
  private String mReferenceRadius;
  private Float mReferenceDefaultAngle;
  private Integer mReferenceDefaultRadius;
  public int[] getRadius(){
    return Arrays.copyOf(mRadius,mCountRadius);
  }
  public float[] getAngles(){
    return Arrays.copyOf(mAngles,mCountAngle);
  }
  public void onAttachedToWindow(){
    super.onAttachedToWindow();
    if (mReferenceAngles != null) {
      mAngles=new float[1];
      setAngles(mReferenceAngles);
    }
    if (mReferenceRadius != null) {
      mRadius=new int[1];
      setRadius(mReferenceRadius);
    }
    if (mReferenceDefaultAngle != null) {
      setDefaultAngle(mReferenceDefaultAngle);
    }
    if (mReferenceDefaultRadius != null) {
      setDefaultRadius(mReferenceDefaultRadius);
    }
    anchorReferences();
  }
  private void anchorReferences(){
    mContainer=(ConstraintLayout)getParent();
    for (int i=0; i < mCount; i++) {
      View view=mContainer.getViewById(mIds[i]);
      if (view == null) {
        continue;
      }
      int radius=DEFAULT_RADIUS;
      float angle=DEFAULT_ANGLE;
      if (mRadius != null && i < mRadius.length) {
        radius=mRadius[i];
      }
 else       if (mReferenceDefaultRadius != null && mReferenceDefaultRadius != -1) {
        mCountRadius++;
        if (mRadius == null) {
          mRadius=new int[1];
        }
        mRadius=getRadius();
        mRadius[mCountRadius - 1]=radius;
      }
 else {
        Log.e("CircularFlow","Added radius to view with id: " + mMap.get(view.getId()));
      }
      if (mAngles != null && i < mAngles.length) {
        angle=mAngles[i];
      }
 else       if (mReferenceDefaultAngle != null && mReferenceDefaultAngle != -1) {
        mCountAngle++;
        if (mAngles == null) {
          mAngles=new float[1];
        }
        mAngles=getAngles();
        mAngles[mCountAngle - 1]=angle;
      }
 else {
        Log.e("CircularFlow","Added angle to view with id: " + mMap.get(view.getId()));
      }
      ConstraintLayout.LayoutParams params=(ConstraintLayout.LayoutParams)view.getLayoutParams();
      params.circleAngle=angle;
      params.circleConstraint=mViewCenter;
      params.circleRadius=radius;
      view.setLayoutParams(params);
    }
    applyLayoutFeatures();
  }
  public void addViewToCircularFlow(  View view,  int radius,  float angle){
    if (containsId(view.getId())) {
      return;
    }
    addView(view);
    mCountAngle++;
    mAngles=getAngles();
    mAngles[mCountAngle - 1]=angle;
    mCountRadius++;
    mRadius=getRadius();
    mRadius[mCountRadius - 1]=(int)(radius * myContext.getResources().getDisplayMetrics().density);
    anchorReferences();
  }
  public void updateRadius(  View view,  int radius){
    if (!isUpdatable(view)) {
      Log.e("CircularFlow","It was not possible to update radius to view with id: " + view.getId());
      return;
    }
    int indexView=indexFromId(view.getId());
    if (indexView > mRadius.length) {
      return;
    }
    mRadius=getRadius();
    mRadius[indexView]=(int)(radius * myContext.getResources().getDisplayMetrics().density);
    anchorReferences();
  }
  public void updateAngle(  View view,  float angle){
    if (!isUpdatable(view)) {
      Log.e("CircularFlow","It was not possible to update angle to view with id: " + view.getId());
      return;
    }
    int indexView=indexFromId(view.getId());
    if (indexView > mAngles.length) {
      return;
    }
    mAngles=getAngles();
    mAngles[indexView]=angle;
    anchorReferences();
  }
  public void updateReference(  View view,  int radius,  float angle){
    if (!isUpdatable(view)) {
      Log.e("CircularFlow","It was not possible to update radius and angle to view with id: " + view.getId());
      return;
    }
    int indexView=indexFromId(view.getId());
    if (getAngles().length > indexView) {
      mAngles=getAngles();
      mAngles[indexView]=angle;
    }
    if (getRadius().length > indexView) {
      mRadius=getRadius();
      mRadius[indexView]=(int)(radius * myContext.getResources().getDisplayMetrics().density);
    }
    anchorReferences();
  }
  public void setDefaultAngle(  float angle){
    DEFAULT_ANGLE=angle;
  }
  public void setDefaultRadius(  int radius){
    DEFAULT_RADIUS=radius;
  }
  public int removeView(  View view){
    int index=super.removeView(view);
    if (index == -1) {
      return index;
    }
    ConstraintSet c=new ConstraintSet();
    c.clone(mContainer);
    c.clear(view.getId(),ConstraintSet.CIRCLE_REFERENCE);
    c.applyTo(mContainer);
    if (index < mAngles.length) {
      mAngles=removeAngle(mAngles,index);
      mCountAngle--;
    }
    if (index < mRadius.length) {
      mRadius=removeRadius(mRadius,index);
      mCountRadius--;
    }
    anchorReferences();
    return index;
  }
  private float[] removeAngle(  float[] angles,  int index){
    if (angles == null || index < 0 || index >= mCountAngle) {
      return angles;
    }
    return removeElementFromArray(angles,index);
  }
  private int[] removeRadius(  int[] radius,  int index){
    if (radius == null || index < 0 || index >= mCountRadius) {
      return radius;
    }
    return removeElementFromArray(radius,index);
  }
  private void setAngles(  String idList){
    if (idList == null) {
      return;
    }
    int begin=0;
    mCountAngle=0;
    while (true) {
      int end=idList.indexOf(',',begin);
      if (end == -1) {
        addAngle(idList.substring(begin).trim());
        break;
      }
      addAngle(idList.substring(begin,end).trim());
      begin=end + 1;
    }
  }
  private void setRadius(  String idList){
    if (idList == null) {
      return;
    }
    int begin=0;
    mCountRadius=0;
    while (true) {
      int end=idList.indexOf(',',begin);
      if (end == -1) {
        addRadius(idList.substring(begin).trim());
        break;
      }
      addRadius(idList.substring(begin,end).trim());
      begin=end + 1;
    }
  }
  private void addAngle(  String angleString){
    if (angleString == null || angleString.length() == 0) {
      return;
    }
    if (myContext == null) {
      return;
    }
    if (mAngles == null) {
      return;
    }
    if (mCountAngle + 1 > mAngles.length) {
      mAngles=Arrays.copyOf(mAngles,mAngles.length + 1);
    }
    mAngles[mCountAngle]=Integer.parseInt(angleString);
    mCountAngle++;
  }
  private void addRadius(  String radiusString){
    if (radiusString == null || radiusString.length() == 0) {
      return;
    }
    if (myContext == null) {
      return;
    }
    if (mRadius == null) {
      return;
    }
    if (mCountRadius + 1 > mRadius.length) {
      mRadius=Arrays.copyOf(mRadius,mRadius.length + 1);
    }
    mRadius[mCountRadius]=(int)(Integer.parseInt(radiusString) * myContext.getResources().getDisplayMetrics().density);
    mCountRadius++;
  }
  public static int[] removeElementFromArray(  int[] array,  int index){
    int[] newArray=new int[array.length - 1];
    for (int i=0, k=0; i < array.length; i++) {
      if (i == index) {
        continue;
      }
      newArray[k++]=array[i];
    }
    return newArray;
  }
  public static float[] removeElementFromArray(  float[] array,  int index){
    float[] newArray=new float[array.length - 1];
    for (int i=0, k=0; i < array.length; i++) {
      if (i == index) {
        continue;
      }
      newArray[k++]=array[i];
    }
    return newArray;
  }
  public boolean isUpdatable(  View view){
    if (!containsId(view.getId())) {
      return false;
    }
    int indexView=indexFromId(view.getId());
    return indexView != -1;
  }
  public CircularFlow(){
    myContext=new r.android.content.Context();
  }
  public void setViewCenter(  int mViewCenter){
    this.mViewCenter=mViewCenter;
  }
  public void setMyRadius(  String idList){
    if (mRadius == null) {
      mRadius=new int[1];
    }
    setRadius(idList);
  }
  public void setMyAngles(  String idList){
    if (mAngles == null) {
      mAngles=new float[1];
    }
    setAngles(idList);
  }
  public void setReferenceDefaultAngle(  Float mReferenceDefaultAngle){
    this.mReferenceDefaultAngle=mReferenceDefaultAngle;
  }
  public void setReferenceDefaultRadius(  Integer mReferenceDefaultRadius){
    this.mReferenceDefaultRadius=mReferenceDefaultRadius;
  }
}
