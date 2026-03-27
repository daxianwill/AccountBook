package com.sym.accountbook.data.repository

import com.sym.accountbook.data.dao.CategoryDao
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getAllCategoriesOnce(): List<Category> {
        return categoryDao.getAllCategoriesOnce()
    }

    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
}
