package com.example.sharedregular

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}