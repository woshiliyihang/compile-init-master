#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${ANDROID_BUILD_TOP:-}" ]]; then
  if [[ -f build/envsetup.sh ]]; then
    # shellcheck disable=SC1091
    source build/envsetup.sh
  else
    echo "ERROR: Please run this script at AOSP root or export ANDROID_BUILD_TOP." >&2
    exit 1
  fi
fi

LUNCH_TARGET="${LUNCH_TARGET:-aosp_arm64-userdebug}"
OUT_DIR_OVERRIDE="${OUT_DIR:-}"

# shellcheck disable=SC2154
source build/envsetup.sh
lunch "${LUNCH_TARGET}"

if [[ -n "${OUT_DIR_OVERRIDE}" ]]; then
  export OUT_DIR="${OUT_DIR_OVERRIDE}"
fi

m android.hardware.jason-service android.hardware.jason-client

PRODUCT_OUT_DIR="${PRODUCT_OUT:-${OUT_DIR}/target/product/${TARGET_PRODUCT}}"
ARTIFACT_DIR="${ARTIFACT_DIR:-${PWD}/artifacts}"
mkdir -p "${ARTIFACT_DIR}"

cp "${PRODUCT_OUT_DIR}/vendor/bin/hw/android.hardware.jason-service" "${ARTIFACT_DIR}/"
cp "${PRODUCT_OUT_DIR}/vendor/bin/android.hardware.jason-client" "${ARTIFACT_DIR}/"
cp "${PRODUCT_OUT_DIR}/vendor/etc/init/android.hardware.jason-service.rc" "${ARTIFACT_DIR}/"
cp "${PRODUCT_OUT_DIR}/vendor/etc/vintf/manifest/android.hardware.jason-service.xml" "${ARTIFACT_DIR}/"

echo "Artifacts exported to ${ARTIFACT_DIR}"
