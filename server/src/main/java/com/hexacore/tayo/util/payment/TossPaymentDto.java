package com.hexacore.tayo.util.payment;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class TossPaymentDto {
    @Getter
    @Builder
    public static class TossBillingRequest {
        private String customerKey;
        private String authKey;
    }

    @Getter
    @Builder
    public static class TossConfirmRequest {
        private Integer amount;
        private String customerKey;
        private String orderId;
        private String orderName;
    }

    @Getter
    public static class TossBilling {
        private String mid;
        private String customerKey;
        private String billingKey;
        private String authenticatedAt;
        private String method;
        private TossBillingCard card;
    }

    @Getter
    public static class TossBillingCard {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }

    @Getter
    public static class TossPaymentResponse {
        private String version;
        private String paymentKey;
        private String type;
        private String orderId;
        private String orderName;
        private String mid;
        private String currency;
        private String method;
        private Integer totalAmount;
        private Integer balanceAmount;
        private String status;
        private String requestedAt;
        private String approvedAt;
        private List<TossCancel> cancels;
    }

    @Getter
    private static class TossCancel {
        private int cancelAmount;
        private String cancelReason;
        private int taxFreeAmount;
        private int taxExemptionAmount;
        private int refundableAmount;
        private int easyPayDiscountAmount;
        private String canceledAt;
        private String transactionKey;
        private String receiptKey;
    }
}
