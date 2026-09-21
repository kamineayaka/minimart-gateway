package com.minimart.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ApplicationConfigContractTest {

	@Test
	void applicationYamlIsSelfContainedWithoutNacos() throws Exception {
		String yaml = Files.readString(Path.of("src/main/resources/application.yaml"));
		assertThat(yaml).doesNotContain("nacos");
		assertThat(yaml).doesNotContain("lb://");
		assertThat(yaml).contains("minimart.services.member.host");
		assertThat(yaml).doesNotContain("compatibility-verifier:\n      enabled: false");
	}

	@Test
	void runtimeProfileIsNotRequired() {
		assertThat(Path.of("src/main/resources/application-runtime.yaml")).doesNotExist();
	}
}
