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

@Composable
fun ScanScreen() {
    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (scannedBarcode == null) {
            Button(
                onClick = {
                    val options = GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
                        .enableAutoZoom()
                        .build()
                    val scanner = GmsBarcodeScanning.getClient(context, options)
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            scannedBarcode = barcode.rawValue
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
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
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(onClick = { scannedBarcode = null }) {
                Text("戻る（再スキャン）")
            }
        }
    }
}
