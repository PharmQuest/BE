//package com.pharmquest.pharmquest.domain.supplements.service;
//
//import com.pharmquest.pharmquest.domain.supplements.data.Category;
//import com.pharmquest.pharmquest.domain.supplements.repository.CategoryRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CrawlProductServiceImpl implements CrawlProductService {
//    private final CategoryRepository categoryRepository;
//
//    @Override
//    public List<String> crawlProductDetails(String link) {
//        int retryCount = 0;
//        final int maxRetries = 5;
//        while (retryCount < maxRetries) {
//            try {
//                Document doc = Jsoup.connect(link)
//                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
//                        .followRedirects(true)  // 리다이렉션 따라가기
//                        .timeout(30000)         // 타임아웃 증가
//                        .get();
//
//                log.info("크롤링 URL: {}", link);
//                return extractCategories(doc);
//
//            } catch (IOException e) {
//                log.error("크롤링 에러 URL [{}]: {}", link, e.getMessage());
//
//                // 429 에러가 발생한 경우 재시도
//                if (e.getMessage().contains("429")) {
//                    retryCount++;
//                    if (retryCount < maxRetries) {
//                        try {
//                            // 일정 시간 대기 (예: 2초)
//                            Thread.sleep(2000 * retryCount);
//                        } catch (InterruptedException ie) {
//                            log.error("Sleep 중 에러 발생: {}", ie.getMessage());
//                        }
//                    }
//                } else {
//                    return Collections.emptyList();
//                }
//            }
//        }
//        return Collections.emptyList();
//    }
//
//    private List<String> extractCategories(Document doc) {
//        Element element = doc.getElementById("INTRODUCE");
//
//        if (element == null){
//            log.warn("INTRODUCE 요소를 찾을 수 없습니다.");
//            return Collections.emptyList();
//        }
//        // 페이지의 해당 텍스트를 가져옴
//        String text = element.text();
//        log.info("크롤링된 INTRODUCE 텍스트: {}", text);
//
//        List<String> foundCategories = new ArrayList<>();
//        String[] targetKeywords = {"눈건강", "면역력", "관절", "피부건강", "간건강", "미네랄", "뼈건강"};
//
//        for (String keyword: targetKeywords) {
//            if (text.contains(keyword)) {
//                if(!categoryRepository.existsByName(keyword)) {
//                    categoryRepository.save(Category.builder().name(keyword).build());
//                }
//                foundCategories.add(keyword);
//            }
//        }
//        return foundCategories;
//    }
//}