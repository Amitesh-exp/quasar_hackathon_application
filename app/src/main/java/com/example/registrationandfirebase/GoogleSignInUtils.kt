package com.example.registrationandfirebase

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleSignInUtils {
    companion object {
        fun doGoogleSignIn (context: Context,
                            scope: CoroutineScope,
                            launcher: ActivityResultLauncher<Intent>?,
                            login: () -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredetialOptions(context))
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(context,request)
                    when(result.credential) {
                        is CustomCredential ->{
                            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                val googleTokenId = googleIdTokenCredential.idToken
                                val authCredential = GoogleAuthProvider.getCredential(googleTokenId,null)
                                val user = Firebase.auth.signInWithCredential(authCredential).await().user
                                user?.let {
                                    if (it.isAnonymous.not()) {
                                        login.invoke()
                                    }
                                }
                            }
                        }
                        else -> {

                        }
                    }
                } catch (e:NoCredentialException) {
                    launcher?.launch(getIntent())
                } catch (e:GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }

        fun getIntent():Intent {
            return Intent (android.provider.Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        fun getCredetialOptions(context:Context):CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

    }
}