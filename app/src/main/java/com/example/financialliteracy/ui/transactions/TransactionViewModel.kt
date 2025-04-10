package com.example.financialliteracy.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialliteracy.data.database.AppDatabase
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()
    
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val expenseTransactions: LiveData<List<Transaction>> = transactionDao.getTransactionsByType(CategoryType.EXPENSE)
    val incomeTransactions: LiveData<List<Transaction>> = transactionDao.getTransactionsByType(CategoryType.INCOME)
    
    val totalExpense: LiveData<Double> = transactionDao.getTotalAmountByType(CategoryType.EXPENSE)
    val totalIncome: LiveData<Double> = transactionDao.getTotalAmountByType(CategoryType.INCOME)
    
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }
    
    fun getTransactionsByCategory(categoryId: Long): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }
    
    fun getTotalAmountByTypeAndPeriod(type: CategoryType, startDate: Date, endDate: Date): LiveData<Double> {
        return transactionDao.getTotalAmountByTypeAndPeriod(type, startDate, endDate)
    }
    
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionDao.updateTransaction(transaction)
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)
        }
    }
    
    fun getWeeklyTransactions(): LiveData<List<Transaction>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.time
        
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }
    
    fun getMonthlyTransactions(): LiveData<List<Transaction>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.time
        
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }
    
    suspend fun getTransactionById(transactionId: Long): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }
} 