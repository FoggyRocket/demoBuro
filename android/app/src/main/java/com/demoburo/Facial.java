package com.demoburo;




import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.miteksystems.facialcapture.science.api.params.FacialCaptureApi;
import com.miteksystems.facialcapture.workflow.FacialCaptureWorkflowActivity;
import com.miteksystems.facialcapture.workflow.params.FacialCaptureWorkflowParameters;
import com.miteksystems.misnap.params.CameraApi;
import com.miteksystems.misnap.params.MiSnapApi;

import org.json.JSONException;
import org.json.JSONObject;




public class Facial extends ReactContextBaseJavaModule {

    private static final long PREVENT_DOUBLE_CLICK_TIME_MS = 1000;
    private long mTime;
    protected static final String LICENSE_KEY = "{\"signature\":\"qnluwptgoqUBKofOJgZcPA4\\/KtEsOlvHyh9IW4kGPAzAlMcRMNc1dB3\\/qT3QfMb+pz+Gl\\/U9jQ93R\\/oNGXVGRB48XAa9l1byLhu8FvfazdSkO7yFIXtPRI6+m4rnyJIqUjgnhawjwBKECvTRRuiNwesC2xLrw0+ADiJWnk8RZWLWzj5nY\\/ua9o3NiMnCISYOhHy8J9W8O\\/83qdaKxmUnANPnFwlyTnd1HOlDWWmg86aPArj2AHn9ckNCUQF6WD9Y7gRsXXQW+NTHcwPyCzwZDApX92GDc4mj\\/fwoV42W1PzpUMx7n8dEukP2mZATCtwBCVJ7Tbc6H\\/R2JF5pkd\\/ytQ==\",\"organization\":\"Daon\",\"signed\":{\"features\":[\"ALL\"],\"expiry\":\"2019-08-08 00:00:00\",\"applicationIdentifier\":\"com.miteksystems.*\"},\"version\":\"2.1\"}";

    private static final int IMAGE_PICKER_REQUEST = 467081;
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_PICKER_CANCELLED = "E_PICKER_CANCELLED";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";

    private Promise mPickerPromise;


    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            Activity currentActivity = getCurrentActivity();
            JSONObject newObj = new JSONObject();
            if (MiSnapApi.RESULT_PICTURE_CODE == requestCode) {

                if (mPickerPromise != null) {
                    if (Activity.RESULT_OK == resultCode) {
                        if (data != null) {

                            byte[] image = data.getByteArrayExtra(MiSnapApi.RESULT_PICTURE_DATA);


                            String base = Base64.encodeToString(image, Base64.DEFAULT);
                            String mibi = data.getExtras().getString(MiSnapApi.RESULT_MIBI_DATA);

                            try {
                                switch (data.getExtras().getString(MiSnapApi.RESULT_CODE)) {
                                    // MiSnap check capture
                                    case FacialCaptureApi.RESULT_SPOOF_DETECTED:
                                        newObj.put("MiSnapResultCode","SPOOF_DETECTED");
                                        break;
                                    case FacialCaptureApi.RESULT_UNVERIFIED:
                                    case MiSnapApi.RESULT_SUCCESS_STILL:
                                        newObj.put("MiSnapResultCode","UNVERIFIED");
                                        break;
                                    case MiSnapApi.RESULT_SUCCESS_VIDEO:
                                        newObj.put("MiSnapResultCode","VERIFIED");
                                        break;
                                }

                                newObj.put("image","data:image/jpeg;base64,"+base);

                                mPickerPromise.resolve(newObj.toString());
                            }catch (JSONException e)  {

                                mPickerPromise.reject("Error:", e);
                            }


                        } else {
//                            // Image canceled, stop
                            mPickerPromise.reject(E_NO_IMAGE_DATA_FOUND, "No image data found");

                        }
                    }
                    mPickerPromise = null;

                }
            }
        }
    };
    private Bitmap formBitmapImage(byte[] byteImage) {
        System.gc();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;    // downscale by 2 as it's just a review

        Bitmap sourceBmp = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
        int height = sourceBmp.getHeight();
        int width = sourceBmp.getWidth();


        Bitmap targetBmp  = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


        sourceBmp.recycle();    // no use anymore


        return targetBmp;
    }

    Facial(ReactApplicationContext reactContext){
        super(reactContext);

        // Add the listener for `onActivityResult`
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @ReactMethod
    private void startFacial(final Promise promise) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        // Prevent multiple MiSnap instances by preventing multiple button presses
        if (System.currentTimeMillis() - mTime < PREVENT_DOUBLE_CLICK_TIME_MS) {
            // Double-press detected
            return;
        }
        mTime = System.currentTimeMillis();

        // Add in parameter info for MiSnap
        //ParameterOverrides overrides = new ParameterOverrides(this);
        //Map<String, Integer> paramMap = overrides.load();

        JSONObject jjs = new JSONObject();
        try {
            // MiSnap-specific parameters
            jjs.put(CameraApi.MiSnapAllowScreenshots, 1);
            // FacialCapture-specific parameters
            jjs.put(FacialCaptureApi.FacialCaptureLicenseKey, LICENSE_KEY);
            jjs.put(FacialCaptureApi.BlinkThreshold, 500);
            jjs.put(FacialCaptureApi.CaptureEyesOpen, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jjsWorkflow = new JSONObject();
        try {
            // Optionally add in customizable runtime settings for the FacialCapture workflow.
            // NOTE: These don't go into the JOB_SETTINGS because they are for your app, not for core FacialCapture.
            jjsWorkflow.put(FacialCaptureWorkflowParameters.FACIALCAPTURE_WORKFLOW_MESSAGE_DELAY, 500);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPickerPromise = promise;
        try {
            final Intent intentFacialCapture = new Intent(currentActivity,FacialCaptureWorkflowActivity.class);

            intentFacialCapture.putExtra(MiSnapApi.JOB_SETTINGS, jjs.toString());
            intentFacialCapture.putExtra(FacialCaptureWorkflowParameters.EXTRA_WORKFLOW_PARAMETERS, jjsWorkflow.toString());
            currentActivity.startActivityForResult(intentFacialCapture,MiSnapApi.RESULT_PICTURE_CODE); //(intentFacialCapture, MiSnapApi.RESULT_PICTURE_CODE);

        } catch (Exception e) {
            mPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, e);
            mPickerPromise = null;
        }





    }
    @Override
    public String getName(){
        return "Facial";
    }

}
