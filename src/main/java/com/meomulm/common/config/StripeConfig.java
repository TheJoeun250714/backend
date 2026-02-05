
package com.meomulm.common.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Stripe SDK 초기화 설정
 *
 * config.properties 에 다음과 같이 추가:
 *   stripe.secret.key=${STRIPE_SECRET_KEY}
 *
 * 환경변수 STRIPE_SECRET_KEY 값은:
 *   테스트 → sk_test_xxxxx
 *   프로덕션 → sk_live_xxxxx
 */

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        System.out.println("[Stripe] API 키 설정 완료");
    }
}
