package com.example.final_project.func

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.final_project.page.AddMenu
import com.example.final_project.page.Add_Accounting
import com.example.final_project.page.Add_NewList
import com.example.final_project.page.Add_NewRestaurant
import com.example.final_project.page.ModifyData
import com.example.final_project.page.Page_Accounting
import com.example.final_project.page.Page_Dishes
import com.example.final_project.page.Page_Main
import com.example.final_project.page.Page_Role
import com.example.final_project.page.Page_Setting


@Composable
fun Fun_Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Main.route) {
        composable (Screen.Main.route){ Page_Main() }
        composable(Screen.Accounting.route) { Page_Accounting() }
        composable(Screen.Dishes.route) { Page_Dishes() }
        composable(Screen.Role.route) { Page_Role() }
        composable(Screen.Setting.route) { Page_Setting() }
        composable(Screen.AddAccounting.route) { Add_Accounting() }
        composable(Screen.AddNewList.route) { Add_NewList() }
        composable(Screen.Add_NewRestaurant.route) {Add_NewRestaurant()}
        composable(Screen.ModifyData.route + "/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            val context = LocalContext.current
            val accountingItem = itemId?.let {
                loadJsonFromInternalStorage(context, "accounting_data.json")
                    ?.let { parseJsonToAccountingItems(it) }
                    ?.find { it.id == itemId }
            }

            if (accountingItem != null) {
                ModifyData(accountingItem)
            }
        }
    }
}

// 導航路由 避免命名錯誤
sealed class Screen(val route: String) {
    data object Main : Screen("Main")
    data object Accounting : Screen("Accounting")
    data object Dishes : Screen("Dishes")
    data object Role : Screen("Role")
    data object Setting : Screen("Setting")
    data object AddAccounting : Screen("Add_Accounting")
    data object ModifyData : Screen("ModifyData")
    data object AddNewList : Screen("Add_NewList")
    data object Add_NewRestaurant : Screen("Add_NewRestaurant")
}