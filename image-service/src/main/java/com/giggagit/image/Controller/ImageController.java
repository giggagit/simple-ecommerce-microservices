package com.giggagit.image.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.giggagit.image.Model.Image;
import com.giggagit.image.Service.ImageService;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ImageController
 */
@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Image> imagePage = imageService.findAll(pageable);
        return ResponseEntity.ok(imagePage);
    }

    @PostMapping
    public ResponseEntity<?> postImage(@NotNull @RequestPart MultipartFile file, @Validated @RequestPart Image image) {

        image.setUrl(null);
        Image imageData = imageService.imageUpload(image, file);
        URI location = null;

        try {
            location = new URI("/images/" + image.getProduct().getUpc() + "/id/" + imageData.getId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{upc}")
    public ResponseEntity<?> getImageUpc(@PathVariable long upc) {
        if (!imageService.isProduct(upc)) {
            return ResponseEntity.notFound().build();
        }

        List<Image> images = imageService.findByUpc(upc);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{upc}")
    public ResponseEntity<?> deleteImageByUpc(@PathVariable long upc) {
        imageService.deleteByUpc(upc);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{upc}/id/{id}")
    public ResponseEntity<?> getImageId(@PathVariable long upc, @PathVariable long id) {
        Optional<Image> imageOptional = imageService.findById(id);

        if (!imageOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(imageOptional.get());
    }

    @PutMapping("/{upc}/id/{id}")
    public ResponseEntity<?> putImageUpc(@PathVariable long upc, @PathVariable long id,
            @NotNull @RequestPart MultipartFile file, @Validated @RequestPart Image image) {
        Optional<Image> imageOptional = imageService.findById(id);

        if (!imageOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Image imageGet = imageOptional.get();

        imageGet.setDateModified(LocalDateTime.now());
        imageGet.setType(image.getType());

        image = imageService.imageUpload(imageGet, file);
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{upc}/id/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") long id) {
        imageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storage/{upc}/{name:.+}")
    public ResponseEntity<Resource> getImageFile(@PathVariable long upc, @PathVariable String name) {
        String mediaType = null;
        Resource resource = imageService.imageDownload(upc, name);

        try {
            mediaType = Files.probeContentType(Paths.get(resource.getURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mediaType)).body(resource);
    }

}