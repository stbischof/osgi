/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.nio.channels;
public abstract class ServerSocketChannel extends java.nio.channels.spi.AbstractSelectableChannel implements java.nio.channels.NetworkChannel {
	protected ServerSocketChannel(java.nio.channels.spi.SelectorProvider var0)  { super((java.nio.channels.spi.SelectorProvider) null); } 
	public abstract java.nio.channels.SocketChannel accept() throws java.io.IOException;
	public final java.nio.channels.ServerSocketChannel bind(java.net.SocketAddress var0) throws java.io.IOException { return null; }
	public abstract java.nio.channels.ServerSocketChannel bind(java.net.SocketAddress var0, int var1) throws java.io.IOException;
	public static java.nio.channels.ServerSocketChannel open() throws java.io.IOException { return null; }
	public abstract <T> java.nio.channels.ServerSocketChannel setOption(java.net.SocketOption<T> var0, T var1) throws java.io.IOException;
	public abstract java.net.ServerSocket socket();
	public final int validOps() { return 0; }
}
