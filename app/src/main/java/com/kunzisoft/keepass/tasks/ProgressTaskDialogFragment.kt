/*
 * Copyright 2019 Jeremy Jamet / Kunzisoft.
 *
 * This file is part of KeePass DX.
 *
 *  KeePass DX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  KeePass DX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePass DX.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.kunzisoft.keepass.tasks

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.kunzisoft.keepass.R
import com.kunzisoft.keepass.utils.Util

open class ProgressTaskDialogFragment : DialogFragment(), ProgressTaskUpdater {

    @StringRes
    private var title = UNDEFINED
    @StringRes
    private var message = UNDEFINED

    private var titleView: TextView? = null
    private var messageView: TextView? = null
    private var progressView: ProgressBar? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = it.layoutInflater

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            @SuppressLint("InflateParams")
            val root = inflater.inflate(R.layout.progress_dialog, null)
            builder.setView(root)

            titleView = root.findViewById(R.id.progress_dialog_title)
            messageView = root.findViewById(R.id.progress_dialog_message)
            progressView = root.findViewById(R.id.progress_dialog_bar)

            updateTitle(title)
            updateMessage(message)

            isCancelable = false
            Util.lockScreenOrientation(it)

            return builder.create()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        Util.unlockScreenOrientation(activity)
        super.onDismiss(dialog)
    }

    fun setTitle(@StringRes titleId: Int) {
        this.title = titleId
    }

    private fun updateView(textView: TextView?, @StringRes resId: Int) {
        if (resId == UNDEFINED) {
            textView?.visibility = View.GONE
        } else {
            textView?.setText(resId)
            textView?.visibility = View.VISIBLE
        }
    }

    fun updateTitle(resId: Int) {
        this.title = resId
        updateView(titleView, title)
    }

    override fun updateMessage(resId: Int) {
        this.message = resId
        updateView(messageView, message)
    }

    companion object {

        private const val PROGRESS_TASK_DIALOG_TAG = "progressDialogFragment"

        private const val UNDEFINED = -1

        fun start(fragmentManager: FragmentManager,
                  @StringRes titleId: Int,
                  @StringRes messageId: Int? = null): ProgressTaskDialogFragment {
            // Create an instance of the dialog fragment and show it
            val dialog = ProgressTaskDialogFragment()
            dialog.updateTitle(titleId)
            messageId?.let {
                dialog.updateMessage(it)
            }
            dialog.show(fragmentManager, PROGRESS_TASK_DIALOG_TAG)
            return dialog
        }

        fun stop(activity: FragmentActivity) {
            val fragmentTask = activity.supportFragmentManager.findFragmentByTag(PROGRESS_TASK_DIALOG_TAG)
            if (fragmentTask != null) {
                val loadingDatabaseDialog = fragmentTask as ProgressTaskDialogFragment
                loadingDatabaseDialog.dismissAllowingStateLoss()
                Util.unlockScreenOrientation(activity)
            }
        }
    }
}
