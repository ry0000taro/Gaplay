package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

import androidx.navigation.NavController
import com.example.ry0000tarodojo2026.Routes
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel

@Composable
fun ScanScreen(viewModel: MainViewModel, navController: NavController) {
    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("カップ麺のデータを検索中...")
        } else if (scannedBarcode == null) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Button(
                onClick = {
                    errorMessage = null
                    val options = GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
                        .enableAutoZoom()
                        .build()
                    val scanner = GmsBarcodeScanning.getClient(context, options)
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            val code = barcode.rawValue
                            if (code != null) {
                                isLoading = true
                                viewModel.searchNoodle(
                                    janCode = code,
                                    onSuccess = {
                                        isLoading = false
                                        navController.navigate(Routes.SEARCH_LIST) {
                                            popUpTo(Routes.SEARCH_LIST) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    onError = { error ->
                                        isLoading = false
                                        scannedBarcode = code
                                        errorMessage = error
                                    }
                                )
                            } else {
                                errorMessage = "バーコードの値を読み取れませんでした"
                            }
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                            errorMessage = "スキャンに失敗したか、キャンセルされました"
                        }
                },
                modifier = Modifier.size(200.dp, 80.dp)
            ) {
                Text("SCAN", style = MaterialTheme.typography.headlineMedium)
            }
        } else {
            Text(
                text = "読み取り結果",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = scannedBarcode ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
            Button(onClick = {
                scannedBarcode = null
                errorMessage = null
            }) {
                Text("戻る（再スキャン）")
            }
        }
    }
}
