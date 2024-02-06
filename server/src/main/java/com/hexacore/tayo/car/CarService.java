package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.ModelDto;
import com.hexacore.tayo.car.model.ModelEntity;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    @Autowired
    private ModelRepository modelRepository;

    public ResponseDto createCategory(ModelDto modelDto) {
        ModelEntity model = new ModelEntity();

        model.setCategory(modelDto.getCategory());
        model.setSubCategory(modelDto.getSubCategory());

        modelRepository.save(model);

        return ResponseDto.success(ResponseCode.OK, "차량 모델 등록 성공");
    }
}
