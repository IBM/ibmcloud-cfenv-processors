package com.ibm.boot.autoconfiguration.kafka;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONPropertiesMapper {
    private final List<Class<?>> stream;

    public JSONPropertiesMapper(String[] trustedPackages) {
        if (trustedPackages == null) {
            stream = new ArrayList<>(0);
        } else {
            this.stream = Arrays.stream(trustedPackages)
                    .map(s -> new Reflections(s, new SubTypesScanner(false)))
                    .flatMap((Function<Reflections, Stream<Class<?>>>) reflections -> reflections.getSubTypesOf(Object.class).stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    public String getJsonMapping() {
        List<String> collect = stream.stream()
                .map(aClass -> aClass.getName() + ":" + aClass.getName())
                .collect(Collectors.toList());
        return String.join(",", collect);
    }

    public Map<String, Class<?>> getClassMapping() {
        return stream.stream()
                .collect(Collectors.toMap(aClass -> aClass.getName(), aClass -> aClass));
    }

}
