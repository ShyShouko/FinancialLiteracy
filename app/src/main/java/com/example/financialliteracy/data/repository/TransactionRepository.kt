package com.example.financialliteracy.data.repository

import androidx.lifecycle.LiveData
import com.example.financialliteracy.data.database.TransactionDao
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getTransactionsByType(type: CategoryType): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }
    
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }
    
    fun getTransactionsByCategory(categoryId: Long): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }
    
    fun getTotalAmountByType(type: CategoryType): LiveData<Double> {
        return transactionDao.getTotalAmountByType(type)
    }
    
    fun getTotalAmountByTypeAndPeriod(type: CategoryType, startDate: Date, endDate: Date): LiveData<Double> {
        return transactionDao.getTotalAmountByTypeAndPeriod(type, startDate, endDate)
    }
    
    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
    
    suspend fun getTransactionById(transactionId: Long): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }
}