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
    PHARMACY_SCRAP(HttpStatus.OK, "PHARMACY201", "약국을 스크랩했습니다."),
    PHARMACY_UNSCRAP(HttpStatus.OK, "PHARMACY202", "약국 스크랩을 해제했습니다."),
    PHARMACY_IF_SCRAP(HttpStatus.OK, "PHARMACY203", "약국 스크랩 여부를 성공적으로 조회했습니다."),

    // my page
    MY_PAGE_PHARMACY(HttpStatus.OK, "MYPAGE201", "마이페이지에서 스크랩된 약국 목록을 성공적으로 조회했습니다."),

    // Medicine
    MEDICINE_FETCH_SUCCESS(HttpStatus.OK, "MED2001", "약물 데이터를 성공적으로 불러왔습니다."),
    MEDICINE_SAVE_SUCCESS(HttpStatus.CREATED, "MED2002", "약물 데이터를 성공적으로 저장했습니다."),
    MEDICINE_TRANSLATE_SUCCESS(HttpStatus.OK, "MED2003", "약물 데이터를 성공적으로 번역했습니다."),

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
