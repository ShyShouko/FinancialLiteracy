package com.example.financialliteracy.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import com.example.financialliteracy.databinding.FragmentAddTransactionBinding
import com.example.financialliteracy.ui.category.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
    
    private var selectedCategoryId: Long = 0
    private var transactionType: CategoryType = CategoryType.EXPENSE
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        transactionViewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        
        setupUI()
        setupDatePicker()
        observeCategories()
        setupSaveButton()
    }
    
    private fun setupUI() {
        // Настройка типа транзакции (доход/расход)
        binding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            transactionType = when (checkedId) {
                R.id.radioIncome -> CategoryType.INCOME
                else -> CategoryType.EXPENSE
            }
            updateCategorySpinner()
        }
        
        // Начальная настройка даты
        binding.dateText.setText(dateFormat.format(calendar.time))
    }
    
    private fun setupDatePicker() {
        binding.dateLayout.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.dateText.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    
    private fun observeCategories() {
        categoryViewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            if (transactionType == CategoryType.INCOME) {
                updateCategoryAdapter(categories.map { it.name to it.id }.toMap())
            }
        }
        
        categoryViewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            if (transactionType == CategoryType.EXPENSE) {
                updateCategoryAdapter(categories.map { it.name to it.id }.toMap())
            }
        }
    }
    
    private fun updateCategorySpinner() {
        if (transactionType == CategoryType.INCOME) {
            categoryViewModel.incomeCategories.value?.let { categories ->
                updateCategoryAdapter(categories.map { it.name to it.id }.toMap())
            }
        } else {
            categoryViewModel.expenseCategories.value?.let { categories ->
                updateCategoryAdapter(categories.map { it.name to it.id }.toMap())
            }
        }
    }
    
    private fun updateCategoryAdapter(categories: Map<String, Long>) {
        val categoryNames = categories.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.categorySpinner.adapter = adapter
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val categoryName = categoryNames[position]
                selectedCategoryId = categories[categoryName] ?: 0
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategoryId = 0
            }
        }
    }
    
    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val amountStr = binding.amountEditText.text.toString()
            val note = binding.noteEditText.text.toString()
            
            if (amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Введите сумму", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (selectedCategoryId == 0L) {
                Toast.makeText(requireContext(), "Выберите категорию", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                Toast.makeText(requireContext(), "Сумма должна быть больше нуля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val transaction = Transaction(
                amount = amount,
                date = calendar.time,
                categoryId = selectedCategoryId,
                note = note,
                type = transactionType
            )
            
            transactionViewModel.insertTransaction(transaction)
            findNavController().popBackStack()
        }
        
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 