package com.namada.app.util

import android.view.View
import androidx.fragment.app.Fragment
import com.namada.app.MainActivity
import com.namada.app.R

fun Fragment.showActionbarAndBottomNavigation() {
    if (requireActivity() is MainActivity) {
        (requireActivity() as MainActivity).supportActionBar?.show()
        (requireActivity() as MainActivity).findViewById<View>(
            R.id.nav_view
        )?.visibility = View.VISIBLE
    }
}

fun Fragment.hideActionbarAndBottomNavigation() {
    if (requireActivity() is MainActivity) {
        (requireActivity() as MainActivity).supportActionBar?.hide()
        (requireActivity() as MainActivity).findViewById<View>(
            R.id.nav_view
        )?.visibility = View.GONE
    }
}

fun Fragment.hideBottomNavigation() {
    if (requireActivity() is MainActivity) {
        (requireActivity() as MainActivity).findViewById<View>(
            R.id.nav_view
        )?.visibility = View.GONE
    }
}

fun Fragment.setActionBarTitle(title: String){
    if (requireActivity() is MainActivity) {
        (requireActivity() as MainActivity).supportActionBar?.title = title
    }
}