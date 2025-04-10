package com.example.financialliteracy.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialliteracy.data.database.AppDatabase
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val categoryDao = AppDatabase.getDatabase(application).categoryDao()
    
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    val expenseCategories: LiveData<List<Category>> = categoryDao.getCategoriesByType(CategoryType.EXPENSE)
    val incomeCategories: LiveData<List<Category>> = categoryDao.getCategoriesByType(CategoryType.INCOME)
    
    fun insertCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.insertCategory(category)
        }
    }
    
    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.updateCategory(category)
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.deleteCategory(category)
        }
    }
    
    fun deleteCustomCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.deleteCustomCategories()
        }
    }
}