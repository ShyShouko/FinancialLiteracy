package com.example.financialliteracy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import com.example.financialliteracy.utils.DateConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Transaction::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_literacy_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database.categoryDao(), context)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        private suspend fun populateDatabase(categoryDao: CategoryDao, context: Context) {
            // Добавляем предустановленные категории расходов
            val expenseCategories = listOf(
                Category(
                    name = context.getString(R.string.category_food),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_food),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_transport),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_transport),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_entertainment),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_entertainment),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_shopping),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_shopping),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_health),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_health),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_housing),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_housing),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_education),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_education),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_other),
                    type = CategoryType.EXPENSE,
                    color = context.getColor(R.color.category_other),
                    isDefault = true
                )
            )
            
            // Добавляем предустановленные категории доходов
            val incomeCategories = listOf(
                Category(
                    name = context.getString(R.string.category_salary),
                    type = CategoryType.INCOME,
                    color = context.getColor(R.color.category_salary),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_gifts),
                    type = CategoryType.INCOME,
                    color = context.getColor(R.color.category_gifts),
                    isDefault = true
                ),
                Category(
                    name = context.getString(R.string.category_other),
                    type = CategoryType.INCOME,
                    color = context.getColor(R.color.category_other),
                    isDefault = true
                )
            )
            
            // Вставляем категории в базу данных
            categoryDao.insertCategories(expenseCategories + incomeCategories)
        }
    }
} 