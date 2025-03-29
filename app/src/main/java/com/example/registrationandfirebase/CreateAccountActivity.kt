package com.example.registrationandfirebase

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.registrationandfirebase.databinding.ActivityCreateAccountBinding
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    private val binding : ActivityCreateAccountBinding by lazy {
        ActivityCreateAccountBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        val text = "By creating an account or signing you agree to our Terms and Conditions"
        val spannable = SpannableString(text)

        val start = text.indexOf("Terms and Conditions")
        val end = start + "Terms and Conditions".length

        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val typeface = ResourcesCompat.getFont(this, R.font.inter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            spannable.setSpan(typeface?.let { TypefaceSpan(it) }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tAndCText.text = spannable

        binding.btnCreateAccount.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val rePassword = binding.rePasswordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                Toast.makeText(this,"Input fields cannot be empty",Toast.LENGTH_SHORT).show()
            }
            else if (!password.equals(rePassword)) {
                Toast.makeText(this,"Password and Confirmation Password must be same",Toast.LENGTH_SHORT).show()
            }
            else {
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,LogInActivity::class.java))
                        } else {
                            Toast.makeText(this, "Registration Failed : ${task.exception?.message})", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}