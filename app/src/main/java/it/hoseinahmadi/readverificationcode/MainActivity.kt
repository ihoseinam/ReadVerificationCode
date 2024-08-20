package it.hoseinahmadi.readverificationcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import it.hoseinahmadi.readverificationcode.ui.theme.ReadVerificationCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadVerificationCodeTheme {
                val requestPermission =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                        Toast.makeText(this, "permission is ok", Toast.LENGTH_SHORT).show()
                    }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            if (checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermission.launch(Manifest.permission.READ_SMS)
                            }
                            if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermission.launch(Manifest.permission.RECEIVE_SMS)
                            }

                        }) {
                            Text(text = "permission")
                        }
                        var code by remember { mutableStateOf("") }
                        OutlinedTextField(value = code, onValueChange = { code = it })
                        val filter = IntentFilter()
                        filter.addAction("service.to.activity.transfer")
                        val updateUiReceiver = object : BroadcastReceiver() {
                            override fun onReceive(context: Context?, p1: Intent?) {
                                val isCode = p1?.getStringExtra("code")
                                code = isCode.toString()
                            }

                        }
                       registerReceiver(updateUiReceiver, filter, RECEIVER_EXPORTED)

                    }
                }
            }
        }
    }
}