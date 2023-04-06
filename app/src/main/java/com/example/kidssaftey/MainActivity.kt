package com.example.kidssaftey

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var bottomNavBar:BottomNavigationView?=null
lateinit var auth: FirebaseAuth
var nav_view:NavigationView?=null

class MainActivity : AppCompatActivity() {
//    hame  bahut ssari permissions mangni hai
//    to ham uske liye permissions ka array bana lete hai



    private var actionBarDrawerToggle : ActionBarDrawerToggle?=null
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private val permissions = arrayOf(
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.READ_CONTACTS,
    android.Manifest.permission.INTERNET,
    android.Manifest.permission.POST_NOTIFICATIONS,
    android.Manifest.permission.CALL_PHONE,
)
//    give a permission code for further validation
    private val permissionCode=103


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId", "AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth=FirebaseAuth.getInstance()
        val name= auth.currentUser?.displayName.toString()
        val gmail= auth.currentUser?.email.toString()
        val phoneNumber= auth.currentUser?.phoneNumber.toString()
        val imageUrl=auth.currentUser?.photoUrl.toString()




//        firestore cloud
        val db=Firebase.firestore


        val user = hashMapOf(
            "name" to name,
            "gmail" to gmail,
            "phone Number" to phoneNumber,
            "imageurl" to imageUrl

        )
        db.collection("users")
            .document(gmail).set(user).addOnSuccessListener {  }
            .addOnFailureListener{}

        askForAllPermissions()
        bottomNavBar=findViewById(R.id.bottom_nav_bar)
        nav_view=findViewById(R.id.nav_view)
        val drawerLayout=findViewById<DrawerLayout>(R.id.const_layout)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#6c63ff")))
        supportActionBar?.setDisplayShowTitleEnabled(false)


        nav_view!!.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_logout->{
                    showLogoutDialog()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_settings->{
                    val intent=Intent(this@MainActivity,ProfileFrag::class.java)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                else->return@setNavigationItemSelectedListener true
            }
        }

        bottomNavBar!!.setOnItemSelectedListener {

            if(it.itemId==R.id.nav_guard)
            {
                inflateFrag(GuardFrag.newInstance())
            }
            else if(it.itemId==R.id.nav_home)
            {
                inflateFrag(HomeFrag.newInstance())
            }
            else if(it.itemId==R.id.nav_dashboard)
            {
                inflateFrag(MapsFragment())
            }
            else if(it.itemId==R.id.nav_profile)
            {
                inflateFrag(ProfileFrag.newInstance())
            }
            true
        }
        bottomNavBar!!.selectedItemId=R.id.nav_home
//        it means jab bhi ham app khole to home wala hi selected hona chahiye



        if(isAllPermissionsGranted())
        {
            if(isLocationEnabled(this))
            {
                setUpLocationListener()
            }
            else
            {
                showGPSNotEnabledDialog(this)
            }

        }
        else
        {
            askForAllPermissions()
        }

        updateNavHeader()
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // for getting the current location update after every 1 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        Log.d("Location83","onLocationResult: latitude ${location.latitude}")
                        Log.d("Location83","onLocationResult: longitude ${location.longitude}")

                        val gmail= auth.currentUser?.email.toString()
                        val db=Firebase.firestore
                        val locationData= mutableMapOf<String,Any>(
                            "lat" to location.latitude.toString(),
                            "long" to location.longitude.toString()
                        )
                        db.collection("users").document(gmail).update(locationData).addOnSuccessListener {
                        }.addOnFailureListener {  }

                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle?.onOptionsItemSelected(item) == true) {
            true
        } else super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForAllPermissions() {
        ActivityCompat.requestPermissions(this@MainActivity,permissions,permissionCode)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     private fun isAllPermissionsGranted(): Boolean {
        for(item in permissions)
        {
            if( ContextCompat
                .checkSelfPermission(
                    this,
                    item
                ) == PackageManager.PERMISSION_GRANTED)
            {
                return false
            }

        }
       return true
    }
    /**
     * Function to check if location of the device is enabled or not
     */
    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    private fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps))
            .setMessage(context.getString(R.string.required_for_this_app))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                context.startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }


    private fun inflateFrag(newInstance: Fragment) {
        val transition=supportFragmentManager.beginTransaction()
        transition.replace(R.id.container,newInstance)
        transition.commit()
    }

    private fun showLogoutDialog()
    {
        val dialogBuilder= AlertDialog.Builder(this,R.style.CustomAlertDialog)
        dialogBuilder .setTitle("Log Out")
            .setMessage("Are you sure you want to logout ?")
            .setPositiveButton("Yes"){ _, _ ->  //dialog,which
                if(signOutFromApp())
                {
                    val intent=Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.cancel()
            }.show()
    }
    private fun signOutFromApp():Boolean
    {
        auth.signOut()
        Toast.makeText(this,"Logout Successfully", Toast.LENGTH_LONG).show()
        return true
    }

    private  fun updateNavHeader()
    {
        val navigationView=findViewById<NavigationView>(R.id.nav_view)
        val headerView=navigationView.getHeaderView(0)
        val nav_name=headerView.findViewById<TextView>(R.id.user_name)
        val nav_email=headerView.findViewById<TextView>(R.id.user_email)
        val nav_pic=headerView.findViewById<ImageView>(R.id.profile_pic)
        val currentUser=auth.currentUser
        if (currentUser != null) {
            nav_email.text = currentUser.email
            nav_name.text=currentUser.displayName
            val userpic=currentUser.photoUrl
            Glide.with(this).load(userpic).into(nav_pic)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==permissionCode)
        {
            if(isAllPermissionsGranted())
            {
                setUpLocationListener()

            }
            else
            {

            }
        }
    }

}