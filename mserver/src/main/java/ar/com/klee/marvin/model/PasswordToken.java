package ar.com.klee.marvin.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PasswordToken {

	private static final String SHA_1 = "SHA-1";
	private Long userId;
	private Long expirationTime;
	@Id
	private String code;
	
	public PasswordToken() {
		super();
	}

	public PasswordToken(Long userId, Integer duration) {
		this.userId = userId;
		this.expirationTime = System.currentTimeMillis() + duration;
		this.code = generateSHA1();
	}

	private String generateSHA1() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(SHA_1);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("error generating code", e);
		}
		String codeToHash = String.valueOf(userId.hashCode()) + String.valueOf(expirationTime);
        StringBuilder sb = new StringBuilder();
		byte[] bytes = md.digest(codeToHash.getBytes());
		for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
		return sb.toString();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isExpired() {
		return expirationTime < System.currentTimeMillis();
	}
	
	
}
