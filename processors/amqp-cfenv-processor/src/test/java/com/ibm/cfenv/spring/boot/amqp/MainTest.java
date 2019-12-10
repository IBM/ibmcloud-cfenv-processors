/*
 *   Copyright 2019 IBM Corporation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.ibm.cfenv.spring.boot.amqp;

import io.pivotal.cfenv.test.AbstractCfEnvTests;
import org.assertj.core.api.Assertions;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MainTest extends AbstractCfEnvTests {
//    @Test
    public void testStuff() throws IOException {
        try {
            System.setProperty("VCAP_APPLICATION", "yes");

            File file = ResourceUtils.getFile("classpath:multiple-bindings-vcap-services.json");
            String fileContents = new String(Files.readAllBytes(file.toPath()));
            mockVcapServices(fileContents);
            Environment environment = getEnvironment();
            String property = environment.getProperty("sslcontext.contexts.amqp.trustedcert");
            String validSelfSignedCert = "MIIDfDCCAmSgAwIBAgIJANbFzLaShHP1MA0GCSqGSIb3DQEBCwUAMGwxEDAOBgNVBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24xEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEDAOBgNVBAMTB1Vua25vd24wHhcNMTkxMTIxMDAzNDAxWhcNMjAwMjE5MDAzNDAxWjBsMRAwDgYDVQQGEwdVbmtub3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYDVQQKEwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMRAwDgYDVQQDEwdVbmtub3duMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArH6mfoPuQoKKT/7V7e6LYuF08rE2IjksKWG8YhPgjDmxkRlweneRVrN93NJ8l0C9H3st7Val+fDbpECQe17FACde8fE4zYcJcPptanpNO4c5UCGkLmh/33fasMpoJyDJobseXaeRnI8poXik7yY1N2v5DvfhkB9BxlHcqpPVi4oaAfXdBkSdHSxtK5Z/1mRqHww80KakX2VX0mXL8b9pAlf3j8RA91T93bbRTpg6pNihpy0UScRhMTDZB8Ps5RE6e/AnYLOOp4dbelAh2HG0pcNHHwkdAh+kae61MNnIuPSgqgmVQY6dlmpRSa7X2B0q/QFoXdW4CgALZaH+G93mzQIDAQABoyEwHzAdBgNVHQ4EFgQUK/xh6cLkXsqdijElSEzHiCGU4yIwDQYJKoZIhvcNAQELBQADggEBAIqRZkEmpy9GlQ/ucNH8bIw0KY2Mz1ddkSohg+yuKBYgP/h1uku5aWgJZdWOKjrhm1e2RryUFgaqylkgBc1AQWEa54TbDMNgmYEmLaKZp+C+6nYHx2UyMToAa7F/exLLcuqmspcmfuS/zMZkHEuyHQS561PuzfHnRlfzdykZPfe2wInSUkWY825OyYjWL6mEAkiYMRYLu7r5+Y7aLqLMIU8GMc7sYUuRDigKLAoaVH2rs0ARzPepHGmHZ2AJ9Z+h3IfQzgBGW+JlwINymGLfsDBqVnlM8HYGPydThGCFOVPTzUH5GFAv6z3EmtWrEOTfSRnvlfFqgPnbbvFoKvhDeGE=";
            Assertions.assertThat(property).isEqualTo(validSelfSignedCert);
        } finally {
            System.clearProperty("VCAP_APPLICATION");
        }
    }
}
