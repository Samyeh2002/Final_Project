package com.example.final_project.func

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableIntStateOf
import androidx.navigation.NavHostController
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

object GlobalVariables {


    @SuppressLint("StaticFieldLeak")
    lateinit var navController: NavHostController
    lateinit var drawerState: androidx.compose.material3.DrawerState
    lateinit var scope: kotlinx.coroutines.CoroutineScope
    var selectedItemIndex by mutableIntStateOf(0)
    var gesturesEnabled by mutableStateOf(true)
    var nowNavigation by mutableStateOf("")


    @Composable
    fun Initialize() {
        navController = rememberNavController()
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        scope = rememberCoroutineScope()
        selectedItemIndex = rememberSaveable { mutableIntStateOf(0) }.intValue
        gesturesEnabled = rememberSaveable { mutableStateOf(true).value }
        nowNavigation = rememberSaveable { mutableStateOf("").value}
    }

    @Composable
    fun currentDestination(): String? {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        return currentBackStackEntry?.destination?.route
    }
}