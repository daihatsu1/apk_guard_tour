package ai.digital.patrol.ui

//import com.master.permissionhelper.PermissionHelper
import ai.digital.patrol.databinding.ActivitySplashBinding
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.ui.login.LoginActivity
import ai.digital.patrol.ui.login.LoginViewModel
import ai.digital.patrol.ui.login.LoginViewModelFactory
import ai.digital.patrol.ui.main.MainActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
//    private var permissionHelper: PermissionHelper? = null

    private lateinit var binding: ActivitySplashBinding
    private lateinit var intent_to_other_activity: Intent
    private var PERMISSION_CODE = 100
    private lateinit var permissions: Array<String>

    private val loginViewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory()).get(
            LoginViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setConfig()

        Handler(Looper.getMainLooper()).postDelayed({
            go()
        }, 3000)

    }


    private fun go() {
        val accessToken = PreferenceHelper.appsPrefs(this).getString(Cons.APIKEY, null)
        val nextIntent: Intent = if (accessToken != null) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, LoginActivity::class.java)
        }
        nextIntent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
//        nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(nextIntent)
        finish()
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