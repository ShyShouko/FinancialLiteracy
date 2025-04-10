package com.example.financialliteracy.ui.home

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.databinding.FragmentHomeBinding
import com.example.financialliteracy.ui.category.CategoryViewModel
import com.example.financialliteracy.ui.transactions.TransactionViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var analysisViewModel: AnalysisViewModel
    
    // Флаг для определения темного режима
    private var isDarkMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        // Определяем текущую тему
        isDarkMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        analysisViewModel = ViewModelProvider(requireActivity())[AnalysisViewModel::class.java]

        setupPieChart()
        setupButtons()
        observeData()
        
        // Инициализируем и немедленно обновляем данные круговой диаграммы
        updatePieChartWithCurrentData()
    }
    
    private fun updatePieChartWithCurrentData() {
        val transactions = transactionViewModel.expenseTransactions.value
        val categories = categoryViewModel.expenseCategories.value
        
        if (transactions != null && categories != null && transactions.isNotEmpty()) {
            updatePieChart()
        } else {
            // Наблюдаем за изменениями в категориях и транзакциях для первоначального обновления
            observeCategoriesAndTransactions()
        }
    }
    
    private fun observeCategoriesAndTransactions() {
        // Наблюдаем за AnalysisViewModel для получения актуальных данных о транзакциях по категориям
        analysisViewModel.expenseByCategory.observe(viewLifecycleOwner) { expenseMap ->
            if (expenseMap.isNotEmpty()) {
                updatePieChartWithData(expenseMap)
            }
        }
        
        // Принудительно инициируем загрузку и расчет данных
        analysisViewModel.updateExpenseByCategory()
    }
    
    private fun updatePieChartWithData(expenseMap: Map<com.example.financialliteracy.data.model.Category, Double>) {
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        
        expenseMap.forEach { (category, amount) ->
            entries.add(PieEntry(amount.toFloat(), category.name))
            colors.add(category.color)
        }
        
        if (entries.isEmpty()) {
            // Если нет данных, скрываем диаграмму
            binding.pieChart.visibility = View.GONE
            binding.chartTitle.visibility = View.GONE
        } else {
            binding.pieChart.visibility = View.VISIBLE
            binding.chartTitle.visibility = View.VISIBLE
            
            val dataSet = PieDataSet(entries, "Категории")
            dataSet.colors = colors
            dataSet.valueTextSize = 14f
            dataSet.valueTextColor = if (isDarkMode) Color.WHITE else Color.BLACK
            
            val data = PieData(dataSet)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return formatCurrency(value.toDouble())
                }
            })
            
            binding.pieChart.data = data
            binding.pieChart.invalidate()
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            setEntryLabelTextSize(12f)
            // Устанавливаем цвет текста в зависимости от темы
            setEntryLabelColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            legend.textSize = 14f
            legend.textColor = if (isDarkMode) Color.WHITE else Color.BLACK
            setDrawEntryLabels(false)
            setCenterText("Расходы")
            setCenterTextSize(18f)
            setCenterTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            setHoleColor(if (isDarkMode) 
                resources.getColor(R.color.black, null) 
                else resources.getColor(R.color.white, null))
        }
    }

    private fun setupButtons() {
        binding.addIncomeButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment()
            // TODO: передать параметр для типа транзакции (доход)
            findNavController().navigate(action)
        }

        binding.addExpenseButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment()
            // TODO: передать параметр для типа транзакции (расход)
            findNavController().navigate(action)
        }
    }

    private fun observeData() {
        // Наблюдаем за доходами
        transactionViewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            val formattedIncome = formatCurrency(income ?: 0.0)
            binding.incomeText.text = formattedIncome
            updateBalance()
        }

        // Наблюдаем за расходами
        transactionViewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            val formattedExpense = formatCurrency(expense ?: 0.0)
            binding.expenseText.text = formattedExpense
            updateBalance()
            updatePieChart()
        }

        // Наблюдаем за изменениями в категориях расходов для обновления диаграммы
        transactionViewModel.expenseTransactions.observe(viewLifecycleOwner) { transactions ->
            // Группируем транзакции по категориям для диаграммы
            updatePieChart()
        }
        
        // Наблюдаем за изменениями в категориях для обновления диаграммы
        categoryViewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            updatePieChart()
        }
    }

    private fun updateBalance() {
        val income = transactionViewModel.totalIncome.value ?: 0.0
        val expense = transactionViewModel.totalExpense.value ?: 0.0
        val balance = income - expense
        binding.balanceText.text = formatCurrency(balance)
        
        // Установка цвета в зависимости от знака баланса
        val balanceColor = when {
            balance > 0 -> resources.getColor(R.color.income, null)
            balance < 0 -> resources.getColor(R.color.expense, null)
            else -> if (isDarkMode) Color.WHITE else Color.BLACK
        }
        binding.balanceText.setTextColor(balanceColor)
    }

    private fun updatePieChart() {
        val transactions = transactionViewModel.expenseTransactions.value ?: return
        val categories = categoryViewModel.expenseCategories.value ?: return

        if (transactions.isEmpty() || categories.isEmpty()) {
            binding.pieChart.visibility = View.GONE
            binding.chartTitle.visibility = View.GONE
            return
        }

        // Группируем транзакции по категориям
        val expensesByCategory = transactions.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        // Создаем записи для диаграммы
        expensesByCategory.forEach { (categoryId, amount) ->
            val category = categories.find { it.id == categoryId }
            if (category != null) {
                entries.add(PieEntry(amount.toFloat(), category.name))
                colors.add(category.color)
            }
        }

        if (entries.isEmpty()) {
            // Если нет данных, скрываем диаграмму
            binding.pieChart.visibility = View.GONE
            binding.chartTitle.visibility = View.GONE
        } else {
            binding.pieChart.visibility = View.VISIBLE
            binding.chartTitle.visibility = View.VISIBLE

            val dataSet = PieDataSet(entries, "Категории")
            dataSet.colors = colors
            dataSet.valueTextSize = 14f
            dataSet.valueTextColor = if (isDarkMode) Color.WHITE else Color.BLACK

            val data = PieData(dataSet)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return formatCurrency(value.toDouble())
                }
            })

            binding.pieChart.data = data
            binding.pieChart.invalidate()
        }
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
        return format.format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 