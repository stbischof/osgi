/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.dmt.tc4.ext.util;

import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;
import java.util.List;
public class ArrayAssert {

	public static void assertEquivalenceArrays(Object[] expected, Object[] actual) {
		assertEquivalenceArrays(null, expected, actual);
	}

	public static void assertEquivalenceArrays(String message, Object[] expected, Object[] actual) {
		if ((expected == null) || (actual == null)) {
			assertTrue(expected == actual);
			return;
		}
		List<Object> actualList = Arrays.asList(actual);
		for (int i = 0; i < expected.length; i++) {
			if (message != null) {
				assertTrue(message, actualList.contains(expected[i]));
			} else {
				assertTrue(actualList.contains(expected[i]));
			}
		}
	}
}
