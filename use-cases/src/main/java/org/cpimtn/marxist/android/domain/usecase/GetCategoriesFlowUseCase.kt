package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Category
import org.cpimtn.marxist.android.domain.repository.CategoryRepository

class GetCategoriesFlowUseCase(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> = categoryRepository.getCategories()
}
