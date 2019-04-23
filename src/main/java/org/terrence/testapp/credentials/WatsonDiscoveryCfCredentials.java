/*
 * Copyright 2019 the original author or authors.
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
package org.terrence.testapp.credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;

import org.terrence.testapp.uri.WatsonDiscoveryUriInfo;

/**
 * Access service credentials.
 *
 * @author Mark Pollack
 */
public class WatsonDiscoveryCfCredentials extends CfCredentials {

    public WatsonDiscoveryCfCredentials(Map<String, Object> credentialsData) {
        super(credentialsData);
    }

    /**
     * Return the username, under the keys 'username' and 'user' in the credential
     * map. If it is not found try to obtain the usernam from the uri field.
     * 
     * @return value of the username, null if not found.
     */
    public String getVersion() {
        String version = getString("version", "versionDate");
        if (version != null) {
            return version;
        }
        UriInfo uriInfo = createOrGetUriInfo();
        return (uriInfo != null) ? uriInfo.getVersion() : null;
    }

    /**
     * Return UriInfo derived from URI field
     * 
     * @param uriScheme a uri scheme name to use as a prefix to search of the uri
     *                  field
     * @return the UriInfo object
     */
    public UriInfo getUriInfo(String uriScheme) {
        String uri = getUri(uriScheme);
        UriInfo uriInfo;
        if (uri == null) {
            String hostname = getHost();
            String port = getPort();
            String username = getUsername();
            String password = getPassword();
            String version = getVersion();
            String databaseName = getName();
            uriInfo = new UriInfo(uriScheme, hostname, Integer.valueOf(port), username, password, version,
                    databaseName);
        } else {
            uriInfo = new UriInfo(uri);
        }
        return uriInfo;
    }

}