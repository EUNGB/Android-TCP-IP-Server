package com.example.tcpip_server

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var serverIP: TextView
    lateinit var btnDevice1: Button
    lateinit var btnDevice2: Button

    var server = SocketClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serverIP = findViewById(R.id.tv_server_ip)
        btnDevice1 =  findViewById(R.id.btn_device1)
        btnDevice1.setOnClickListener(this)
        btnDevice2 = findViewById(R.id.btn_device2)
        btnDevice2.setOnClickListener(this)

        try {
            val ip = getLocalIpAddress()
            serverIP.text = ip.toString()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }

        Thread {
            server.start()
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_device1 -> {
                server.sendDevice1("디바이스 1 응답하라")
            }
            R.id.btn_device2 -> {
                server.sendDevice2("디바이스 2 응답하라")
            }
        }
    }


    @SuppressLint("ServiceCast")
    @Throws(UnknownHostException::class)
    private fun getLocalIpAddress(): String? {

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ipInt = wifiInfo.ipAddress

        return InetAddress.getByAddress(
            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()
        )
            .hostAddress
    }

}