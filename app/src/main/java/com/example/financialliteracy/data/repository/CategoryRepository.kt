package com.example.financialliteracy.data.repository

import androidx.lifecycle.LiveData
import com.example.financialliteracy.data.database.CategoryDao
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType

class CategoryRepository(private val categoryDao: CategoryDao) {
    
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    
    fun getCategoriesByType(type: CategoryType): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }
    
    suspend fun getCategoryById(categoryId: Long): Category? {
        return categoryDao.getCategoryById(categoryId)
    }
    
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
} 