package com.hexacore.tayo.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetSubCategoryListResponseDto {

    private List<GetSubCategoryResponseDto> models;
}
