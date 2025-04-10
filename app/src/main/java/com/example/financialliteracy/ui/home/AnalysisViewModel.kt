package com.example.financialliteracy.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.financialliteracy.data.database.AppDatabase
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    
    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()
    private val categoryDao = AppDatabase.getDatabase(application).categoryDao()
    
    // Данные о транзакциях
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val expenseTransactions: LiveData<List<Transaction>> = transactionDao.getTransactionsByType(CategoryType.EXPENSE)
    val incomeTransactions: LiveData<List<Transaction>> = transactionDao.getTransactionsByType(CategoryType.INCOME)
    
    // Данные о категориях
    val expenseCategories: LiveData<List<Category>> = categoryDao.getCategoriesByType(CategoryType.EXPENSE)
    val incomeCategories: LiveData<List<Category>> = categoryDao.getCategoriesByType(CategoryType.INCOME)
    
    // Общие суммы
    val totalExpense: LiveData<Double> = transactionDao.getTotalAmountByType(CategoryType.EXPENSE)
    val totalIncome: LiveData<Double> = transactionDao.getTotalAmountByType(CategoryType.INCOME)
    
    // Данные для круговых диаграмм
    val expenseByCategory = MediatorLiveData<Map<Category, Double>>()
    val incomeByCategory = MediatorLiveData<Map<Category, Double>>()
    
    // Периоды для анализа
    private val _currentPeriod = MutableLiveData<AnalysisPeriod>(AnalysisPeriod.MONTH)
    val currentPeriod: LiveData<AnalysisPeriod> = _currentPeriod
    
    // Временные интервалы для периодов
    private val _periodStartDate = MutableLiveData<Date>()
    val periodStartDate: LiveData<Date> = _periodStartDate
    
    private val _periodEndDate = MutableLiveData<Date>()
    val periodEndDate: LiveData<Date> = _periodEndDate
    
    // Транзакции за выбранный период
    private val _periodTransactions = MediatorLiveData<List<Transaction>>()
    val periodTransactions: LiveData<List<Transaction>> = _periodTransactions
    
    // Статистика за период
    val periodExpense: LiveData<Double> = _periodTransactions.map { transactions ->
        transactions.filter { it.type == CategoryType.EXPENSE }.sumOf { it.amount }
    }
    
    val periodIncome: LiveData<Double> = _periodTransactions.map { transactions ->
        transactions.filter { it.type == CategoryType.INCOME }.sumOf { it.amount }
    }
    
    val periodBalance: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(periodIncome) { recalculateBalance() }
        addSource(periodExpense) { recalculateBalance() }
    }
    
    private fun MediatorLiveData<Double>.recalculateBalance() {
        val income = periodIncome.value ?: 0.0
        val expense = periodExpense.value ?: 0.0
        this.value = income - expense
    }
    
    init {
        // Обновляем данные для диаграмм при изменении транзакций или категорий
        expenseByCategory.addSource(expenseTransactions) { updateExpenseByCategory() }
        expenseByCategory.addSource(expenseCategories) { updateExpenseByCategory() }
        
        incomeByCategory.addSource(incomeTransactions) { updateIncomeByCategory() }
        incomeByCategory.addSource(incomeCategories) { updateIncomeByCategory() }
        
        // Инициализируем период по умолчанию
        setPeriod(AnalysisPeriod.MONTH)
        
        // Настраиваем медиатор для отслеживания транзакций за период
        _periodTransactions.addSource(allTransactions) { updatePeriodTransactions() }
        _periodTransactions.addSource(_periodStartDate) { updatePeriodTransactions() }
        _periodTransactions.addSource(_periodEndDate) { updatePeriodTransactions() }
    }
    
    // Установка периода анализа
    fun setPeriod(period: AnalysisPeriod) {
        _currentPeriod.value = period
        
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        _periodEndDate.value = endDate
        
        // Расчет начальной даты в зависимости от выбранного периода
        calendar.time = endDate
        when (period) {
            AnalysisPeriod.WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            AnalysisPeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
            }
            AnalysisPeriod.QUARTER -> calendar.add(Calendar.MONTH, -3)
            AnalysisPeriod.YEAR -> calendar.add(Calendar.YEAR, -1)
            AnalysisPeriod.ALL_TIME -> calendar.set(2000, 0, 1) // Достаточно раннее время
        }
        _periodStartDate.value = calendar.time
    }
    
    // Обновление данных за период
    private fun updatePeriodTransactions() {
        val startDate = _periodStartDate.value ?: return
        val endDate = _periodEndDate.value ?: return
        val allTransactions = allTransactions.value ?: return
        
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = allTransactions.filter { transaction ->
                transaction.date in startDate..endDate
            }
            _periodTransactions.postValue(filtered)
        }
    }
    
    // Обновление данных для круговых диаграмм
    private fun updateExpenseByCategory() {
        val transactions = expenseTransactions.value ?: return
        val categories = expenseCategories.value ?: return
        
        viewModelScope.launch(Dispatchers.Default) {
            val result = mutableMapOf<Category, Double>()
            val categoriesMap = categories.associateBy { it.id }
            
            transactions.forEach { transaction ->
                val category = categoriesMap[transaction.categoryId] ?: return@forEach
                result[category] = (result[category] ?: 0.0) + transaction.amount
            }
            
            expenseByCategory.postValue(result)
        }
    }
    
    private fun updateIncomeByCategory() {
        val transactions = incomeTransactions.value ?: return
        val categories = incomeCategories.value ?: return
        
        viewModelScope.launch(Dispatchers.Default) {
            val result = mutableMapOf<Category, Double>()
            val categoriesMap = categories.associateBy { it.id }
            
            transactions.forEach { transaction ->
                val category = categoriesMap[transaction.categoryId] ?: return@forEach
                result[category] = (result[category] ?: 0.0) + transaction.amount
            }
            
            incomeByCategory.postValue(result)
        }
    }
    
    // Получение общего баланса (доход - расход)
    fun getTotalBalance(): LiveData<Double> {
        val result = MediatorLiveData<Double>()
        result.addSource(totalIncome) {
            result.value = (totalIncome.value ?: 0.0) - (totalExpense.value ?: 0.0)
        }
        result.addSource(totalExpense) {
            result.value = (totalIncome.value ?: 0.0) - (totalExpense.value ?: 0.0)
        }
        return result
    }
}

// Перечисление для периодов анализа
enum class AnalysisPeriod {
    WEEK, MONTH, QUARTER, YEAR, ALL_TIME
} 