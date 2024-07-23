package goantire

import (
	"fmt"
	util "godroidguard/src/util"
	"os"
	"strings"
)

const TracerPidTag = "TracerPid:\t"
const FridaTag1 = "gdbus"
const FridaTag2 = "gmain"
const JDWPTag = "jdwp"
const GDBTag = "gdb"

func checkAll() {

	// TracerPID
	tpid := make([]byte, 256)
	f, _ := os.Open("/proc/self/status")
	f.Read(tpid)

	if strings.Split(string(tpid), TracerPidTag)[1][0] != '0' {
		util.AndroidExit("active tracerpid")
	}

	// JDWP GDB Frida
	task, _ := os.ReadDir("proc/self/task")
	for _, path := range task {
		curr := make([]byte, 256)
		f, _ := os.Open(fmt.Sprintf("proc/self/task/%s/status", path.Name()))
		f.ReadAt(curr, 6)

		if strings.ToLower(string(curr)[:3]) == GDBTag {
			util.AndroidExit("gdb")
		}
		if strings.ToLower(string(curr)[:4]) == JDWPTag {
			util.AndroidExit("jdwp")
		}
		if strings.ToLower(string(curr)[:5]) == FridaTag1 {
			util.AndroidExit("frida (gdbus)")
		}
		if strings.ToLower(string(curr)[:4]) == FridaTag2 {
			util.AndroidExit("frida (gmain)")
		}
	}
}
