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

package java.io;
public class PipedInputStream extends java.io.InputStream {
	protected final static int PIPE_SIZE = 1024;
	protected byte[] buffer;
	protected int in;
	protected int out;
	public PipedInputStream() { } 
	public PipedInputStream(int var0) { } 
	public PipedInputStream(java.io.PipedOutputStream var0) throws java.io.IOException { } 
	public PipedInputStream(java.io.PipedOutputStream var0, int var1) throws java.io.IOException { } 
	public void connect(java.io.PipedOutputStream var0) throws java.io.IOException { }
	public int read() throws java.io.IOException { return 0; }
	protected void receive(int var0) throws java.io.IOException { }
}
