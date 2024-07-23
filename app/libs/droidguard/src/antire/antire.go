package goantire

import util "godroidguard/src/util"

type SAntire struct {
}

func (_ *SAntire) AppEnv() {
	util.AndroidDebugLog("goantire::AppEnv", "<>")
	checkAll()
}

func (_ *SAntire) DeviceEnv() {
	util.AndroidDebugLog("goantire::DeviceEnv", "<>")
	checkRoot()
	checkEmulator()
}
