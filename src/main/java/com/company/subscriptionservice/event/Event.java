package com.company.subscriptionservice.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

public class Event<K, T> {

    private final K key;

    private final T data;

    private final ZonedDateTime eventCreatedAt;

    public Event() {
        this.key = null;
        this.data = null;
        this.eventCreatedAt = null;
    }

    public Event(K key, T data) {
        this.key = key;
        this.data = data;
        this.eventCreatedAt = now();
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public ZonedDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}
