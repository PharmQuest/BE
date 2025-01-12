package com.pharmquest.pharmquest.supplements.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSupplements is a Querydsl query type for Supplements
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSupplements extends EntityPathBase<Supplements> {

    private static final long serialVersionUID = 903759985L;

    public static final QSupplements supplements = new QSupplements("supplements");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QSupplements(String variable) {
        super(Supplements.class, forVariable(variable));
    }

    public QSupplements(Path<? extends Supplements> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSupplements(PathMetadata metadata) {
        super(Supplements.class, metadata);
    }

}

