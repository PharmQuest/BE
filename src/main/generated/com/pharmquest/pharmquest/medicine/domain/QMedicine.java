package com.pharmquest.pharmquest.medicine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMedicine is a Querydsl query type for Medicine
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedicine extends EntityPathBase<Medicine> {

    private static final long serialVersionUID = 1466805917L;

    public static final QMedicine medicine = new QMedicine("medicine");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QMedicine(String variable) {
        super(Medicine.class, forVariable(variable));
    }

    public QMedicine(Path<? extends Medicine> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMedicine(PathMetadata metadata) {
        super(Medicine.class, metadata);
    }

}

