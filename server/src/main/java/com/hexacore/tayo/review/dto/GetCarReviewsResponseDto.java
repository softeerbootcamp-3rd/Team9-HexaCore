package com.hexacore.tayo.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GetCarReviewsResponseDto {

    private Long id;
    private Writer writer;
    private String contents;
    private Integer rate;

    public GetCarReviewsResponseDto(Long id, String name, String profileImgUrl, String contents, Integer rate) {
        this.id = id;
        this.writer = new Writer(name, profileImgUrl);
        this.contents = contents;
        this.rate = rate;
    }

    @AllArgsConstructor
    @Getter
    private static class Writer {

        private String name;
        private String profileImgUrl;

        @Override
        public String toString() {
            return "{"
                    + "name='" + name + '\''
                    + ", profileImgUrl='" + profileImgUrl + '\''
                    + '}';
        }
    }

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", writer=" + writer
                + ", contents='" + contents + '\''
                + ", rate=" + rate
                + '}';
    }
}
