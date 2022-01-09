package org.demen.androidpowermanager;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** AndroidPowerManagerPlugin */
public class AndroidPowerManagerPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, PluginRegistry.ActivityResultListener {

  private static final String METHOD_IS_IGNORING_BATTERY_OPTIMIZATIONS = "isIgnoringBatteryOptimizations";
  private static final String METHOD_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "requestIgnoreBatteryOptimizations";
  private static final int REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATIONS = 1224;

  private static final String CHANNEL = "flutter.demen.org/android_power_manager";

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  private Context applicationContext;
  private Activity activity;
  private ActivityPluginBinding binding;
  private MethodChannel.Result pendingResult;
  private Handler handler = new Handler(Looper.getMainLooper());

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    binding = activityPluginBinding;
    binding.addActivityResultListener(this);
    activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
    binding.removeActivityResultListener(this);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    binding = activityPluginBinding;
    binding.addActivityResultListener(this);
    activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    binding.removeActivityResultListener(this);
    activity = null;
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    applicationContext = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      switch (call.method) {
        case METHOD_IS_IGNORING_BATTERY_OPTIMIZATIONS:
          PowerManager powerManager = (PowerManager) applicationContext.getSystemService(Application.POWER_SERVICE);
          if (powerManager != null) {
            result.success(powerManager.isIgnoringBatteryOptimizations(applicationContext.getPackageName()));
          } else {
            ClassNotFoundException error = new ClassNotFoundException();
            result.error(error.getMessage(), error.getLocalizedMessage(), error);
          }
          break;
        case METHOD_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS:
          pendingResult = result;
          Intent intent = new Intent();
          intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
          intent.setData(Uri.parse("package:" + activity.getPackageName()));
          activity.startActivityForResult(intent, REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATIONS);
          break;
        default:
          result.notImplemented();
          break;
      }
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public boolean onActivityResult(int requestCode, final int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATIONS) {
      if (pendingResult != null) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            pendingResult.success(resultCode == Activity.RESULT_OK);
          }
        });
      }
      return true;
    }
    return false;
  }
}
