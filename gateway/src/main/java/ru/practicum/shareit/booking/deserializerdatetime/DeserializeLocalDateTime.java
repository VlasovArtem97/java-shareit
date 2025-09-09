//package ru.practicum.shareit.booking.deserializerdatetime;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class DeserializeLocalDateTime extends JsonDeserializer<LocalDateTime> {
//
//    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//
//    @Override
//    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        String dateStr = p.getText();
//        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
//        ZonedDateTime zonedDateTime = ZonedDateTime.now();
//
//        return localDateTime.atZone(ZoneOffset.systemDefault())
//                .withZoneSameInstant(ZoneOffset.UTC)
//                .toLocalDateTime();
//    }
//}
