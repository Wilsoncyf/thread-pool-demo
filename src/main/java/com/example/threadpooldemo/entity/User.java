package com.example.threadpooldemo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users") // 指定表名
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    // 省略构造函数、getter/setter (Lombok)
}