package com.exavalu.customer.product.portal.reusable.service;

import org.springframework.stereotype.Service;

@Service
public class CardValidatorService {

    public boolean isValidCardNumber(String cardNumber) {
        int nDigits = cardNumber.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = cardNumber.charAt(i) - '0';

            if (isSecond) {
                d = d * 2;
            }

            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    public boolean isValidCVV(String cvv) {
        return cvv.matches("\\d{3}");
    }
}

