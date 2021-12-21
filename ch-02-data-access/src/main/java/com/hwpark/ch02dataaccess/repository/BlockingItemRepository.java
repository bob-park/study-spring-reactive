package com.hwpark.ch02dataaccess.repository;

import org.springframework.data.repository.CrudRepository;

import com.hwpark.ch02dataaccess.domain.Item;

public interface BlockingItemRepository extends CrudRepository<Item, String> {

}
