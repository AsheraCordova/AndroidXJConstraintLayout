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
 * Copyright (C) 2017 The Android Open Source Project
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
