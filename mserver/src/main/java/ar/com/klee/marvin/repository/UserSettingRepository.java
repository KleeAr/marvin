package ar.com.klee.marvin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ar.com.klee.marvin.model.UserSetting;

@Repository
public interface UserSettingRepository extends CrudRepository<UserSetting, Long> {

	Iterable<UserSetting> findByUserId(Long userId);
}
