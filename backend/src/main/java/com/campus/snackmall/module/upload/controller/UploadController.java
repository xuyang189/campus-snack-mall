package com.campus.snackmall.module.upload.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.upload.service.UploadService;
import com.campus.snackmall.security.RequireLogin;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequireLogin
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", uploadService.uploadImage(file));
    }
}
