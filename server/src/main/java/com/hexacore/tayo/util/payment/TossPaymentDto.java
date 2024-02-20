package com.hexacore.tayo.util.payment;

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
}
