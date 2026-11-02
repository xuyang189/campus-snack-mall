package com.campus.snackmall.module.upload.service;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    Map<String, String> uploadImage(MultipartFile file);
}
