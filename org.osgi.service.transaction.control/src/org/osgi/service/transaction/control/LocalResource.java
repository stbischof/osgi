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
package org.osgi.service.transaction.control;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Resources that can integrate with local transactions should do so using this
 * interface
 */
@ConsumerType
public interface LocalResource {

	/**
	 * Commit the resource
	 * 
	 * @throws TransactionException
	 */
	void commit() throws TransactionException;

	/**
	 * Roll back the resource
	 * 
	 * @throws TransactionException
	 */
	void rollback() throws TransactionException;
}
