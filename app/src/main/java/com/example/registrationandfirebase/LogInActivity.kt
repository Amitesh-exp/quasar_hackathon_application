package com.example.registrationandfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.registrationandfirebase.databinding.ActivityCreateAccountBinding
import com.example.registrationandfirebase.databinding.ActivityLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LogInActivity : AppCompatActivity() {

    private val binding : ActivityLogInBinding by lazy {
        ActivityLogInBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, PostLoginActivity::class.java))
            finish()
        }
    }

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
        val launcher = registerForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            GoogleSignInUtils.doGoogleSignIn(context = this,
                scope = lifecycleScope,
                launcher = null,
                login = {
                    startActivity(Intent(this,PostLoginActivity::class.java))
                })
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPasswordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,"Input fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else {
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,PostLoginActivity::class.java))
                            finish()
                        }
                        else {
                            Toast.makeText(this,"Login Failed: ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnGoogleLogin.setOnClickListener {
            GoogleSignInUtils.doGoogleSignIn(context = this,
                scope = lifecycleScope,
                launcher = launcher,
                login = {
                    startActivity(Intent(this,PostLoginActivity::class.java))
                })
        }
    }
}