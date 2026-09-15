package com.prepcommerce.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.prepcommerce.app.data.local.dao.ContentDao
import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.*

@Database(
    entities = [ChapterEntity::class, QuestionEntity::class, SamplePaperEntity::class,
        UserProfileEntity::class, TestAttemptEntity::class, AttemptAnswerEntity::class,
        BookmarkEntity::class, GameScoreEntity::class, XpLogEntity::class,
        ChapterProgressEntity::class, ReportedQuestionEntity::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "prepcommerce.db")
                    .build().also { INSTANCE = it }
            }
    }
}
