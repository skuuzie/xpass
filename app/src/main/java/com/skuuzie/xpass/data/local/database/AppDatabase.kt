package com.skuuzie.xpass.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Credential::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao
}