/*
 *     Digital Patrol Guard
 *     ListObjectFragment.kt
 *     Created by ImamSyahrudin on 5/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/5/22, 10:33 AM
 */

package ai.digital.patrol.ui.form

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.digital.patrol.R
import ai.digital.patrol.ui.form.viewmodel.ListObjectViewModel

class ListObjectFragment : Fragment() {

    companion object {
        fun newInstance() = ListObjectFragment()
    }

    private lateinit var viewModel: ListObjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_object, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListObjectViewModel::class.java)
        // TODO: Use the ViewModel
    }

}