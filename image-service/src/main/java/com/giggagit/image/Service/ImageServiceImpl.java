package com.giggagit.image.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.giggagit.image.Client.CategoryClient;
import com.giggagit.image.Exception.ImageException;
import com.giggagit.image.Exception.ProductNotFoundException;
import com.giggagit.image.Exception.StorageException;
import com.giggagit.image.Model.Image;
import com.giggagit.image.Model.Product;
import com.giggagit.image.Model.Type;
import com.giggagit.image.Properties.StorageProperties;
import com.giggagit.image.Repository.ImageRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * ImageServiceImpl
 */
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final CategoryClient categoryClient;
    private final StorageProperties storageProperties;

    public ImageServiceImpl(ImageRepository imageRepository, CategoryClient categoryClient,
            StorageProperties storageProperties) {
        this.imageRepository = imageRepository;
        this.categoryClient = categoryClient;
        this.storageProperties = storageProperties;
    }

    @Override
    public void save(Image image) {
        imageRepository.save(image);
    }

    @Override
    @Transactional
    public void delete(Image image, List<Image> images) {
        if (images.size() > 1) {
            deleteImage(image);
        } else {
            deleteDirectory(image.getProduct().getUpc());
        }

        imageRepository.delete(image);
    }

    @Override
    @Transactional
    public void deleteByUpc(long upc) {
        deleteDirectory(upc);
        imageRepository.deleteByProductUpc(upc);
    }

    @Override
    public Optional<Image> findById(long ImageId) {
        return imageRepository.findById(ImageId);
    }

    @Override
    public Page<Image> findAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    @Override
    public List<Image> findByUpc(long upc) {
        return imageRepository.findByProductUpc(upc);
    }

    @Override
    @Transactional
    public Image imageUpload(Image image, List<Image> images, MultipartFile file) {

        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        String fileExtension = "";
        String originalFilename = file.getOriginalFilename();

        // Validate file extension
        if (originalFilename.lastIndexOf(".") != -1 && originalFilename.lastIndexOf(".") != 0) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        } else {
            throw new StorageException("Can not store file without extension.");
        }

        // Validate product UPC
        if (images.isEmpty()) {
            ResponseEntity<Product> productResponse = categoryClient.getProduct(image.getProduct().getUpc());

            if (productResponse.getStatusCode().isError()) {
                throw new ProductNotFoundException("Product UPC not found!, UPC: " + image.getProduct().getUpc());
            }
        }

        // Generate image's name
        String imageName = "";
        String upc = image.getProduct().getUpc().toString();

        // Count type for images runing number
        if (image.getType().equals(Type.COVER)) {
            Optional<Image> imageType = images.stream().filter(type -> type.getType().equals(Type.COVER)).findFirst();

            if (imageType.isPresent()) {
                if (!imageType.get().equals(image)) {
                    throw new ImageException("Duplicate product's cover, it can have only one.");
                }
            }

            imageName = upc + "_cover" + fileExtension;
        } else {
            long countType = images.stream().filter(type -> type.getType().equals(Type.OPTIONAL)).count();
            String result = "01";

            if (countType > 0L) {
                // Get optional image number
                String regex = "[0-9]{2}(?=\\.)";
                List<Integer> imagenNumbers = new ArrayList<>();
                Pattern pattern = Pattern.compile(regex);

                images.stream().map(i -> pattern.matcher(i.getUrl())).filter(Matcher::find).map(m -> m.group())
                        .forEach(s -> imagenNumbers.add(Integer.parseInt(s)));

                // Find the first missing number
                BitSet bitSet = new BitSet();
                imagenNumbers.stream().forEach(n -> bitSet.set(n - 1));
                result = String.format("%02d", bitSet.nextClearBit(0) + 1);
            }

            imageName = upc + "_optional_" + result + fileExtension;
        }

        Path directory = null;

        try {
            directory = Files.createDirectories(Paths.get(storageProperties.getUploadDir() + upc));

            try (InputStream inputStream = file.getInputStream()) {
                // Copy image to storage
                Path path = Paths.get(directory + "/" + imageName);
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete old image if file name is differnt
        if (image.getUrl() != null) {
            String oldImage = image.getUrl().substring(image.getUrl().lastIndexOf('/') + 1, image.getUrl().length());

            if (!imageName.equals(oldImage)) {
                Path oldPath = Paths.get(directory + "/" + oldImage);

                try {
                    Files.delete(oldPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        image.setUrl("/images/storage/" + image.getProduct().getUpc() + "/" + imageName);
        save(image);
        return image;
    }

    @Override
    public Resource imageDownload(long upc, String name) {
        Resource resource = null;

        try {
            Path file = Paths.get(storageProperties.getUploadDir() + upc + "/" + name);
            resource = new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return resource;
    }

    @Override
    public void deleteImage(Image image) {
        String fileName = image.getUrl().substring(image.getUrl().lastIndexOf('/') + 1, image.getUrl().length());
        String directory = storageProperties.getUploadDir() + image.getProduct().getUpc();
        Path file = Paths.get(directory + "/" + fileName);

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDirectory(long upc) {
        try (Stream<Path> paths = Files.walk(Paths.get(storageProperties.getUploadDir() + upc))) {
            List<Path> deletePaths = paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList());

            for (Path path : deletePaths) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isProduct(long upc) {
        return imageRepository.existsByProductUpc(upc);
    }

    @Override
    public boolean isImage(long ImageId) {
        return imageRepository.existsById(ImageId);
    }

}