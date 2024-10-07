package com.example.final_project.page

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.final_project.func.RestaurantItem
import com.example.final_project.func.loadJsonFromInternalStorage
import com.example.final_project.func.writeJsonToInternalStorage
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.final_project.func.DishItem
import com.example.final_project.func.parseJsonToRestaurantItems
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.final_project.func.AccountingItem
import com.example.final_project.func.parseJsonToAccountingItems
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.func.YearPickerDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page_Dishes() {

    // 变量
    val context = LocalContext.current
    var restaurantItems by remember { mutableStateOf(emptyList<RestaurantItem>()) }
    var refreshTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(refreshTrigger) {
        Log.e("test2234","r2333")
    }

    // 加载初始数据
    LaunchedEffect(Unit) {
        val jsonString = loadJsonFromInternalStorage(context, "restaurants_data.json")
        if (jsonString != null) {
            restaurantItems = parseJsonToRestaurantItems(jsonString)
        }
    }

    // 删除项目
    fun deleteItem(item: RestaurantItem) {
        restaurantItems = restaurantItems.filter { it.id != item.id }
        writeJsonToInternalStorage(context, "restaurants_data.json", restaurantItems)
    }

    // 更新项目
    fun updateItem(updatedItem: RestaurantItem) {
        restaurantItems = restaurantItems.map {
            if (it.id == updatedItem.id) updatedItem else it
        }
        writeJsonToInternalStorage(context, "restaurants_data.json", restaurantItems)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("菜餚") },
                navigationIcon = {
                    IconButton(onClick = { GlobalVariables.scope.launch { GlobalVariables.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff000000)) // 设置标题栏颜色
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { GlobalVariables.navController.navigate("Add_NewRestaurant") },
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
        containerColor = Color(0xff000000) // 设置中间区域颜色
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 标题
            item {
                Text(
                    text = "計畫表",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // 创建左右滑动的计划表
            item {
                LazyRow(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .height(250.dp)
                                .padding(8.dp)
                                .clickable { GlobalVariables.navController.navigate("Add_NewList") }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // 標題
            item {
                Text(
                    text = "餐廳",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // 展開餐廳列表
            items(restaurantItems) { target ->
                ExpandableCardRestaurant(
                    item = target,
                    onDelete = { deleteItem(it) },
                    onUpdate = {  }
                )
            }
        }
    }
}

// 建立新清單 ()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_NewList() {
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
//                    val newItem = AccountingItem(
//                        date = selectedDate,
//                        title = selectedType,
//                        amount = amount,
//                        details = note
//                    )
//
//                    val existingItems = loadJsonFromInternalStorage(context, "accounting_data.json")?.let {
//                        parseJsonToAccountingItems(it)
//                    } ?: emptyList()
//
//                    val updatedItems = (existingItems + newItem).sortedBy {
//                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)?.time
//                    }
//                    Log.e("test_test", updatedItems.toString())
//                    writeJsonToInternalStorage(context, "accounting_data.json", updatedItems)
//                    GlobalVariables.navController.popBackStack()
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

    }
}

// 建立新餐廳
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_NewRestaurant() {

    // 變數
    var refreshTrigger by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var time by remember { mutableStateOf(mutableListOf<List<Int>>(
        mutableListOf(0,0),
        mutableListOf(0,0),
        mutableListOf()
    ))}
    var dishes by remember {mutableStateOf(mutableListOf<DishItem>()) }
    var timeChoice by remember { mutableStateOf(0)}

    val context = LocalContext.current

    var ttch by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(false) }
    var showDate2 by remember { mutableStateOf(false) }

    LaunchedEffect(refreshTrigger) {
        Log.e("test2234","r2")
    }

    Log.e("testdishes",dishes.toString())
    Log.e("test2234","r1"+time.toString())

    // 方法
    fun removeDishById(id: String) {
        dishes = dishes.filter { it.id != id }.toMutableList()
    }

    fun updateDishById(id: String, newDish: DishItem) {
        dishes = dishes.map { dish ->
            if (dish.id == id) {
                newDish
            } else {
                dish
            }
        }.toMutableList()
    }

    //
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
                    val newRestaurant = RestaurantItem(
                        name = name,
                        address = address,
                        time = time,
                        dishes = dishes
                    )

                    Log.e("win2234",newRestaurant.toString())

                    val existingItems =
                        loadJsonFromInternalStorage(context, "restaurants_data.json")?.let {
                            parseJsonToRestaurantItems(it)
                        } ?: emptyList()

                    val updatedItems = existingItems + newRestaurant
                    writeJsonToInternalStorage(context, "restaurants_data.json", updatedItems)
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 餐廳名稱
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "名稱:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("輸入名稱:", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }
            }

            // 輸入地址
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "地址:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("輸入地址:", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                            keyboardType = KeyboardType.Text,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }
            }

            // 輸入開店日期
            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "開店時間:", color = Color.White, modifier = Modifier
                        .width(100.dp)
                        .height(25.dp))
                    TextButton(onClick = { showDate = true; timeChoice=1 }) {
                        Text(text= if (time[0].isEmpty()) "設定時間" else "${if (time[0][0]>12) "下午" else "上午"} ${time[0][0]}點 ${time[0][1]}分", color = Color.White)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "關店時間:", color = Color.White, modifier = Modifier
                        .width(100.dp)
                        .height(25.dp))
                    TextButton(onClick = { showDate = true; timeChoice=2 }) {
                        Text(text= if (time[1].isEmpty()) "設定時間" else "${if (time[1][0]>12) "下午" else "上午"} ${time[1][0]}點 ${time[1][1]}分", color = Color.White)
                    }
                }

                // 設定星期幾有開
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "星期:", color = Color.White, modifier = Modifier
                        .width(100.dp)
                        .height(25.dp))
                    TextButton(onClick = { showDate2 = true; timeChoice=3 }) {
                        Text(text= if (time[2].isEmpty()) "設定時間" else getWeekNames(time[2]), color = Color.White)
                    }
                }
            }

            //
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 左邊的直線
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Gray)
                    )

                    // 中间的文本
                    Text(
                        text = "新增菜單",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // 右邊的直線
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Gray)
                    )
                }
            }

            // 新增菜單
            dishes.forEach { dish ->
                item {
                    ExpandableCardMenu(
                        menu = dish,
                        onDelete = {removeDishById(dish.id)},
                        onUpdate = {newdish ->
                            updateDishById(dish.id,newdish)
                        }
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(8.dp)
                        .clickable { ttch = !ttch },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                }
            }
            Log.e("testdishes","Hello1")

            if (ttch ) {
                item {
                    AddMenu(
                        initialDishItem = dishes,
                        onDismissRequest = {ttch = false},
                        onYearMonthSelected = { ans ->
                            dishes = ans.toMutableList()
                            ttch = false
                            Log.e("testdishes",dishes.toString())
                        }
                    )
                }
            }

            if (showDate) {
                item {
                    ShowDatePicker(
                        initialDishItem = time,
                        onValue = timeChoice,
                        onDismissRequest = { showDate = false},
                        onTimeSelected = { selectedTime,ch ->
                            time = selectedTime.toMutableList()
                            showDate = false
                            refreshTrigger++
                        }
                    )
                }
                Log.e("test2234",time.toString())
            }

            // 選擇星期
            if (showDate2) {
                item {
                    WeekDialog(
                        initialYear = time,
                        onValue = timeChoice,
                        onDismissRequest = { showDate2 = false},
                        onYearMonthSelected = { ans,ch ->
                            time = ans.toMutableList()
                            showDate2 = false
                            refreshTrigger++
                        }
                    )
                }
                Log.e("test2234",time.toString())
            }


            Log.e("testdishes","Hello")
        }
    }
}

// 繪製餐廳Card
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCardRestaurant(item: RestaurantItem, onDelete: (RestaurantItem) -> Unit, onUpdate: () -> Unit) {

    // 變數
    var expanded by remember { mutableStateOf(false) }

    // 程式
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                expanded = !expanded
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 用於顯示是收入還是支出
                Text(
                    text = "餐廳名稱:${item.name}",
                    modifier = Modifier.weight(1f)
                )

                Row (
                    modifier = Modifier.align(Alignment.CenterVertically),
                ){

                    // 用於顯示金額
                    Text(
                        text = "餐點數量${item.dishes.size}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // 用於提示按下觸發擴張
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(30.dp)
                            .offset(y = 1.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                DashedDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
//                    Button(
//                        onClick = { GlobalVariables.navController.navigate("ModifyData/${item.id}") },
//                        shape = RoundedCornerShape(3.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0x00000000),
//                            contentColor = Color.White
//                        )
//                    ) {
//                        Text("編輯")
//                    }
                    Spacer(Modifier.width(12.dp))
                    Text(text = "|", modifier = Modifier.offset(y = 12.dp))
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { onDelete(item); expanded = false }, shape = RoundedCornerShape(3.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White
                    )) {
                        Text("刪除")
                    }
                }
            }
        }
    }
}

// 繪製菜單card
@Composable
fun ExpandableCardMenu(menu: DishItem, onDelete: (String) -> Unit,onUpdate: (DishItem) -> Unit) {

    // 變數
    var expanded by remember { mutableStateOf(false) }
    var tth by remember {mutableStateOf(false)}

    var willmenu by remember {mutableStateOf(mutableListOf(menu))}
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        Log.e("ppooi","es")
    }

    // 方法

    //
    Card(
        modifier = Modifier
            .fillMaxSize()
            //.height(80.dp)
            .padding(8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "餐點名稱:${menu.name}",
                    modifier = Modifier.weight(1f)
                )

                Row (
                    modifier = Modifier.align(Alignment.CenterVertically),
                ){

                    // 用於顯示金額
                    Text(
                        text = if (menu.price!=0) "價錢: ${menu.price}" else "價錢: 0",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // 用於提示按下觸發擴張
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(30.dp)
                            .offset(y = 1.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                DashedDivider()
                Text(
                    text = if (menu.description != "") "描述: ${menu.description}" else "描述: 無",
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { tth=true },
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x00000000),
                            contentColor = Color.White
                        )
                    ) {
                        Text("編輯")
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(text = "|", modifier = Modifier.offset(y = 12.dp))
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { onDelete(menu.id); expanded = false }, shape = RoundedCornerShape(3.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White
                    )) {
                        Text("刪除")
                    }
                }

                if(tth) {
                    AddMenuPlus(
                        initialDishItem = willmenu,
                        onDismissRequest = {tth = false},
                        onYearMonthSelected = { ans ->
                            Log.e("test0987","ans"+ans.toString())
                            willmenu = ans
                            tth = false
                            expanded = false
                            onUpdate(willmenu[0])
                            refreshTrigger++
                            Log.e("test0987",willmenu.toString())
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenu(
    initialDishItem: (MutableList<DishItem>),
    onDismissRequest: () -> Unit,
    onYearMonthSelected: (MutableList<DishItem>) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        )
    ) {
        // 變數
        var name by remember { mutableStateOf("") } // 名稱
        var price by remember { mutableStateOf("") } // 價錢
        var describe by remember { mutableStateOf("") } // 描述
        val context = LocalContext.current

        // 功能
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
                        val newDish = DishItem(name = name, description = describe, price = price.toIntOrNull() ?: 0)
                        initialDishItem.add(newDish)
                        onYearMonthSelected(initialDishItem)
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

                // 名稱
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "名稱:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("輸入金額", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }

                // 價錢
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "價錢:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("輸入內容", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }

                // 描述
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "備註:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = describe,
                        onValueChange = { describe = it },
                        label = { Text("輸入內容", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

// 繪製日期
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePicker (
    initialDishItem: (MutableList<List<Int>>),
    onValue: (Int),
    onDismissRequest: () -> Unit,
    onTimeSelected: (MutableList<List<Int>>, Int) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        )
    ) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var timeChoice = onValue

        val dialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                if (timeChoice==1) {
                    initialDishItem[0] = listOf(selectedHour,selectedMinute)
                    onTimeSelected(initialDishItem, 0)
                } else if (timeChoice==2) {
                    initialDishItem[1] = listOf(selectedHour,selectedMinute)
                    onTimeSelected(initialDishItem, 1)
                } else {
                    initialDishItem[2] = listOf(selectedHour,selectedMinute)
                    onTimeSelected(initialDishItem, 2)
                }
            },
            hour,
            minute,
            true
        )

        // 設置取消操作
        dialog.setOnCancelListener {
            onDismissRequest()
        }

        // 顯示對話框
        dialog.show()
    }
}

//
@Composable
fun WeekDialog(
    initialYear: (MutableList<List<Int>>),
    onValue: (Int),
    onDismissRequest: () -> Unit,
    onYearMonthSelected: (MutableList<List<Int>>, Int) -> Unit
)  {
    var selectedWeeks by remember { mutableStateOf(initialYear[2].toMutableList()) }
    var refreshTrigger2 by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger2) {
        Log.e("ggdd","123")
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                WeekGrid(
                    selectedWeeks = selectedWeeks,
                    onWeekSelected = { week ->
                        if (selectedWeeks.contains(week)) {
                            selectedWeeks.remove(week)
                            refreshTrigger2++
                        } else {
                            selectedWeeks.add(week)
                            refreshTrigger2++
                        }
                        selectedWeeks = selectedWeeks.toMutableList() // 触发重组
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        val updatedYear = initialYear.toMutableList()
                        updatedYear[2] = selectedWeeks
                        onYearMonthSelected(updatedYear, onValue)
                    }) {
                        Text(text = "確認")
                    }
                }
            }
        }
    }
}

@Composable
fun WeekGrid(selectedWeeks: List<Int>, onWeekSelected: (Int) -> Unit) {
    val weeks = listOf(
        "週一", "週二", "週三",
        "週四", "週五", "週六",
        "週日"
    )

    Column {
        for (i in 0 until 7) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                val weekIndex = i + 1
                Text(
                    text = weeks[i],
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .background(
                            if (selectedWeeks.contains(weekIndex)) Color.Blue else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onWeekSelected(weekIndex) }
                        .wrapContentSize(Alignment.Center),
                    color = if (selectedWeeks.contains(weekIndex)) Color.White else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 星期数字到名稱的映射
fun getWeekName(day: Int): String {
    return when (day) {
        1 -> "週一"
        2 -> "週二"
        3 -> "週三"
        4 -> "週四"
        5 -> "週五"
        6 -> "週六"
        7 -> "週日"
        else -> ""
    }
}

// 將星期數字列表轉換為星期名稱列表
fun getWeekNames(days: List<Int>): String {
    return days.joinToString(", ") { getWeekName(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuPlus(
    initialDishItem: (MutableList<DishItem>),
    onDismissRequest: () -> Unit,
    onYearMonthSelected: (MutableList<DishItem>) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        )
    ) {
        // 變數
        var name by remember { mutableStateOf(initialDishItem[0].name) } // 名稱
        var price by remember { mutableStateOf(initialDishItem[0].price.toString()) } // 價錢
        var describe by remember { mutableStateOf(initialDishItem[0].description) } // 描述
        var dd by remember { mutableStateOf(initialDishItem) }
        val context = LocalContext.current

        // 功能
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
                        val newDish = DishItem(name = name, description = describe, price = price.toIntOrNull() ?: 0)
                        dd[0] = newDish
                        onYearMonthSelected(dd)
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

                // 名稱
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "名稱:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("輸入金額", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }

                // 價錢
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "價錢:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("輸入內容", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }

                // 描述
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "備註:", color = Color.White, modifier = Modifier
                        .width(50.dp)
                        .height(25.dp))
                    OutlinedTextField(
                        value = describe,
                        onValueChange = { describe = it },
                        label = { Text("輸入內容", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}