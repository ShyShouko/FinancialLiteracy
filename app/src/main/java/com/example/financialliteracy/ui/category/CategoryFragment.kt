package com.example.financialliteracy.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financialliteracy.R
import com.example.financialliteracy.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var categoryViewModel: CategoryViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        // Настройка кнопки добавления новой категории
        binding.addCategoryButton.setOnClickListener {
            val action = CategoryFragmentDirections.actionCategoryFragmentToAddCategoryFragment()
            findNavController().navigate(action)
        }
        
        // Настройка RecyclerView для категорий
        // Будет реализовано в следующих обновлениях
    }
    
    private fun observeData() {
        // Наблюдение за списком категорий доходов
        categoryViewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            // Обновление списка категорий доходов
            // Будет реализовано в следующих обновлениях
        }
        
        // Наблюдение за списком категорий расходов
        categoryViewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            // Обновление списка категорий расходов
            // Будет реализовано в следующих обновлениях
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 