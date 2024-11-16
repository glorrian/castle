package ru.bivchallenge.persistence

import ru.bivchallenge.dto.Entity

class MockEntity implements Entity {
    long id
    String name
    String description

    MockEntity(long id, String name, String description) {
        this.id = id
        this.name = name
        this.description = description
    }

    long id() {
        return id
    }
}
