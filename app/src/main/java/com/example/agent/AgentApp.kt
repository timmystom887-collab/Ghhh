package com.example.agent

import android.app.Application

class AgentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize global components, Room, WorkManager if needed
    }
}
