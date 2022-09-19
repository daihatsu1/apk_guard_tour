package ai.digital.patrol.ui.nfc

import kotlin.math.floor

class StringUtils {
    companion object {
        fun randomString(length: Int): String {
            val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            var str = ""
            for (i in 0..length) {
                str += chars[floor(Math.random() * chars.length).toInt()]
            }
            return str
        }
    }
}
