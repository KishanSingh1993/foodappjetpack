package com.kishan.foodappjetpack.api

import com.kishan.foodappjetpack.data.Dish
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class DishRepositoryTest {

    @Mock
    private lateinit var dishApi: DishApi

    private lateinit var dishRepository: DishRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Since DishRepository creates its own Retrofit instance, we need to inject the mocked API
        dishRepository = DishRepository()
        // Use reflection or modify DishRepository to allow injecting the API for testing
        val apiField = DishRepository::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(dishRepository, dishApi)
    }

    @Test
    fun `getDishes returns list of dishes on success`() = runBlocking {
        // Arrange
        val expectedDishes = listOf(
            Dish(dishName = "Paneer Tikka", imageUrl = "https://example.com/paneer-tikka.jpg"),
            Dish(dishName = "Jeera Rice", imageUrl = "https://example.com/jeera-rice.jpg")
        )
        `when`(dishApi.getDishes()).thenReturn(expectedDishes)

        // Act
        val result = dishRepository.getDishes()

        // Assert
        assertEquals(expectedDishes, result)
    }

    @Test
    fun `getDishes throws exception on failure`() = runBlocking {
        // Arrange
        val exception = RuntimeException("Network error")
        `when`(dishApi.getDishes()).thenThrow(exception)

        // Act & Assert
        try {
            dishRepository.getDishes()
            // If no exception is thrown, fail the test
            assert(false) { "Expected an exception to be thrown" }
        } catch (e: Exception) {
            assertEquals(exception.message, e.message)
        }
    }
}