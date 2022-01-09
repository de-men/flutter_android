import 'dart:async';

import 'package:flutter/services.dart';

/// The Power API Manger to check/request battery optimizations
class AndroidPowerManager {
  static const MethodChannel _channel =
  const MethodChannel('flutter.demen.org/android_power_manager');

  /// Return whether the application is on the device's power allowlist.
  static Future<bool?> get isIgnoringBatteryOptimizations async =>
      await _channel.invokeMethod<bool?>('isIgnoringBatteryOptimizations');

  /// Ask the user to allow an app to ignore battery optimizations (that is,
  /// put them on the whitelist of apps)
  static Future<bool?> requestIgnoreBatteryOptimizations() async =>
      await _channel.invokeMethod<bool?>('requestIgnoreBatteryOptimizations');
}
