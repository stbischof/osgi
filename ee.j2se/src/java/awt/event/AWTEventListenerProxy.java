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

package java.awt.event;
public class AWTEventListenerProxy extends java.util.EventListenerProxy<java.awt.event.AWTEventListener> implements java.awt.event.AWTEventListener {
	public AWTEventListenerProxy(long var0, java.awt.event.AWTEventListener var1)  { super((java.awt.event.AWTEventListener) null); } 
	public void eventDispatched(java.awt.AWTEvent var0) { }
	public long getEventMask() { return 0l; }
}
