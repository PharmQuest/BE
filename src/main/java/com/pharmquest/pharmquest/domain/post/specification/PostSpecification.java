package com.pharmquest.pharmquest.domain.post.specification;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> dynamicQuery(Long userId, String keyword,  PostCategory category, Country country) {
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

            //신고한 게시물은 검색할 수 없도록 조건 추가
            if (userId != null) {

                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PostReport> subRoot = subquery.from(PostReport.class);
                subquery.select(subRoot.get("post").get("id"))
                        .where(criteriaBuilder.equal(subRoot.get("user").get("id"), userId)); // userId 조건 추가

                Predicate notInSubquery = criteriaBuilder.not(root.get("id").in(subquery));
                predicates.add(notInSubquery);
            }

            // 삭제된 게시물 제외 조건 추가
            Predicate postNotDeletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
            predicates.add(postNotDeletedPredicate);

            // 모든 조건을 AND로 결합
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        }

}
