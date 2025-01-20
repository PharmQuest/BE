package com.pharmquest.pharmquest.domain.post.specification;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> dynamicQuery(String keyword,  PostCategory category, Country country) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // keyword가 title 또는 content에 포함되는지 확인
            if (keyword != null) {
                String likePattern = "%" + keyword + "%"; // 검색어 앞뒤로 % 추가
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), likePattern);
                Predicate contentPredicate = criteriaBuilder.like(root.get("content"), likePattern);
                predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate));
            }

            // category 조건 추가
            if (category != null && category != PostCategory.ALL) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            //country 조건 추가
            if (country != null) {
                predicates.add(criteriaBuilder.equal(root.get("country"), country));
            }

            // 모든 조건을 AND로 결합
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        }

}
