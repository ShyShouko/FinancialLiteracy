package com.example.financialliteracy.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialliteracy.data.database.AppDatabase
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import com.example.financialliteracy.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>
    val incomeTransactions: LiveData<List<Transaction>>
    val expenseTransactions: LiveData<List<Transaction>>
    val totalIncome: LiveData<Double>
    val totalExpense: LiveData<Double>
    
    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        allTransactions = repository.allTransactions
        incomeTransactions = repository.getTransactionsByType(CategoryType.INCOME)
        expenseTransactions = repository.getTransactionsByType(CategoryType.EXPENSE)
        totalIncome = repository.getTotalAmountByType(CategoryType.INCOME)
        totalExpense = repository.getTotalAmountByType(CategoryType.EXPENSE)
    }
    
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }
    
    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
    }
    
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }
    
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): LiveData<List<Transaction>> {
        return repository.getTransactionsBetweenDates(startDate, endDate)
    }
    
    fun getTransactionsByCategory(categoryId: Long): LiveData<List<Transaction>> {
        return repository.getTransactionsByCategory(categoryId)
    }
    
    fun getTotalAmountByTypeAndPeriod(type: CategoryType, startDate: Date, endDate: Date): LiveData<Double> {
        return repository.getTotalAmountByTypeAndPeriod(type, startDate, endDate)
    }
    
    fun getWeeklyTransactions(): LiveData<List<Transaction>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.time
        
        return repository.getTransactionsBetweenDates(startDate, endDate)
    }
    
    fun getMonthlyTransactions(): LiveData<List<Transaction>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.time
        
        return repository.getTransactionsBetweenDates(startDate, endDate)
    }
    
    suspend fun getTransactionById(transactionId: Long): Transaction? {
        return repository.getTransactionById(transactionId)
    }
} 