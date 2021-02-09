package com.example.batmanhood.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.R
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {
    @Inject lateinit var stockAndIndexApiHelper : StockAndIndexApiHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupActionBar()

        // Click event for sign-up button.
        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateName(name) && validateEmail(email) && validatePassword(password)) {
            // Show the progress dialog.

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                // If the registration is successfully done
                                if (task.isSuccessful) {
                                    // Firebase registered user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!
                                    // Registered Email
                                    val registeredEmail = firebaseUser.email!!
                                    val user = User(
                                            firebaseUser.uid, name, registeredEmail
                                    )
                                    // call the registerUser function of FirestoreClass to make an entry in the database.
                                    FirestoreClass(stockAndIndexApiHelper).registerUser(this@RegisterActivity, user)
                                } else {
                                    hideProgressDialog()
                                    Toast.makeText(
                                            this@RegisterActivity,
                                            task.exception!!.message,
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
        }
    }



    //Validates whether the email address is not empty or has the correct format
    private fun validateEmail(email: String) : Boolean{
        return if(TextUtils.isEmpty(email)){
            showErrorSnackBar("Please enter an email")
            false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showErrorSnackBar("Please enter a correctly formatted email address")
            false
        } else {
            true
        }
    }

    //Validates the password is not empty or fits our desired parameters
    private fun validatePassword(password: String): Boolean {
        val pattern: String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=()-])(?=\\S+\$).{8,20}\$"
        var passwordPattern = Pattern.compile(pattern)
        return if(TextUtils.isEmpty(password)){
            showErrorSnackBar("Please enter a password")
            false
        } else if(!passwordPattern.matcher(password).matches()){
            showErrorSnackBar("Please enter a password with at least 1 digit, one lowercase letter, one uppercase letter, and one special character Password must also have a length between 8 and 20 characters")
            false
        } else {
            true
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateName(name: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {

        Toast.makeText(
                this@RegisterActivity,
                "You have successfully registered.",
                Toast.LENGTH_SHORT
        ).show()

        // Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
       //-------------> FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        this.finish()
    }

}