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

class CategoryFragment : Fragment(), CategoryAdapter.OnCategoryClickListener {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    
    private var currentCategoryType = CategoryType.EXPENSE
    
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
        observeCategories()
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
                when (tab?.position) {
                    0 -> {
                        currentCategoryType = CategoryType.EXPENSE
                        observeCategories()
                    }
                    1 -> {
                        currentCategoryType = CategoryType.INCOME
                        observeCategories()
                    }
                }
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
            // Переход к фрагменту добавления категории с передачей текущего типа
            val action = CategoryFragmentDirections.actionCategoryFragmentToAddCategoryFragment(currentCategoryType)
            findNavController().navigate(action)
        }
    }
    
    private fun observeCategories() {
        when (currentCategoryType) {
            CategoryType.EXPENSE -> {
                categoryViewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
                    categoryAdapter.submitList(categories)
                }
            }
            CategoryType.INCOME -> {
                categoryViewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
                    categoryAdapter.submitList(categories)
                }
            }
        }
    }
    
    override fun onCategoryClick(category: Category) {
        // Обработка нажатия на категорию
        val action = CategoryFragmentDirections.actionCategoryFragmentToEditCategoryFragment(category.id)
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 