package ar.com.klee.marvin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ar.com.klee.marvin.model.PasswordToken;

@Repository
public interface PasswordTokenRepository extends
		CrudRepository<PasswordToken, String> {

	Iterable<PasswordToken> findByCode(String code);

	void deleteByUserId(Long userId);
}
