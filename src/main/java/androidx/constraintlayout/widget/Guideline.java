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
 * Copyright (C) 2015 The Android Open Source Project
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
