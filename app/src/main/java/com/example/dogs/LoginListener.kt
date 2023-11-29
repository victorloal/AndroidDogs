package com.example.dogs

interface OnLoginResultListener {
    fun onLoginSuccess()
    fun onLoginFailure()
    fun onLogout()
}