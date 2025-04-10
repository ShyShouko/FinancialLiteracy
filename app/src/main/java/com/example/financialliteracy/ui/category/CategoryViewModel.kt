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
    
    // LiveData для всех категорий
    val allCategories: LiveData<List<Category>>
    
    // LiveData для категорий расходов
    val expenseCategories: LiveData<List<Category>>
    
    // LiveData для категорий доходов
    val incomeCategories: LiveData<List<Category>>
    
    init {
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        
        allCategories = repository.allCategories
        expenseCategories = repository.expenseCategories
        incomeCategories = repository.incomeCategories
    }
    
    /**
     * Вставка новой категории
     */
    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }
    
    /**
     * Обновление существующей категории
     */
    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.update(category)
    }
    
    /**
     * Удаление категории
     */
    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }
    
    /**
     * Получение категории по ID
     */
    suspend fun getCategoryById(categoryId: Long): Category? {
        return repository.getCategoryById(categoryId)
    }
    
    /**
     * Удаление всех категорий, созданных пользователем
     */
    fun deleteCustomCategories() = viewModelScope.launch {
        repository.deleteCustomCategories()
    }
}