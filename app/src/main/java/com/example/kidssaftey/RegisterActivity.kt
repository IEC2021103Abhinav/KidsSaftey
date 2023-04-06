package com.example.kidssaftey

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.kidssaftey.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class RegisterActivity : AppCompatActivity() {
    companion object
    {
        lateinit var auth: FirebaseAuth

    }
    private lateinit var mProgressDialog: Dialog
    //buttons
    var bRegister: Button?=null
    //edit text
    var etFirstName: EditText?=null
    private var etLastName: EditText?=null
    private var etEmail: EditText?=null
    private var etPassword: EditText?=null
    private var etConfPassword: EditText?=null
    private var cbTermConditions: CheckBox?=null
    var backbtn: ImageView?=null
    var isAllEditTextCheck=false

    private lateinit var textlogin : TextView
    private lateinit var mGoogleSignInClient:GoogleSignInClient
    private var Req_Code=102

    private lateinit var binding: ActivityRegisterBinding
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding= ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth= FirebaseAuth.getInstance()
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN

        etEmail=binding.emailEt
        etFirstName=binding.firstName
        etLastName=binding.lastName
        etPassword=binding.passwordEt
        etConfPassword=binding.confPasswordEt
        cbTermConditions=binding.termsCondCbox
        bRegister=binding.registerBtn
        backbtn=binding.backImage

        backbtn!!.setOnClickListener{
            val intent= Intent(this@RegisterActivity,LoginActivity::class.java)
            startActivity(intent)

        }

        bRegister?.setOnClickListener { registerUser() }

        textlogin = findViewById(R.id.login)
        textlogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()

         mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


    }
    private fun registerUser()
    {
        val email= etEmail?.text.toString()
        val password=etPassword?.text.toString()
        val name=etFirstName?.text.toString()+" " +etLastName?.text.toString()
        isAllEditTextCheck=CheckAllEditText()
        if(isAllEditTextCheck){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->
                    hideProgressDialog()
                    if(task.isSuccessful)
                    {
                        val user=auth.currentUser
                        val updates= UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build()
                        user!!.updateProfile(updates)
                        Toast.makeText(this,"Registration Succeeded", Toast.LENGTH_LONG).show()
                        val intent= Intent(this@RegisterActivity,LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Log.i("TAG",task.exception.toString())
                        Toast.makeText(this,"Registration Failed", Toast.LENGTH_LONG).show()
                    }
                }

        }
        else
        {
            Toast.makeText(this,"Registration is not successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmail(text: Editable?): Boolean {
        val email: CharSequence = text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun CheckAllEditText(): Boolean {
        if(etFirstName?.length()  ==0)
        {
            etFirstName?.error = "First Name cannot be blank"
            return false;
        }
        if(etLastName?.length()==0)
        {
            etLastName?.error="Last Name cannot be blank"
            return false;
        }
        if(etEmail?.length()==0)
        {
            etEmail?.error="Email ID cannot be blank"
            return false;
        }
        if(!isEmail(etEmail?.editableText))
        {
            etEmail?.error="The email address is invalid"
            return false
        }
        if (etPassword!!.length() == 0) {
            etPassword!!.error = "Password cannot be blank"
            return false
        } else if (etPassword!!.length() < 8) {
            etPassword!!.error = "Password must be of minimum 8 characters"
            return false
        }
        if(etConfPassword!!.length()==0)
        {
            etConfPassword!!.error="Confirm your password first."
            return false
        }
        if(!etConfPassword?.equals(etConfPassword)!!)
        {
            etConfPassword!!.error="The passwords do not match."
            return false
        }
        if (cbTermConditions?.isChecked == true) {
            (cbTermConditions!!.text.toString() + " ")
        } else {
            (cbTermConditions!!.text.toString() + "UnChecked")
            Toast.makeText(this@RegisterActivity,"Check the terms and conditions", Toast.LENGTH_SHORT).show()
            return false
        }
        return true

    }
    private fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun SignIn(view: View) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    // onActivityResult() function : this is where
    // we provide the task and data for the Google Account
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){
                if(it.isSuccessful)
                {
                    SharedPref.putBoolean(PrefConst.IS_USER_LOGGED_IN,true)
                    startActivity(Intent(this,MainActivity::class.java))
                }
                else
                {
                    Log.w("Fire89", "signInWithCredential:failure", it.exception)
                }
            }

    }

}