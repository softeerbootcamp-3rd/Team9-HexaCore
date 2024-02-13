package com.hexacore.tayo.category;

import com.hexacore.tayo.category.dto.GetSubCategoryListResponseDto;
import com.hexacore.tayo.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Response> getCategories() {
        GetSubCategoryListResponseDto getSubCategoryListResponseDto = categoryService.getSubCategories();
        return Response.of(HttpStatus.OK, getSubCategoryListResponseDto);
    }
}
