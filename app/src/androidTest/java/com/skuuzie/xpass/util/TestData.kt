package com.skuuzie.xpass.util

object TestData {
    fun getTestLoginUsername(): String = "firestorm"
    fun getTestLoginPassword(): String = "snowflake"

    fun getTestPlatform(increment: Int): String {
        return "Platform ${increment}"
    }

    fun getTestUsername(increment: Int): String {
        return "Username ${increment}"
    }

    fun getTestEmail(increment: Int): String {
        return "Email ${increment}"
    }

    fun getTestPassword(increment: Int): String {
        return "Password ${increment}"
    }
}