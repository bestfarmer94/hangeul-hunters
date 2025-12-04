package com.example.hangeulhunters.application.user.controller;

import com.example.hangeulhunters.application.user.dto.CreditRequest;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.application.user.service.CreditService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/credit")
@RequiredArgsConstructor
@Tag(name = "Credit", description = "크레딧 포인트 관리 API")
public class CreditController extends ControllerSupport {

    private final CreditService creditService;

    /**
     * Charge credit points
     */
    @PostMapping("/charge")
    @Operation(summary = "크레딧 충전", description = "사용자의 크레딧 포인트를 충전합니다")
    public ResponseEntity<UserDto> chargeCredit(@RequestBody @Valid CreditRequest request) {
        UserDto user = creditService.chargeCredit(getCurrentUserId(), request.getAmount());
        return ResponseEntity.ok(user);
    }

    /**
     * Deduct credit points
     */
    @PostMapping("/deduct")
    @Operation(summary = "크레딧 차감", description = "사용자의 크레딧 포인트를 차감합니다")
    public ResponseEntity<UserDto> deductCredit(@RequestBody @Valid CreditRequest request) {
        UserDto user = creditService.deductCredit(getCurrentUserId(), request.getAmount());
        return ResponseEntity.ok(user);
    }

    /**
     * Get credit balance
     */
    @GetMapping
    @Operation(summary = "크레딧 잔액 조회", description = "사용자의 현재 크레딧 포인트를 조회합니다")
    public ResponseEntity<Integer> getBalance() {
        Integer balance = creditService.getCreditBalance(getCurrentUserId());
        return ResponseEntity.ok(balance);
    }
}
