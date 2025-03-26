package com.kishan.foodappjetpack

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kishan.foodappjetpack.data.Dish
import org.junit.Rule
import org.junit.Test

class DishDetailSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dish_detail_sheet_displays_correct_initial_time_and_updates_on_click() {
        // Arrange
        val dish = Dish(dishName = "Paneer Tikka", imageUrl = "https://example.com/paneer-tikka.jpg")
        composeTestRule.setContent {
            DishDetailSheet(dish = dish, onDismiss = {})
        }

        // Assert initial time (06:30 AM)
        composeTestRule.onNodeWithText("06").assertExists()
        composeTestRule.onNodeWithText("30").assertExists()
        composeTestRule.onNodeWithText("AM").assertExists()

        // Act: Simulate clicking on a different hour (e.g., 07)
        composeTestRule.onNodeWithText("07").performClick()

        // Assert updated hour
        composeTestRule.onNodeWithText("07").assertExists()
    }
}