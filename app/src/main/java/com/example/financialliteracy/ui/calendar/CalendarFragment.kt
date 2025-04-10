package com.example.financialliteracy.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financialliteracy.databinding.FragmentCalendarBinding
import com.example.financialliteracy.ui.transactions.TransactionViewModel
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
    private val locale = Locale("ru", "RU")
    private val selectedDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", locale)
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    
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
        
        setupCalendar()
        setupButtons()
        updateTransactionsList()
    }
    
    private fun setupCalendar() {
        // Доработаем полностью в следующих обновлениях
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = firstDayOfWeekFromLocale()
        
        binding.monthYearText.text = monthFormatter.format(currentMonth)
        
        // Будет полноценно реализовано в будущих обновлениях
    }
    
    private fun setupButtons() {
        binding.weekViewButton.setOnClickListener {
            // Будет реализовано в будущих обновлениях
        }
        
        binding.monthViewButton.setOnClickListener {
            // Будет реализовано в будущих обновлениях
        }
    }
    
    private fun updateTransactionsList() {
        // Будет реализовано в будущих обновлениях
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