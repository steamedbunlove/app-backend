package com.njmeixun.schedule.dao;

import com.njmeixun.schedule.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {

    User findByUsername(String username);
}
