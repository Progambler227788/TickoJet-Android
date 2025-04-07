package com.talhaatif.tickojet.data.remote.client


import android.util.Log
import com.google.gson.Gson
import com.talhaatif.tickojet.BuildConfig
import com.talhaatif.tickojet.responseModel.SeatUpdate
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val eventId: String,
    private val onSeatUpdate: (List<String>, String) -> Unit
) {
    private val client = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS) // Keep connection alive
        .build()

    private var webSocket: WebSocket? = null
    private val gson = Gson()

    fun connect() {
        val request = Request.Builder()
            .url(BuildConfig.API_BASE_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected")
                // Proper STOMP CONNECT frame
                webSocket.send("""
                    CONNECT
                    accept-version:1.2
                    heart-beat:10000,10000

                    \u0000
                """.trimIndent())

                // STOMP SUBSCRIBE frame
                webSocket.send("""
                    SUBSCRIBE
                    id:sub-$eventId
                    destination:/topic/seats

                    \u0000
                """.trimIndent())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Raw message: $text")
                try {
                    if (text.startsWith("MESSAGE")) {
                        val payload = text.substringAfter("\n\n").substringBeforeLast("\u0000")
                        Log.d("WebSocket", "Parsing payload: $payload")
                        val seatUpdate = gson.fromJson(payload, SeatUpdate::class.java)
                        if (seatUpdate.eventId == eventId) {
                            onSeatUpdate(seatUpdate.seatNumbers, seatUpdate.status)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Parse error", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Connection failed: ${t.message}")
                // Auto-reconnect logic could go here
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "User exit")
    }
}