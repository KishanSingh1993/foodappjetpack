package com.kishan.foodappjetpack

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.kishan.foodappjetpack.data.Category
import com.kishan.foodappjetpack.data.Dish
import com.kishan.foodappjetpack.viewmodel.DishViewModel
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

// Hardcoded categories for "What's on your mind?" section
val whatsOnYourMindCategories = listOf(
    Category("Rice items", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80"),
    Category("Indian", "https://images.unsplash.com/photo-1601050690597-df0568f70950?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80"),
    Category("Curries", "https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80"),
    Category("Soups", "https://images.unsplash.com/photo-1547592166-23ac45744acd?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80"),
    Category("Desserts", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80"),
    Category("Snack", "https://images.unsplash.com/photo-1558961283-6e6b1c4b4b47?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DishViewModel = viewModel()) {
    val uiState by viewModel.uiState
    var selectedDish by remember { mutableStateOf<Dish?>(null) }
    var sheetState = rememberModalBottomSheetState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedNavItem by remember { mutableStateOf("Cook") } // Track selected navigation item

    // Show or hide bottom sheet based on selected dish
    LaunchedEffect(selectedDish) {
        if (selectedDish != null) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    Scaffold(
        topBar = {
            TopBar(searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavigationDrawer(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // "What's on your mind?" section
                WhatsOnYourMindSection(categories = whatsOnYourMindCategories)

                // Recommendations section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Show all",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { /* TODO: Handle show all */ }
                    )
                }
                Box {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        uiState.error != null -> {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            val filteredDishes = if (searchQuery.isEmpty()) uiState.dishes else uiState.dishes.filter {
                                val displayName = it.dishName
                                displayName.contains(searchQuery, ignoreCase = true)
                            }
                            println("Filtered Dishes: $filteredDishes")
                            DishList(
                                dishes = filteredDishes,
                                onDishClick = { selectedDish = it }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* TODO: Handle explore all dishes */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Explore all dishes")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = { /* TODO: Handle confused what to cook */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Confused what to cook?",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // ModalBottomSheet for dish details
        if (selectedDish != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedDish = null },
                sheetState = sheetState
            ) {
                DishDetailSheet(dish = selectedDish!!, onDismiss = { selectedDish = null })
            }
        }
    }
}

@Composable
fun TopBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = MaterialTheme.shapes.large
                ),
            placeholder = { Text("Search for dish or ingredient") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Italian Spaghetti... Scheduled 6:30 AM",
                color = Color.White,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { /* TODO: Handle power off */ }) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Power",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NavigationDrawer(selectedItem: String, onItemSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight()
            .background(Color.White)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavigationItem(
            label = "Cook",
            icon = Icons.Default.LocalDining,
            isSelected = selectedItem == "Cook",
            onClick = { onItemSelected("Cook") }
        )
        NavigationItem(
            label = "Favourites",
            icon = Icons.Default.Favorite,
            isSelected = selectedItem == "Favourites",
            onClick = { onItemSelected("Favourites") }
        )
        NavigationItem(
            label = "Manual",
            icon = Icons.Default.Book,
            isSelected = selectedItem == "Manual",
            onClick = { onItemSelected("Manual") }
        )
        NavigationItem(
            label = "Device",
            icon = Icons.Default.Devices,
            isSelected = selectedItem == "Device",
            onClick = { onItemSelected("Device") }
        )
        NavigationItem(
            label = "Preferences",
            icon = Icons.Default.Person,
            isSelected = selectedItem == "Preferences",
            onClick = { onItemSelected("Preferences") }
        )
        NavigationItem(
            label = "Settings",
            icon = Icons.Default.Settings,
            isSelected = selectedItem == "Settings",
            onClick = { onItemSelected("Settings") }
        )
    }
}

@Composable
fun NavigationItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                if (isSelected) Color(0xFFFFA500) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color(0xFF3F51B5),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WhatsOnYourMindSection(categories: List<Category>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "What's on your mind?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category = category)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Card(
        modifier = Modifier
            .clickable { /* TODO: Add filtering logic if required */ },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(category.imageUrl),
                contentDescription = category.name,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DishList(dishes: List<Dish>, onDishClick: (Dish) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(dishes) { dish ->
            DishItem(dish = dish, onClick = { onDishClick(dish) })
        }
    }
}

@Composable
fun DishItem(dish: Dish, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(dish.imageUrl),
                contentDescription = dish.dishName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = dish.dishName,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "★ 4.2",
                    color = Color(0xFFFFA500),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "30 min • Medium prep.",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun DishDetailSheet(dish: Dish, onDismiss: () -> Unit) {
    // State for selected hour, minute, and AM/PM
    var selectedHour by remember { mutableStateOf(6) } // Default to 06
    var selectedMinute by remember { mutableStateOf(30) } // Default to 30
    var isAm by remember { mutableStateOf(true) } // Default to AM

    // LazyListState for hours and minutes
    val hoursListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedHour - 1)
    val minutesListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedMinute)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title and Close Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Schedule cooking time",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Date and Time Picker Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hours Picker
                LazyColumn(
                    modifier = Modifier
                        .height(80.dp)
                        .width(60.dp),
                    state = hoursListState,
                    flingBehavior = rememberSnapperFlingBehavior(hoursListState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items((1..12).toList()) { hour ->
                        Text(
                            text = hour.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (hour == selectedHour) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier
                                .height(40.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .clickable {
                                    selectedHour = hour
                                }
                        )
                    }
                }
                Text(
                    text = " : ",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Minutes Picker
                LazyColumn(
                    modifier = Modifier
                        .height(80.dp)
                        .width(60.dp),
                    state = minutesListState,
                    flingBehavior = rememberSnapperFlingBehavior(minutesListState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items((0..59).toList()) { minute ->
                        Text(
                            text = minute.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (minute == selectedMinute) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier
                                .height(40.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .clickable {
                                    selectedMinute = minute
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                // AM/PM Toggle
                Column {
                    Button(
                        onClick = { isAm = true },
                        modifier = Modifier
                            .width(60.dp)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAm) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f),
                            contentColor = if (isAm) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ) {
                        Text("AM", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { isAm = false },
                        modifier = Modifier
                            .width(60.dp)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAm) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f),
                            contentColor = if (!isAm) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    ) {
                        Text("PM", fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { /* TODO: Handle delete */ }) {
                Text(
                    text = "Delete",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedButton(
                onClick = { /* TODO: Handle re-schedule */ },
                border = BorderStroke(1.dp, Color(0xFFFFA500)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFFA500)
                )
            ) {
                Text("Re-schedule")
            }
            Button(
                onClick = { /* TODO: Handle cook now */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                )
            ) {
                Text("Cook Now")
            }
        }
    }

    // Update selected hour and minute based on scroll position
    LaunchedEffect(hoursListState.firstVisibleItemIndex) {
        val visibleHour = hoursListState.firstVisibleItemIndex + 1
        if (visibleHour in 1..12) {
            selectedHour = visibleHour
        }
    }

    LaunchedEffect(minutesListState.firstVisibleItemIndex) {
        val visibleMinute = minutesListState.firstVisibleItemIndex
        if (visibleMinute in 0..59) {
            selectedMinute = visibleMinute
        }
    }
}

//@Composable
//fun DishDetailSheet(dish: Dish, onDismiss: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Title and Close Button
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Schedule cooking time",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(start = 8.dp)
//            )
//            IconButton(
//                onClick = onDismiss,
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(Color.LightGray.copy(alpha = 0.2f), shape = CircleShape)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = "Close",
//                    tint = MaterialTheme.colorScheme.onSurface
//                )
//            }
//        }
//
//        // Date and Time Picker Section
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.LightGray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "05 29",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "06 : 30",
//                    style = MaterialTheme.typography.headlineLarge,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                Column {
//                    Button(
//                        onClick = { /* TODO: Toggle to AM */ },
//                        modifier = Modifier
//                            .width(60.dp)
//                            .height(36.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primary,
//                            contentColor = Color.White
//                        ),
//                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
//                    ) {
//                        Text("AM", fontSize = 14.sp)
//                    }
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Button(
//                        onClick = { /* TODO: Toggle to PM */ },
//                        modifier = Modifier
//                            .width(60.dp)
//                            .height(36.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.LightGray.copy(alpha = 0.2f),
//                            contentColor = MaterialTheme.colorScheme.onSurface
//                        ),
//                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
//                    ) {
//                        Text("PM", fontSize = 14.sp)
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "07 31",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Action Buttons
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            TextButton(onClick = { /* TODO: Handle delete */ }) {
//                Text(
//                    text = "Delete",
//                    color = Color.Red,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//            OutlinedButton(
//                onClick = { /* TODO: Handle re-schedule */ },
//                border = BorderStroke(1.dp, Color(0xFFFFA500)),
//                colors = ButtonDefaults.outlinedButtonColors(
//                    contentColor = Color(0xFFFFA500)
//                )
//            ) {
//                Text("Re-schedule")
//            }
//            Button(
//                onClick = { /* TODO: Handle cook now */ },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFFFA500),
//                    contentColor = Color.White
//                )
//            ) {
//                Text("Cook Now")
//            }
//        }
//    }
//}