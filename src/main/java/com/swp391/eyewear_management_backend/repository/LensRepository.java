package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Lens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "lenses")
public interface LensRepository extends JpaRepository<Lens, Long> {
}
