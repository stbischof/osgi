package org.osgi.test.cases.converter.junit.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;

public class RecordTest {

	public static record RecordA( //
			int propInt, //
			boolean propBool, //
			int propIntNull, //
			boolean propBoolNull, //
			Integer propInteger, //
			String propString, //
			String propStringNull, //
			String propStringCaseInSENSITIVE, //
			String other) {
	}

	public static record RecordNoParam() {

	}

	public static record RecordException(String nonNullString) {
		public RecordException {

			if (nonNullString == null) {
				throw new IllegalArgumentException("nonNullEx");
			}
		}
	}

	// java.lang.VerifyError: Control flow falls through code end
	// Error exists in the bytecode
	// in java 17 when calling constructor
	public static record RecordVerifyException(String nonNullString) {
		public RecordVerifyException {
			throw new IllegalArgumentException();
		}
	}

	// test with java 17
	// jshell> record RecordVerifyException(String nonNullString) {
	// ...> public RecordVerifyException {
	// ...> throw new IllegalArgumentException();
	// ...> }
	// ...> }
	// | created record RecordVerifyException
	//
	// jshell> new RecordVerifyException("");
	// | Exception java.lang.IllegalArgumentException
	// | at RecordVerifyException.<init> (#1:3)
	// | at (#2:1)

	@Test()
	void testMapToRecordVerifyException() throws Exception {

		Map<String,Object> map = new HashMap<>();
		map.put("nonNullString", null);
		Converter c = Converters.standardConverter();
		Assertions.assertThatExceptionOfType(ConversionException.class)
				.isThrownBy(() -> {
					c.convert(map).to(RecordVerifyException.class);
				});
	}

	@Test
	void testMapToRecordException() throws Exception {
		Map<String,Object> map = new HashMap<>();
		map.put("nonNullString", null);
		Converter c = Converters.standardConverter();
		Assertions.assertThatExceptionOfType(ConversionException.class)
				.isThrownBy(() -> {
					c.convert(map).to(RecordException.class);
				});
	}

	@Test
	void testMapToRecord() throws Exception {

		Map<String,Object> map = new HashMap<>();
		map.put("propInt", 1);
		map.put("propBool", true);
		map.put("propIntNull", null);
		map.put("propBoolNull", null);
		map.put("propInteger", Integer.valueOf(1));
		map.put("propString", "String");
		map.put("propStringNull", null);
		map.put("propStringCaseInSensitive", "foo");
		// not add the Entry "other"

		Converter c = Converters.standardConverter();
		RecordA r = c.convert(map).to(RecordA.class);
		assertThat(r.propInt).isEqualTo(1);
		assertThat(r.propBool).isEqualTo(true);
		assertThat(r.propIntNull).isEqualTo(0);
		assertThat(r.propBoolNull).isEqualTo(false);
		assertThat(r.propInteger).isEqualTo(1);
		assertThat(r.propString).isEqualTo("String");
		assertThat(r.propStringNull).isNull();
		assertThat(r.propStringCaseInSENSITIVE).isEqualTo("foo");
		assertThat(r.other).isNull();
	}

	@Test
	void testRecordToMap() throws Exception {
		Converter c = Converters.standardConverter();

		RecordA record = new RecordA(1, true, 1, true, 2, "String", null,
				"foo", "foo2");

		Map<String,Object> map = c.convert(record)
				.to(new TypeReference<Map<String,Object>>() {
				});

		assertThat(map).containsEntry("propInt", 1);
		assertThat(map).containsEntry("propBool", true);
		assertThat(map).containsEntry("propIntNull", 1);
		assertThat(map).containsEntry("propBoolNull", true);
		assertThat(map).containsEntry("propInteger", 2);
		assertThat(map).containsEntry("propString", "String");
		assertThat(map).containsEntry("propStringNull", null);
		assertThat(map).containsEntry("propStringCaseInSENSITIVE", "foo");
		assertThat(map).containsEntry("other", "foo2");
		assertThat(map).hasSize(9);

	}

	@Test
	void testRecordToMap_noParam() throws Exception {
		Converter c = Converters.standardConverter();

		RecordNoParam record = new RecordNoParam();

		Map<String,Object> map = c.convert(record)
				.to(new TypeReference<Map<String,Object>>() {
				});

		assertThat(map).hasSize(0);

	}

	@Test
	void testMapToRecord_noParam() throws Exception {
		Converter c = Converters.standardConverter();
		Map<String,Object> map = new HashMap<>();
		RecordNoParam r = c.convert(map).to(RecordNoParam.class);
		assertThat(r).isNotNull();

		map.put("foo", "bar");
		r = c.convert(map).to(RecordNoParam.class);
		assertThat(r).isNotNull();
	}
}
