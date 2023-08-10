package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.ResultDTO;
import org.example.util.MinioUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @program: chat-room
 * @description: 文件操作控制类
 * @author: stop.yc
 * @create: 2023-08-09 13:00
 **/
@RestController
@Slf4j
@RequestMapping(value = "/file", produces = "application/json;charset=utf-8")
public class FileController {

    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    public ResultDTO fileUpload(@RequestParam("file") MultipartFile file) {
        log.info("file 为: {}", file);
        String url = minioUtil.fileUpload(file);
        return ResultDTO.ok(url);
    }
}
