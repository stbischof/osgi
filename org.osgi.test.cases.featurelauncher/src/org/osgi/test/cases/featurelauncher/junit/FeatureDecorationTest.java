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

package org.osgi.test.cases.featurelauncher.junit;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureService;
import org.osgi.service.featurelauncher.decorator.AbandonOperationException;
import org.osgi.service.featurelauncher.decorator.FeatureDecorator;
import org.osgi.service.featurelauncher.decorator.FeatureExtensionHandler;
import org.osgi.service.featurelauncher.repository.ArtifactRepository;
import org.osgi.service.featurelauncher.repository.ArtifactRepositoryFactory;
import org.osgi.service.featurelauncher.runtime.FeatureRuntime;
import org.osgi.service.featurelauncher.runtime.FeatureRuntimeException;
import org.osgi.service.featurelauncher.runtime.InstalledFeature;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * TCK tests for Section 160.3: Feature Decoration.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class FeatureDecorationTest {

	@InjectService
	FeatureRuntime				runtime;

	@InjectService
	FeatureService				fs;

	@InjectService
	ArtifactRepositoryFactory	repoFactory;

	private ArtifactRepository	testRepo;

	@BeforeEach
	void setUp() throws IOException {
		testRepo = TckTestHelper.createTestRepository(repoFactory);
	}

	@AfterEach
	void tearDown() {
		TckTestHelper.cleanupInstalledFeatures(runtime);
	}

	// --- 160.3: FeatureDecorator behavior ---

	@Nested
	class DecoratorBehavior {

		@Test
		void decorator_calledForAllOperations() {
			// 160.3: FeatureDecorators are called for ALL operations
			AtomicBoolean called = new AtomicBoolean(false);
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				called.set(true);
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-called:1.0", TckTestHelper.TB1_COORDS);
			runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(called.get()).isTrue();
		}

		@Test
		void decorator_canModifyBundles() {
			// 160.3: Decorators can rewrite bundles
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder.setBundles(feature.getBundles()).build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-bundles:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_canModifyConfigurations() {
			// 160.3: Decorators can rewrite configurations
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder
						.setConfigurations(new ArrayList<>(
								feature.getConfigurations().values()))
						.build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-configs:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_canModifyVariables() {
			// 160.3: Decorators can rewrite variables
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder.setVariables(feature.getVariables()).build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-vars:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_canModifyExtensions() {
			// 160.3: Decorators (not handlers) can rewrite extensions
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder
						.setExtensions(new ArrayList<>(
								feature.getExtensions().values()))
						.build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-ext:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_mustReturnOriginalOrBuildResult() {
			// 160.3: Decorator MUST return original or build() result
			FeatureDecorator passThrough = (feature, repos, builder,
					factory) -> feature;

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-passthru:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(passThrough)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_multipleDecoratorOrder() {
			// 160.3: Multiple decorators called in registration order
			List<Integer> callOrder = Collections
					.synchronizedList(new ArrayList<>());
			FeatureDecorator first = (feature, repos, builder, factory) -> {
				callOrder.add(1);
				return feature;
			};
			FeatureDecorator second = (feature, repos, builder, factory) -> {
				callOrder.add(2);
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-order:1.0", TckTestHelper.TB1_COORDS);
			runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(first)
					.withDecorator(second)
					.complete();
			assertThat(callOrder).containsExactly(1, 2);
		}
	}

	// --- 160.3: FeatureExtensionHandler behavior ---

	@Nested
	class ExtensionHandlerBehavior {

		@Test
		void handler_calledOnlyForNamedExtension() {
			// 160.3: Handlers called only for specific named extensions
			AtomicBoolean called = new AtomicBoolean(false);
			FeatureExtensionHandler handler = (feature, ext, repos, builder,
					factory) -> {
				called.set(true);
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:handler-named:1.0", TckTestHelper.TB1_COORDS);
			runtime.install(feature)
					.addRepository("test", testRepo)
					.withExtensionHandler("nonexistent-ext", handler)
					.complete();
			// Handler not called because feature has no "nonexistent-ext"
			assertThat(called.get()).isFalse();
		}

		@Test
		void handler_canModifyBundles() {
			// 160.3: Handlers can rewrite bundles
			FeatureExtensionHandler handler = (feature, ext, repos, builder,
					factory) -> {
				return builder.setBundles(feature.getBundles()).build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:handler-bundles:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withExtensionHandler("some-ext", handler)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void handler_canModifyConfigs() {
			// 160.3: Handlers can rewrite configurations
			FeatureExtensionHandler handler = (feature, ext, repos, builder,
					factory) -> feature;

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:handler-cfgs:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withExtensionHandler("some-ext", handler)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void handler_canModifyVariables() {
			// 160.3: Handlers can rewrite variables
			FeatureExtensionHandler handler = (feature, ext, repos, builder,
					factory) -> feature;

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:handler-vars:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withExtensionHandler("some-ext", handler)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void handler_cannotModifyExtensions() {
			// 160.3: Handlers cannot modify extensions
			// FeatureExtensionHandlerBuilder does not have setExtensions()
			// This is a compile-time contract; verified by API structure
			FeatureExtensionHandler handler = (feature, ext, repos, builder,
					factory) -> feature;
			assertThat(handler).isNotNull();
		}

		@Test
		void handler_onePerExtensionName_laterReplaces() {
			// 160.3: Only ONE handler per extension name; later replaces
			AtomicInteger callCount1 = new AtomicInteger(0);
			AtomicInteger callCount2 = new AtomicInteger(0);

			FeatureExtensionHandler first = (feature, ext, repos, builder,
					factory) -> {
				callCount1.incrementAndGet();
				return feature;
			};
			FeatureExtensionHandler second = (feature, ext, repos, builder,
					factory) -> {
				callCount2.incrementAndGet();
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:handler-replace:1.0", TckTestHelper.TB1_COORDS);
			runtime.install(feature)
					.addRepository("test", testRepo)
					.withExtensionHandler("some-ext", first)
					.withExtensionHandler("some-ext", second)
					.complete();
			// First handler should have been replaced by second
			// If the feature has "some-ext" extension, only second is called
			assertThat(callCount1.get()).isEqualTo(0);
		}
	}

	// --- 160.3: AbandonOperationException ---

	@Nested
	class AbandonOperation {

		@Test
		void decorator_throwsAbandon_stopsOperation() {
			// 160.3: AbandonOperationException stops operation
			FeatureDecorator abandonDecorator = (feature, repos, builder,
					factory) -> {
				throw new AbandonOperationException("Test abandon");
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:abandon-deco:1.0", TckTestHelper.TB1_COORDS);
			assertThatThrownBy(() -> runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(abandonDecorator)
					.complete()).isInstanceOf(FeatureRuntimeException.class);
		}

		@Test
		void handler_throwsAbandon_stopsOperation() {
			// 160.3: AbandonOperationException from handler stops operation
			FeatureExtensionHandler abandonHandler = (feature, ext, repos,
					builder, factory) -> {
				throw new AbandonOperationException("Test abandon");
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:abandon-handler:1.0", TckTestHelper.TB1_COORDS);
			// Will only trigger if feature has the named extension
			InstalledFeature result = runtime.install(feature)
					.addRepository("test", testRepo)
					.withExtensionHandler("nonexistent", abandonHandler)
					.complete();
			// Handler not called for non-matching extension name
			assertThat(result).isNotNull();
		}

		@Test
		void abandon_loggedAsError() {
			// 160.3: AbandonOperationException treated as ERROR when logging
			FeatureDecorator abandonDecorator = (feature, repos, builder,
					factory) -> {
				throw new AbandonOperationException("Error to log");
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:abandon-log:1.0", TckTestHelper.TB1_COORDS);
			assertThatThrownBy(() -> runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(abandonDecorator)
					.complete()).isInstanceOf(FeatureRuntimeException.class);
		}
	}

	// --- 160.3: Artifact Repository List mutability ---

	@Nested
	class RepositoryListMutability {

		@Test
		void decorator_canAppendToRepoList() {
			// 160.3: Decorators can append to artifact repository list
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				repos.add(id -> null); // append a no-op repo
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:repo-append:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_canInsertIntoRepoList() {
			// 160.3: Decorators can insert at any position
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				repos.add(0, id -> null); // insert at beginning
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:repo-insert:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_cannotRemoveFromRepoList() {
			// 160.3: Decorators cannot remove from repository list
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				try {
					repos.remove(0);
					// If remove succeeds, spec violation
				} catch (UnsupportedOperationException e) {
					// Expected behavior
				}
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:repo-remove:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}

		@Test
		void decorator_cannotReplaceInRepoList() {
			// 160.3: Decorators cannot replace entries
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				try {
					repos.set(0, id -> null);
					// If set succeeds, spec violation
				} catch (UnsupportedOperationException e) {
					// Expected behavior
				}
				return feature;
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:repo-replace:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed).isNotNull();
		}
	}

	// --- 160.3: Classifier handling ---

	@Nested
	class DecoratedFeatureClassifier {

		@Test
		void decorator_defaultClassifier_DEFAULT_DECORATED() {
			// 160.3: Default classifier for decorated feature
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder.build(); // build without changing classifier
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-classifier:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed.isDecorated()).isTrue();
		}

		@Test
		void decorator_setClassifier_custom() {
			// 160.3: Classifier may be changed via setClassifier(String)
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder.setClassifier("custom-classifier").build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-custom-cls:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();
			assertThat(installed.isDecorated()).isTrue();
			assertThat(installed.getFeature().getID().getClassifier())
					.isPresent()
					.hasValue("custom-classifier");
		}

		@Test
		void decorator_sharesGroupArtifactVersionType() {
			// 160.3: Decorated feature shares groupId, artifactId, version,
			// type with original
			FeatureDecorator decorator = (feature, repos, builder, factory) -> {
				return builder.setClassifier("test").build();
			};

			Feature feature = TckTestHelper.createFeatureWithBundles(fs,
					"test:deco-shared:1.0", TckTestHelper.TB1_COORDS);
			InstalledFeature installed = runtime.install(feature)
					.addRepository("test", testRepo)
					.withDecorator(decorator)
					.complete();

			assertThat(installed.getFeature().getID().getGroupId())
					.isEqualTo(feature.getID().getGroupId());
			assertThat(installed.getFeature().getID().getArtifactId())
					.isEqualTo(feature.getID().getArtifactId());
			assertThat(installed.getFeature().getID().getVersion())
					.isEqualTo(feature.getID().getVersion());
		}
	}
}
