package com.hexacore.tayo.reservation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class TossPayment {

    @Getter
    @Builder
    public static class TossApproveRequest {
        private String paymentKey;
        private String orderId;
        private Integer amount;
    }

    @Getter
    @Builder
    public static class TossCancelRequest {
        private String cancelReason;
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
