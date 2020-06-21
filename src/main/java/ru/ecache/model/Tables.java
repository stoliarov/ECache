package ru.ecache.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Tables {

    /**
     * Имя таблицы или полное имя класса JPA-сущности.
     */
    private List<String> tableIds;

    public Tables() {
        tableIds = new ArrayList<>();
    }

    public Tables(List<String> tableIds) {
        this();
        this.tableIds.addAll(tableIds);
    }

    public void merge(List<String> tableIds) {

        if (tableIds == null) {
            return;
        }

        this.tableIds.addAll(tableIds);
    }
}
