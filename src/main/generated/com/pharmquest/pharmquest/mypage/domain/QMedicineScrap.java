package com.pharmquest.pharmquest.mypage.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMedicineScrap is a Querydsl query type for MedicineScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedicineScrap extends EntityPathBase<MedicineScrap> {

    private static final long serialVersionUID = 334987923L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMedicineScrap medicineScrap = new QMedicineScrap("medicineScrap");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pharmquest.pharmquest.medicine.domain.QMedicine medicine;

    public final com.pharmquest.pharmquest.user.domain.QUser user;

    public QMedicineScrap(String variable) {
        this(MedicineScrap.class, forVariable(variable), INITS);
    }

    public QMedicineScrap(Path<? extends MedicineScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMedicineScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMedicineScrap(PathMetadata metadata, PathInits inits) {
        this(MedicineScrap.class, metadata, inits);
    }

    public QMedicineScrap(Class<? extends MedicineScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.medicine = inits.isInitialized("medicine") ? new com.pharmquest.pharmquest.medicine.domain.QMedicine(forProperty("medicine")) : null;
        this.user = inits.isInitialized("user") ? new com.pharmquest.pharmquest.user.domain.QUser(forProperty("user")) : null;
    }

}

