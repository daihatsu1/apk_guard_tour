package ai.digital.patrol.helper

import android.nfc.NfcAdapter.ReaderCallback
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.util.Log
import java.lang.StringBuilder
import java.nio.charset.Charset
import kotlin.experimental.and

class NFCCardReader : ReaderCallback {
    //     MainActivity mainActivity;
    //
    //    public NFCCardReader(MainActivity mainActivity) {
    //        this.mainActivity = mainActivity;
    //    }
    override fun onTagDiscovered(tag: Tag) {
//        val tagId = bytesToHexString(tag.id)
        //        mainActivity.displayTagId(tagId);
        val tags = readTag(tag)
        Log.d("TAG",tags!!)
    }

     fun readTag(tag: Tag): String? {
        //        return MifareClassic.get(tag)?.use { mifare ->
//            mifare.connect()
//            val payload = mifare.readBlock(4)
//            String(payload, Charset.forName("US-ASCII"))
//        }
        return MifareUltralight.get(tag)?.use { mifare ->
            mifare.connect()
            val payload = mifare.readPages(4)
            String(payload, Charset.forName("US-ASCII"))
        }
    }
//    }

//     fun bytesToHexString(src: ByteArray?): String? {
//        val stringBuilder = StringBuilder("0x")
//        if (src == null || src.isEmpty()) {
//            return null
//        }
//        val buffer = CharArray(2)
//        for (b in src) {
//            buffer[0] = Character.forDigit(b ushr 4 and 0x0F, 16)
//            buffer[1] = Character.forDigit(b and 0x0F, 16)
//            println(buffer)
//            stringBuilder.append(buffer)
//        }
//        return stringBuilder.toString()
//    }
}