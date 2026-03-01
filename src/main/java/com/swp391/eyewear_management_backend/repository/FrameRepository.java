package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Frame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "frames")
public interface FrameRepository extends JpaRepository<Frame, Long> {
}
