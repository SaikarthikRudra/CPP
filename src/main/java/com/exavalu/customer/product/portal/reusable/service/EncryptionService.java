package com.exavalu.customer.product.portal.reusable.service;

import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

	private final AES256TextEncryptor textEncryptor;

	public EncryptionService(@Value("${jasypt.encryptor.password}") String encryptionPassword) {
		this.textEncryptor = new AES256TextEncryptor();
		this.textEncryptor.setPassword(encryptionPassword);
	}

	public String encrypt(String data) {
		return textEncryptor.encrypt(data);
	}

	public String decrypt(String encryptedData) {
		return textEncryptor.decrypt(encryptedData);
	}

	public boolean matchEncryptedValues(String encryptedData1, String encryptedData2) {
		String decryptedData1 = textEncryptor.decrypt(encryptedData1);
		String decryptedData2 = textEncryptor.decrypt(encryptedData2);
		return decryptedData1.equals(decryptedData2);
	}
}
