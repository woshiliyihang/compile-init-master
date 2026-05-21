#pragma once

#include <aidl/android/hardware/jason/BnJason.h>

namespace aidl::android::hardware::jason {

class Jason : public BnJason {
  public:
    ndk::ScopedAStatus getVersion(std::string* _aidl_return) override;
};

}  // namespace aidl::android::hardware::jason
