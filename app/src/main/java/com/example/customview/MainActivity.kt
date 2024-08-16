package com.example.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.StyleRes

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5_style)
        //setContentView(binding.root)

        //プログラムでstyleを設定した場合gravityは正しく反映されるかの検証
        val textView = findViewById<TextView>(R.id.textView53)
        applyStyleToTextView(textView, R.style.addGravity6)
    }

    fun applyStyleToTextView(textView: TextView, @StyleRes styleRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(styleRes)
            Log.d("Test1", "applyStyleToTextView( styleRes: $styleRes")
        }
    }
}