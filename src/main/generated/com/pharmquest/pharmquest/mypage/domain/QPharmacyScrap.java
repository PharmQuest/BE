package com.pharmquest.pharmquest.mypage.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPharmacyScrap is a Querydsl query type for PharmacyScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPharmacyScrap extends EntityPathBase<PharmacyScrap> {

    private static final long serialVersionUID = 911980474L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPharmacyScrap pharmacyScrap = new QPharmacyScrap("pharmacyScrap");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pharmquest.pharmquest.pharmacy.domain.QPharmacy pharmacy;

    public final com.pharmquest.pharmquest.user.domain.QUser user;

    public QPharmacyScrap(String variable) {
        this(PharmacyScrap.class, forVariable(variable), INITS);
    }

    public QPharmacyScrap(Path<? extends PharmacyScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPharmacyScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPharmacyScrap(PathMetadata metadata, PathInits inits) {
        this(PharmacyScrap.class, metadata, inits);
    }

    public QPharmacyScrap(Class<? extends PharmacyScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pharmacy = inits.isInitialized("pharmacy") ? new com.pharmquest.pharmquest.pharmacy.domain.QPharmacy(forProperty("pharmacy")) : null;
        this.user = inits.isInitialized("user") ? new com.pharmquest.pharmquest.user.domain.QUser(forProperty("user")) : null;
    }

}

