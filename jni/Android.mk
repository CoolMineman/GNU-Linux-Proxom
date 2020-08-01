LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := proxom
LOCAL_SRC_FILES := proxom.c

include $(BUILD_SHARED_LIBRARY)