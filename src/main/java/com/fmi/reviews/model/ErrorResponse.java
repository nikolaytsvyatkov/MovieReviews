package com.fmi.reviews.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponse {
    @NonNull
    private int code;
    @NonNull
    private String message;

    List<String> violations;
}
