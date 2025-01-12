package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.domain.Supplements;

import java.util.List;

public interface SupplementsService {
    List<Supplements> getSupplements();
    void saveSupplements();
}