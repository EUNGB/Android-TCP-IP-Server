package com.example.tcpip_server

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap


fun main() {
    SocketClient().start()
}

class SocketClient {

    var clients: HashMap<String, DataOutputStream> = HashMap()
    var device1: DataOutputStream? = null
    var device2: DataOutputStream? = null

    var serverSocket: ServerSocket? = null

    init {
        Collections.synchronizedMap(clients)
    }

    fun start() {
        val port = 5001
        var socket: Socket? = null

        try {
            // 서버 소켓 생성 > accept(대기) > 접속 시 ip주소 획득 > 출력
            // MultiThread 생성
            serverSocket = ServerSocket(port)
            Log.d("Server >>", "접속 대기중")

            while (true) {
                if (serverSocket != null) {
                    socket = serverSocket!!.accept()
                    val ip = socket.inetAddress // ip 주소 획득
                    Log.d("ip 주소 >> ", ip.toString())
                    MultiThread(socket).start()
                } else {
                    print("socket null")
                }
            }
        } catch (e: IOException) {
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun sendDevice1(msg: String) {
        if (device1 != null) {
            Thread {
                try {
                    val output = DataOutputStream(device1)
                    output.writeUTF(msg)
                } catch (e: IOException) {

                }
            }.start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun sendDevice2(msg: String) {
        if (device2 != null) {
            Thread {
                try {
                    val output = DataOutputStream(device2)
                    output.writeUTF(msg)
                } catch (e: IOException) {
                }
            }.start()
        }
    }


    inner class MultiThread constructor(_socket: Socket) : Thread() {
        var socket: Socket? = null
        var input: DataInputStream? = null
        var output: DataOutputStream? = null

        init {
            this.socket = _socket
            try {
                input = DataInputStream(_socket.getInputStream())
                output = DataOutputStream(_socket.getOutputStream())
            } catch (e: IOException) {

            }
        }

        override fun run() {
            try {
                // mac 주소를 받아옴 > 출력
//                mac = input?.readUTF()
                val nickName = input?.readUTF()
                val ip = socket!!.inetAddress // ip 주소 획득

                clients.put(nickName ?: "", output!!)
                if (device1 == null) {
                    device1 = output!!
                } else {
                    device2 = output!!
                }

                sendMsg("$nickName 님이 접속했습니다.")

                while (input != null) {
                    try {
                        val temp = input!!.readUTF()
                        sendMsg(temp)
                        print(temp)
                    } catch (e: IOException) {
                        sendMsg("No Message")
                        break
                    }
                }
            } catch (e: IOException) {

            }
        }


        private fun sendMsg(msg: String) {
            val it: Iterator<String> = clients.keys.iterator()

            while (it.hasNext()) {
                try {
                    val dos: DataOutputStream = clients[it.next()]!!
                    output = DataOutputStream(dos)
                    output!!.writeUTF(msg)
                } catch (e: IOException) {
                }
            }
        }
    }
}