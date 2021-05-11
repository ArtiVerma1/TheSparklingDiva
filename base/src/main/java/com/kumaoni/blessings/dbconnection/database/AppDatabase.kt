package com.kumaoni.blessings.dbconnection.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kumaoni.blessings.dbconnection.dao.AppLocalDataDao
import com.kumaoni.blessings.dbconnection.dao.CartItemDataDao
import com.kumaoni.blessings.dbconnection.dao.ItemDataDao
import com.kumaoni.blessings.dbconnection.dao.LivePreviewDao
import com.kumaoni.blessings.dbconnection.entities.*

@Database(entities = [AppLocalData::class, UserLocalData::class, CustomerTokenData::class, ItemData::class, CartItemData::class, LivePreviewData::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract val itemDataDao: ItemDataDao
    abstract val cartItemDataDao: CartItemDataDao
    abstract fun appLocalDataDaoDao(): AppLocalDataDao
    abstract fun getLivePreviewDao(): LivePreviewDao
}
