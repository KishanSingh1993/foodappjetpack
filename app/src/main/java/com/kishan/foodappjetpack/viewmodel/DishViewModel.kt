package com.kishan.foodappjetpack.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kishan.foodappjetpack.api.DishRepository
import com.kishan.foodappjetpack.data.Dish
import kotlinx.coroutines.launch

data class DishUiState(
    val isLoading: Boolean = false,
    val dishes: List<Dish> = emptyList(),
    val error: String? = null
)

class DishViewModel(private val repository: DishRepository = DishRepository()) : ViewModel() {
    private val _uiState = mutableStateOf(DishUiState())
    val uiState: State<DishUiState> = _uiState

    init {
        loadDishes()
    }

    fun loadDishes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val dishes = repository.getDishes() ?: emptyList()
                _uiState.value = _uiState.value.copy(isLoading = false, dishes = dishes)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    dishes = _uiState.value.dishes, // Explicitly retain current value
                    error = e.message
                )
            }
        }
    }
}

//package com.kishan.foodappjetpack.viewmodel
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.kishan.foodappjetpack.api.DishRepository
//import com.kishan.foodappjetpack.data.Dish
//import kotlinx.coroutines.launch
//import androidx.compose.runtime.State
//
//
//data class DishUiState(
//    val isLoading: Boolean = false,
//    val dishes: List<Dish> = emptyList(),
//    val error: String? = null
//)
//
//class DishViewModel(private val repository: DishRepository = DishRepository()) : ViewModel() {
//    //private val repository = DishRepository()
//
//    // Use MutableState to hold the uiState
//    private val _uiState = mutableStateOf(DishUiState())
//    val uiState: State<DishUiState> = _uiState // Expose as read-only State
//
//    init {
//        loadDishes()
//    }
//
//    fun loadDishes() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            try {
//                val dishes = repository.getDishes()
//                println("Dishes: $dishes") // Add this to inspect the data
//                _uiState.value = _uiState.value.copy(isLoading = false, dishes = dishes)
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
//            }
//        }
//    }
//}