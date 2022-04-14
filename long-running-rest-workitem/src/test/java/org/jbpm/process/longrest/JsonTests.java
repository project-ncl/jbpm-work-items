/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
 */
package org.jbpm.process.longrest;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jbpm.process.longrest.util.Json;
import org.jbpm.process.longrest.util.Mapper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class JsonTests {

    @Test
    public void shouldSanitizeSerializedObject() throws JsonProcessingException {
        A a = new A();
        String serialized = Mapper.getInstance().writeValueAsString(a);
        String sanitized = Json.sanitiseSerializedObject(serialized);
        A sanitizedObject = Mapper.getInstance().readValue(sanitized, A.class);
        Assert.assertEquals("***", ((B)sanitizedObject.map.get("B")).map.get("Authorization"));
    }

    static class A {
        Map<String, B> map = Collections.singletonMap("B", new B());

        public void setMap(Map<String, B> map) {
            this.map = map;
        }

        public Map<String, B> getMap() {
            return map;
        }
    }

    static class B {
        Map<String, String> map = Collections.singletonMap("Authorization", "Secret token");

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        public Map<String, String> getMap() {
            return map;
        }
    }
}
