package com.example.final_project.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.final_project.func.GlobalVariables
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.final_project.func.AccountingItem
import com.example.final_project.func.loadJsonFromInternalStorage
import com.example.final_project.func.parseJsonToAccountingItems
import com.example.final_project.func.writeJsonToInternalStorage
import kotlinx.coroutines.launch
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.final_project.func.CircularStatistics_Today
import com.example.final_project.func.ColorIndicator
import com.example.final_project.func.DishItem
import com.example.final_project.func.RestaurantItem
import com.example.final_project.func.getCurrentDateString
import com.example.final_project.func.loadRecommendedDishes
import com.example.final_project.func.parseJsonToRestaurantItems
import com.example.final_project.func.saveRecommendedDishes
import java.text.SimpleDateFormat
import java.util.*

//簡介:App點開的第一個畫面

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page_Main() {

    // 變數
    val context = LocalContext.current

    // 假設從存儲中加載並解析了記帳項目
    val accountingItems = loadJsonFromInternalStorage(context, "accounting_data.json")?.let {
        parseJsonToAccountingItems(it)
    } ?: emptyList()

    // 過濾 只留下當日的
    val filteredItems = accountingItems.filter {
        it.date == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // 計算當天的支出和收入總和
    val totalExpense = filteredItems.filter { it.title == "支出" }.sumOf { it.amount.toDouble() }
    val totalIncome = filteredItems.filter { it.title == "收入" }.sumOf { it.amount.toDouble() }

    val totalAmount = totalExpense + totalIncome
    val expensePercentage = if (totalAmount == 0.0) 0.0 else totalExpense / totalAmount
    val incomePercentage = if (totalAmount == 0.0) 0.0 else totalIncome / totalAmount

    //
    val (savedDishes, savedDate) = loadRecommendedDishes(context)
    val currentDate = getCurrentDateString()

    //
    val recommendedDishes = remember {
        if (savedDate != currentDate || savedDishes == null) {
            val newDishes = generateRecommendations(context)
            saveRecommendedDishes(context, newDishes)
            newDishes
        } else {
            savedDishes
        }
    }



    // 主程式
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("首頁") },
                navigationIcon = {
                    IconButton(onClick = { GlobalVariables.scope.launch{ GlobalVariables.drawerState.open()} }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff000000))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { GlobalVariables.navController.navigate("Add_Accounting") },
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .size(70.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        containerColor = Color(0xff000000)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "今日紀錄",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        if (filteredItems.isEmpty()) {
                            CircularStatistics_Today(
                                percentages = listOf(1.0f),
                                colors = listOf(Color(0xFF9C27B0)) // 蓝色: 支出, 金色: 收入
                            )
                        } else {
                            CircularStatistics_Today(
                                percentages = listOf(expensePercentage.toFloat(), incomePercentage.toFloat()),
                                colors = listOf(Color(0xFF00BFFF), Color(0xFFFFD700)) // 蓝色: 支出, 金色: 收入
                            )
                        }

                        // 空格
                        Spacer(modifier = Modifier.width(17.dp))

                        //
                        Column(
                            //verticalArrangement = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ColorIndicator(color = Color(0xFF00BFFF), text = "支出:${totalExpense.toInt()}")
                            Spacer(modifier = Modifier.width(16.dp))
                            ColorIndicator(color = Color(0xFFFFD700), text = "收入:${totalIncome.toInt()}")
                        }
                    }
                }
            }

            // 今日推薦
            item {
                Text(
                    text = "今日推薦",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        recommendedDishes.forEach { dish ->
                            Text(
                                text = "${dish.name}: ${dish.price}元",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Accounting() {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("支出") }
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val today = remember {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.format(calendar.time)
    }

    if (selectedDate.isEmpty()) {
        selectedDate = today
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新增") },
                navigationIcon = {
                    IconButton(onClick = { GlobalVariables.scope.launch { GlobalVariables.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff000000))
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newItem = AccountingItem(
                        date = selectedDate,
                        title = selectedType,
                        amount = amount,
                        details = note
                    )

                    val existingItems = loadJsonFromInternalStorage(context, "accounting_data.json")?.let {
                        parseJsonToAccountingItems(it)
                    } ?: emptyList()

                    val updatedItems = (existingItems + newItem).sortedBy {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)?.time
                    }
                    Log.e("test_test", updatedItems.toString())
                    writeJsonToInternalStorage(context, "accounting_data.json", updatedItems)
                    GlobalVariables.navController.popBackStack()
                },
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "確認")
            }
        },
        containerColor = Color(0xff000000)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "類型:", color = Color.White, modifier = Modifier
                    .width(50.dp)
                    .height(25.dp))
                TypeButton("支出", selectedType) { selectedType = "支出" }
                TypeButton("收入", selectedType) { selectedType = "收入" }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "金額:", color = Color.White, modifier = Modifier
                    .width(50.dp)
                    .height(25.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("輸入金額", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        cursorColor = Color.White,
//                        focusedBorderColor = Color.White,
//                        unfocusedBorderColor = Color.Gray,
//                        containerColor = Color(0xFF1C1C1C)
//                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "備註:", color = Color.White, modifier = Modifier
                    .width(50.dp)
                    .height(25.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("輸入內容", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        cursorColor = Color.White,
//                        focusedBorderColor = Color.White,
//                        unfocusedBorderColor = Color.Gray,
//                        containerColor = Color(0xFF1C1C1C)
//                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "時間:", color = Color.White, modifier = Modifier
                    .width(50.dp)
                    .height(25.dp))
                TextButton(onClick = { datePickerDialog.show() }) {

                    Text(text = if (selectedDate.isEmpty()) "選擇日期" else selectedDate,
                        color = Color.White,
                        modifier = Modifier.offset(y = 3.dp),
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypeButton(type: String, selectedType: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (type == selectedType) Color.White else Color(0xFF1C1C1C),
            contentColor = if (type == selectedType) Color.Black else Color.White
        ),
        modifier = Modifier
            .height(40.dp)
    ) {
        Text(text = type)
    }
}

fun generateRecommendations(context: Context, maxPrice: Int = 300): List<DishItem> {
    val restaurants = loadRestaurantsData(context)
    val affordableDishes = mutableListOf<DishItem>()

    restaurants.forEach { restaurant ->
        affordableDishes.addAll(restaurant.dishes.filter { it.price <= maxPrice })
    }

    affordableDishes.shuffle()

    val recommendedDishes = mutableListOf<DishItem>()
    var totalPrice = 0

    for (dish in affordableDishes) {
        if (totalPrice + dish.price <= maxPrice) {
            recommendedDishes.add(dish)
            totalPrice += dish.price
        }
    }

    return recommendedDishes
}

fun loadRestaurantsData(context: Context): List<RestaurantItem> {
    val jsonString = loadJsonFromInternalStorage(context, "restaurants_data.json")
    return jsonString?.let { parseJsonToRestaurantItems(it) } ?: emptyList()
}
