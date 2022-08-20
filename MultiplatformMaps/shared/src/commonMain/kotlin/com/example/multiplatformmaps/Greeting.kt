package com.example.multiplatformmaps

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}