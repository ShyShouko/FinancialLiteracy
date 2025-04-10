package com.example.financialliteracy.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financialliteracy.data.model.Category
import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import com.example.financialliteracy.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter : ListAdapter<TransactionWithCategory, TransactionAdapter.TransactionViewHolder>(
    TransactionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("d MMMM", Locale("ru", "RU"))
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))

        fun bind(item: TransactionWithCategory) {
            val transaction = item.transaction
            val category = item.category

            // Установка названия категории
            binding.categoryNameText.text = category.name

            // Установка цвета категории
            binding.categoryIndicator.setCardBackgroundColor(category.color)

            // Форматирование и установка даты и примечания
            val dateText = dateFormat.format(transaction.date)
            val noteText = if (transaction.note.isNotEmpty()) "${transaction.note} • " else ""
            binding.noteDateText.text = "$noteText$dateText"

            // Форматирование и установка суммы
            val prefix = if (transaction.type == CategoryType.EXPENSE) "-" else "+"
            val amount = currencyFormat.format(transaction.amount).replace("₽", "₽")
            binding.amountText.text = "$prefix$amount"

            // Установка цвета для суммы
            val context = binding.root.context
            val colorRes = if (transaction.type == CategoryType.EXPENSE) {
                com.example.financialliteracy.R.color.expense
            } else {
                com.example.financialliteracy.R.color.income
            }
            binding.amountText.setTextColor(context.getColor(colorRes))
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionWithCategory>() {
        override fun areItemsTheSame(
            oldItem: TransactionWithCategory,
            newItem: TransactionWithCategory
        ): Boolean {
            return oldItem.transaction.id == newItem.transaction.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionWithCategory,
            newItem: TransactionWithCategory
        ): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Класс для хранения транзакции вместе с её категорией
 */
data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category
) 