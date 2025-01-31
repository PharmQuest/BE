package com.pharmquest.pharmquest.domain.supplements.service.DailyMed;

import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.DailyMedResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMedServiceImpl implements DailyMedService {
    private static final String DAILY_MED_URL = "https://dailymed.nlm.nih.gov/dailymed/services/v2/spls.json?search=supplement?labeltype=human";
    private static final String DETAIL_URL = "https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/{setId}.xml";
    private static final String IMAGE_URL = "https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/{setId}/media.json";

    private final TranslationService translationService;

    @Override
    public List<DailyMedResponseDTO.ExtractedInfo> extractSupplementInfo() {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<DailyMedResponseDTO.SearchResponse> response =
                    restTemplate.getForEntity(DAILY_MED_URL, DailyMedResponseDTO.SearchResponse.class);

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().stream()
                        .limit(100)
                        .map(item -> DailyMedResponseDTO.ExtractedInfo.builder()
                                .setid(item.getSetid())
                                .title(item.getTitle())
                                .build())
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch data from DailyMed API: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch data from DailyMed API", e);
        }
    }

    @Override
    public DailyMedResponseDTO.DetailInfo getDetailInfo(String setId, String title) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String xmlResponse = restTemplate.getForObject(DETAIL_URL, String.class, setId);

            if (xmlResponse == null) {
                log.error("Failed to fetch XML data for setId: {}", setId);
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));

            String manufacturer = extractManufacturerFromTitle(title);
            String processedTitle = removeManufacturerFromTitle(title);
            String fullTitle = processedTitle.replaceAll("\\(.*?\\)", "").trim() + " / " + translate(processedTitle.replaceAll("\\(.*?\\)", "").trim());
            String imageUrl = fetchImageUrl(setId);
            String dosage = translate(extractSectionText(document, "34068-7"));
            String purpose = translate(extractSectionText(document, "34067-9"));
            String warning = translate(extractSectionText(document, "34071-1"));

            if (dosage == null || purpose == null || warning == null) {
                return null;
            }

            return DailyMedResponseDTO.DetailInfo.builder()
                    .title(fullTitle)
                    .manufacturer(manufacturer)
                    .dosage(dosage)
                    .purpose(purpose)
                    .warning(warning)
                    .imageUrl(imageUrl)
                    .build();

        } catch (Exception e) {
            log.error("Error processing XML for setId {}: {}", setId, e.getMessage());
            throw new RuntimeException("Failed to process XML data", e);
        }
    }

    private String extractSectionText(Document document, String code) {
        try {
            NodeList sections = document.getElementsByTagName("section");
            for (int i = 0; i < sections.getLength(); i++) {
                Element section = (Element) sections.item(i);
                NodeList codes = section.getElementsByTagName("code");

                for (int j = 0; j < codes.getLength(); j++) {
                    Element codeElement = (Element) codes.item(j);
                    if (code.equals(codeElement.getAttribute("code"))) {
                        NodeList textNodes = section.getElementsByTagName("text");
                        if (textNodes.getLength() > 0) {
                            return cleanText(textNodes.item(0).getTextContent());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract section text for code {}: {}", code, e.getMessage());
        }
        return null;
    }

    private String fetchImageUrl(String setId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = IMAGE_URL.replace("{setId}", setId); // setId로 URL 생성
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            // JSON 데이터에서 이미지 URL 추출
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            List<Map<String, String>> media = (List<Map<String, String>>) data.get("media");

            if (media != null && !media.isEmpty()) {
                return media.get(0).get("url"); // 첫 번째 이미지 URL 반환
            }
        } catch (Exception e) {
            log.error("Failed to fetch image URL for setId {}: {}", setId, e.getMessage());
        }
        return null; // 이미지 URL이 없으면 null 반환
    }

    private String translate(String text) {
        if (text == null) return null;
        try {
            return translationService.translateText(text, "ko");
        } catch (Exception e) {
            log.error("Translation failed: {}", e.getMessage());
            return text;
        }
    }

    private String cleanText(String text) {
        if (text == null) return null;

        String cleaned = text
                .replaceAll("â¢|•", "")
                .replaceAll("Â", "")
                .replaceAll("ï»¿", "")
                .replaceAll("\\s+", " ")
                .replaceAll("\\n\\s*", " ")
                .replaceAll("(?i)directions:?\\s*", "")
                .replaceAll("(?i)warnings:?\\s*", "")
                .replaceAll("(?i)uses:?\\s*", "")
                .replaceAll(":\\s+", ": ")
                .trim();

        return cleaned;
    }

    private String extractManufacturerFromTitle(String title) {
        if (title == null || !title.contains("[")) return null;

        int startIndex = title.indexOf("[") + 1;
        int endIndex = title.indexOf("]");
        if (startIndex > 0 && endIndex > startIndex) {
            return title.substring(startIndex, endIndex).trim();
        }
        return null;
    }

    private String removeManufacturerFromTitle(String title) {
        if (title == null || !title.contains("[")) return title;

        int startIndex = title.indexOf("[");
        int endIndex = title.indexOf("]") + 1;

        if (startIndex > 0 && endIndex > startIndex) {
            return title.substring(0, startIndex).trim();
        }
        return title;
    }
}
