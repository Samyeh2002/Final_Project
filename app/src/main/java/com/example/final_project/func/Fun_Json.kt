package com.example.final_project.func

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Composable
//import com.example.final_project.page.AccountingItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

// 從內部存儲讀取 JSON 文件
fun loadJsonFromInternalStorage(context: Context, fileName: String): String? {
    return try {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val reader = FileReader(file)
            val content = reader.readText()
            reader.close()
            content
        } else {
            null
        }
    } catch (e: Exception) {
        // 显示错误信息或者记录日志
        Log.e("loadJsonFromInternalStorage", "Error loading data", e)
        null
    }
}

// 向內部存儲寫入 JSON 文件
fun writeJsonToInternalStorage(context: Context, fileName: String, data: Any) {
    val file = File(context.filesDir, fileName)
    val writer = FileWriter(file)
    val gson = Gson()
    val jsonString = gson.toJson(data)
    writer.write(jsonString)
    writer.close()
}

// 解析 JSON 文件
fun parseJsonToAccountingItems(jsonString: String): List<AccountingItem> {
    val gson = Gson()
    val listType = object : TypeToken<List<AccountingItem>>() {}.type
    return gson.fromJson(jsonString, listType)
}

// json結構_記帳
data class AccountingItem(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val title: String,
    val amount: String,
    val details: String
)

// 解析 JSON 文件為 RestaurantItem 列表
fun parseJsonToRestaurantItems(jsonString: String): List<RestaurantItem> {
    val gson = Gson()
    val listType = object : TypeToken<List<RestaurantItem>>() {}.type
    return gson.fromJson(jsonString, listType)
}

// 餐廳資料類
data class RestaurantItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val time: List<List<Int>>,
    val dishes: List<DishItem>
)

// 餐點資料類
data class DishItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val price: Int
)

// 菜單生成
fun saveRecommendedDishes(context: Context, dishes: List<DishItem>) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("recommended_dishes", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(dishes)
    editor.putString("dishes", json)
    editor.putString("date", getCurrentDateString())
    editor.apply()
}

fun loadRecommendedDishes(context: Context): Pair<List<DishItem>?, String?> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("recommended_dishes", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("dishes", null)
    val date = sharedPreferences.getString("date", null)
    val dishesType = object : TypeToken<List<DishItem>>() {}.type
    val dishes: List<DishItem>? = gson.fromJson(json, dishesType)
    return Pair(dishes, date)
}

fun getCurrentDateString(): String {
    val current = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return current.format(formatter)
}

