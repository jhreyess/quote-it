package com.quoteit.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [
    FolderEntity::class,
    QuoteEntity::class,
    PostEntity::class,
    FolderQuoteCrossRef::class],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun folderDao(): FolderDAO
    abstract fun quoteDao(): QuoteDAO
    abstract fun folderQuoteDao(): FolderQuoteDAO
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quote-it-db")
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.folderDao())
                    }
                }
            }
        }

        fun populateDatabase(folderDao: FolderDAO) {
            val favFolder = FolderEntity(1, "Mis favoritos", 0)
            val userFolder = FolderEntity(2, "Hechas por mi", 0)
            folderDao.insertFolder(favFolder)
            folderDao.insertFolder(userFolder)
        }
    }
}