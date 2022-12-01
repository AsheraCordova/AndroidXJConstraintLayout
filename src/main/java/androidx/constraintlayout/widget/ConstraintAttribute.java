package androidx.constraintlayout.widget;
import r.android.graphics.Color;
import r.android.graphics.drawable.ColorDrawable;
import r.android.graphics.drawable.Drawable;
import r.android.util.Log;
import r.android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
public class ConstraintAttribute {
  private static final String TAG="TransitionLayout";
  private boolean mMethod=false;
  String mName;
  private AttributeType mType;
  private int mIntegerValue;
  private float mFloatValue;
  private String mStringValue;
  boolean mBooleanValue;
  private int mColorValue;
  public enum AttributeType {  INT_TYPE,   FLOAT_TYPE,   COLOR_TYPE,   COLOR_DRAWABLE_TYPE,   STRING_TYPE,   BOOLEAN_TYPE,   DIMENSION_TYPE,   REFERENCE_TYPE}
  public void setValue(  float[] value){
switch (mType) {
case REFERENCE_TYPE:
case INT_TYPE:
      mIntegerValue=(int)value[0];
    break;
case FLOAT_TYPE:
  mFloatValue=value[0];
break;
case COLOR_DRAWABLE_TYPE:
case COLOR_TYPE:
mColorValue=Color.HSVToColor(value);
mColorValue=(mColorValue & 0xFFFFFF) | (clamp((int)(0xFF * value[3])) << 24);
break;
case STRING_TYPE:
throw new RuntimeException("Color does not have a single color to interpolate");
case BOOLEAN_TYPE:
mBooleanValue=value[0] > 0.5;
break;
case DIMENSION_TYPE:
mFloatValue=value[0];
}
}
public ConstraintAttribute(ConstraintAttribute source,Object value){
mName=source.mName;
mType=source.mType;
setValue(value);
}
public void setValue(Object value){
switch (mType) {
case REFERENCE_TYPE:
case INT_TYPE:
mIntegerValue=(Integer)value;
break;
case FLOAT_TYPE:
mFloatValue=(Float)value;
break;
case COLOR_TYPE:
case COLOR_DRAWABLE_TYPE:
mColorValue=(Integer)value;
break;
case STRING_TYPE:
mStringValue=(String)value;
break;
case BOOLEAN_TYPE:
mBooleanValue=(Boolean)value;
break;
case DIMENSION_TYPE:
mFloatValue=(Float)value;
break;
}
}
public static HashMap<String,ConstraintAttribute> extractAttributes(HashMap<String,ConstraintAttribute> base,View view){
HashMap<String,ConstraintAttribute> ret=new HashMap<>();
Class<? extends View> viewClass=view.getClass();
for (String name : base.keySet()) {
ConstraintAttribute constraintAttribute=base.get(name);
try {
if (name.equals("BackgroundColor")) {
ColorDrawable viewColor=(ColorDrawable)view.getBackground();
Object val=viewColor.getColor();
ret.put(name,new ConstraintAttribute(constraintAttribute,val));
}
 else {
Method method=viewClass.getMethod("getMap" + name);
Object val=method.invoke(view);
ret.put(name,new ConstraintAttribute(constraintAttribute,val));
}
}
 catch (NoSuchMethodException e) {
e.printStackTrace();
}
catch (IllegalAccessException e) {
e.printStackTrace();
}
catch (InvocationTargetException e) {
e.printStackTrace();
}
}
return ret;
}
public static void setAttributes(View view,HashMap<String,ConstraintAttribute> map){
Class<? extends View> viewClass=view.getClass();
for (String name : map.keySet()) {
ConstraintAttribute constraintAttribute=map.get(name);
String methodName=name;
if (!constraintAttribute.mMethod) {
methodName="set" + methodName;
}
try {
Method method;
switch (constraintAttribute.mType) {
case INT_TYPE:
method=viewClass.getMethod(methodName,Integer.TYPE);
method.invoke(view,constraintAttribute.mIntegerValue);
break;
case FLOAT_TYPE:
method=viewClass.getMethod(methodName,Float.TYPE);
method.invoke(view,constraintAttribute.mFloatValue);
break;
case COLOR_DRAWABLE_TYPE:
method=viewClass.getMethod(methodName,Drawable.class);
ColorDrawable drawable=new ColorDrawable();
drawable.setColor(constraintAttribute.mColorValue);
method.invoke(view,drawable);
break;
case COLOR_TYPE:
method=viewClass.getMethod(methodName,Integer.TYPE);
method.invoke(view,constraintAttribute.mColorValue);
break;
case STRING_TYPE:
method=viewClass.getMethod(methodName,CharSequence.class);
method.invoke(view,constraintAttribute.mStringValue);
break;
case BOOLEAN_TYPE:
method=viewClass.getMethod(methodName,Boolean.TYPE);
method.invoke(view,constraintAttribute.mBooleanValue);
break;
case DIMENSION_TYPE:
method=viewClass.getMethod(methodName,Float.TYPE);
method.invoke(view,constraintAttribute.mFloatValue);
break;
case REFERENCE_TYPE:
method=viewClass.getMethod(methodName,Integer.TYPE);
method.invoke(view,constraintAttribute.mIntegerValue);
}
}
 catch (NoSuchMethodException e) {
Log.e(TAG,e.getMessage());
Log.e(TAG," Custom Attribute \"" + name + "\" not found on "+ viewClass.getName());
Log.e(TAG,viewClass.getName() + " must have a method " + methodName);
}
catch (IllegalAccessException e) {
Log.e(TAG," Custom Attribute \"" + name + "\" not found on "+ viewClass.getName());
e.printStackTrace();
}
catch (InvocationTargetException e) {
Log.e(TAG," Custom Attribute \"" + name + "\" not found on "+ viewClass.getName());
e.printStackTrace();
}
}
}
private static int clamp(int c){
int N=255;
c&=~(c >> 31);
c-=N;
c&=(c >> 31);
c+=N;
return c;
}
}
