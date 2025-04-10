package com.example.financialliteracy.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financialliteracy.R
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.databinding.ItemCategoryBinding

class CategoryAdapter(private val listener: OnCategoryClickListener) :
    ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val listener: OnCategoryClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.categoryNameTextView.text = category.name
            binding.categoryColorView.setCardBackgroundColor(category.color)

            // Настройка меню категории
            binding.categoryMenuButton.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.menu_category, popup.menu)
                
                // Если категория по умолчанию, то скрываем пункт "Удалить"
                if (category.isDefault) {
                    popup.menu.findItem(R.id.action_delete).isVisible = false
                }
                
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> {
                            listener.onCategoryClick(category)
                            true
                        }
                        R.id.action_delete -> {
                            // Функция удаления категории будет реализована позже
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }

            // Обработка клика по всей карточке
            binding.root.setOnClickListener {
                listener.onCategoryClick(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }
} 