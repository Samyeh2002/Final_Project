package com.example.final_project.page

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.final_project.func.GlobalVariables
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import com.example.final_project.func.loadJsonFromInternalStorage
import com.example.final_project.func.parseJsonToAccountingItems
import com.example.final_project.func.parseJsonToRestaurantItems
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page_Setting() {

    // 變數
    var selectedYear by remember { mutableIntStateOf(2023) }
    var selectedMonth by remember { mutableIntStateOf(1) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = {
                        GlobalVariables.scope.launch { GlobalVariables.drawerState.open() }
                        GlobalVariables.gesturesEnabled = true

                        val route = when (GlobalVariables.selectedItemIndex) {
                            0 -> "Main"
                            1 -> "Accounting"
                            2 -> "Dishes"
                            else -> null
                        }

                        route?.let {
                            GlobalVariables.navController.navigate(it)
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff000000))
            )
        },
        containerColor = Color(0xff000000)
    ) { paddingValues ->
        // 可滚动的内容区域
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Button(onClick = { showDatePicker = true }) {
                Text(text = "Select Year and Month")
            }
            Text(
                text = "Selected Year and Month: $selectedYear-${"%02d".format(selectedMonth)}",
                modifier = Modifier.padding(top = 16.dp)
            )

            // 添加大量的項目以使其可滾動
            val context = LocalContext.current
            Button(onClick = {
                // 從文件加載JSON數據
                val jsonString = loadJsonFromInternalStorage(context, "accounting_data.json")
                if (jsonString != null) {
                    // 解析 JSON 數據
                    val accountingItems = parseJsonToAccountingItems(jsonString)

                    // 進行輸出
                    for (item in accountingItems) {
                        Log.d(
                            "AccountingItem",
                            "ID: ${item.id}, Date: ${item.date}, Title: ${item.title}, Amount: ${item.amount}, Details: ${item.details}"
                        )
                    }
                    Log.d(
                        "AccountingItem",
                        "----------------------------------------------------------------------------"
                    )
                } else {
                    Log.d("AccountingItem", "Failed to load JSON data")
                }
            }) {
                // 按钮内容
            }

            Button(onClick = {
                // 從文件加載
                val jsonString = loadJsonFromInternalStorage(context, "restaurants_data.json")
                if (jsonString != null) {
                    // 解析 JSON 數據
                    val accountingItems = parseJsonToRestaurantItems(jsonString)

                    // 將數據
                    for (item in accountingItems) {
                        Log.d(
                            "RestaurantItem",
                            "ID: ${item.id}, Date: ${item.address}, Title: ${item.dishes}, Amount: ${item.name}"
                        )
                    }
                    Log.d(
                        "RestaurantItem",
                        "----------------------------------------------------------------------------"
                    )
                } else {
                    Log.d("RestaurantItem", "Failed to load JSON data")
                }
            }) {
                // 按钮内容
                Text(text = "RestaurantItem")
            }

            Button(onClick = {
                try {
                    val file = File(context.filesDir, "restaurants_data.json")
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    Log.e("deleteJsonFile", "Error deleting file", e)
                }
            }) {
                // 按钮内容
                Text(text = "RestaurantItem 刪除")
            }
        }
    }
}