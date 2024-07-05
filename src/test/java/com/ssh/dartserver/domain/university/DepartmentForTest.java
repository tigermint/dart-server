package com.ssh.dartserver.domain.university;

public enum DepartmentForTest {
    COMPUTER_SCIENCE("컴퓨터공학과"),
    COMPUTER_PLAY("컴퓨터게임학과"),
    ELECTRICAL_ENGINEERING("전자공학과"),
    MECHANICAL_ENGINEERING("기계공학과"),
    CHEMICAL_ENGINEERING("화학공학과"),
    ARCHITECTURE("건축학과"),
    INDUSTRIAL_DESIGN("산업디자인학과"),
    BUSINESS_ADMINISTRATION("경영학과"),
    ECONOMICS("경제학과"),
    LAW("법학과"),
    PSYCHOLOGY("심리학과"),
    SOCIOLOGY("사회학과"),
    ENGLISH_LANGUAGE_AND_LITERATURE("영어영문학과"),
    CHINESE_LANGUAGE_AND_LITERATURE("중국어문학과"),
    JAPANESE_LANGUAGE_AND_LITERATURE("일본어문학과"),
    HISTORY("사학과"),
    PHILOSOPHY("철학과"),
    MATHEMATICS("수학과"),
    PHYSICS("물리학과"),
    CHEMISTRY("화학과"),
    LIFE_SCIENCES("생명과학과"),
    ENVIRONMENTAL_ENGINEERING("환경공학과"),
    MEDICINE("의학과"),
    NURSING("간호학과"),
    PHARMACY("약학과"),
    DENTISTRY("치의학과"),
    VETERINARY_MEDICINE("수의학과"),
    PHYSICAL_EDUCATION("체육학과"),
    MUSIC("음악학과"),
    FINE_ARTS("미술학과"),
    THEATER_AND_FILM("연극영화학과");

    private final String koreanName;

    DepartmentForTest(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
