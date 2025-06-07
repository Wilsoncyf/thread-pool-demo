package com.example.threadpooldemo.controller;

import com.example.threadpooldemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    @Autowired
    private UserService userService;

    @PostMapping("/upload-sync")
    public ResponseEntity<String> uploadFileSynchronously(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一个非空文件。");
        }
        try {
            long startTime = System.currentTimeMillis();
            userService.saveUsersFromCsv(file);
            long endTime = System.currentTimeMillis();
            return ResponseEntity.ok("文件处理成功，耗时: " + (endTime - startTime) + " ms");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("文件处理失败: " + e.getMessage());
        }
    }

    // --- 新增的异步接口 ---
    @PostMapping("/upload-async")
    public ResponseEntity<String> uploadFileAsynchronously(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一个非空文件。");
        }
        try {
            userService.saveUsersFromCsvAsync(file);
            // 注意这里的区别！我们立刻返回了响应。
            return ResponseEntity.ok("文件上传成功，正在后台异步处理...");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("文件提交处理失败: " + e.getMessage());
        }
    }

}