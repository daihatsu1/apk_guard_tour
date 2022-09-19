/*
 *     Digital Patrol Guard
 *     NfcData.kt
 *     Created by ImamSyahrudin on 7/9/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 9/7/22, 12:05 AM
 */

package ai.digital.patrol.data.entity

data class NfcData(
    val id:String,
    val hex:String,
    val reservedHex:String,
    val dec:Long,
    val reversed_dec: Long,
    val tech: Array<String>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NfcData

        if (id != other.id) return false
        if (hex != other.hex) return false
        if (reservedHex != other.reservedHex) return false
        if (dec != other.dec) return false
        if (reversed_dec != other.reversed_dec) return false
        if (!tech.contentEquals(other.tech)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + hex.hashCode()
        result = 31 * result + reservedHex.hashCode()
        result = 31 * result + dec.hashCode()
        result = 31 * result + reversed_dec.hashCode()
        result = 31 * result + tech.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "NfcData(id='$id', hex='$hex', reservedHex='$reservedHex', dec=$dec, reversed_dec=$reversed_dec, tech=${tech.contentToString()})"
    }
}
