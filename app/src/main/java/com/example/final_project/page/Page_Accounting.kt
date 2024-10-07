package com.example.final_project.page

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.final_project.func.GlobalVariables
import com.example.final_project.func.parseJsonToAccountingItems
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.final_project.func.AccountingItem
import com.example.final_project.func.GlobalVariables.navController
import com.example.final_project.func.loadJsonFromInternalStorage
import com.example.final_project.func.writeJsonToInternalStorage
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.TextStyle
import java.util.Calendar
import java.util.Date
import com.example.final_project.func.CircularStatistics_Month
import com.example.final_project.func.ColorIndicator
import com.example.final_project.func.YearMonthPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page_Accounting() {

    // 變數
    val context = LocalContext.current
    var accountingItems by remember { mutableStateOf(emptyList<AccountingItem>()) } // 用於暫存紀錄 後續操作都靠這
    var selectedYear by remember { mutableIntStateOf(SimpleDateFormat("yyyy", Locale.getDefault()).format( Date() ).toInt()) }
    var selectedMonth by remember { mutableIntStateOf(SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toInt()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Log.e("help123",selectedMonth.toString())

    //
    LaunchedEffect(Unit) {
        val jsonString = loadJsonFromInternalStorage(context, "accounting_data.json")
        if (jsonString != null) {
            accountingItems = parseJsonToAccountingItems(jsonString).sortedBy {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)?.time
            }
        }
    }

    // 刪除
    fun deleteItem(item: AccountingItem) {
        accountingItems = accountingItems.filter { it.id != item.id }
        writeJsonToInternalStorage(context, "accounting_data.json", accountingItems)
    }

    // 新增
    fun updateItem(updatedItem: AccountingItem) {
        accountingItems = accountingItems.map {
            if (it.id == updatedItem.id) updatedItem else it
        }
        writeJsonToInternalStorage(context, "accounting_data.json", accountingItems)
    }

    val filteredItems = accountingItems.filter {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
        val calendar = Calendar.getInstance().apply {if (date != null) { time = date }}
        calendar.get(Calendar.YEAR) == selectedYear && (calendar.get(Calendar.MONTH) + 1) == selectedMonth
    }

    val dateTitle = if (filteredItems.isNotEmpty()) {
        "${selectedYear}年${selectedMonth}月的紀錄"
    } else {
        "${selectedYear}年${selectedMonth}月沒有消費紀錄"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("記帳") },
                navigationIcon = {
                    IconButton(onClick = { GlobalVariables.scope.launch { GlobalVariables.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(text = "選擇日期", color = Color.White, style = TextStyle(fontSize = 16.sp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff000000)) // 設定標題列(TopBar)的顏色
            )
        },
        containerColor = Color(0xff000000) // 設定中間區域的顏色
    ) { paddingValues ->
        if (showDatePicker) {
            YearMonthPickerDialog(
                initialYear = selectedYear,
                initialMonth = selectedMonth,
                onDismissRequest = { showDatePicker = false },
                onYearMonthSelected = { year, month ->
                    selectedYear = year
                    selectedMonth = month
                    showDatePicker = false
                }
            )
        }

        // 计算收入和支出比例
        val totalIncome = filteredItems.filter { it.title == "收入" }.sumOf { it.amount.toDouble() }
        val totalExpense = filteredItems.filter { it.title == "支出" }.sumOf { it.amount.toDouble()}
        val totalAmount = totalIncome + totalExpense
        val incomePercentage =  totalIncome / totalAmount * 100
        val expensePercentage = totalExpense / totalAmount * 100

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                // 顯示標題
                Text(
                    text = dateTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )

                // 輸出圖表
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    // 輸出圖表
                    Row(modifier = Modifier.padding(16.dp)) {
                        if (filteredItems.isEmpty()) {
                            CircularStatistics_Month(
                                percentages = listOf(1.0f),
                                colors = listOf(Color(0xFF9C27B0)) // 蓝色: 支出, 金色: 收入
                            )
                        } else {
                            CircularStatistics_Month(
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

            val groupedItems = filteredItems.groupBy { it.date }.toSortedMap(compareBy {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
            })


            // 條件綁定
            groupedItems.forEach { (date, items) ->

                // 顯示時間
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // 條件綁定
                items.forEach { item ->
                    item {
                        ExpandableCard(
                            item = item,
                            onDelete = { deleteItem(it) },
                            onUpdate = { updateItem(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCard(item: AccountingItem, onDelete: (AccountingItem) -> Unit, onUpdate: (AccountingItem) -> Unit) {

    // 變數
    var expanded by remember { mutableStateOf(false) }

    // 顯示收入or支出紀錄
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                // 用於顯示是收入還是支出
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f)
                )

                Row (
                    modifier = Modifier.align(Alignment.CenterVertically),
                ){

                    // 用於顯示金額
                    Text(
                        text = if (item.title == "支出") "-$ ${item.amount}" else "$ ${item.amount}",
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
                    text = "金額: ${item.amount}",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = item.details,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { navController.navigate("ModifyData/${item.id}") },
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

@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val dashWidth = 10.dp.toPx()
        val dashGap = 5.dp.toPx()
        val totalWidth = size.width
        var x = 0f
        while (x < totalWidth) {
            drawLine(
                color = Color.Gray,
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x + dashWidth, 0f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            x += dashWidth + dashGap
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ModifyData(accountingItem: AccountingItem) {
    var amount by remember { mutableStateOf(accountingItem.amount) }
    var note by remember { mutableStateOf(accountingItem.details) }
    var selectedType by remember { mutableStateOf(accountingItem.title) }
    var selectedDate by remember { mutableStateOf(accountingItem.date) }
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
    val datePickerDialog = android.app.DatePickerDialog(
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
                title = { Text("編輯") },
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
                    val updatedItem = accountingItem.copy(
                        date = selectedDate,
                        title = selectedType,
                        amount = amount,
                        details = note
                    )

                    // 確認存在 根據結果執行操作 目的從json文件取出資料 用於後續更新
                    val existingItems = loadJsonFromInternalStorage(context, "accounting_data.json")?.let {
                        parseJsonToAccountingItems(it)
                    } ?: emptyList()

                    val updatedItems = existingItems.map {
                        if (it.id == updatedItem.id) updatedItem else it
                    }.sortedBy {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)?.time
                    }

                    writeJsonToInternalStorage(context, "accounting_data.json", updatedItems)
                    navController.popBackStack()
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
                    .height(20.dp))
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
                    .height(20.dp))
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
                        .padding(vertical = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "備註:", color = Color.White, modifier = Modifier
                    .width(50.dp)
                    .height(20.dp))
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
                        .padding(vertical = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "時間:", color = Color.White, modifier = Modifier
                    .width(50.dp)
                    .height(20.dp))
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

