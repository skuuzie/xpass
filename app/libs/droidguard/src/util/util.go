package godroidguard

/*
#cgo LDFLAGS: -llog
#include <android/log.h>
#include <stdlib.h>
*/
import "C"

import (
	"unsafe"
)

func AndroidExit(message string) {
	AndroidDebugLog("AndroidExit", message)
	// var c *int
	// *c = rand.Int()
}

func AndroidDebugLog(tag, message string) {
	ctag := C.CString(tag)
	cstr := C.CString(message)
	C.__android_log_write(C.ANDROID_LOG_DEBUG, ctag, cstr)
	C.free(unsafe.Pointer(ctag))
	C.free(unsafe.Pointer(cstr))
}
