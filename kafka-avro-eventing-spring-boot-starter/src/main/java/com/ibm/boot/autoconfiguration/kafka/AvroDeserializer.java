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
package com.ibm.boot.autoconfiguration.kafka;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private static final Logger logger = LoggerFactory.getLogger(AvroDeserializer.class);

    private final List<Schema> schemas = new ArrayList<>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        List<Class<SpecificRecordBase>> classes = (List<Class<SpecificRecordBase>>) configs.get("classes");
        List<Schema> stuff = classes.stream().map(recordBaseClass -> {
            try {
                return recordBaseClass.newInstance().getSchema();
            } catch (Exception e) {
                throw new DeserializationException("Test", null, false, e);
            }
        }).collect(Collectors.toList());
        schemas.addAll(stuff);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        Optional<T> optional = schemas.parallelStream()
                .map(schema -> new SpecificDatumReader(schema))
                .map(specificDatumReader -> {
                    try {
                        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
                        return (T) specificDatumReader.read(null, decoder);
                    } catch (Exception e) {
                        logger.error("Can't deserialize for topic='" + topic, e);
                        throw new DeserializationException("Can't deserialize for topic='" + topic, data, false, e);
                    }
                })
                .filter(o -> o != null)
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        if (data == null) {
            return null;
        }

        Header header = headers.headers("avro-classname").iterator().next();
        SpecificDatumReader<T> specificDatumReader = new SpecificDatumReader<>();
        if (specificDatumReader == null) {
            return null;
        }

        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        try {
            return specificDatumReader.read(null, decoder);
        } catch (IOException ex) {
            throw new SerializationException(
                    "Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
        }

//        try {
//            T result = null;
//
////                LOGGER.debug("data='{}'", DatatypeConverter.printHexBinary(data));
//
//            DatumReader<GenericRecord> datumReader =
//                    new SpecificDatumReader<>(targetType.newInstance().getSchema());
//            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
//
//            result = (T) datumReader.read(null, decoder);
//            LOGGER.debug("deserialized data='{}'", result);
//            return result;
//        } catch (Exception ex) {
//            throw new SerializationException(
//                    "Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
//        }
    }

    @Override
    public void close() {

    }
}
