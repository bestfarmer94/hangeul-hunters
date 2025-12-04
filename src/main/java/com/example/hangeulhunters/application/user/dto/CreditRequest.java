package com.example.hangeulhunters.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "크레딧 충전/차감 요청")
public class CreditRequest {

    @NotNull
    @Min(value = 1, message = "Amount must be at least 1")
    @Max(value = 10000, message = "Amount cannot exceed 10000")
    private Integer amount;
}
