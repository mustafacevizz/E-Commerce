package com.mcvz.e_commerceapp.util

import android.util.Patterns

fun validateEmail(email:String):RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email boş olamaz")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Email formatı yanlış")
    return RegisterValidation.Success

}
fun validatePassword(password:String):RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("Parola boş olamaz")
    if (password.length<6)
        return RegisterValidation.Failed("En az 6 haneli bir Parola giriniz")
    return RegisterValidation.Success
}