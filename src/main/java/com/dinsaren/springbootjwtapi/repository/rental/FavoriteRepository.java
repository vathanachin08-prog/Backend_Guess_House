package com.dinsaren.springbootjwtapi.repository.rental;

import com.dinsaren.springbootjwtapi.models.rental.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Page<Favorite> findByStudentId(int studentId, Pageable pageable);
    boolean existsByStudentIdAndPropertyId(int studentId, Long propertyId);
    boolean existsByStudentIdAndRoomId(int studentId, Long roomId);
    Optional<Favorite> findByIdAndStudentId(Long id, int studentId);
}
