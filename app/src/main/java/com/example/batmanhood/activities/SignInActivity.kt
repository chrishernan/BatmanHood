package com.example.batmanhood.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Gravity
import android.widget.Toast
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.R
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.models.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : BaseActivity() {
    //must do field injection for classes
    @Inject lateinit var stockAndIndexApiHelper: StockAndIndexApiHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setupActionBar()

        btn_sign_in.setOnClickListener {
            signInRegisteredUser(stockAndIndexApiHelper)
        }
    }

    /**
     * Sets up ActionBar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }


    /**
     * Function to Sign in a User that has been registered for the app
     */
    private fun signInRegisteredUser(stockAndIndexApiHelper: StockAndIndexApiHelper) {
        // Here we get the text from editText and trim the space
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateEmail(email) && validatePassword(password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FirestoreClass(stockAndIndexApiHelper).loadUserData(this@SignInActivity)
                    } else {
                        hideProgressDialog()
                        var toast : Toast = Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    }
                }
        }
    }

    //Validates whether the email address is not empty or has the correct format
    private fun validateEmail(email: String) : Boolean{
        return if(TextUtils.isEmpty(email)){
            showErrorToast("Please enter an email",this@SignInActivity)
            false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showErrorToast("Please enter a correctly formatted email address",this@SignInActivity)
            false
        } else {
            true
        }
    }

    //Validates the password is not empty or fits our desired parameters
    private fun validatePassword(password: String): Boolean {
        val pattern: String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=()-])(?=\\S+\$).{8,20}\$"
        val passwordPattern = Pattern.compile(pattern)
        return if(TextUtils.isEmpty(password)){
            showErrorToast("Please enter a password",this@SignInActivity)
            false
        } else if(!passwordPattern.matcher(password).matches()){
            showErrorToast("Please enter a password with at least 1 digit," +
                    "one lowercase letter, one uppercase letter, and one special character" +
            "Password must also have a length between 8 and 20 characters",this@SignInActivity)
            false
        } else {
            true
        }
    }

    /**
     * This function retrieves user information from firebase database after the user
     * has been authenticated
     */
    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        this.finish()
    }

    override fun onStop() {
        super.onStop()
    }
}