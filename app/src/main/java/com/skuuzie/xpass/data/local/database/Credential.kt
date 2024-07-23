package com.skuuzie.xpass.data.local.database

import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

@Entity(tableName = "credentials")
@Parcelize
data class Credential(
    @PrimaryKey
    var uuid: String,
    var platform: String,
    var username: String,
    var email: String,
    var password: String
) : Parcelable

@Dao
interface CredentialDao {
    @Query("SELECT * FROM credentials")
    fun getAllCredential(): Flow<List<Credential>>

    @Insert(entity = Credential::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: Credential)

    @Update(entity = Credential::class)
    suspend fun updateCredential(credential: Credential)

    @Query("DELETE FROM credentials WHERE uuid = :credId")
    suspend fun deleteCredentialById(credId: String)
}