#import "AndroidServicePlugin.h"
#import <android_service/android_service-Swift.h>

@implementation AndroidServicePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidServicePlugin registerWithRegistrar:registrar];
}
@end
