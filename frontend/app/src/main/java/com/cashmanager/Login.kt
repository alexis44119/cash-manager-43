package com.cashmanager

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import khttp.get
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



    login_btn.setOnClickListener {
            // val message: String = textInputEditText.text.toString()
            //  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
     //   val r = get("https://api.github.com/events")
      //  print(r)
        val intent = Intent(this, ArticleActivity ::class.java)
            startActivity(intent)
        }


    }
}