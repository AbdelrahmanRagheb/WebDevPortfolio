package com.taskasync.taskservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDetails {
    @JsonProperty("changes")
    private List<Change> changes;



    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Change {
        @JsonProperty("field")
        private String field;

        @JsonProperty("old")
        private String oldValue;

        @JsonProperty("new")
        private String newValue;
    }
}