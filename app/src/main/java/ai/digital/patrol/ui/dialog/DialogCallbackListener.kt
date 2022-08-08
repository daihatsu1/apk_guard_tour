/*
 *     Digital Patrol Guard
 *     DialogCallbackListener.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.view.View


interface DialogCallbackListener {
    fun onPositiveClickListener(v: View, dialog: Dialog?)
    fun onNegativeClickListener(v: View, dialog: Dialog?)
    fun onDismissClickListener(dialog: DialogInterface)
}