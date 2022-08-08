/*
 *     Digital Patrol Guard
 *     Utils.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.digital.patrol.helper

import ai.digital.patrol.BuildConfig
import ai.digital.patrol.R
import ai.digital.patrol.model.KeyValueModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import java.io.File
import java.text.DateFormat
import java.util.*





object Utils {
    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location?): String {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

//    fun getLocationTitle(context: Context): String {
//        return context.getString(
//            R.string.location_updated,
//            DateFormat.getDateTimeInstance().format(Date())
//        )
//    }

    fun getAssetTypeData(): ArrayList<KeyValueModel> {
        val typeList: ArrayList<KeyValueModel> = ArrayList()
        typeList.add(KeyValueModel(1, "Billboard"))
        typeList.add(KeyValueModel(2, "Non Billboard"))
        return typeList
    }

    fun getAssetOrientationData(): ArrayList<KeyValueModel> {
        val orientationList: ArrayList<KeyValueModel> = ArrayList()
        orientationList.add(KeyValueModel(1, "Vertikal"))
        orientationList.add(KeyValueModel(2, "Horisontal"))
        return orientationList
    }

    fun getAssetLightData(): ArrayList<KeyValueModel> {
        val typeList: ArrayList<KeyValueModel> = ArrayList()
        typeList.add(KeyValueModel(0, "Tidak Ada lampu"))
        typeList.add(KeyValueModel(1, "Front Light"))
        typeList.add(KeyValueModel(2, "Back Light"))
        typeList.add(KeyValueModel(3, "LED"))
        return typeList
    }

    fun urlValidations(url: String): String {
        if (BuildConfig.DEBUG)
            return url.replace("localhost", "10.0.2.2")
        return url
    }
    enum class TypePhoto {
        ASSET_PHOTO, ASSET_AREA_PHOTO, ASSET_STREET_PHOTO
    }

    fun getTypePhotoId(typePhoto: TypePhoto): Int {
        return when (typePhoto) {
            TypePhoto.ASSET_PHOTO -> {
                1
            }
            TypePhoto.ASSET_AREA_PHOTO -> {
                2
            }
            TypePhoto.ASSET_STREET_PHOTO -> {
                3
            }
        }
    }


    fun encoder(filePath: String): String {
        val bytes = File(filePath).readBytes()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }

    fun decoder(base64Str: String): Bitmap? {
        val imageByteArray = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

}
