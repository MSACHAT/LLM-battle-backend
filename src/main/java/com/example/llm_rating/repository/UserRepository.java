package com.example.llm_rating.repository;


import com.example.llm_rating.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // 可以在这里定义自定义的查询方法，Spring Data MongoDB 将为你自动生成相应的实现
}
