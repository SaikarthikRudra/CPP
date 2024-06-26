package com.exavalu.customer.product.portal.reusable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

	@Autowired
	private StringRedisTemplate redisTemplate;

//	@Autowired
//	private TwilioService twilioService;

	private static final String OTP_PREFIX = "otp:";
	private static final String RATE_LIMIT_PREFIX = "rate_limit:";
	private static final int RATE_LIMIT = 5;
	private static final long RATE_LIMIT_PERIOD_SECONDS = 60;
	private final Random random = new Random();

	public Map<String, String> generateOTP(String emailId) {

		Map<String, String> response = new HashMap<>();
		// Check rate limiting
		String rateLimitKey = RATE_LIMIT_PREFIX + emailId;
		if (!isRateLimited(rateLimitKey)) {
			response.put("msg", "Rate limit exceeded. Please try again later.");
			return response;
		}

		// Generate OTP
		String otpKey = OTP_PREFIX + emailId;
		String existingOTP = redisTemplate.opsForValue().get(otpKey);
		if (existingOTP != null) {
			response.put("msg", "An OTP has already been generated for this emailId. Please check your mail.");
			return response;
		}
		
		if(response.containsKey("errorMsg")) {
			System.out.println("errorMsg");
		}

		String otpCode = String.format("%06d", random.nextInt(999999));
		redisTemplate.opsForValue().set(otpKey, otpCode, Duration.ofMinutes(5));
		response.put("msg", "Otp Generated successfully!");
		response.put("otp", otpCode);
//		System.out.println("otp is: "+ otpCode);
//		twilioService.sendOTP(phoneNumber, otpCode);

		return response;
	}

	public boolean verifyOTP(String emailId, String otpCode) {
		String otpKey = OTP_PREFIX + emailId;
		String storedOTP = redisTemplate.opsForValue().get(otpKey);

		if (storedOTP != null && storedOTP.equals(otpCode)) {
			redisTemplate.delete(otpKey);
			return true;
		}

		return false;
	}

	private boolean isRateLimited(String rateLimitKey) {
		Long currentCount = redisTemplate.opsForValue().increment(rateLimitKey, 1);
		if (currentCount == 1) {
			redisTemplate.expire(rateLimitKey, RATE_LIMIT_PERIOD_SECONDS, TimeUnit.SECONDS);
		}
		return currentCount <= RATE_LIMIT;
	}

}