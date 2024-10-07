package com.example.final_project.func

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun YearMonthPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onDismissRequest: () -> Unit,
    onYearMonthSelected: (Int, Int) -> Unit
) {
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
                var selectedYear by remember { mutableStateOf(initialYear) }
                var selectedMonth by remember { mutableStateOf(initialMonth) }
                var yearchoose by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear -= 1 }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Year")
                    }
                    Text(
                        text = selectedYear.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { yearchoose = true }
                    )
                    IconButton(onClick = { selectedYear += 1 }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Year")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                MonthGrid(selectedMonth = selectedMonth, onMonthSelected = { selectedMonth = it })

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        onYearMonthSelected(selectedYear, selectedMonth)
                    }) {
                        Text(text = "確認")
                    }
                }

                if (yearchoose) {
                    YearPickerDialog(
                        initialYear = selectedYear,
                        onDismissRequest = { yearchoose = false },
                        onYearSelected = { selectedYear = it },
                        onConfirmSelected = { yearchoose = false }
                    )
                }
            }
        }
    }
}

@Composable
fun MonthGrid(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    val months = listOf(
        "1月", "2月", "3月",
        "4月", "5月", "6月",
        "7月", "8月", "9月",
        "10月", "11月", "12月"
    )

    Column {
        for (i in 0 until 4) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in 0 until 3) {
                    val monthIndex = i * 3 + j + 1
                    Text(
                        text = months[monthIndex - 1],
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp)
                            .background(
                                if (monthIndex == selectedMonth) Color.Blue else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onMonthSelected(monthIndex) }
                            .wrapContentSize(Alignment.Center),
                        color = if (monthIndex == selectedMonth) Color.White else Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun YearPickerDialog(
    initialYear: Int,
    onDismissRequest: () -> Unit,
    onYearSelected: (Int) -> Unit,
    onConfirmSelected: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            val years = (1900..2100).toList()
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = years.indexOf(initialYear))
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                LazyColumn(state = listState) {
                    items(years) { year ->
                        Text(
                            text = year.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    onYearSelected(year)
                                    onDismissRequest()
                                },
                            fontSize = 20.sp,
                            fontWeight = if (year == initialYear) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        onYearSelected(initialYear)
                        onConfirmSelected()
                    }) {
                        Text(text = "確認")
                    }
                }
            }
        }
    }
}