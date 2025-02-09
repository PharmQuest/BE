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
    AUTHORIZATION_HEADER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH4001", "로그인이 필요한 서비스입니다."),

    // posts
    ILLEGAL_COUNTRY(HttpStatus.BAD_REQUEST, "POST4001", "올바르지 않은 국가."),
    ILLEGAL_POST_CATEGORY(HttpStatus.BAD_REQUEST, "POST4002", "올바르지 않은 카테고리."),
    TITLE_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "POST4003", "제목은 필수 입력 사항입니다."),
    CONTENT_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "POST4004", "내용은 필수 입력 사항입니다."),
    POST_NOT_EXIST(HttpStatus.NOT_FOUND, "POST4005", "해당하는 게시글이 존재하지 않습니다."),
    POST_LIKE_NOT_EXIST(HttpStatus.NOT_FOUND, "POST4006", "좋아요를 누르지 않은 게시글입니다."),
    POST_LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "POST4005", "이미 좋아요를 누른 게시글입니다."),
    NOT_POST_AUTHOR(HttpStatus.BAD_REQUEST, "POST4006", "해당하는 게시글의 작성자가 아닙니다."),

    //comments
    COMMENT_NOT_EXIST(HttpStatus.NOT_FOUND, "COMMENT4001", "해당하는 댓글이 존재하지 않습니다."),
    COMMENT_NOT_IN_POST(HttpStatus.BAD_REQUEST, "COMMENT4002", "해당하는 댓글이 게시글의 댓글 목록에 존재하지 않습니다."),
    NOT_COMMENT_AUTHOR(HttpStatus.BAD_REQUEST, "COMMENT4003", "해당하는 댓글의 작성자가 아닙니다."),
    COMMENT_LIKE_NOT_EXIST(HttpStatus.NOT_FOUND, "COMMENT4004", "좋아요를 누르지 않은 댓글입니다."),
    COMMENT_LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "COMMENT4005", "이미 좋아요를 누른 댓글입니다."),


    // pharmacy
    PHARMACY_PLACE_ID_NULL(HttpStatus.BAD_REQUEST, "PHARMACY4001", "place_id가 없습니다."),
    PLACE_NO_INFO(HttpStatus.BAD_REQUEST, "PHARMACY4002", "해당 징소에 대한 정보가 없습니다"),
    NOT_A_PHARMACY(HttpStatus.BAD_REQUEST, "PHARMACY4003", "해당 place_id에 해당하는 장소가 약국이 아닙니다."),

    PHARMACY_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PHARMACY5000", "알 수 없는 오류입니다."),
    PHARMACY_SCRAP_MAX_EXCEED(HttpStatus.INTERNAL_SERVER_ERROR, "PHARMACY5002", "약국의 최대 스크랩 수를 초과했습니다."),

    // supplement
    SUPPLEMENTS_NO_SEARCH_RESULT(HttpStatus.NOT_FOUND, "SUPP4001", "검색 결과가 없습니다."),
    SUPPLEMENTS_NO_KEYWORD(HttpStatus.BAD_REQUEST, "SUPP4002", "검색어를 입력해주세요."),
    SUPPLEMENTS_NO_FILTERED(HttpStatus.NOT_FOUND, "SUPP4003", "필터링된 결과가 없습니다."),

    // my page
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "MYPAGE4001", "페이지 번호가 1보다 작을 수 없습니다."),
    PAGE_NUMBER_EXCEEDS_TOTAL(HttpStatus.BAD_REQUEST, "MYAPGE4002", "페이지 번호 전체 페이지 수를 초과하였습니다."),
    INVALID_SIZE_NUMBER(HttpStatus.BAD_REQUEST, "MYAPGE4003", "사이즈 번호가 1보다 작을 수 없습니다."),

    // Medicine
    MEDICINE_NOT_FOUND(HttpStatus.NOT_FOUND, "MED4001", "해당하는 약물을 찾을 수 없습니다."),
    MEDICINE_API_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MED4002", "FDA API 요청이 실패했습니다."),
    MEDICINE_TRANSLATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MED4003", "약물 정보 번역에 실패했습니다."),
    MEDICINE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MED4004", "약물 데이터 저장 중 오류가 발생했습니다."),
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
