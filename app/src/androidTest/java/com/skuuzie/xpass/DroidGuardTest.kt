package com.skuuzie.xpass

import androidx.test.ext.junit.runners.AndroidJUnit4
import godroidguard.Godroidguard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

// This is actually just a unit test for DroidGuard module,
// tested in androidTest for library loading simplicity

@RunWith(AndroidJUnit4::class)
class DroidGuardTest {
    @Test
    fun generateRandomBytes_test() {
        val rand_32 = Godroidguard.getRandomBytes(32)
        val rand_64 = Godroidguard.getRandomBytes(64)

        assertEquals(32, rand_32.size)
        assertEquals(64, rand_64.size)
    }

    @Test
    fun genericHashNotNull_test() {
        assertNotNull(Godroidguard.genericHash(byteArrayOf()))
    }

    @Test
    fun authenticatedHashKeyDifference_test() {
        val data = byteArrayOf(1, 2, 3, 4, 5)

        Godroidguard.setKey("k1".encodeToByteArray())
        val k1_1 = Godroidguard.authenticatedHash(data)

        Godroidguard.setKey("k2".encodeToByteArray())
        val k2_1 = Godroidguard.authenticatedHash(data)

        Godroidguard.setKey("k1".encodeToByteArray())
        val k1_2 = Godroidguard.authenticatedHash(data)

        Godroidguard.setKey("k2".encodeToByteArray())
        val k2_2 = Godroidguard.authenticatedHash(data)

        assertEquals(true, k1_1.contentEquals(k1_2))
        assertEquals(true, k2_1.contentEquals(k2_2))
        assertEquals(false, k1_1.contentEquals(k2_1))
    }

    @Test
    fun encryptKeyDifference_test() {
        val data = byteArrayOf(1, 2, 3, 4, 5)

        Godroidguard.setKey("k1".encodeToByteArray())
        val k1e = Godroidguard.encrypt(data)
        val k1d = Godroidguard.decrypt(k1e)

        Godroidguard.setKey("k2".encodeToByteArray())
        val k2e = Godroidguard.encrypt(data)
        val k2d = Godroidguard.decrypt(k2e)

        Godroidguard.setKey("zzz".encodeToByteArray())
        val shouldFail1 = Godroidguard.decrypt(k1e)
        val shouldFail2 = Godroidguard.decrypt(k2e)

        assertNull(shouldFail1)
        assertNull(shouldFail2)

        assertNotNull(k1d)
        assertNotNull(k1e)

        assertEquals(true, data.contentEquals(k1d))
        assertEquals(true, data.contentEquals(k2d))
    }

    @Test
    fun getLastErrorMessage_test() {
        val data = byteArrayOf(1, 2, 3)

        Godroidguard.setKey("k1".encodeToByteArray())
        val k1e = Godroidguard.encrypt(data)

        // Decryption error
        Godroidguard.setKey("k2".encodeToByteArray())
        val shouldFail = Godroidguard.decrypt(k1e)
        var err = Godroidguard.getLastErrorMessage()
        assertEquals("Decryption error", err)

        // Empty data error
        Godroidguard.encrypt(byteArrayOf())
        err = Godroidguard.getLastErrorMessage()
        assertEquals("Data can't be empty", err)
    }
}