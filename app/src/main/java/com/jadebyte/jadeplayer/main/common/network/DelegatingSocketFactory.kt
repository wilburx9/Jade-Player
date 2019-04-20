/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jadebyte.jadeplayer.main.common.network


import android.net.TrafficStats
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

/**
 * Created by Wilberforce on 2019-04-20 at 15:36.
 * Original source: https://github.com/square/okhttp/blob/master/okhttp/src/test/java/okhttp3/DelegatingSocketFactory.java
 * A [SocketFactory] that delegates calls. Sockets can be configured after creation by
 * overriding [.configureSocket].
 */
open class DelegatingSocketFactory(private val delegate: SocketFactory) : SocketFactory() {

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        val socket = delegate.createSocket()
        return configureSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int): Socket {
        val socket = delegate.createSocket(host, port)
        return configureSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(
        host: String, port: Int, localAddress: InetAddress,
        localPort: Int
    ): Socket {
        val socket = delegate.createSocket(host, port, localAddress, localPort)
        return configureSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        val socket = delegate.createSocket(host, port)
        return configureSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(
        host: InetAddress, port: Int, localAddress: InetAddress,
        localPort: Int
    ): Socket {
        val socket = delegate.createSocket(host, port, localAddress, localPort)
        return configureSocket(socket)
    }

    @Throws(IOException::class)
    protected open fun configureSocket(socket: Socket): Socket {
        // Just to overcome Strui
        TrafficStats.setThreadStatsTag(1)
        TrafficStats.tagSocket(socket)
        return socket
    }
}