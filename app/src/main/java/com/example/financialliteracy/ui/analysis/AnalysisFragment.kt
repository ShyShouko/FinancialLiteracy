package com.example.financialliteracy.ui.analysis

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.databinding.FragmentAnalysisBinding
import com.example.financialliteracy.ui.home.AnalysisViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var analysisViewModel: AnalysisViewModel
    private lateinit var pieChart: PieChart
    
    // Флаг для определения темного режима
    private var isDarkMode: Boolean = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, 
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        
        // Определяем текущую тему
        isDarkMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        analysisViewModel = ViewModelProvider(requireActivity())[AnalysisViewModel::class.java]
        
        setupUI()
        setupPieChart()
        observeData()
        
        // Инициировать обновление данных для диаграммы
        analysisViewModel.updateExpenseByCategory()
    }
    
    private fun setupUI() {
        // В этой функции мы настраиваем UI для анализа расходов и доходов
        binding.weeklyAdviceText.text = "Загрузка анализа еженедельных данных..."
        binding.monthlyAdviceText.text = "Загрузка анализа ежемесячных данных..."
    }
    
    private fun setupPieChart() {
        pieChart = PieChart(requireContext())
        binding.chartContainer.addView(pieChart)
        
        pieChart.apply {
            description.isEnabled = false
            setDrawEntryLabels(false)
            legend.textSize = 12f
            legend.textColor = if (isDarkMode) Color.WHITE else Color.BLACK
            setCenterText("Расходы")
            setCenterTextSize(16f)
            setCenterTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            setHoleColor(if (isDarkMode) 
                resources.getColor(R.color.black, null) 
                else resources.getColor(R.color.white, null))
            setExtraOffsets(20f, 0f, 20f, 0f)
        }
    }
    
    private fun observeData() {
        // Наблюдение за данными о расходах по категориям для построения диаграммы
        analysisViewModel.expenseByCategory.observe(viewLifecycleOwner) { expenseMap ->
            if (expenseMap.isNotEmpty()) {
                updatePieChart(expenseMap)
                binding.weeklyAdviceText.text = "За прошлую неделю вы потратили больше всего на: " + 
                        getTopCategories(expenseMap, 3).joinToString(", ") { it.first.name }
                
                binding.monthlyAdviceText.text = buildMonthlySummary(expenseMap)
            } else {
                binding.weeklyAdviceText.text = "Нет данных для анализа за последнюю неделю"
                binding.monthlyAdviceText.text = "Нет данных для анализа за последний месяц"
            }
        }
    }
    
    private fun updatePieChart(expenseMap: Map<com.example.financialliteracy.data.model.Category, Double>) {
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        
        expenseMap.forEach { (category, amount) ->
            entries.add(PieEntry(amount.toFloat(), category.name))
            colors.add(category.color)
        }
        
        val dataSet = PieDataSet(entries, "Категории")
        dataSet.colors = colors
        dataSet.valueTextSize = 14f
        // Устанавливаем цвет текста в зависимости от темы
        dataSet.valueTextColor = if (isDarkMode) Color.WHITE else Color.BLACK
        
        val data = PieData(dataSet)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "₽"
            }
        })
        
        pieChart.data = data
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
    
    private fun getTopCategories(expenseMap: Map<com.example.financialliteracy.data.model.Category, Double>, count: Int): List<Pair<com.example.financialliteracy.data.model.Category, Double>> {
        return expenseMap.entries
            .sortedByDescending { it.value }
            .take(count)
            .map { it.key to it.value }
    }
    
    private fun buildMonthlySummary(expenseMap: Map<com.example.financialliteracy.data.model.Category, Double>): String {
        val totalExpense = expenseMap.values.sum()
        val topCategories = getTopCategories(expenseMap, 2)
        
        if (topCategories.isEmpty()) return "Нет данных для анализа"
        
        val topCategory = topCategories.first()
        val topCategoryPercentage = (topCategory.second / totalExpense * 100).toInt()
        
        return "В этом месяце вы потратили больше всего на категорию \"${topCategory.first.name}\" - " +
                "${topCategory.second.toInt()}₽ ($topCategoryPercentage% от общих расходов).\n" +
                "Общая сумма расходов: ${totalExpense.toInt()}₽."
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 