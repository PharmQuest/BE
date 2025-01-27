package com.pharmquest.pharmquest.domain.user.data;

import com.pharmquest.pharmquest.domain.mypage.data.MedicineScrap;
import com.pharmquest.pharmquest.domain.mypage.data.SupplementScrap;
import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.pharmacy.converter.PlaceIdConverter;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String provider;

    private String providerId;

    @CreationTimestamp
    private Timestamp createDate;

    @Lob
    @Convert(converter = PlaceIdConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> pharmacyScraps; // 스크랩한 약국 목록을 문자열로 저장

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineScrap> medicineScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplementScrap> supplementScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostScrap> postScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReport> postReports;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

}
