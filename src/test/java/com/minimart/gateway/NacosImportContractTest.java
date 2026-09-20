package com.minimart.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class NacosImportContractTest {

	@Test
	void laptopYamlMarksNacosOptional() throws Exception {
		String yaml = Files.readString(Path.of("src/main/resources/application.yaml"));
		assertThat(yaml).contains("optional:nacos:minimart-common.yaml?group=MINIMART");
		assertThat(yaml).contains("optional:nacos:${spring.application.name}.yaml?group=MINIMART");
		assertThat(yaml).doesNotContain("compatibility-verifier:\n      enabled: false");
	}

	@Test
	void runtimeProfileRequiresNacos() throws Exception {
		String yaml = Files.readString(Path.of("src/main/resources/application-runtime.yaml"));
		assertThat(yaml).contains("- nacos:minimart-common.yaml?group=MINIMART");
		assertThat(yaml).contains("- nacos:${spring.application.name}.yaml?group=MINIMART");
		assertThat(yaml).doesNotContain("optional:nacos");
	}
}
