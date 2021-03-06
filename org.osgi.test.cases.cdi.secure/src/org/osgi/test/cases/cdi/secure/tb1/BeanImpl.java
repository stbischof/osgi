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
/**
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
 */

package org.osgi.test.cases.cdi.secure.tb1;

import java.time.Instant;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.service.cdi.annotations.Bean;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.cdi.secure.tbextension.TBExtensionCalled;

@Bean
@Named("beanimpl")
@Requirement(name = "tb.extension", namespace = "osgi.cdi.extension")
@Service
public class BeanImpl implements Callable<Instant> {

	@Override
	public Instant call() throws Exception {
		return whenExtensionCalled;
	}

	@Inject
	@TBExtensionCalled
	Instant whenExtensionCalled;

}
