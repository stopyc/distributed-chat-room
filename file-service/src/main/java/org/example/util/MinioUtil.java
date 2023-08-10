package org.example.util;

import io.minio.*;
import io.minio.http.Method;
import org.example.config.MinioProperties;
import org.example.pojo.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * @program: chat-room
 * @description: minio操作工具类
 * @author: stop.yc
 * @create: 2023-08-09 13:01
 **/
@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioProperties minioProperties;

    public String fileUpload(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            throw new BusinessException("文件为空");
        }

        String targetFilename = getTargetFilename(Objects.requireNonNull(file.getOriginalFilename()));

        if (!StringUtils.hasText(targetFilename)) {
            throw new BusinessException("目标文件名为空");
        }

        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucket()).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
            }


            InputStream inputStream = file.getInputStream();    //获取file的inputStream
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(minioProperties.getBucket())
                            .object(targetFilename)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            inputStream.close();
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                    .builder()
                    .bucket(minioProperties.getBucket())
                    .object(targetFilename)
                    .method(Method.GET)
                    .build());    //文件访问路径
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getTargetFilename(String originFilename) {
        LocalDate time = TimeUtil.transfer(System.currentTimeMillis() / 1000, LocalDate.class);
        Long userId = SecurityUtils.getUser().getUserId();
        String newName = time + "/"
                + userId + "/"
                + UUID.randomUUID().toString().replaceAll("-", "");
        //+ originFilename.substring(originFilename.lastIndexOf("."));
        //新的名称，pic会是bucket下的文件夹
        return newName;
    }
}
