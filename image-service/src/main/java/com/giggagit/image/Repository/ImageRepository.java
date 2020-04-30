package com.giggagit.image.Repository;

import java.util.List;

import com.giggagit.image.Model.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ImageRepository
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    public void deleteByProductUpc(long upc);

    public List<Image> findByProductUpc(long upc);

    public boolean existsByProductUpc(long upc);

}