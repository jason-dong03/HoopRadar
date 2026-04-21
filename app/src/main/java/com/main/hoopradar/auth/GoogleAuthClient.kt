package com.main.hoopradar.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthClient( // handles google sign-in and firebase authentication
    private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val credentialManager = CredentialManager.create(context) // used to request saved google accts from device

    suspend fun signIn(webClientId: String): Result<Unit> { // starts google sign-in process
        return try {
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build() // configure google sign-in option using web client id

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build() // build request for credentials

            val result = credentialManager.getCredential( // launch google acct picker / sign in screen
                context = context,
                request = request
            )

            val credential = result.credential // get returned credential

            if ( // check if returned credential is google id token
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data) // convert raw cred data into google token object

                val firebaseCredential = GoogleAuthProvider.getCredential( // create firebase cred using google token
                    googleIdTokenCredential.idToken,
                    null
                )

                firebaseAuth.signInWithCredential(firebaseCredential).await() // sign in user with firebase
                Result.success(Unit) // return success if login worked
            } else { // wrong cred type returned
                Result.failure(IllegalStateException("Unexpected credential type"))
            }
        } catch (e: Exception) { // print error and return failure
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun signOut() { // signs current user out of firebase
        firebaseAuth.signOut()
    }
}