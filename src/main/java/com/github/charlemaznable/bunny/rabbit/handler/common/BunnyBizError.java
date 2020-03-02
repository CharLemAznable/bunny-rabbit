package com.github.charlemaznable.bunny.rabbit.handler.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum BunnyBizError {

    QUERY_FAILED("QUERY_FAILED", "Balance Query Failed"),

    CHARGE_FAILED("CHARGE_FAILED", "Account Charge Failed"),

    DEDUCT_FAILED("DEDUCT_FAILED", "Balance Deduct Failed"),

    ADVANCE_FAILED("ADVANCE_FAILED", "Payment Advance Failed"),

    COMMIT_QUERY_FAILED("COMMIT_QUERY_FAILED", "Payment Commit Query Sequence Failed"),

    COMMIT_FAILED("COMMIT_FAILED", "Payment Commit Failed"),

    ROLLBACK_QUERY_FAILED("ROLLBACK_QUERY_FAILED", "Payment Rollback Query Sequence Failed"),

    ROLLBACK_FAILED("ROLLBACK_FAILED", "Payment Rollback Failed");

    private String respCode;
    private String respDesc;
}
