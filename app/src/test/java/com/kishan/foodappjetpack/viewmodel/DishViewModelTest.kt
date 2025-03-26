package com.kishan.foodappjetpack.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kishan.foodappjetpack.api.DishRepository
import com.kishan.foodappjetpack.data.Dish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class DishViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dishRepository: DishRepository

    private lateinit var dishViewModel: DishViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        dishViewModel = DishViewModel()
        // Inject the mocked repository into the ViewModel
        val repositoryField = DishViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(dishViewModel, dishRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDishes updates uiState with dishes on success`() = runTest {
        // Arrange
        val expectedDishes = listOf(
            Dish(dishName = "Paneer Tikka", imageUrl = "https://example.com/paneer-tikka.jpg"),
            Dish(dishName = "Jeera Rice", imageUrl = "https://example.com/jeera-rice.jpg")
        )
        `when`(dishRepository.getDishes()).thenReturn(expectedDishes)

        // Act
        dishViewModel.loadDishes()
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

        // Assert
        val uiState = dishViewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(expectedDishes, uiState.dishes)
        assertEquals(null, uiState.error)
    }

    @Test
    fun `loadDishes updates uiState with error on failure`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        `when`(dishRepository.getDishes()).thenThrow(exception)

        // Act
        dishViewModel.loadDishes()
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

        // Assert
        val uiState = dishViewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(emptyList<Dish>(), uiState.dishes)
        assertEquals(exception.message, uiState.error)
    }
}