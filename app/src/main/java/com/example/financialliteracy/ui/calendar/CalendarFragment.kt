package com.example.financialliteracy.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import com.example.financialliteracy.databinding.FragmentCalendarBinding
import com.example.financialliteracy.ui.category.CategoryViewModel
import com.example.financialliteracy.ui.transactions.TransactionAdapter
import com.example.financialliteracy.ui.transactions.TransactionViewModel
import com.example.financialliteracy.ui.transactions.TransactionWithCategory
import com.google.android.material.tabs.TabLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    
    private val locale = Locale("ru", "RU")
    private val selectedDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", locale)
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    private val weekFormatter = DateTimeFormatter.ofPattern("d MMMM - ", locale)
    
    // Режим отображения календаря: true - неделя, false - месяц
    private var isWeekMode = true
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        transactionViewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        
        setupRecyclerView()
        setupTabLayout()
        setupCalendar()
        updateTransactionsList()
        updateHeader()
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }
    
    private fun setupTabLayout() {
        binding.viewTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isWeekMode = tab?.position == 0
                updateCalendarView()
                updateHeader()
                updateTransactionsList()
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Ничего не делаем
            }
            
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Ничего не делаем
            }
        })
    }
    
    private fun setupCalendar() {
        // Инициализация календаря
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = firstDayOfWeekFromLocale()
        
        // Настройка календаря будет расширена в будущих обновлениях
        updateCalendarView()
    }
    
    private fun updateCalendarView() {
        // Обновление вида календаря в зависимости от выбранного режима (неделя/месяц)
        // Реализация будет дополнена в будущих обновлениях
        
        if (isWeekMode) {
            // Настройки для недельного вида
            binding.calendarView.apply {
                // Здесь будут настройки для недельного вида
                // Например, изменение высоты и количества отображаемых дней
            }
        } else {
            // Настройки для месячного вида
            binding.calendarView.apply {
                // Здесь будут настройки для месячного вида
                // Например, отображение полного месяца
            }
        }
    }
    
    private fun updateHeader() {
        val currentMonth = YearMonth.now()
        
        if (isWeekMode) {
            // Формирование заголовка для недельного вида
            val startOfWeek = selectedDate.clone() as Calendar
            startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
            
            val endOfWeek = startOfWeek.clone() as Calendar
            endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
            
            val headerText = "${dateFormat.format(startOfWeek.time)} - ${dateFormat.format(endOfWeek.time)}"
            binding.monthYearText.text = headerText
            binding.dailyTransactionsTitle.text = "Транзакции за неделю"
        } else {
            // Формирование заголовка для месячного вида
            binding.monthYearText.text = monthFormatter.format(currentMonth)
            binding.dailyTransactionsTitle.text = "Транзакции за месяц"
        }
    }
    
    private fun updateTransactionsList() {
        // Обновление списка транзакций в зависимости от выбранного режима и периода
        if (isWeekMode) {
            // Получение транзакций за неделю
            val startOfWeek = selectedDate.clone() as Calendar
            startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
            startOfWeek.set(Calendar.HOUR_OF_DAY, 0)
            startOfWeek.set(Calendar.MINUTE, 0)
            startOfWeek.set(Calendar.SECOND, 0)
            
            val endOfWeek = startOfWeek.clone() as Calendar
            endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
            endOfWeek.set(Calendar.HOUR_OF_DAY, 23)
            endOfWeek.set(Calendar.MINUTE, 59)
            endOfWeek.set(Calendar.SECOND, 59)
            
            observeTransactions(startOfWeek.time, endOfWeek.time)
        } else {
            // Получение транзакций за месяц
            val startOfMonth = selectedDate.clone() as Calendar
            startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
            startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
            startOfMonth.set(Calendar.MINUTE, 0)
            startOfMonth.set(Calendar.SECOND, 0)
            
            val endOfMonth = startOfMonth.clone() as Calendar
            endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
            endOfMonth.set(Calendar.HOUR_OF_DAY, 23)
            endOfMonth.set(Calendar.MINUTE, 59)
            endOfMonth.set(Calendar.SECOND, 59)
            
            observeTransactions(startOfMonth.time, endOfMonth.time)
        }
    }
    
    private fun observeTransactions(startDate: Date, endDate: Date) {
        // Наблюдение за транзакциями и категориями для формирования объединенного списка
        transactionViewModel.getTransactionsBetweenDates(startDate, endDate)
            .observe(viewLifecycleOwner) { transactions ->
                // Загрузим все категории и объединим их с транзакциями
                categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
                    val transactionsWithCategories = transactions.mapNotNull { transaction ->
                        val category = categories.find { it.id == transaction.categoryId }
                        if (category != null) {
                            TransactionWithCategory(transaction, category)
                        } else null
                    }
                    
                    // Обновляем список транзакций в адаптере
                    transactionAdapter.submitList(transactionsWithCategories)
                    
                    // Обновляем информацию о суммах
                    updateSummary(transactions)
                }
            }
    }
    
    private fun updateSummary(transactions: List<Transaction>) {
        // Расчет итоговых значений
        var totalIncome = 0.0
        var totalExpense = 0.0
        
        transactions.forEach { transaction ->
            when (transaction.type) {
                CategoryType.INCOME -> totalIncome += transaction.amount
                CategoryType.EXPENSE -> totalExpense += transaction.amount
            }
        }
        
        val balance = totalIncome - totalExpense
        
        // Обновление отображения
        binding.periodIncomeText.text = formatCurrency(totalIncome)
        binding.periodExpenseText.text = formatCurrency(totalExpense)
        binding.periodBalanceText.text = formatCurrency(balance)
        
        // Установка цвета для баланса: зеленый если положительный, красный если отрицательный
        val balanceColor = if (balance >= 0) 
            resources.getColor(com.example.financialliteracy.R.color.income, null)
        else
            resources.getColor(com.example.financialliteracy.R.color.expense, null)
        
        binding.periodBalanceText.setTextColor(balanceColor)
    }
    
    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        return format.format(amount)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 