package com.dinsaren.springbootjwtapi.services;

import com.dinsaren.springbootjwtapi.constants.Constants;
import com.dinsaren.springbootjwtapi.models.FileImageDetail;
import com.dinsaren.springbootjwtapi.models.res.UploadImageRes;
import com.dinsaren.springbootjwtapi.repository.FileImageDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

@Slf4j
@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Value("${spring.upload.server.path}")
    String serverPath;
    @Autowired
    private FileImageDetailRepository fileImageDetailRepository;

    @Override
    public UploadImageRes uploadFile(MultipartFile files) {

        if (files == null || files.isEmpty()) {
            log.error("Failed to store empty file");
            return new UploadImageRes();
        }

        File uploadDir = new File(serverPath).getAbsoluteFile();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        UploadImageRes imageRes = new UploadImageRes();
        String originalFilename = files.getOriginalFilename() != null ? files.getOriginalFilename() : "image.jpg";
        String name = StringUtils.cleanPath(originalFilename);
        String extension = getFileExtension(name);
        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }
        String extensions = "jpeg,png,jpg,webp,gif";
        if (!extensions.contains(extension.toLowerCase())) {
            log.error("Invalid File Extension {}", extension);
        }
        imageRes.setFileName(Calendar.getInstance().getTimeInMillis() + "." + extension);

        File destFile = new File(uploadDir, imageRes.getFileName()).getAbsoluteFile();
        if (destFile.getParentFile() != null && !destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        FileImageDetail fileImageDetail = new FileImageDetail();
        try {
            files.transferTo(destFile.toPath());
            fileImageDetail.setFilePath(destFile.getAbsolutePath());
            fileImageDetail.setFileType(files.getContentType());
            fileImageDetail.setFileName(imageRes.getFileName());
            fileImageDetail.setOriginalFileName(getFileNoExtension(name));
            fileImageDetail.setFileSize(files.getSize());
            fileImageDetail.setStatus(Constants.STATUS_ACTIVE);
            fileImageDetailRepository.save(fileImageDetail);
            log.info("Successfully uploaded image to: {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("upload file fail", e);
        } finally {
            log.info("Final uploaded image detail: {}", fileImageDetail);
        }
        imageRes.setFileName(fileImageDetail.getFileName());
        return imageRes;
    }

    String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }

    String getFileNoExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return null;
        }
        return fileName.substring(0, dotIndex);
    }

    @Override
    public FileImageDetail findImageByFileName(String filename) {
        FileImageDetail detail = fileImageDetailRepository.findByFileNameAndStatus(filename, Constants.STATUS_ACTIVE);
        if (detail == null) {
            try {
                File uploadDir = new File(serverPath).getAbsoluteFile();
                File fallbackFile = new File(uploadDir, filename).getAbsoluteFile();
                if (fallbackFile.exists() && fallbackFile.isFile()) {
                    detail = new FileImageDetail();
                    detail.setFilePath(fallbackFile.getAbsolutePath());
                    detail.setFileName(filename);
                    detail.setStatus(Constants.STATUS_ACTIVE);
                }
            } catch (Exception ignored) {}
        }
        return detail;
    }

}
