package com.example.financialliteracy.ui.calendar

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", locale)
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    private val weekFormatter = DateTimeFormatter.ofPattern("d MMMM - ", locale)
    
    // Календари для интервала дат
    private val startDateCalendar = Calendar.getInstance()
    private val endDateCalendar = Calendar.getInstance()
    
    // Режим отображения календаря: true - неделя, false - месяц
    private var isWeekMode = true
    
    // Флаг для определения пользовательского периода
    private var isCustomPeriod = false
    
    // Анимации
    private val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 300 }
    private val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 300 }
    
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
        setupDateRangePicker()
        setupButtons()
        updateTransactionsList()
        updateHeader()
        updateDateRangeTexts()
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }
    
    private fun setupButtons() {
        // Кнопка добавления транзакции
        binding.addTransactionButton.setOnClickListener {
            val action = CalendarFragmentDirections.actionCalendarFragmentToAddTransactionFragment()
            findNavController().navigate(action)
        }
    }
    
    private fun setupTabLayout() {
        binding.viewTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isWeekMode = tab?.position == 0
                isCustomPeriod = false // Сброс пользовательского периода при переключении вкладок
                updateCalendarView()
                updateHeader()
                updateTransactionsList()
                updateDateRangeTexts()
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
    
    private fun setupDateRangePicker() {
        // Кнопка выбора интервала дат
        binding.selectDateRangeButton.setOnClickListener {
            showDateRangeDialog()
        }
        
        // Кнопка применения интервала дат
        binding.applyDateRangeButton.setOnClickListener {
            isCustomPeriod = true
            hideDateRangeDialog()
            updateTransactionsList()
            binding.dailyTransactionsTitle.text = "Транзакции за выбранный период"
            updateDateRangeTexts()
        }
        
        // Кнопка отмены выбора интервала
        binding.cancelDateRangeButton.setOnClickListener {
            hideDateRangeDialog()
        }
        
        // Настройка выбора начальной даты
        binding.startDateText.setOnClickListener {
            showDatePickerDialog(true)
        }
        
        // Настройка выбора конечной даты
        binding.endDateText.setOnClickListener {
            showDatePickerDialog(false)
        }
    }
    
    private fun showDateRangeDialog() {
        // Обновляем поля с датами прежде чем показать диалог
        binding.startDateText.setText(simpleDateFormat.format(startDateCalendar.time))
        binding.endDateText.setText(simpleDateFormat.format(endDateCalendar.time))
        
        // Показываем диалог с анимацией
        binding.dateRangeCard.visibility = View.VISIBLE
        binding.dateRangeCard.startAnimation(fadeIn)
    }
    
    private fun hideDateRangeDialog() {
        // Скрываем диалог с анимацией
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.dateRangeCard.visibility = View.GONE
            }
        })
        binding.dateRangeCard.startAnimation(fadeOut)
    }
    
    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = if (isStartDate) startDateCalendar else endDateCalendar
        
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                
                if (isStartDate) {
                    // Устанавливаем время на начало дня
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    
                    binding.startDateText.setText(simpleDateFormat.format(calendar.time))
                    
                    // Проверка, чтобы начальная дата не была позже конечной
                    if (calendar.after(endDateCalendar)) {
                        endDateCalendar.time = calendar.time
                        endDateCalendar.set(Calendar.HOUR_OF_DAY, 23)
                        endDateCalendar.set(Calendar.MINUTE, 59)
                        endDateCalendar.set(Calendar.SECOND, 59)
                        binding.endDateText.setText(simpleDateFormat.format(endDateCalendar.time))
                    }
                } else {
                    // Устанавливаем время на конец дня
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    
                    binding.endDateText.setText(simpleDateFormat.format(calendar.time))
                    
                    // Проверка, чтобы конечная дата не была раньше начальной
                    if (calendar.before(startDateCalendar)) {
                        startDateCalendar.time = calendar.time
                        startDateCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        startDateCalendar.set(Calendar.MINUTE, 0)
                        startDateCalendar.set(Calendar.SECOND, 0)
                        binding.startDateText.setText(simpleDateFormat.format(startDateCalendar.time))
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show()
    }
    
    private fun updateDateRangeTexts() {
        if (!isCustomPeriod) {
            if (isWeekMode) {
                // Получение дат начала и конца недели
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
                
                startDateCalendar.time = startOfWeek.time
                endDateCalendar.time = endOfWeek.time
            } else {
                // Получение дат начала и конца месяца
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
                
                startDateCalendar.time = startOfMonth.time
                endDateCalendar.time = endOfMonth.time
            }
        }
        
        // Установка текста в поля выбора даты и в текстовое поле периода
        binding.startDateText.setText(simpleDateFormat.format(startDateCalendar.time))
        binding.endDateText.setText(simpleDateFormat.format(endDateCalendar.time))
        
        // Обновление текста текущего периода
        val periodText = "Период: ${simpleDateFormat.format(startDateCalendar.time)} - ${simpleDateFormat.format(endDateCalendar.time)}"
        binding.currentRangeText.text = periodText
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
            
            if (!isCustomPeriod) {
                binding.dailyTransactionsTitle.text = "Транзакции за неделю"
            }
        } else {
            // Формирование заголовка для месячного вида
            binding.monthYearText.text = monthFormatter.format(currentMonth)
            
            if (!isCustomPeriod) {
                binding.dailyTransactionsTitle.text = "Транзакции за месяц"
            }
        }
    }
    
    private fun updateTransactionsList() {
        if (isCustomPeriod) {
            // Если выбран пользовательский период, используем даты из полей интервала
            observeTransactions(startDateCalendar.time, endDateCalendar.time)
        } else if (isWeekMode) {
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