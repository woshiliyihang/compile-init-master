#include "Jason.h"

#include <android-base/properties.h>
#include <android/log.h>

namespace aidl::android::hardware::jason {

ndk::ScopedAStatus Jason::getVersion(std::string* _aidl_return) {
    std::string version = android::base::GetProperty("ro.build.version.release", "unknown");
    __android_log_print(ANDROID_LOG_INFO, "JasonHal", "Android version: %s", version.c_str());
    *_aidl_return = version;
    return ndk::ScopedAStatus::ok();
}

}  // namespace aidl::android::hardware::jason
