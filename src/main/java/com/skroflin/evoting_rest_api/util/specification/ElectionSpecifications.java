package com.skroflin.evoting_rest_api.util.specification;

import com.skroflin.evoting_rest_api.filter.ElectionFilter;
import com.skroflin.evoting_rest_api.models.Candidate;
import com.skroflin.evoting_rest_api.models.Election;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ElectionSpecifications {

    public static Specification<Election> withFilter(ElectionFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.title() != null && !filter.title().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("electionName")), "%" + filter.title().toLowerCase().trim() + "%"));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("electionStatus"), filter.status()));
            }

            if (filter.candidateName() != null && !filter.candidateName().isBlank()) {
                String searchTerm = "%" + filter.candidateName().toLowerCase().trim() + "%";
                Join<Election, Candidate> candidateJoin = root.join("candidates", JoinType.INNER);

                predicates.add(cb.like(cb.lower(candidateJoin.get("candidateFullName")), searchTerm));

                query.distinct(true);
            }

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("electionStartTime"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("electionEndTime"), filter.endDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}