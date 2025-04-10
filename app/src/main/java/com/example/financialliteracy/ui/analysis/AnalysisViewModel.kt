package com.example.financialliteracy.ui.analysis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.financialliteracy.data.database.AppDatabase
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import com.example.financialliteracy.data.repository.CategoryRepository
import com.example.financialliteracy.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    
    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository
    
    private val _weeklyAdvice = MutableLiveData<List<FinancialAdvice>>()
    val weeklyAdvice: LiveData<List<FinancialAdvice>> = _weeklyAdvice
    
    private val _monthlyAdvice = MutableLiveData<List<FinancialAdvice>>()
    val monthlyAdvice: LiveData<List<FinancialAdvice>> = _monthlyAdvice
    
    // Общие данные по категориям расходов (для диаграмм)
    private val _categoryExpenses = MediatorLiveData<Map<Long, Double>>()
    val categoryExpenses: LiveData<Map<Long, Double>> = _categoryExpenses
    
    init {
        val database = AppDatabase.getDatabase(application)
        transactionRepository = TransactionRepository(database.transactionDao())
        categoryRepository = CategoryRepository(database.categoryDao())
        
        // Запускаем анализ при инициализации
        analyzeWeeklyTransactions()
        analyzeMonthlyTransactions()
        updateCategoryExpenses()
    }
    
    private fun analyzeWeeklyTransactions() = viewModelScope.launch {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.time
        
        val transactions = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
        transactions.observeForever { transactionList ->
            _weeklyAdvice.value = FinancialAnalyzer.analyzeTransactions(
                transactionList,
                startDate,
                endDate
            )
        }
    }
    
    private fun analyzeMonthlyTransactions() = viewModelScope.launch {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.time
        
        val transactions = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
        transactions.observeForever { transactionList ->
            _monthlyAdvice.value = FinancialAnalyzer.analyzeTransactions(
                transactionList,
                startDate,
                endDate
            )
        }
    }
    
    private fun updateCategoryExpenses() {
        val expenseTransactions = transactionRepository.getTransactionsByType(CategoryType.EXPENSE)
        _categoryExpenses.addSource(expenseTransactions) { transactions ->
            viewModelScope.launch {
                _categoryExpenses.value = calculateCategoryExpenses(transactions)
            }
        }
    }
    
    private fun calculateCategoryExpenses(transactions: List<Transaction>): Map<Long, Double> {
        return transactions.groupBy { it.categoryId }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
    }
    
    fun analyzeCustomPeriod(startDate: Date, endDate: Date) = viewModelScope.launch {
        val transactions = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
        transactions.observeForever { transactionList ->
            _monthlyAdvice.value = FinancialAnalyzer.analyzeTransactions(
                transactionList,
                startDate,
                endDate
            )
        }
    }
} 