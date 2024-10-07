package com.example.final_project.func

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.example.final_project.MainActivity
import kotlinx.coroutines.launch

@Composable
fun Fun_Back(test:MainActivity) {

    val currentDestination = GlobalVariables.currentDestination()

    BackHandler {
        when {
            GlobalVariables.drawerState.isOpen -> {
                Log.e("test_123", "11111112")
                GlobalVariables.scope.launch { GlobalVariables.drawerState.close() }
            }

            currentDestination == "Role" -> {
                Log.e("test_123", "111111132221")
                GlobalVariables.navController.popBackStack()
                GlobalVariables.scope.launch { GlobalVariables.drawerState.open() }
                GlobalVariables.gesturesEnabled = true
            }

            currentDestination == "Setting" -> {
                Log.e("test_123", "1111111322212")
                GlobalVariables.navController.popBackStack()
                GlobalVariables.scope.launch { GlobalVariables.drawerState.open() }
                GlobalVariables.gesturesEnabled = true
            }

            GlobalVariables.drawerState.isClosed && GlobalVariables.navController.previousBackStackEntry == null && GlobalVariables.selectedItemIndex == 0 -> {
                Log.e("test_123", "11111113")
                test.finish()
            }

            GlobalVariables.drawerState.isClosed -> {
                Log.e("test_123", "11111114")
                GlobalVariables.selectedItemIndex = 0  // 更新 selectedItemIndex 觸發重構
                GlobalVariables.navController.navigate("Main") {
                    popUpTo(0)
                }
            }

            else -> {
                Log.e("test_123", "11111115")
            }
        }
    }
}