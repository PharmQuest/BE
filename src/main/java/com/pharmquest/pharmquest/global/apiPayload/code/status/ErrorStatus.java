package com.pharmquest.pharmquest.global.apiPayload.code.status;

import com.pharmquest.pharmquest.global.apiPayload.code.BaseErrorCode;
import com.pharmquest.pharmquest.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // COMMON - 가장 일반적 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다"),

    // posts
    ILLEGAL_COUNTRY(HttpStatus.BAD_REQUEST, "POST4001", "올바르지 않은 국가."),
    ILLEGAL_POST_CATEGORY(HttpStatus.BAD_REQUEST, "POST4002", "올바르지 않은 카테고리."),
    TITLE_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "POST4003", "제목은 필수 입력 사항입니다."),
    CONTENT_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "POST4004", "내용은 필수 입력 사항입니다."),
    POST_NOT_EXIST(HttpStatus.BAD_REQUEST, "POST4005", "해당하는 게시글이 존재하지 않습니다."),
    COMMENT_NOT_EXIST(HttpStatus.BAD_REQUEST, "POST4006", "해당하는 댓글이 존재하지 않습니다."),
    COMMENT_NOT_IN_POST(HttpStatus.BAD_REQUEST, "POST4007", "해당하는 댓글이 게시글의 댓글 목록에 존재하지 않습니다."),
    NOT_POST_AUTHOR(HttpStatus.BAD_REQUEST, "POST4008", "해당하는 게시글의 작성자가 아닙니다."),

    // pharmacy
    PHARMACY_BAD_PLACE_ID(HttpStatus.BAD_REQUEST, "PHARMACY4001", "약국의 place_id가 올바르지 않습니다."),
    PLACE_NO_RESULT(HttpStatus.BAD_REQUEST, "PHARMACY4002", "해당 place_id에 해당하는 장소가 없습니다"),
    NOT_A_PHARMACY(HttpStatus.BAD_REQUEST, "PHARMACY4003", "해당 place_id에 해당하는 장소가 약국이 아닙니다."),

    PHARMACY_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PHARMACY5000", "알 수 없는 오류입니다."),
    PHARMACY_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PHARMACY5001", "google api 요청이 잘못되었습니다. 관리자 문의 바랍니다"),
    PHARMACY_SCRAP_MAX_EXCEED(HttpStatus.INTERNAL_SERVER_ERROR, "PHARMACY5002", "약국의 최대 스크랩 수를 초과했습니다."),




    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
