package com.mcvz.e_commerceapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.ShoppingActivity

fun Fragment.hideBottomNavigationView() {
    val bottomNavigationView=(activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility= View.GONE
}
fun Fragment.showBottomNavigationView() {
    val bottomNavigationView=(activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility= View.VISIBLE
}