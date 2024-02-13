package com.hexacore.tayo.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoDto {

    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
}
