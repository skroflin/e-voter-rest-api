package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.UsedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsedTokenRepository extends JpaRepository<UsedToken, String> {
}
