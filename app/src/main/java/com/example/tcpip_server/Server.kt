package com.example.tcpip_server

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap

class Server {

    var clients: HashMap<String, DataOutputStream> = HashMap()
    var serverSocket: ServerSocket? = null

    init {
        Collections.synchronizedMap(clients)
    }

    fun main(args: Array<String>) {
        start()
    }

    private fun start() {
        val port = 5001
        var socket: Socket? = null

        try {
            // 서버 소켓 생성 > accept(대기) > 접속 시 ip주소 획득 > 출력
            // MultiThread 생성
            serverSocket = ServerSocket(port)
            Log.d("Start >> ", "접속 대기중")
            while (true) {
                if (serverSocket != null) {
                    socket = serverSocket!!.accept()
                    val ip = socket.inetAddress // ip 주소 획득
                    print("ip 주소 >> $ip connected")

                }
            }
        } catch (e: IOException) {
        }

    }


    inner class MultiThread constructor(_socket: Socket) : Thread() {
        var socket: Socket? = null
        var mac: String? = null
        var msg: String? = null

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
                mac = input?.readUTF()
                print("mac 주소 >>> $mac")

                clients.put(mac!!, output!!)
                sendMsg("$mac > 접속")

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