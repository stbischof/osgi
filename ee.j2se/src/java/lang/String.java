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

package java.lang;
public final class String implements java.io.Serializable, java.lang.CharSequence, java.lang.Comparable<java.lang.String> {
	public final static java.util.Comparator<java.lang.String> CASE_INSENSITIVE_ORDER; static { CASE_INSENSITIVE_ORDER = null; }
	public String() { } 
	public String(java.lang.String var0) { } 
	public String(java.lang.StringBuffer var0) { } 
	public String(java.lang.StringBuilder var0) { } 
	public String(byte[] var0) { } 
	/** @deprecated */
	@java.lang.Deprecated
	public String(byte[] var0, int var1) { } 
	public String(byte[] var0, int var1, int var2) { } 
	/** @deprecated */
	@java.lang.Deprecated
	public String(byte[] var0, int var1, int var2, int var3) { } 
	public String(byte[] var0, int var1, int var2, java.lang.String var3) throws java.io.UnsupportedEncodingException { } 
	public String(byte[] var0, int var1, int var2, java.nio.charset.Charset var3) { } 
	public String(byte[] var0, java.lang.String var1) throws java.io.UnsupportedEncodingException { } 
	public String(byte[] var0, java.nio.charset.Charset var1) { } 
	public String(char[] var0) { } 
	public String(char[] var0, int var1, int var2) { } 
	public String(int[] var0, int var1, int var2) { } 
	public char charAt(int var0) { return '\0'; }
	public int codePointAt(int var0) { return 0; }
	public int codePointBefore(int var0) { return 0; }
	public int codePointCount(int var0, int var1) { return 0; }
	public int compareTo(java.lang.String var0) { return 0; }
	public int compareToIgnoreCase(java.lang.String var0) { return 0; }
	public java.lang.String concat(java.lang.String var0) { return null; }
	public boolean contains(java.lang.CharSequence var0) { return false; }
	public boolean contentEquals(java.lang.CharSequence var0) { return false; }
	public boolean contentEquals(java.lang.StringBuffer var0) { return false; }
	public static java.lang.String copyValueOf(char[] var0) { return null; }
	public static java.lang.String copyValueOf(char[] var0, int var1, int var2) { return null; }
	public boolean endsWith(java.lang.String var0) { return false; }
	public boolean equalsIgnoreCase(java.lang.String var0) { return false; }
	public static java.lang.String format(java.lang.String var0, java.lang.Object... var1) { return null; }
	public static java.lang.String format(java.util.Locale var0, java.lang.String var1, java.lang.Object... var2) { return null; }
	public byte[] getBytes() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public void getBytes(int var0, int var1, byte[] var2, int var3) { }
	public byte[] getBytes(java.lang.String var0) throws java.io.UnsupportedEncodingException { return null; }
	public byte[] getBytes(java.nio.charset.Charset var0) { return null; }
	public void getChars(int var0, int var1, char[] var2, int var3) { }
	public int indexOf(int var0) { return 0; }
	public int indexOf(int var0, int var1) { return 0; }
	public int indexOf(java.lang.String var0) { return 0; }
	public int indexOf(java.lang.String var0, int var1) { return 0; }
	public java.lang.String intern() { return null; }
	public boolean isEmpty() { return false; }
	public int lastIndexOf(int var0) { return 0; }
	public int lastIndexOf(int var0, int var1) { return 0; }
	public int lastIndexOf(java.lang.String var0) { return 0; }
	public int lastIndexOf(java.lang.String var0, int var1) { return 0; }
	public int length() { return 0; }
	public boolean matches(java.lang.String var0) { return false; }
	public int offsetByCodePoints(int var0, int var1) { return 0; }
	public boolean regionMatches(int var0, java.lang.String var1, int var2, int var3) { return false; }
	public boolean regionMatches(boolean var0, int var1, java.lang.String var2, int var3, int var4) { return false; }
	public java.lang.String replace(char var0, char var1) { return null; }
	public java.lang.String replace(java.lang.CharSequence var0, java.lang.CharSequence var1) { return null; }
	public java.lang.String replaceAll(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String replaceFirst(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String[] split(java.lang.String var0) { return null; }
	public java.lang.String[] split(java.lang.String var0, int var1) { return null; }
	public boolean startsWith(java.lang.String var0) { return false; }
	public boolean startsWith(java.lang.String var0, int var1) { return false; }
	public java.lang.CharSequence subSequence(int var0, int var1) { return null; }
	public java.lang.String substring(int var0) { return null; }
	public java.lang.String substring(int var0, int var1) { return null; }
	public char[] toCharArray() { return null; }
	public java.lang.String toLowerCase() { return null; }
	public java.lang.String toLowerCase(java.util.Locale var0) { return null; }
	public java.lang.String toUpperCase() { return null; }
	public java.lang.String toUpperCase(java.util.Locale var0) { return null; }
	public java.lang.String trim() { return null; }
	public static java.lang.String valueOf(char var0) { return null; }
	public static java.lang.String valueOf(double var0) { return null; }
	public static java.lang.String valueOf(float var0) { return null; }
	public static java.lang.String valueOf(int var0) { return null; }
	public static java.lang.String valueOf(long var0) { return null; }
	public static java.lang.String valueOf(java.lang.Object var0) { return null; }
	public static java.lang.String valueOf(boolean var0) { return null; }
	public static java.lang.String valueOf(char[] var0) { return null; }
	public static java.lang.String valueOf(char[] var0, int var1, int var2) { return null; }
}
