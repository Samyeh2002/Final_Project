package com.example.final_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.final_project.func.Fun_Back
import com.example.final_project.ui.theme.Final_ProjectTheme
import com.example.final_project.func.Fun_Drawer
import com.example.final_project.func.GlobalVariables
import com.example.final_project.func.GlobalVariables.drawerState
import com.example.final_project.func.GlobalVariables.navController
import com.example.final_project.func.GlobalVariables.scope
import com.example.final_project.func.GlobalVariables.selectedItemIndex
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Final_ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    //color = MaterialTheme.colorScheme.background
                ) {
                    GlobalVariables.Initialize()

                    Fun_Back(this)

                    Fun_Drawer()
                }
            }
        }
    }
}









