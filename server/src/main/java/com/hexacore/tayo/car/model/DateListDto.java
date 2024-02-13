package com.hexacore.tayo.car.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DateListDto {

    private static class LocalDateTimeListDeserializer extends JsonDeserializer<List<List<LocalDateTime>>> {

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm:ss"
        );

        @Override
        public List<List<LocalDateTime>> deserialize(JsonParser jsonParser,
                DeserializationContext deserializationContext)
                throws IOException {
            ArrayNode arrayNode = jsonParser.getCodec().readTree(jsonParser);

            List<List<LocalDateTime>> result = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                ArrayNode innerArrayNode = (ArrayNode) arrayNode.get(i);
                List<LocalDateTime> innerList = new ArrayList<>();

                for (int j = 0; j < innerArrayNode.size(); j++) {
                    String dateTimeString = innerArrayNode.get(j).asText();
                    LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
                    innerList.add(localDateTime);
                }

                result.add(innerList);
            }

            return result;
        }
    }

    private static class LocalDateTimeListSerializer extends JsonSerializer<List<List<LocalDateTime>>> {

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm:ss"
        );

        @Override
        public void serialize(List<List<LocalDateTime>> value, JsonGenerator jsonGenerator,
                SerializerProvider serializers)
                throws IOException {
            jsonGenerator.writeStartArray();

            for (List<LocalDateTime> innerList : value) {
                jsonGenerator.writeStartArray();

                for (LocalDateTime localDateTime : innerList) {
                    String dateTimeString = localDateTime.format(DATE_TIME_FORMATTER);
                    jsonGenerator.writeString(dateTimeString);
                }

                jsonGenerator.writeEndArray();
            }

            jsonGenerator.writeEndArray();
        }
    }

    @JsonSerialize(using = LocalDateTimeListSerializer.class)
    @JsonDeserialize(using = LocalDateTimeListDeserializer.class)
    private List<List<LocalDateTime>> dates;
}
