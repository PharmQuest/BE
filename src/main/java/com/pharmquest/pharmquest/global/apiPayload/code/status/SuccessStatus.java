package com.pharmquest.pharmquest.global.apiPayload.code.status;

import com.pharmquest.pharmquest.global.apiPayload.code.BaseCode;
import com.pharmquest.pharmquest.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    //일반적 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // home
    HOME_POSTS(HttpStatus.OK, "HOME201", "홈 게시글을 성공적으로 불러왔습니다."),
    // 커스텀 응답 코드
    _CREATED_ACCESS_TOKEN(HttpStatus.CREATED, "201", "액세스 토큰 재발행에 성공했습니다."),

    // pharmacy
    PHARMACY_SCRAP(HttpStatus.OK, "PHARMACY201", "약국을 마이페이지에 스크랩했습니다."),
    PHARMACY_UNSCRAP(HttpStatus.OK, "PHARMACY202", "약국을 스크랩을 해제했습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
