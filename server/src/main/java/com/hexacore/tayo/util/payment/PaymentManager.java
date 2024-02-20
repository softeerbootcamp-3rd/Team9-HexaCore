package com.hexacore.tayo.util.payment;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.util.payment.TossPaymentDto.TossBilling;
import com.hexacore.tayo.util.payment.TossPaymentDto.TossBillingRequest;
import com.hexacore.tayo.util.payment.TossPaymentDto.TossConfirmRequest;
import com.hexacore.tayo.util.payment.TossPaymentDto.TossPaymentResponse;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentManager {
    private final RestTemplate restTemplate;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    /* Billing Key 발급 */
    public TossBilling requestBillingKey(String customerKey, String authKey) {
        String encodedCredentials = getEncodedCredentials();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedCredentials);
        HttpEntity<TossBillingRequest> requestHttpEntity = new HttpEntity<>(
                TossBillingRequest.builder()
                        .customerKey(customerKey)
                        .authKey(authKey)
                        .build(), headers
        );

        try {
            ResponseEntity<TossBilling> response = restTemplate.exchange(
                    "https://api.tosspayments.com/v1/billing/authorizations/issue",
                    HttpMethod.POST,
                    requestHttpEntity,
                    TossBilling.class
            );

            if (response.getBody() == null || !response.getStatusCode().equals(HttpStatus.OK)) {
                // 빌링 키 발급 실패
                throw new GeneralException(ErrorCode.TOSS_PAYMENTS_FAILED);
            }
            return response.getBody();
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.TOSS_PAYMENTS_FAILED, e.getMessage());
        }
    }

    /* 빌링 결제(자동 결제) 요청 */
    public TossPaymentResponse confirmBilling(Integer amount, String orderName, String customerName, String customerKey, String billingKey) {
        String encodedCredentials = getEncodedCredentials();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedCredentials);

        String orderId = UUID.randomUUID().toString();
        HttpEntity<TossConfirmRequest> requestEntity = new HttpEntity<>(
                TossConfirmRequest.builder().amount(amount).customerKey(customerKey).orderId(orderId).orderName(orderName).customerName(customerName).build(), headers);
        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    "https://api.tosspayments.com/v1/billing/" + billingKey,
                    HttpMethod.POST,
                    requestEntity,
                    TossPaymentResponse.class
            );

            if (response.getBody() == null || !"DONE".equals(response.getBody().getStatus())) {
                // 결제 승인 실패
                throw new GeneralException(ErrorCode.TOSS_PAYMENTS_FAILED);
            }
            return response.getBody();
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.TOSS_PAYMENTS_FAILED, e.getMessage());
        }
    }

    private String getEncodedCredentials() {
        String credentials = tossSecretKey + ":";
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
