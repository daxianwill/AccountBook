package com.sym.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sym.accountbook.data.entity.Budget
import com.sym.accountbook.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BudgetRepository = BudgetRepository(application)

    val enabledBudgets: LiveData<List<Budget>> = repository.getEnabledBudgets().asLiveData()
    val totalBudget: LiveData<Budget?> = repository.getTotalBudget().asLiveData()

    fun insertBudget(budget: Budget) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            repository.deleteBudget(budgetId)
        }
    }
}

class BudgetViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
