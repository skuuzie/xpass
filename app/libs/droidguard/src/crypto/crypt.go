package gocrypt

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/hmac"
	"crypto/rand"
	"crypto/sha512"
	"encoding/binary"
)

type Cryptor struct {
	Key          []byte
	ErrorMessage string
}

const num1 = 0xdeadbeef
const num2 = 0xbfab02ff
const num3 = 0xaabbccdd
const num4 = 0xfaaaaaaa
const nonceSize = 12
const aad = "gocrypt"

const zeroLengthErr = "Data can't be empty"
const invalidKeyLengthErr = "Invalid key length, must be 16, 24, or 32 bytes"
const blockCipherErr = "Unsupported block cipher"
const decryptionErr = "Decryption error"

func (c *Cryptor) AuthenticatedEncrypt(in []byte) []byte {
	if len(in) == 0 {
		c.ErrorMessage = zeroLengthErr
		return nil
	}

	_key, _nonce := transformKey(c.Key), getRandomBytes(nonceSize)

	block, err := aes.NewCipher(_key[:32])
	if err != nil {
		c.ErrorMessage = invalidKeyLengthErr
		return nil
	}

	cipher, err := cipher.NewGCM(block)
	if err != nil {
		c.ErrorMessage = blockCipherErr
		return nil
	}

	ret := cipher.Seal(nil, _nonce, in, []byte(aad))

	return append(_nonce, ret...)
}

func (c *Cryptor) AuthenticatedDecrypt(in []byte) []byte {
	if len(in) == 0 {
		c.ErrorMessage = zeroLengthErr
		return nil
	}

	_key, _nonce := transformKey(c.Key), in[:nonceSize]

	block, err := aes.NewCipher(_key[:32])
	if err != nil {
		c.ErrorMessage = invalidKeyLengthErr
		return nil
	}

	cipher, err := cipher.NewGCM(block)
	if err != nil {
		c.ErrorMessage = blockCipherErr
		return nil
	}

	ret, err := cipher.Open(nil, _nonce, in[nonceSize:], []byte(aad))
	if err != nil {
		c.ErrorMessage = decryptionErr
		return nil
	}

	return ret
}

func (c *Cryptor) AuthenticatedHash(in []byte) []byte {
	h1 := hmac.New(sha512.New, c.Key)
	h1.Write(in)
	return h1.Sum(nil)
}

func (c *Cryptor) GenericHash(in []byte) []byte {
	h1 := hmac.New(sha512.New, getModuleKey())
	h1.Write(in)
	return h1.Sum(nil)
}

func (c *Cryptor) ReloadKey(in []byte) {
	// HMAC_SHA512(ModuleKey, SHA512(InputKey)) ^ (ModuleKey ^ InputKey)

	h1 := hmac.New(sha512.New, getModuleKey())
	v1 := xorBuffer(getModuleKey(), in)
	h2 := sha512.New()

	h2.Write(in)
	h1.Write(h2.Sum(nil))

	c.Key = xorBuffer(h1.Sum(nil), v1)
}

func (c *Cryptor) GetRandomBytes(size int) []byte {
	return getRandomBytes(size)
}

func (c *Cryptor) GetLastErrorMessage() string {
	return c.ErrorMessage
}

// Internal use
// Transform user's key to 64 bytes
func transformKey(in []byte) []byte {

	h1 := hmac.New(sha512.New, in)
	h2 := hmac.New(sha512.New, in)

	v1 := make([]byte, 16)
	binary.LittleEndian.PutUint32(v1, num1)

	v2 := make([]byte, 16)
	binary.LittleEndian.PutUint32(v2, num2)

	v3 := make([]byte, 16)
	binary.LittleEndian.PutUint32(v3, num3)

	v4 := make([]byte, 16)
	binary.LittleEndian.PutUint32(v4, num4)

	h1.Write(v1)
	h1.Write(v2)
	h2.Write(v3)
	h2.Write(v4)

	h1.Write(h2.Sum(nil))

	return h1.Sum(nil)
}

func getRandomBytes(size int) []byte {
	r := make([]byte, size)
	rand.Read(r)
	return r
}

// Internal use
func getModuleKey() []byte {
	return []byte{
		181, 140, 67, 57, 193, 19, 11, 59, 81, 127, 117, 68, 159, 227, 152, 217,
		59, 25, 214, 54, 248, 236, 81, 65, 28, 29, 46, 53, 249, 85, 69, 116,
		140, 187, 70, 245, 185, 242, 59, 106, 32, 178, 33, 18, 5, 77, 171, 75,
		206, 82, 166, 180, 202, 1, 153, 98, 127, 27, 243, 21, 127, 235, 24, 43}
}

// Internal use
//
// xor b1 against b2 for len(b1)
func xorBuffer(b1, b2 []byte) []byte {
	r := make([]byte, len(b1))
	for i := 0; i < len(b1); i++ {
		r[i] = b1[i] ^ b2[i%len(b2)]
	}
	return r
}
