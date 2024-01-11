package com.mcvz.e_commerceapp.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcvz.e_commerceapp.R

fun Fragment.setupBottomSheetDialog(
    onSendClick:(String)->Unit
){
    val dialog=BottomSheetDialog(requireContext(),R.style.DialogStyle)
    val view=layoutInflater.inflate(R.layout.reset_password_dialog,null)
    dialog.setContentView(view)
    dialog.behavior.state=BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val etEmail=view.findViewById<EditText>(R.id.etResetPassword)
    val btnSend=view.findViewById<Button>(R.id.btnSendResetPassword)
    val btnCancel=view.findViewById<Button>(R.id.btnCancelResetPassword)

    btnSend.setOnClickListener {
        val email=etEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

}