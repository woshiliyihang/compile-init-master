#include <aidl/android/hardware/jason/IJason.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>

#include <iostream>

using aidl::android::hardware::jason::IJason;

int main() {
    ABinderProcess_setThreadPoolMaxThreadCount(0);

    const std::string instance = std::string() + IJason::descriptor + "/default";
    ndk::SpAIBinder binder(AServiceManager_waitForService(instance.c_str()));
    if (!binder.get()) {
        std::cerr << "Failed to get service: " << instance << std::endl;
        return 1;
    }

    std::shared_ptr<IJason> service = IJason::fromBinder(binder);
    if (!service) {
        std::cerr << "Failed to cast binder to IJason" << std::endl;
        return 2;
    }

    std::string version;
    ndk::ScopedAStatus status = service->getVersion(&version);
    if (!status.isOk()) {
        std::cerr << "getVersion failed: " << status.getDescription() << std::endl;
        return 3;
    }

    std::cout << "Android version from HAL: " << version << std::endl;
    return 0;
}
