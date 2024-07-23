package godroidguard

import (
	goantire "godroidguard/src/antire"
	gocrypt "godroidguard/src/crypto"
	util "godroidguard/src/util"
)

type Crypt interface {

	// Encrypt input
	//
	// Returns the encrypted input, nil if failure
	AuthenticatedEncrypt(in []byte) []byte

	// Decrypt input
	//
	// Returns the decrypted input, nil if failure
	AuthenticatedDecrypt(in []byte) []byte

	// Hash the input without user key. The same input will always produce the same output.
	//
	// Returns the digest
	GenericHash(in []byte) []byte

	// Hash the input with user key. The output will depends on the current key loaded with ReloadKey().
	//
	// Returns the digest
	AuthenticatedHash(in []byte) []byte

	// Reload the cryptographic key. Does not persist previous key information.
	//
	// ReloadKey("Hello") will always produce the same cipher/plain text any time.
	// input can be any length
	ReloadKey(in []byte)

	// RNG
	GetRandomBytes(size int) []byte

	// Get last error message occured
	GetLastErrorMessage() string
}

type Antire interface {

	// Check the app's environment
	AppEnv()

	// Check the device's environment
	DeviceEnv()
}

// Facade
type DroidGuard struct {
	Cryptor Crypt        // Interface
	Error   *util.DError // Struct
	Watcher Antire       // Interface
}

// Provide DroidGuard
func NewDroidGuard(key []byte) *DroidGuard {
	c := &DroidGuard{
		Cryptor: &gocrypt.Cryptor{Key: key},
		Error:   &util.DError{Message: ""},
		Watcher: &goantire.SAntire{},
	}
	c.Cryptor.ReloadKey(key)
	return c
}
