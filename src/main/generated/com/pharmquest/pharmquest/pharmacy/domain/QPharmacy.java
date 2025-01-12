package com.pharmquest.pharmquest.pharmacy.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPharmacy is a Querydsl query type for Pharmacy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPharmacy extends EntityPathBase<Pharmacy> {

    private static final long serialVersionUID = 1402042767L;

    public static final QPharmacy pharmacy = new QPharmacy("pharmacy");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QPharmacy(String variable) {
        super(Pharmacy.class, forVariable(variable));
    }

    public QPharmacy(Path<? extends Pharmacy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPharmacy(PathMetadata metadata) {
        super(Pharmacy.class, metadata);
    }

}

