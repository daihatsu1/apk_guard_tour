/*
 *     Digital Patrol Guard
 *     MainFormActivity.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 10:47 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.R
import ai.digital.patrol.data.entity.NfcData
import ai.digital.patrol.databinding.ActivityMainFormBinding
import ai.digital.patrol.helper.AppEvent
import ai.digital.patrol.helper.EventBus
import ai.digital.patrol.helper.Utils
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.experimental.and

class MainFormActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainFormBinding
    val TAG = "MainFormActivity"
    var nfcAdapter: NfcAdapter? = null
    var pendingIntent: PendingIntent? = null
    var decTarget: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.layoutToolbar.toolbar)
        binding.layoutToolbar.settings.visibility = GONE

        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setToolBarText()

        Utils.connectionChecker(
            this,
            applicationContext,
            binding.layoutToolbar.viewOnlineStatus,
            null
        )

        /**snip **/
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.package.ACTION_LOGOUT")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("onReceive", "Logout in progress")
                finish()
            }
        }, intentFilter)
        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        //If no NfcAdapter, display that the device has no NFC
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null) {
            Toast.makeText(
                this, "NO NFC Capabilities",
                Toast.LENGTH_SHORT
            ).show()
        }
        val notifyIntent = Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                application,
                0,
                notifyIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                application,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    fun enableNFC(decTarget: String) {
        if (nfcAdapter != null) {
            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
            Log.d("nfcAdapter", "enable")
            Log.d("nfcAdapter", "decTarget is $decTarget")
            this.decTarget = decTarget
        }
    }

    fun disableNFC() {
        if (nfcAdapter != null) {
            nfcAdapter!!.disableForegroundDispatch(this)
            Log.d("nfcAdapter", "disable")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setToolBarText() {
        binding.layoutToolbar.tvToolbarUsername.text = "PATROL GUARD"

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("SetTextI18n")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val data = resolveIntent(intent)
        if (data != null) {
            if (this.decTarget != null) {
                if (this.decTarget == data.dec.toString()) {
                    EventBus.post(AppEvent.NFC_MATCHED)
                    showToast("SCAN SUKSES, SILAKAN MELANJUTKAN PATROLI ANDA")
                    Log.d("nfcAdapter", "NFC_MATCHED")

                }else{
                    EventBus.post(AppEvent.NFC_NO_MATCHED)
                    showToast("CHECKPOINT TIDAK SESUAI, PERIKAS KEMBALI KARTU DAN CHEKPOINT")
                    Log.d("nfcAdapter", "NFC_NO_MATCHED")

                }
            } else {
                showToast("Terjadi kesalahan, periksa kembali kartu atau checkpoint yang di pilih");
            }
        }
    }

    private fun resolveIntent(intent: Intent): NfcData? {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = (intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?)!!
            return detectTagData(tag)
        }
        return null
    }

    //For detection
    private fun detectTagData(tag: Tag?): NfcData? {
        val sb = StringBuilder()
        val id = tag!!.id
        sb.append("ID (hex): ").append(toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
        val data = toHex(id)?.let {
            NfcData(
                Arrays.toString(tag.id),
                it,
                toReversedHex(id),
                toDec(id),
                toReversedDec(id),
                tag.techList
            )
        }
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        Log.v(TAG, data.toString())
        return data
    }

    private fun toHex(bytes: ByteArray): String? {
        val sb = java.lang.StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Byte = bytes[i] and 0xff.toByte()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b.toInt()))
            if (i > 0) {
                sb.append(" ")
            }
        }
        return sb.toString()
    }

    private fun toReversedHex(bytes: ByteArray): String {
        val sb = java.lang.StringBuilder()
        for (i in bytes.indices) {
            if (i > 0) {
                sb.append(" ")
            }
            val b: Byte = bytes[i] and 0xff.toByte()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b.toInt()))
        }
        return sb.toString()
    }

    private fun toDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (aByte in bytes) {
            val value: Byte = aByte and 0xffL.toByte()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun toReversedDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value: Byte = bytes[i] and 0xffL.toByte()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    fun writeTag(mifareUlTag: MifareUltralight) {
        try {
            mifareUlTag.connect()
            mifareUlTag.writePage(4, "get ".toByteArray(Charset.forName("US-ASCII")))
            mifareUlTag.writePage(5, "fast".toByteArray(Charset.forName("US-ASCII")))
            mifareUlTag.writePage(6, " NFC".toByteArray(Charset.forName("US-ASCII")))
            mifareUlTag.writePage(7, " now".toByteArray(Charset.forName("US-ASCII")))
        } catch (e: IOException) {
            Log.e(TAG, "IOException while writing MifareUltralight...", e)
        } finally {
            try {
                mifareUlTag.close()
            } catch (e: IOException) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e)
            }
        }
    }

    fun readTag(mifareUlTag: MifareUltralight?): String? {
        try {
            mifareUlTag!!.connect()
            val payload = mifareUlTag.readPages(4)
            return String(payload, StandardCharsets.US_ASCII)
        } catch (e: IOException) {
            Log.e(TAG, "IOException while reading MifareUltralight message...", e)
        } finally {
            if (mifareUlTag != null) {
                try {
                    mifareUlTag.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing tag...", e)
                }
            }
        }
        return null
    }

    override fun onPause() {
        super.onPause()
        //Onpause stop listening
//        disableNFC()
    }

}