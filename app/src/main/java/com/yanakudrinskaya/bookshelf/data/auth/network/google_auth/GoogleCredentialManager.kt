package com.yanakudrinskaya.bookshelf.data.auth.network.google_auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models.GoogleSignInResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleCredentialManager(private val context: Context) {

    private val credentialManager = CredentialManager.Companion.create(context)

    suspend fun signInWithGoogle(activity: Activity): GoogleSignInResult =
        withContext(Dispatchers.IO) {
            try {
                val clientId = context.getString(R.string.default_web_client_id)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false)
                    .setNonce(generateNonce())
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val response = credentialManager.getCredential(
                    request = request,
                    context = activity
                )

                val idToken = extractIdTokenFromResponse(response)
                if (idToken != null) {
                    GoogleSignInResult.Success(idToken)
                } else {
                    GoogleSignInResult.Error("Failed to extract ID token")
                }
            } catch (e: GetCredentialException) {
                GoogleSignInResult.Error(e.message ?: "Google sign in failed")
            } catch (e: Exception) {
                GoogleSignInResult.Error(e.message ?: "Unknown error during Google sign in")
            }
        }

    private fun extractIdTokenFromResponse(response: GetCredentialResponse): String? {
        return try {
            when (val credential = response.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.Companion
                            .createFrom(credential.data)
                        googleIdTokenCredential.idToken
                    } else {
                        null
                    }
                }
                else -> {
                    null
                }
            }
        } catch (e: GoogleIdTokenParsingException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun generateNonce(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..32)
            .map { allowedChars.random() }
            .joinToString("")
    }
}