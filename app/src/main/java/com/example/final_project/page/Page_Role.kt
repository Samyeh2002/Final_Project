package com.example.final_project.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.unit.dp
import com.example.final_project.func.GlobalVariables
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page_Role() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("切換角色") },
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
        // 可滾動的內容區域
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // 添加大量的項目以使其可滾動

        }
    }
}