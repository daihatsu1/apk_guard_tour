/*
 *     Digital Patrol Guard
 *     MainFormActivity.kt
 *     Created by ImamSyahrudin on 5/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/5/22, 10:32 AM
 */

package ai.digital.patrol.ui.form

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import ai.digital.patrol.R
import ai.digital.patrol.databinding.ActivityMainFormBinding

class MainFormActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.layoutToolbar.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}