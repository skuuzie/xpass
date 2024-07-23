package goantire

import (
	util "godroidguard/src/util"
	"os"
	"os/exec"
	"strings"
)

const ReleaseKeysTag = "release-keys"
const AOSPTag = "aosp"
const SDKGooglePhoneTag = "sdk_gphone64_x86_64"

func getSuBinariesPath() []string {
	return []string{
		"/data/local/su",
		"/data/local/bin/su",
		"/data/local/xbin/su",
		"/sbin/su",
		"/su/bin/su",
		"/system/bin/su",
		"/system/bin/.ext/su",
		"/system/bin/failsafe/su",
		"/system/sd/xbin/su",
		"/system/usr/we-need-root/su",
		"/system/xbin/su",
		"/cache/su",
		"/data/su",
		"/dev/su",
	}
}

func checkEmulator() {
	out, err := exec.Command("/bin/sh", "-c", "getprop ro.build.tags").Output()
	if err != nil {
		util.AndroidDebugLog("CheckEmulator", err.Error())
	} else {
		if string(out) != ReleaseKeysTag {
			util.AndroidExit("build-tags")
		}
	}

	out, err = exec.Command("/bin/sh", "-c", "getprop ro.product.vendor.model").Output()
	if err != nil {
		util.AndroidDebugLog("CheckEmulator", err.Error())
	} else {
		if strings.ToLower(string(out)) != SDKGooglePhoneTag {
			util.AndroidExit("phone model")
		}
	}

	out, err = exec.Command("/bin/sh", "-c", "getprop ro.dalvik.vm.native.bridge").Output()
	if err != nil {
		util.AndroidDebugLog("CheckEmulator", err.Error())
	} else {
		if len(string(out)) > 0 {
			util.AndroidExit("translation lib")
		}
	}
}

func checkRoot() {
	for _, path := range getSuBinariesPath() {
		_, err := os.Open(path)
		if err != nil {
			util.AndroidExit("root")
		}
	}
}
