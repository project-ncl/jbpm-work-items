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
package org.jbpm.process.longrest.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jbpm.process.longrest.RemoteInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Json {

    private static final Logger logger = LoggerFactory.getLogger(Json.class);

    public static <T> T escape(T o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return (T) StringEscapeUtils.escapeJson((String) o);
        } else if (o instanceof Map) {
            Map<?, ?> m = (Map) o;
            Map result = new HashMap();
            for (Map.Entry e : m.entrySet()) {
                result.put(escape(e.getKey()), escape(e.getValue()));
            }
            return (T) result;
        } else if (o instanceof List) {
            return (T) ((List<?>) o).stream()
                    .map(e -> escape(e))
                    .collect(Collectors.toList());
        } else if (o instanceof Set) {
            return (T) ((Set<?>) o).stream()
                    .map(e -> escape(e))
                    .collect(Collectors.toSet());
        } else {
            return o;
        }
    }

    public static <T> T unescape(T o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return (T) StringEscapeUtils.unescapeJson((String) o);
        } else if (o instanceof Map) {
            Map<?, ?> m = (Map) o;
            Map result = new HashMap();
            for (Map.Entry e : m.entrySet()) {
                result.put(unescape(e.getKey()), unescape(e.getValue()));
            }
            return (T) result;
        } else if (o instanceof List) {
            return (T) ((List<?>) o).stream()
                    .map(e -> unescape(e))
                    .collect(Collectors.toList());
        } else if (o instanceof Set) {
            return (T) ((Set<?>) o).stream()
                    .map(e -> unescape(e))
                    .collect(Collectors.toSet());
        } else {
            return o;
        }
    }

    public static String sanitiseSerializedObject(String serialized) throws JsonProcessingException {
        ObjectMapper mapper = Mapper.getInstance();
        Map object = mapper.readValue(serialized, Map.class);
        return mapper.writeValueAsString(sanitizeMap(object));
    }

    public static Map<String, ?> sanitizeMap(Map<String, ?> object) {
        return object.entrySet().stream()
                //Collectors.toMap fails on null values https://bugs.openjdk.java.net/browse/JDK-8148463
                .collect(HashMap::new, (m,entry) -> {
                    logger.trace("Entry: {} - {}, ", entry.getKey(), entry.getValue());
                    if (entry.getValue() != null && entry.getValue() instanceof Map) {
                        m.put(entry.getKey(), sanitizeMap((Map<String, ?>)entry.getValue()));
                    } else {
                        m.put(entry.getKey(), sanitizeValue(entry));
                    }
                }, HashMap::putAll);
    }

    private static Object sanitizeValue(Map.Entry<String, ?> entry) {
        if ("authorization".equalsIgnoreCase(entry.getKey())) {
            return "***";
        } else {
            return entry.getValue();
        }
    }
}
