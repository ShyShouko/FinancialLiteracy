package com.example.financialliteracy.ui.category

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.databinding.FragmentAddCategoryBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class AddCategoryFragment : Fragment() {

    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var categoryViewModel: CategoryViewModel
    
    private var selectedColor: Int = Color.parseColor("#FF5722") // Orange default
    private var categoryType: CategoryType = CategoryType.EXPENSE
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        
        setupTypeSpinner()
        setupColorPicker()
        setupSaveButton()
    }
    
    private fun setupTypeSpinner() {
        val categoryTypes = arrayOf("Расход", "Доход")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categoryTypes)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        
        binding.categoryTypeSpinner.adapter = adapter
        binding.categoryTypeSpinner.setSelection(0) // По умолчанию "Расход"
    }
    
    private fun setupColorPicker() {
        // Устанавливаем изначальный цвет для превью
        binding.colorPreview.setCardBackgroundColor(selectedColor)
        
        // Настраиваем обработчик клика на превью цвета
        binding.colorPreview.setOnClickListener {
            showColorPickerDialog()
        }
        
        // Настраиваем обработчик клика на сам ColorPickerView
        binding.colorPickerView.setOnClickListener {
            showColorPickerDialog()
        }
    }
    
    private fun showColorPickerDialog() {
        ColorPickerDialog.Builder(requireContext())
            .setTitle("Выберите цвет")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Выбрать", ColorEnvelopeListener { envelope, _ ->
                selectedColor = envelope.color
                binding.colorPreview.setCardBackgroundColor(selectedColor)
            })
            .setNegativeButton("Отмена") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .attachAlphaSlideBar(false)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }
    
    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val categoryName = binding.categoryNameEditText.text.toString().trim()
            
            if (categoryName.isEmpty()) {
                Toast.makeText(requireContext(), "Введите название категории", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            categoryType = when(binding.categoryTypeSpinner.selectedItemPosition) {
                1 -> CategoryType.INCOME
                else -> CategoryType.EXPENSE
            }
            
            val category = Category(
                name = categoryName,
                type = categoryType,
                color = selectedColor
            )
            
            categoryViewModel.insertCategory(category)
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