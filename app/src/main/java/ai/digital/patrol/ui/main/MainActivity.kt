/*
 *     Digital Patrol Guard
 *     MainActivity.kt
 *     Created by ImamSyahrudin on 5/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/5/22, 10:32 AM
 */

package ai.digital.patrol.ui.main

import ai.digital.patrol.databinding.ActivityMainBinding
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.form.MainFormActivity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private var confirmationStartPatrolCallback: DialogCallbackListener? = null
    private var confirmationStartUnscheduledPatrolCallback: DialogCallbackListener? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var btnStartUnscheduledPatrol: Button
    private lateinit var btnStartPatrol: Button
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
       setListener()
        btnStartUnscheduledPatrol = binding.layoutMain.startUnscheduledPatrol
        btnStartPatrol = binding.layoutMain.startPatrol

        // Set click action
        btnStartUnscheduledPatrol.setOnClickListener(View.OnClickListener {
            dialogConfirmStartUnscheduledPatrol()
        })

        btnStartPatrol.setOnClickListener(View.OnClickListener {
            dialogConfirmStartPatrol()
        })
    }

    /**
     * Set listener
     *
     * initialize listener for mainActivity
     *
     */
    private fun setListener(){
        confirmationStartPatrolCallback= object :DialogCallbackListener{
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                Toast.makeText(applicationContext, "onPositiveClickListener()", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
//                MainFormActivity
                val mainFormActivity = Intent(this@MainActivity, MainFormActivity::class.java)
                startActivity(mainFormActivity)
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                Toast.makeText(applicationContext, "onNegativeClickListener()", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
                Toast.makeText(applicationContext, "onDismissClickListener()", Toast.LENGTH_SHORT).show()
            }
        }

        confirmationStartUnscheduledPatrolCallback= object :DialogCallbackListener{
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                Toast.makeText(applicationContext, "onPositiveClickListener()", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
                val mainFormActivity = Intent(this@MainActivity, MainFormActivity::class.java)
                startActivity(mainFormActivity)
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                Toast.makeText(applicationContext, "onNegativeClickListener()", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
                Toast.makeText(applicationContext, "onDismissClickListener()", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dialogConfirmStartUnscheduledPatrol(){
        val title = "APAKAH ANDA YAKIN MEMULAI PATROLI DILUAR JADWAL?"
        val subTitle = ""
        val positiveText = "LANJUTKAN"
        val negativeText = "BATAL"

        DialogFragment.newInstance(title, subTitle, positiveText, negativeText, confirmationStartPatrolCallback!!)
            .show(supportFragmentManager, "dialogConfirmStartUnscheduledPatrol")
    }

    private fun dialogConfirmStartPatrol() {
        val title = "APAKAH ANDA YAKIN MEMULAI PATROLI SEKARANG?"
        val subTitle = "HARI INI, SHIFT 1, SENIN, 22 JULI 2022?"
        val positiveText = "LANJUTKAN"
        val negativeText = "BATAL"

        DialogFragment.newInstance(title, subTitle, positiveText, negativeText, confirmationStartPatrolCallback!!)
            .show(supportFragmentManager, "dialogConfirmStartPatrol")
    }

}

