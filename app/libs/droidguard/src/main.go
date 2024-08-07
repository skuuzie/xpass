package godroidguard

import (
	service "godroidguard/src/service"
	"time"
)

var firstBeat bool
var droidGuard *service.DroidGuard

// reserved symbol for entry point
func init() {
	firstBeat = true
	droidGuard = service.NewDroidGuard([]byte("godroidguard"))
	droidGuard.Watcher.DeviceEnv()
	go func() {
		for {
			droidGuard.Watcher.AppEnv()
			time.Sleep(2 * time.Second)
		}
	}()
}

// Check whether the module is initialized for the first time
func IsInitialization() bool {
	return firstBeat
}

// Set the cryptographic key. The struct is stateless, previous SetKey() calls does not matter
//
// SetKey("somekey") can always be used to decrypt any ciphertext encrypted with "somekey",
// regardless any other key has been set before
func SetKey(key []byte) {
	firstBeat = false
	droidGuard.Cryptor.ReloadKey(key)
	droidGuard.Error.Message = droidGuard.Cryptor.GetLastErrorMessage()
}

// Encrypt the input, accepts len(in) > 0
//
// Returns encrypted input, nil if failure
func Encrypt(in []byte) []byte {
	r := droidGuard.Cryptor.AuthenticatedEncrypt(in)
	droidGuard.Error.Message = droidGuard.Cryptor.GetLastErrorMessage()
	return r
}

// Decrypt the input, accepts len(in) > 0
//
// Returns encrypted input, nil if failure
func Decrypt(in []byte) []byte {
	r := droidGuard.Cryptor.AuthenticatedDecrypt(in)
	droidGuard.Error.Message = droidGuard.Cryptor.GetLastErrorMessage()
	return r
}

// Hash the input without using the user key
func GenericHash(in []byte) []byte {
	return droidGuard.Cryptor.GenericHash(in)
}

// Hash the input using the user key from SetKey()
func AuthenticatedHash(in []byte) []byte {
	return droidGuard.Cryptor.AuthenticatedHash(in)
}

// Generate cryptographically secure random bytes
func GetRandomBytes(size int) []byte {
	return droidGuard.Cryptor.GetRandomBytes(size)
}

// Get last error message occured
func GetLastErrorMessage() string {
	return droidGuard.Error.Message
}
