package com.example.dogs.user

interface OnLoginResultListener {
    fun onLoginSuccess()
    fun onLoginFailure()
    fun onLogout()
}