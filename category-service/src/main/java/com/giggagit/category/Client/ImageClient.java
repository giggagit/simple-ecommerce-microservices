package com.giggagit.category.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ImageClient
 */
@FeignClient("image-service")
public interface ImageClient {

    @PostMapping("/images/{upc}")
    public void postImagesUpc(@PathVariable long upc);

    @DeleteMapping("/images/{upc}")
    public void deleteImagesUpc(@PathVariable long upc);

}