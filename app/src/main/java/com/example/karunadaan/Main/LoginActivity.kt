package com.example.karunadaan.Main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.example.karunadaan.data.Resource
import com.example.karunadaan.R
import com.example.karunadaan.entity.User
import com.example.karunadaan.repository.AuthRepositoryImpl
import com.example.karunadaan.viewmodel.AuthViewModel
import com.example.karunadaan.viewmodel.AuthViewModelFactory
import com.example.karunadaan.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


//@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {


    private val userViewModel: UserViewModel by viewModels()

    lateinit var changeToSignUp:TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var forgetPassword: TextView
    private lateinit var cardViewUsername:CardView
    private lateinit var cardViewConfirmPassword:CardView
    private lateinit var catergoryOfUser:CardView
    private lateinit var userCategory: RadioGroup
    private lateinit var loginButton: Button
    private lateinit var progessBar:ProgressBar
    private lateinit var db:FirebaseFirestore
    //private lateinit var googleLoginButton: Button
    //private lateinit var signupText: TextView
    private lateinit var mAuth: FirebaseAuth
    //private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private var loginFlag=true;
    private lateinit var authViewModel: AuthViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()


        emailEditText = findViewById(R.id.emailEditText)
        userNameEditText=findViewById(R.id.userNameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText =findViewById(R.id.confirmPasswordEditText)
        userCategory=findViewById(R.id.radioGroupUserType)
        forgetPassword=findViewById(R.id.forgetPassword)
        cardViewUsername=findViewById(R.id.cardViewUsername)
        cardViewConfirmPassword=findViewById(R.id.cardViewConfirmPassword)
        catergoryOfUser=findViewById(R.id.catergoryOfUser)
        loginButton = findViewById(R.id.loginButton)
        progessBar=findViewById(R.id.loginProgessBar)
       // googleLoginButton = findViewById(R.id.googleLoginButton)
        //signupText = findViewById(R.id.signupText)
        changeToSignUp = findViewById(R.id.changeToSignUp)
        db= FirebaseFirestore.getInstance()

        mAuth = FirebaseAuth.getInstance()

        val authRepo = AuthRepositoryImpl.getInstance(FirebaseAuth.getInstance())
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepo))[AuthViewModel::class.java]
        //authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        loginButton.setOnClickListener {
            if( loginFlag) loginUser()
            else signUpUser()
        }
        changeToSignUp.setOnClickListener {
            if( loginFlag) changeToSignUp();
            else changeToLogin();
        }
        // update layout when keyboard opens
        val rootView = findViewById<View>(R.id.mainLogin)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            var minUi=findViewById<View>(R.id.mainLogin)
            if (keypadHeight > screenHeight * 0.15) { // Keyboard is open
                minUi.animate().translationY(-150f).setDuration(200).start() // Move up by 150 pixels
            } else {
                minUi.translationY = 0f // Reset when keyboard closes
            }
        }
    }
    private fun changeToLogin (){
        loginFlag=true;
        loginButton.text="Login"
        changeToSignUp.text="New User?"
        cardViewUsername.visibility= View.GONE
        cardViewConfirmPassword.visibility= View.GONE
        catergoryOfUser.visibility=View.GONE
        forgetPassword.visibility=View.VISIBLE
    }
    private fun changeToSignUp (){
        loginFlag=false;
        loginButton.text="Sign Up"
        changeToSignUp.text="Existing User"
        forgetPassword.visibility=View.GONE
        cardViewUsername.visibility= View.VISIBLE
        cardViewConfirmPassword.visibility= View.VISIBLE
        catergoryOfUser.visibility= View.VISIBLE
    }
    private fun loginUser() {
        var email = emailEditText.text.toString()
        var password =passwordEditText.text.toString()
        if( email.equals("") ){
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please enter email id ",Snackbar.LENGTH_SHORT).show()
        } else if( password.equals("") ){
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please enter password ",Snackbar.LENGTH_SHORT).show()
        }
        else{

            val loginFlow = authViewModel.loginFlow
            authViewModel.login(email, password)
            authViewModel.loginFlow.observe(this) { resource ->
                when (resource) {
                    is Resource.Loading -> progessBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("is_logged_in", true)  // Store login status
                        editor.apply()
                        progessBar.visibility = View.GONE
                        startActivity(Intent(this, MainActivity2::class.java))
                        finish()
                    }
                    is Resource.Failure -> {
                        progessBar.visibility = View.GONE
                        Toast.makeText(this, resource.exception.message, Toast.LENGTH_LONG).show()
                    }

                    null -> {
                        Toast.makeText(this, "Null output", Toast.LENGTH_LONG).show()

                    }
                }
            }

//            { success, errorMessage ->
////                progressBar.visibility = ProgressBar.GONE
//                if (!success) {
//                    Toast.makeText(this, "Login Failed: $errorMessage", Toast.LENGTH_SHORT).show()
//                } else {
//                    var intent=Intent(this,MainActivity2::class.java)
//                    startActivity(intent)
//                }
//            }
        }
    }

    private fun signUpUser() {
        var email = emailEditText.text.toString()
        var password =passwordEditText.text.toString()
        var userName= userNameEditText.text.toString()
        var confirmPassword=confirmPasswordEditText.text.toString()
        var selectedId=userCategory.checkedRadioButtonId
        if( email.equals("") ){
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please enter email id ",Snackbar.LENGTH_SHORT).show()
        } else if( password.equals("") ){
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please enter password ",Snackbar.LENGTH_SHORT).show()
        } else if( userName.equals("")) {
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please enter User Name ",Snackbar.LENGTH_SHORT).show()
        } else if( !confirmPassword.equals(password) ) {
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please check password ",Snackbar.LENGTH_SHORT).show()
        }  else if( selectedId==-1 ) {
            Snackbar.make(this.findViewById(R.id.mainLogin),"Please select category ",Snackbar.LENGTH_SHORT).show()
        }
        else{
            val selectedRadioButton: RadioButton = findViewById(selectedId)
            val role = selectedRadioButton.text.toString()
            val signupFlow = authViewModel.signUpFlow
            authViewModel.signup(userName, email, password)
            authViewModel.signUpFlow.observe(this) { resource ->
                when (resource) {
                    is Resource.Loading -> progessBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                            val user = FirebaseAuth.getInstance().currentUser
                            val userData = User(user!!.uid, userName, email, role)

                            // Store user data in Firestore
                        // Store user data in Firestore
                        db.collection("users").document(user.uid).set(userData)
                        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).setValue(userData)

                        progessBar.visibility = View.GONE
                        startActivity(Intent(this, MainActivity2::class.java))
                        finish()
                    }
                    is Resource.Failure -> {
                        progessBar.visibility = View.GONE
                        Toast.makeText(this, resource.exception.message, Toast.LENGTH_LONG).show()
                    }

                    null -> {
                        Toast.makeText(this, "Null output", Toast.LENGTH_LONG).show()

                    }
                }
            }

            // Call ViewModel Function
//            userViewModel.signUpUser(userName, email, password, role) { success, errorMessage ->
//                if (!success) {
//                    Toast.makeText(this, "Signup Failed: $errorMessage", Toast.LENGTH_SHORT).show()
//                } else{
//                    var intent= Intent(this,MainActivity2::class.java)
//                    startActivity(intent)
//                }
//            }
        }
    }
}