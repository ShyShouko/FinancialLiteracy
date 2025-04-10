package com.example.financialliteracy.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialliteracy.data.database.AppDatabase
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>
    val incomeCategories: LiveData<List<Category>>
    val expenseCategories: LiveData<List<Category>>
    
    init {
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        allCategories = repository.allCategories
        incomeCategories = repository.getCategoriesByType(CategoryType.INCOME)
        expenseCategories = repository.getCategoriesByType(CategoryType.EXPENSE)
    }
    
    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }
    
    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }
    
    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }
    
    suspend fun getCategoryById(categoryId: Long): Category? {
        return repository.getCategoryById(categoryId)
    }
}