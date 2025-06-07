package com.example.threadpooldemo.service;

import com.example.threadpooldemo.entity.User;
import com.example.threadpooldemo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 关键点1: 注入自身，以调用代理类的方法
    @Autowired
    @Lazy
    private UserService self;

    public void saveUsersFromCsv(MultipartFile file) throws Exception {
        log.info("开始处理CSV文件...");
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            // 跳过表头
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User();
                user.setName(data[0]);
                user.setEmail(data[1]);
                users.add(user);
            }
        }
        log.info("CSV文件解析完成，共 {} 条用户数据，开始存入数据库...", users.size());
        // 一次性保存所有用户
        userRepository.saveAll(users);
        log.info("所有用户数据已成功存入数据库。");
    }

    public void saveUsersFromCsvAsync(MultipartFile file) throws Exception {
        log.info("开始异步批处理CSV文件...");
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine(); // 跳过表头

            // 关键点2: 不再逐行提交，而是创建小批次
            int batchSize = 1000; // 例如，每1000行一个批次
            List<User> userBatch = new ArrayList<>(batchSize);

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User();
                user.setName(data[0]);
                user.setEmail(data[1]);
                userBatch.add(user);

                if (userBatch.size() >= batchSize) {
                    // 关键点3: 提交整个批次到异步方法，并传入副本
                    self.saveUserBatch(new ArrayList<>(userBatch));
                    userBatch.clear(); // 清空批次以便重用
                }
            }

            // 处理最后一批不足batchSize的数据
            if (!userBatch.isEmpty()) {
                self.saveUserBatch(userBatch);
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("所有批处理任务已提交至线程池，主线程耗时: {} ms", (endTime - startTime));
    }

    /**
     * 这是处理一个“批次”的异步方法
     * 它会在一个事务中完成整个批次的保存
     */
    @Async("taskExecutor")
    @Transactional // 保证批处理的事务性
    public void saveUserBatch(List<User> userBatch) {
        long batchStartTime = System.currentTimeMillis();
        try {
            userRepository.saveAll(userBatch);
            long batchEndTime = System.currentTimeMillis();
            log.info("线程 [{}]: 成功保存 {} 条用户数据, 耗时 {} ms",
                    Thread.currentThread().getName(), userBatch.size(), (batchEndTime - batchStartTime));
        } catch (Exception e) {
            log.error("线程 [{}]: 批处理保存失败: {}", Thread.currentThread().getName(), e.getMessage());
        }
    }
}