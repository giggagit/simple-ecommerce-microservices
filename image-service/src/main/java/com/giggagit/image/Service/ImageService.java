package com.giggagit.image.Service;

import java.util.List;
import java.util.Optional;

import com.giggagit.image.Model.Image;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * ImageService
 */
public interface ImageService {

    public void save(Image image);

    public void delete(Image image);

    public void deleteById(long ImageId);

    public void deleteByUpc(long upc);

    public Optional<Image> findById(long ImageId);

    public Page<Image> findAll(Pageable pageable);

    public List<Image> findByUpc(long upc);

    public Image imageUpload(Image image, MultipartFile file);

    public Resource imageDownload(long upc, String name);

    public boolean isProduct(long upc);

    public boolean isImage(long ImageId);

}