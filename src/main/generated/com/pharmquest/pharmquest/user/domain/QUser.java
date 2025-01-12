package com.pharmquest.pharmquest.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 831520831L;

    public static final QUser user = new QUser("user");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.pharmquest.pharmquest.mypage.domain.MedicineScrap, com.pharmquest.pharmquest.mypage.domain.QMedicineScrap> medicineScraps = this.<com.pharmquest.pharmquest.mypage.domain.MedicineScrap, com.pharmquest.pharmquest.mypage.domain.QMedicineScrap>createList("medicineScraps", com.pharmquest.pharmquest.mypage.domain.MedicineScrap.class, com.pharmquest.pharmquest.mypage.domain.QMedicineScrap.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<com.pharmquest.pharmquest.mypage.domain.PharmacyScrap, com.pharmquest.pharmquest.mypage.domain.QPharmacyScrap> pharmacyScraps = this.<com.pharmquest.pharmquest.mypage.domain.PharmacyScrap, com.pharmquest.pharmquest.mypage.domain.QPharmacyScrap>createList("pharmacyScraps", com.pharmquest.pharmquest.mypage.domain.PharmacyScrap.class, com.pharmquest.pharmquest.mypage.domain.QPharmacyScrap.class, PathInits.DIRECT2);

    public final ListPath<com.pharmquest.pharmquest.mypage.domain.SupplementScrap, com.pharmquest.pharmquest.mypage.domain.QSupplementScrap> supplementScraps = this.<com.pharmquest.pharmquest.mypage.domain.SupplementScrap, com.pharmquest.pharmquest.mypage.domain.QSupplementScrap>createList("supplementScraps", com.pharmquest.pharmquest.mypage.domain.SupplementScrap.class, com.pharmquest.pharmquest.mypage.domain.QSupplementScrap.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

