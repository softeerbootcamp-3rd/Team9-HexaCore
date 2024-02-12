package com.hexacore.tayo.test;

import com.hexacore.tayo.car.ModelRepository;
import com.hexacore.tayo.car.model.ModelEntity;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private ModelRepository modelRepository;

    public String testDataResponse() {
        return "hello World";
    }

    public boolean testResponse() {
        return true;
    }

    public Page<ModelEntity> testException() {
        throw new GeneralException(ErrorCode.USER_UNAUTHORIZED);
    }

    public Page<ModelEntity> testPage() {
        Pageable pageable = PageRequest.of(0, 5);

        return modelRepository.findAll(pageable);
    }
}
