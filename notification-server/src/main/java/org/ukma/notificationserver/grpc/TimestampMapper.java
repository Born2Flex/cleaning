package org.ukma.notificationserver.grpc;

import com.google.protobuf.Timestamp;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TimestampMapper {

    default Timestamp mapToTimestamp(LocalDateTime orderTime) {
        if (orderTime == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(orderTime.toEpochSecond(ZoneOffset.UTC))
                .setNanos(orderTime.getNano())
                .build();
    }

    default LocalDateTime mapToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneOffset.UTC);
    }
}
