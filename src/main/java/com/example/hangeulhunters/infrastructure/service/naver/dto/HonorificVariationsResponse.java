package com.example.hangeulhunters.infrastructure.service.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HonorificVariationsResponse {

    @Schema(description = "친밀함 레벨 Low", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lowIntimacyExpressions")
    private ExpressionsByFormality lowIntimacyExpressions;

    @Schema(description = "친밀함 레벨 Medium", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("mediumIntimacyExpressions")
    private ExpressionsByFormality mediumIntimacyExpressions;

    @Schema(description = "친밀함 레벨 High", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("highIntimacyExpressions")
    private ExpressionsByFormality highIntimacyExpressions;

    @Schema(description = "예시 및 설명", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("explain")
    private String explain;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpressionsByFormality {
        @Schema(description = "비격식체 표현", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("lowFormality")
        private String lowFormality;

        @Schema(description = "격식체 표현", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("mediumFormality")
        private String mediumFormality;

        @Schema(description = "격식체 표현", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("highFormality")
        private String highFormality;
    }
}