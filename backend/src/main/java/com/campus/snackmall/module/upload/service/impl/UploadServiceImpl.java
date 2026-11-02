package com.campus.snackmall.module.upload.service.impl;

import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.config.UploadProperties;
import com.campus.snackmall.module.upload.service.UploadService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServiceImpl implements UploadService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final UploadProperties uploadProperties;

    public UploadServiceImpl(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public Map<String, String> uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension) || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(400, "仅支持jpg/jpeg/png/gif/webp格式");
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String newFilename = UUID.randomUUID() + "." + extension.toLowerCase();
        Path dir = Paths.get(uploadProperties.getPath(), datePath);
        Path target = dir.resolve(newFilename);
        try {
            Files.createDirectories(dir);
            file.transferTo(target);
        } catch (IOException ex) {
            throw new BusinessException(500, "上传失败");
        }
        return Map.of("url", uploadProperties.getAccessUrlPrefix() + datePath + "/" + newFilename);
    }
}
