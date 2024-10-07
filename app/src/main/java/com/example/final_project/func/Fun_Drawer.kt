package com.example.final_project.func

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Fun_Drawer() {
    val currentDestination = GlobalVariables.currentDestination()
    Log.e("test_123","Hello")

    // 尚未定義
    val items = listOf(
        NavigationItem(
            title = "首頁",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "Main"
        ),
        NavigationItem(
            title = "記帳",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            route = "Accounting"
        ),
        NavigationItem(
            title = "菜餚",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = "Dishes"
        )
    )

    // 抽屜
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Scaffold (
                    topBar = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                GlobalVariables.scope.launch {
                                    GlobalVariables.drawerState.close()
                                }
                                GlobalVariables.gesturesEnabled = false
                                GlobalVariables.navController.navigate("Role")
                            }
                            .background(Color(0xFF655D8A))
                        ) {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                //
                                Spacer(modifier = Modifier.size(20.dp))

                                // 中間標題
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    Text(
                                        text = "App Title",
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 8.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                                }

                                // 頂測右邊欄位
                                Row {
                                    IconButton(
                                        onClick = {
                                            GlobalVariables.scope.launch {
                                                GlobalVariables.drawerState.close()
                                            }
                                            GlobalVariables.gesturesEnabled = false
                                            GlobalVariables.navController.navigate("Setting")
                                        }
                                    ) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                ){ paddingValues ->
                    Column (
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ){
                        Spacer(modifier = Modifier.height(16.dp))
                        items.forEachIndexed{index, item ->
                            NavigationDrawerItem(
                                label = {
                                    Text(text = item.title)
                                },
                                selected = currentDestination==item.route,
                                onClick = {
                                    GlobalVariables.selectedItemIndex = index
                                    GlobalVariables.scope.launch {
                                        GlobalVariables.drawerState.close()
                                    }
                                    GlobalVariables.navController.navigate(item.route) {}//{popUpTo(0)}
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (index == GlobalVariables.selectedItemIndex) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                badge = {
                                    item.badgeCount?.let {
                                        Text(text = item.badgeCount.toString())
                                    }
                                },
//                                colors = NavigationDrawerItemDefaults.colors(
//                                    selectedContainerColor = Color.Yellow,
//                                    unselectedContainerColor = Color.Red
//                                ),
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            }
        },
        drawerState = GlobalVariables.drawerState,
        gesturesEnabled = GlobalVariables.gesturesEnabled
    ) {
        Fun_Navigation(GlobalVariables.navController)
    }
}

// 結構
data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val badgeCount: Int? = null
)