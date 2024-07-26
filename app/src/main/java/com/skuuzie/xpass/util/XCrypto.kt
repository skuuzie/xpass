package com.skuuzie.xpass.util

import android.util.Base64
import godroidguard.Godroidguard

// some godroidguard wrapper and crypto util
object XCrypto {

    // ByteArray to hex string
    fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    /*
       Decrypt base64 encoded input with key previously set with Godroidguard.setKey().
       Returns bytearray of the decrypted input, else throwable
    */
    fun decryptBase64(input: String): ByteArray {
        val decoded = Base64.decode(input, Base64.URL_SAFE)
            ?: throw Exception("Input is not Base64.URL_SAFE")
        val decrypted = Godroidguard.decrypt(decoded)
            ?: throw Exception("Decryption error")
        return decrypted
    }

    /*
        Encrypt input with key previously set with Godroidguard.setKey().
        Returns base64 encoded of the encrypted input, else throwable
     */
    fun encryptBase64(input: ByteArray): String {
        val encrypted = Godroidguard.encrypt(input)
            ?: throw Exception("Decryption error")
        val encoded = Base64.encodeToString(encrypted, Base64.URL_SAFE)
            ?: throw Exception("Base64 encoding error")
        return encoded
    }

    /*
        Higher level call for Godroidguard.setKey() just in case
     */
    fun setKey(key: ByteArray) {
        Godroidguard.setKey(key)
    }

    /*
        Only used for password hashing on surface level, internal and sensitive implementation
        is still handled by the native Go code
     */
    fun getSurfaceKey(): ByteArray {
        setKey(
            byteArrayOf(
                42,
                21,
                25,
                47,
                8,
                29,
                122,
                8,
                105,
                18,
                56,
                100,
                80,
                118,
                23,
                18,
                52,
                14,
                62,
                92,
                38,
                102,
                66,
                97,
                54,
                99,
                16,
                86,
                93,
                6,
                104,
                81
            )
        )
        return Godroidguard.genericHash(byteArrayOf(1))
    }
}