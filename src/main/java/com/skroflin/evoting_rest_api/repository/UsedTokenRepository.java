package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.UsedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedTokenRepository extends JpaRepository<UsedToken, String> {
}
