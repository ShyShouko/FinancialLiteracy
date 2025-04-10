package com.example.financialliteracy.ui.analysis

import com.example.financialliteracy.data.model.CategoryType
import com.example.financialliteracy.data.model.Transaction
import java.util.Date

class FinancialAnalyzer {
    
    companion object {
        // Рекомендуемый процент сбережений от доходов
        private const val RECOMMENDED_SAVINGS_PERCENT = 20
        
        // Максимальный рекомендуемый процент затрат на определенные категории
        private const val MAX_FOOD_PERCENT = 30
        private const val MAX_ENTERTAINMENT_PERCENT = 10
        private const val MAX_HOUSING_PERCENT = 30
        
        /**
         * Анализирует транзакции за указанный период и возвращает список рекомендаций
         */
        fun analyzeTransactions(
            transactions: List<Transaction>,
            startDate: Date,
            endDate: Date
        ): List<FinancialAdvice> {
            val adviceList = mutableListOf<FinancialAdvice>()
            
            // Группируем транзакции по типу
            val incomeTransactions = transactions.filter { it.type == CategoryType.INCOME }
            val expenseTransactions = transactions.filter { it.type == CategoryType.EXPENSE }
            
            // Подсчитываем общую сумму доходов и расходов
            val totalIncome = incomeTransactions.sumOf { it.amount }
            val totalExpense = expenseTransactions.sumOf { it.amount }
            
            // Анализ общего баланса
            val balance = totalIncome - totalExpense
            val savingsPercent = if (totalIncome > 0) ((balance / totalIncome) * 100) else 0.0
            
            // Рекомендации по сбережениям
            if (savingsPercent < RECOMMENDED_SAVINGS_PERCENT) {
                adviceList.add(
                    FinancialAdvice(
                        title = "Увеличьте сбережения",
                        description = "Старайтесь сохранять не менее $RECOMMENDED_SAVINGS_PERCENT% от вашего дохода. " +
                                "Сейчас вы сохраняете только ${savingsPercent.toInt()}%.",
                        priority = AdvicePriority.HIGH
                    )
                )
            } else {
                adviceList.add(
                    FinancialAdvice(
                        title = "Отличные сбережения",
                        description = "Вы сохраняете ${savingsPercent.toInt()}% от своего дохода, что превышает рекомендуемый минимум в $RECOMMENDED_SAVINGS_PERCENT%. Продолжайте в том же духе!",
                        priority = AdvicePriority.LOW
                    )
                )
            }
            
            // Если расходы превышают доходы
            if (totalExpense > totalIncome) {
                adviceList.add(
                    FinancialAdvice(
                        title = "Расходы превышают доходы",
                        description = "За указанный период ваши расходы превысили доходы на ${(totalExpense - totalIncome)}₽. " +
                                "Рекомендуем пересмотреть бюджет и сократить ненужные расходы.",
                        priority = AdvicePriority.CRITICAL
                    )
                )
            }
            
            // Группируем расходы по категориям
            val expensesByCategory = expenseTransactions.groupBy { it.categoryId }
                .mapValues { it.value.sumOf { transaction -> transaction.amount } }
            
            // Анализ расходов по категориям
            expensesByCategory.forEach { (categoryId, amount) ->
                val percentOfTotal = if (totalExpense > 0) ((amount / totalExpense) * 100) else 0.0
                
                // Здесь можно добавить более детальный анализ по каждой категории
                // Например, если процент расходов на определенную категорию слишком высок
                
                // Это просто пример, в реальной реализации нужно определить categoryName по categoryId
                when (categoryId) {
                    // Предположим, что у нас есть ID категорий для анализа
                    // В реальном приложении их нужно получать из базы данных
                    1L -> { // Еда (просто для примера)
                        if (percentOfTotal > MAX_FOOD_PERCENT) {
                            adviceList.add(
                                FinancialAdvice(
                                    title = "Высокие расходы на еду",
                                    description = "Расходы на еду составляют ${percentOfTotal.toInt()}% от общих расходов. " +
                                            "Рекомендуется не превышать $MAX_FOOD_PERCENT%. Рассмотрите возможность готовить дома чаще.",
                                    priority = AdvicePriority.MEDIUM
                                )
                            )
                        }
                    }
                    3L -> { // Развлечения (просто для примера)
                        if (percentOfTotal > MAX_ENTERTAINMENT_PERCENT) {
                            adviceList.add(
                                FinancialAdvice(
                                    title = "Высокие расходы на развлечения",
                                    description = "Расходы на развлечения составляют ${percentOfTotal.toInt()}% от общих расходов. " +
                                            "Рекомендуется не превышать $MAX_ENTERTAINMENT_PERCENT%. Рассмотрите более экономичные варианты досуга.",
                                    priority = AdvicePriority.MEDIUM
                                )
                            )
                        }
                    }
                }
            }
            
            // Если список рекомендаций пуст, добавим общую рекомендацию
            if (adviceList.isEmpty()) {
                adviceList.add(
                    FinancialAdvice(
                        title = "Ваши финансы в хорошем состоянии",
                        description = "Мы не обнаружили проблем в вашем финансовом поведении. Продолжайте следить за расходами и доходами.",
                        priority = AdvicePriority.LOW
                    )
                )
            }
            
            return adviceList
        }
    }
}

enum class AdvicePriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class FinancialAdvice(
    val title: String,
    val description: String,
    val priority: AdvicePriority
) 