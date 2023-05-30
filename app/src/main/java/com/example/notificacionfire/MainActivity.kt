package com.example.notificacionfire

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {
    companion object {
        var token: String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Recupera el valor de la variable global desde las preferencias compartidas
        val prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        token = prefs.getString("miVariableGlobal", "") ?: ""

        //encontramos el boton
        val btn_env = findViewById<Button>(R.id.btn_enviar)
        //RECUPERAMOS LOS TEXTOS
        val titulo = findViewById<EditText>(R.id.titulo)
        val cuerpo = findViewById<EditText>(R.id.cuerpo)
        //cuando de click
        btn_env.setOnClickListener{
            enviarTokenAServidor(token, titulo.text.toString(), cuerpo.text.toString())
        }
    }
    private fun enviarTokenAServidor(token: String, titulo: String, cuerpo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val requestBody = FormBody.Builder()
                .add("token", token)
                .add("titulo", titulo)
                .add("cuerpo", cuerpo)
                .build()
            val request = Request.Builder()
                .url("http://192.168.1.11:3000/registerToken")
                .post(requestBody)
                .build()
            client.newCall(request).execute()
        }
    }


    override fun onPause() {
        super.onPause()

        // Guarda el valor de la variable global en las preferencias compartidas
        val prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("miVariableGlobal", token).apply()
    }
}