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

package org.osgi.test.cases.enocean.rpc;

import org.osgi.service.enocean.EnOceanRPC;

/**
 * @author $Id$
 */
public class QueryFunction implements EnOceanRPC {

	private int	senderId	= -1;

	public int getManufacturerId() {
		return 0x07ff;
	}

	public int getFunctionId() {
		return 0x0007;
	}

	public byte[] getPayload() {
		return new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x66 };
		// 0x55, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x66 };
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int chipId) {
		this.senderId = chipId;
	}

	public String getName() {
		return null;
	}

}
