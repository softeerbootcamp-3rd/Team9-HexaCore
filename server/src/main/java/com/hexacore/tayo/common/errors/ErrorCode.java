package com.hexacore.tayo.common.errors;

import java.util.Optional;
import java.util.function.Predicate;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400
    EMPTY_USER_ID(HttpStatus.BAD_REQUEST, "유저 아이디를 입력해주세요."),
    EMPTY_USER_PASSWORD(HttpStatus.BAD_REQUEST, "유저 비밀번호를 입력해주세요."),
    EMPTY_USER_NAME(HttpStatus.BAD_REQUEST, "유저 이름을 입력해주세요."),
    EMPTY_USER_EMAIL(HttpStatus.BAD_REQUEST, "유저 이메일을 입력해주세요."),
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복되는 유저 이메일 입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다."),
    USER_WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    USER_DELETED(HttpStatus.BAD_REQUEST, "탈퇴한 사용자입니다."),
    USER_HAS_RESERVATION(HttpStatus.BAD_REQUEST, "예약중인 차량이 있습니다."),
    USER_CAR_HAS_RESERVATION(HttpStatus.BAD_REQUEST, "등록한 차량을 예약한 사용자가 있습니다."),
    USER_BILLING_KEY_NOT_EXIST(HttpStatus.BAD_REQUEST, "유저의 빌링키가 존재하지 않습니다."),

    USER_CAR_NOT_EXISTS(HttpStatus.NOT_FOUND, "등록한 차량이 존재하지 않습니다."),
    CAR_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 차량입니다."),
    CAR_IMAGE_INSUFFICIENT(HttpStatus.BAD_REQUEST, "이미지를 5개 이상 등록해야 합니다."),
    ETC_MODEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "DB에 ETC 모델 데이터가 존재하지 않습니다."),
    CAR_NUMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 차량 번호입니다."),
    CAR_UPDATED_BY_OTHERS(HttpStatus.BAD_REQUEST, "해당 차량을 소유한 사용자만 차량 정보를 변경할 수 있습니다."),
    INVALID_CAR_DATE_RANGE_DUPLICATED(HttpStatus.BAD_REQUEST, "예약 가능일자에 겹치는 부분이 있습니다."),
    CAR_DATE_RANGE_UPDATED_BY_OTHERS(HttpStatus.BAD_REQUEST, "해당 차량을 소유한 사용자만 예약 가능일자를 변경할 수 있습니다."),
    CAR_DATE_RANGE_NOT_CONTAIN_RESERVATIONS(HttpStatus.BAD_REQUEST, "변경할 예약 가능일자에 예약대기 혹은 사용중인 예약을 포함하는 일정이 없습니다."),
    USER_ALREADY_HAS_CAR(HttpStatus.BAD_REQUEST, "유저가 이미 차량을 등록했습니다."),
    IMAGE_INDEX_MISMATCH(HttpStatus.BAD_REQUEST, "이미지 개수와 인덱스 개수가 일치하지 않습니다."),
    INVALID_CAR_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 차량 타입입니다."),
    INVALID_FUEL_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 연료 타입입니다."),
    CAR_HAVE_ACTIVE_RESERVATIONS(HttpStatus.BAD_REQUEST, "차량에 활성화된 예약 내역이 있어 차량을 삭제할 수 없습니다."),
    CANNOT_ADD_REVIEW(HttpStatus.BAD_REQUEST, "해당 예약에 대한 리뷰를 작성할 수 없습니다."),
    RESERVATION_REVIEWED_BY_OTHERS(HttpStatus.BAD_REQUEST, "해당 예약과 관련된 게스트/호스트만 리뷰를 작성할 수 있습니다."),
    REVIEW_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 작성된 리뷰가 있습니다."),
    CAR_DATE_RANGE_LOCK_ACQUIRE_FAIL(HttpStatus.BAD_REQUEST, "현재 예약 중인 사용자가 있거나 차량 정보가 수정 중입니다. 잠시 후 다시 시도해주세요."),
    DATE_SIZE_MISMATCH(HttpStatus.BAD_REQUEST, "날짜 구간이 맞지 않습니다."),
    DATE_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, "날짜 형식이 맞지 않습니다."),
    START_DATE_AFTER_END_DATE(HttpStatus.BAD_REQUEST, "예약 시작 날짜가 끝 날짜보다 뒤에 있을 수 없습니다."),

    RESERVATION_HOST_EQUALS_GUEST(HttpStatus.BAD_REQUEST, "호스트와 게스트가 일치하는 예약입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 예약입니다."),
    RESERVATION_DATETIME_LEAST_HOUR(HttpStatus.BAD_REQUEST, "예약 일정은 최소 1시간 이상이어야 합니다."),
    RESERVATION_DATE_NOT_IN_RANGE(HttpStatus.BAD_REQUEST, "예약 가능 날짜의 범위에 맞지 않는 대여/반납일시입니다."),
    RESERVATION_ALREADY_READY_OR_USING(HttpStatus.BAD_REQUEST, "해당 예약일시 구간에 이미 예약대기 혹은 사용중인 예약이 있습니다."),
    RESERVATION_STATUS_CHANGED_BY_OTHERS(HttpStatus.BAD_REQUEST, "해당 예약과 관련된 호스트/게스트만 예약상태를 변경할 수 있습니다."),
    RESERVATION_STATUS_INVALID_CHANGE(HttpStatus.BAD_REQUEST, "요청하신 예약상태로 변경할 수 없습니다."),
    RESERVATION_CANCEL_TOO_LATE(HttpStatus.BAD_REQUEST, "해당 예약의 시작 시간의 24시간 이전에 예약을 취소할 수 있습니다."),
    RESERVATION_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 예약상태는 존재하지 않습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 타입입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_POSITION(HttpStatus.BAD_REQUEST, "위치 정보가 올바르지 않습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 올바르지 않습니다."),

    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 알림입니다."),
    NOTIFICATION_CANNOT_DELETED(HttpStatus.BAD_REQUEST, "알림을 삭제할 수 없는 사용자입니다."),

    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스 입니다"),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),

    INVALID_JWT_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 JWT 토큰입니다."),

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 URL입니다."),

    // 500
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 문제 발생, 다음에 시도해주세요."),
    TOSS_PAYMENTS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제에 실패했습니다."),
    TOSS_PAYMENTS_CANCEL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 취소에 실패했습니다");

    public final HttpStatus httpStatus;
    public final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        return this.httpStatus.value();
    }

    public String getErrorMessage(Throwable e) {
        return this.getErrorMessage(e.getMessage());
    }

    public String getErrorMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getErrorMessage());
    }
}
