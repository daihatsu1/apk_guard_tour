/*
 *     Digital Patrol Guard
 *     KeyValueModel.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.model


data class KeyValueModel(
    val key: Int? = null,
    val value: String? = null
){
    override fun toString(): String {
        return "$value"
    }

    override fun equals(other: Any?): Boolean {
        if (other is KeyValueModel) {
            val c: KeyValueModel = other
            if (c.value.equals(value) && c.key === key) return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = key ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

}