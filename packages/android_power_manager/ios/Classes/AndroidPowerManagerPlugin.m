#import "AndroidPowerManagerPlugin.h"
#import <android_power_manager/android_power_manager-Swift.h>

@implementation AndroidPowerManagerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidPowerManagerPlugin registerWithRegistrar:registrar];
}
@end
