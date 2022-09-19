package ai.digital.patrol.ui

//import com.master.permissionhelper.PermissionHelper
import ai.digital.patrol.databinding.ActivitySplashBinding
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.RuntimePermissions
import ai.digital.patrol.ui.login.LoginActivity
import ai.digital.patrol.ui.login.LoginViewModel
import ai.digital.patrol.ui.login.LoginViewModelFactory
import ai.digital.patrol.ui.main.MainActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceManager


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
//    private var permissionHelper: PermissionHelper? = null

    private lateinit var binding: ActivitySplashBinding
    private lateinit var intent_to_other_activity: Intent
    private var PERMISSION_CODE = 100
    private lateinit var permissions: Array<String>

    //    private lateinit var loginViewModel: LoginViewModel
    private val loginViewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory()).get(
            LoginViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            go()
//            setConfig()
        }, 3000)

    }


    private fun go() {

        val accessToken = PreferenceHelper.appsPrefs(this).getString(Cons.APIKEY, null)

        if (accessToken != null) {
            val mainActivity = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainActivity)
        } else {
            val loginActivity = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(loginActivity)
        }
        finishAffinity()
    }

    private fun setConfig() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val viewMode = prefs.getBoolean(Cons.VIEW_MODE, false)
        if (viewMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }


    companion object {
        private val TAG: String by lazy { "SplashActivity" }
    }
}