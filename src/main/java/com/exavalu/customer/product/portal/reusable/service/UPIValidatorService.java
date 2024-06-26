package com.exavalu.customer.product.portal.reusable.service;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;

@Service
public class UPIValidatorService {

    // Regular expression to match typical UPI ID patterns
    private static final String UPI_ID_PATTERN = "^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$";

    public boolean validateUpiId(String upiId) {
        Pattern pattern = Pattern.compile(UPI_ID_PATTERN);
        Matcher matcher = pattern.matcher(upiId);
        return matcher.matches();
    }

}




