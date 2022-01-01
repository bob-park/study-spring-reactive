package com.hwpark.reactivesecurity.repository;

import org.springframework.data.repository.CrudRepository;

import com.hwpark.commons.domain.User;

public interface UserRepository extends CrudRepository<User, String> {

}
