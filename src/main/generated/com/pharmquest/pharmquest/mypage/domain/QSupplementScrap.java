package com.pharmquest.pharmquest.mypage.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSupplementScrap is a Querydsl query type for SupplementScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSupplementScrap extends EntityPathBase<SupplementScrap> {

    private static final long serialVersionUID = 391704692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSupplementScrap supplementScrap = new QSupplementScrap("supplementScrap");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pharmquest.pharmquest.supplements.domain.QSupplements supplement;

    public final com.pharmquest.pharmquest.user.domain.QUser user;

    public QSupplementScrap(String variable) {
        this(SupplementScrap.class, forVariable(variable), INITS);
    }

    public QSupplementScrap(Path<? extends SupplementScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSupplementScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSupplementScrap(PathMetadata metadata, PathInits inits) {
        this(SupplementScrap.class, metadata, inits);
    }

    public QSupplementScrap(Class<? extends SupplementScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.supplement = inits.isInitialized("supplement") ? new com.pharmquest.pharmquest.supplements.domain.QSupplements(forProperty("supplement")) : null;
        this.user = inits.isInitialized("user") ? new com.pharmquest.pharmquest.user.domain.QUser(forProperty("user")) : null;
    }

}

