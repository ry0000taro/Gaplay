package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ry0000tarodojo2026.ui.theme.Ry0000tarodojo2026Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ry0000tarodojo2026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HomeScreen()
                    }
                }

            }
        }
    }
}



@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(2f) // 2/5の比率
                .fillMaxWidth()
                .background(Color.LightGray), // 地図画像が用意できるまで灰色
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Text(text = "現在地: 東京都渋谷区...")
            }
        }

        // 下部 2/3: 黒い領域
        Box(
            modifier = Modifier
                .weight(5f) // 2/5の比率
                .fillMaxWidth()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "コンテンツエリア", color = Color.White)
        }
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ry0000tarodojo2026Theme {
        HomeScreen()
    }
}