package com.example.financialliteracy.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.databinding.FragmentCategoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

class CategoryFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    
    // Текущий выбранный тип категории: true - расходы, false - доходы
    private var isExpenseSelected = true
    
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
        
        setupRecyclerView()
        setupTabLayout()
        setupAddButton()
        observeData()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(this)
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }
    
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isExpenseSelected = tab?.position == 0
                updateCategoriesList()
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Ничего не делаем
            }
            
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Ничего не делаем
            }
        })
    }
    
    private fun setupAddButton() {
        binding.addCategoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_addCategoryFragment)
        }
    }
    
    private fun observeData() {
        categoryViewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            if (isExpenseSelected) {
                categoryAdapter.submitList(categories)
            }
        }
        
        categoryViewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            if (!isExpenseSelected) {
                categoryAdapter.submitList(categories)
            }
        }
    }
    
    private fun updateCategoriesList() {
        if (isExpenseSelected) {
            categoryViewModel.expenseCategories.value?.let { categories ->
                categoryAdapter.submitList(categories)
            }
        } else {
            categoryViewModel.incomeCategories.value?.let { categories ->
                categoryAdapter.submitList(categories)
            }
        }
    }
    
    // Реализация интерфейса CategoryClickListener
    override fun onCategoryClick(category: Category) {
        // При клике на категорию ничего не делаем
    }
    
    override fun onEditClick(category: Category) {
        // Редактирование категории будет реализовано в следующих обновлениях
        // TODO: Добавить переход к экрану редактирования
    }
    
    override fun onDeleteClick(category: Category) {
        // Подтверждение удаления
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление категории")
            .setMessage("Вы уверены, что хотите удалить категорию \"${category.name}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                categoryViewModel.deleteCategory(category)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 