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

package java.rmi.server;
public interface RemoteRef extends java.io.Externalizable {
	public final static java.lang.String packagePrefix = "sun.rmi.server";
	public final static long serialVersionUID = 3632638527362204081l;
	/** @deprecated */
	@java.lang.Deprecated
	void done(java.rmi.server.RemoteCall var0) throws java.rmi.RemoteException;
	java.lang.String getRefClass(java.io.ObjectOutput var0);
	java.lang.Object invoke(java.rmi.Remote var0, java.lang.reflect.Method var1, java.lang.Object[] var2, long var3) throws java.lang.Exception;
	/** @deprecated */
	@java.lang.Deprecated
	void invoke(java.rmi.server.RemoteCall var0) throws java.lang.Exception;
	/** @deprecated */
	@java.lang.Deprecated
	java.rmi.server.RemoteCall newCall(java.rmi.server.RemoteObject var0, java.rmi.server.Operation[] var1, int var2, long var3) throws java.rmi.RemoteException;
	boolean remoteEquals(java.rmi.server.RemoteRef var0);
	int remoteHashCode();
	java.lang.String remoteToString();
}
