package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * Unit tests for GetPostsFlowUseCase. No Android; mocks repository.
 */
class GetPostsFlowUseCaseTest {

    private val postRepository: PostRepository = mockk()
    private val useCase = GetPostsFlowUseCase(postRepository)

    @Test
    fun invoke_returns_flow_from_repository() = runTest {
        val posts = listOf(
            Post(1, "2024-01-01", "slug", "Title", "Excerpt", emptyList(), emptyList())
        )
        every { postRepository.getPosts() } returns flowOf(posts)

        val result = useCase().first()

        assertEquals(posts, result)
        verify(exactly = 1) { postRepository.getPosts() }
    }

    @Test
    fun invoke_returns_empty_list_from_repository() = runTest {
        every { postRepository.getPosts() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(emptyList<Post>(), result)
    }
}
