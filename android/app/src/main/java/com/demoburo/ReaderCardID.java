package com.demoburo;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.miteksystems.misnap.misnapworkflow.params.WorkflowApi;
import com.miteksystems.misnap.params.MiSnapApi;
import com.miteksystems.misnap.analyzer.MiSnapAnalyzerResult;
import com.miteksystems.misnap.misnapworkflow.MiSnapWorkflowActivity;
import com.miteksystems.misnap.params.CameraApi;
import com.miteksystems.misnap.params.ScienceApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ReaderCardID extends ReactContextBaseJavaModule {
    private static final long PREVENT_DOUBLE_CLICK_TIME_MS = 1000;
    private long mTime;

    private static final int IMAGE_PICKER_REQUEST = 467081;
    private static int mUxWorkflow;
    private static int mGeoRegion;

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
                            Bundle extras = data.getExtras();
                            String miSnapResultCode = extras.getString(MiSnapApi.RESULT_CODE);
//

                            String mibi = data.getExtras().getString(MiSnapApi.RESULT_MIBI_DATA);
//                            mPickerPromise.resolve(base);
                            try {
                                switch (miSnapResultCode) {
                                    // MiSnap check capture
                                    case MiSnapApi.RESULT_SUCCESS_VIDEO:
                                    case MiSnapApi.RESULT_SUCCESS_STILL:
//                                        Log.i("Pruebaa", "MIBI: " + extras.getString(MiSnapApi.RESULT_CODE));


                                        // Now Base64-encode the byte array before sending it to the server.
                                        // e.g. byte[] sEncodedImage = Base64.encode(sImage, Base64.DEFAULT);
                                        //      sendToServer(sEncodedImage);
                                        byte[] image = data.getByteArrayExtra(MiSnapApi.RESULT_PICTURE_DATA);

                                        String base = Base64.encodeToString(image, Base64.DEFAULT);
                                        newObj.put("MiSnapResultCode","UNVERIFIED");
                                        newObj.put("image","data:image/jpeg;base64,"+base);

                                        List<String> warnings = extras.getStringArrayList(MiSnapApi.RESULT_WARNINGS);
                                        if (warnings != null && !warnings.isEmpty()) {
                                            String message = "WARNINGS:";
                                            if ((warnings.contains(MiSnapAnalyzerResult.FrameChecks.WRONG_DOCUMENT.name()))) {
                                                message += "\nWrong document detected";
                                            }
                                            newObj.put("MiSnapResultCode","VERIFIED");
//                                            mPickerPromise.reject("E_NO_IMAGE_DATA_FOUND",message);

                                        }
                                        break;


                                }


                                mPickerPromise.resolve(newObj.toString());
                            }catch (JSONException e)  {

                                mPickerPromise.reject("Error:", e);
                            }


                        } else {
                            // Image canceled, stop
                            mPickerPromise.reject("E_NO_IMAGE_DATA_FOUND", "MiSnap canceled");

                        }
                    }
                    else if (Activity.RESULT_CANCELED == resultCode) {
//                    // Camera not working or not available, stop
                        mPickerPromise.reject("E_NO_IMAGE_DATA_FOUND", "No aborted data found");
                    }
                    mPickerPromise = null;

                }
            }
        }
    };

    @ReactMethod
    private void startReaderBack(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
            return;
        }
        // Prevent multiple MiSnap instances by preventing multiple button presses
        if (System.currentTimeMillis() - mTime < PREVENT_DOUBLE_CLICK_TIME_MS) {
            // Double-press detected
            return;
        }
        mTime = System.currentTimeMillis();
        JSONObject misnapParams = new JSONObject();
        try{
            misnapParams.put(MiSnapApi.MiSnapDocumentType,MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_BACK);
            JSONObject jjs = null;
            try {
                jjs = new JSONObject();
                jjs.put(CameraApi.MiSnapAllowScreenshots, 1);
                jjs.put(MiSnapApi.MiSnapDocumentType,
                        MiSnapApi.PARAMETER_DOCTYPE_DRIVER_LICENSE);
                jjs.put(MiSnapApi.MiSnapOrientation, 0);
                jjs.put(WorkflowApi.MiSnapTrackGlare, WorkflowApi.TRACK_GLARE_ENABLED);
                jjs.put(CameraApi.MiSnapFocusMode, CameraApi.PARAMETER_FOCUS_MODE_HYBRID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPickerPromise = promise;
            try {
                final Intent intentMiSnap = new Intent(currentActivity,MiSnapWorkflowActivity.class);
                if (intentMiSnap != null) {
                    intentMiSnap.putExtra(MiSnapApi.JOB_SETTINGS, jjs.toString());
                    currentActivity.startActivityForResult(intentMiSnap,MiSnapApi.RESULT_PICTURE_CODE); //(intentFacialCapture, MiSnapApi.RESULT_PICTURE_CODE);
                }

            } catch (Exception e) {
                mPickerPromise.reject("E_FAILED_TO_SHOW_PICKER", e);
                mPickerPromise = null;
            }
        }catch(JSONException e){ e.printStackTrace();}


    }

    @ReactMethod
    private void startReaderFront(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
            return;
        }
        // Prevent multiple MiSnap instances by preventing multiple button presses
        if (System.currentTimeMillis() - mTime < PREVENT_DOUBLE_CLICK_TIME_MS) {
            // Double-press detected
            return;
        }
        mTime = System.currentTimeMillis();
        JSONObject misnapParams = new JSONObject();
        try{
            misnapParams.put(MiSnapApi.MiSnapDocumentType,MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT);
            JSONObject jjs = null;
            try {
                jjs = new JSONObject();
                jjs.put(CameraApi.MiSnapAllowScreenshots, 1);
                jjs.put(MiSnapApi.MiSnapDocumentType,
                        MiSnapApi.PARAMETER_DOCTYPE_DRIVER_LICENSE);
                jjs.put(MiSnapApi.MiSnapOrientation, 0);
                jjs.put(WorkflowApi.MiSnapTrackGlare, WorkflowApi.TRACK_GLARE_ENABLED);
                jjs.put(CameraApi.MiSnapFocusMode, CameraApi.PARAMETER_FOCUS_MODE_HYBRID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPickerPromise = promise;
            try {
                final Intent intentMiSnap = new Intent(currentActivity,MiSnapWorkflowActivity.class);
                if (intentMiSnap != null) {
                    intentMiSnap.putExtra(MiSnapApi.JOB_SETTINGS, jjs.toString());
                    currentActivity.startActivityForResult(intentMiSnap,MiSnapApi.RESULT_PICTURE_CODE); //(intentFacialCapture, MiSnapApi.RESULT_PICTURE_CODE);
                }

            } catch (Exception e) {
                mPickerPromise.reject("E_FAILED_TO_SHOW_PICKER", e);
                mPickerPromise = null;
            }
        }catch(JSONException e){ e.printStackTrace();}


    }

    ReaderCardID(ReactApplicationContext reactContext){
        super(reactContext);

        // Add the listener for `onActivityResult`
        reactContext.addActivityEventListener(mActivityEventListener);
    }
    @Override
    public String getName(){
        return "ReaderCardID";
    }

}
