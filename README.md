# Jason AIDL HAL (Android 11 / API 30)

这个仓库提供一个最小可用的 `jason` AIDL HAL 示例，包含：

- `android.hardware.jason` AIDL 接口（`getVersion()`）
- HAL service 实现（读取 `ro.build.version.release` 并通过 Android log 打印）
- 本地/CI 编译脚本
- GitHub Actions 产物上传

## 目录结构

- `aidl/jason/`：拷贝到 AOSP `hardware/interfaces/jason` 的 HAL 工程
- `scripts/build_jason_hal.sh`：AOSP 内模块编译脚本
- `.github/workflows/build-jason-hal.yml`：GitHub Actions 工作流

## HAL 功能

`IJason.getVersion()` 行为：

1. 读取系统属性 `ro.build.version.release`
2. 输出日志：`JasonHal: Android version: <version>`
3. 返回版本字符串给调用者

## 本地虚拟机 (Android 11 AOSP) 使用

在 AOSP 根目录执行：

```bash
mkdir -p hardware/interfaces/jason
rsync -a <this_repo>/aidl/jason/ hardware/interfaces/jason/
cp <this_repo>/scripts/build_jason_hal.sh ./
chmod +x build_jason_hal.sh
export LUNCH_TARGET=aosp_arm64-userdebug
./build_jason_hal.sh
```

编译产物默认输出到 `artifacts/`：

- `android.hardware.jason-service`
- `android.hardware.jason-service.rc`
- `android.hardware.jason-service.xml`

## GitHub Actions 使用

1. Push 当前仓库到 GitHub。
2. 在 **Actions** 页面运行 **Build Jason AIDL HAL**。
3. 输入：
   - `aosp_ref`: 如 `android-11.0.0_r48`
   - `lunch_target`: 如 `aosp_arm64-userdebug`
4. 等待任务完成并下载 artifact（`jason-hal-<aosp_ref>`）。

> 注意：AOSP 全量 sync/build 体积很大，`ubuntu-latest` 可能耗时较久。若你已有本地 VM 或自建 Runner，建议优先在本地 Runner 跑该工作流。


## Windows 10 CMD 一键验证

下载 GitHub Actions 的 artifact 并解压后（目录中有 4 个文件），在 Windows CMD 执行：

```cmd
scripts\windows\verify_jason_hal.cmd <artifact_dir> [serial]
```

示例：

```cmd
scripts\windows\verify_jason_hal.cmd C:\temp\jason-hal emulator-5554
```

脚本会自动：

1. `adb root` / `adb remount`
2. push service、client、rc、xml 到 `/vendor`
3. 重启设备
4. 执行 `/vendor/bin/android.hardware.jason-client` 调用 `getVersion()`

成功时会输出：

```text
Android version from HAL: 11
```

你也可以再看 log：

```cmd
adb logcat -d -s JasonHal
```

## 简单运行验证

将产物刷入设备后：

```bash
adb logcat -s JasonHal
```

如果 framework/client 调用了 `getVersion()`，你会看到类似：

```text
I JasonHal: Android version: 11
```
