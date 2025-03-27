package com.kishan.foodappjetpack.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kishan.foodappjetpack.api.DishRepository
import com.kishan.foodappjetpack.data.Dish
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.Dispatchers

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
        dishViewModel = DishViewModel(dishRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDishes updates uiState with dishes on success`() = runTest {
        val expectedDishes = listOf(
            Dish("Paneer Tikka", "https://example.com/paneer-tikka.jpg"),
            Dish("Jeera Rice", "https://example.com/jeera-rice.jpg")
        )
        `when`(dishRepository.getDishes()).thenReturn(expectedDishes)
        dishViewModel.loadDishes()
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = dishViewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(expectedDishes, uiState.dishes)
        assertEquals(null, uiState.error)
    }

    @Test
    fun `loadDishes updates uiState with error on failure`() = runTest {
        val exception = RuntimeException("Network error")
        `when`(dishRepository.getDishes()).thenThrow(exception)
        dishViewModel.loadDishes()
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = dishViewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(emptyList<Dish>(), uiState.dishes) // Expect empty list, not null
        assertEquals(exception.message, uiState.error)
    }
}