package com.study.Sunny.domain.excel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExcelResult {

    private boolean success = true;
    private List<RowResult> results = new ArrayList<>();

    @Data
    public static class RowResult {
        private int rowNum;
        private ExcelData excelData;
        private Map<String, Object> status = new HashMap<>();

        public RowResult(ExcelData excelData) {
             if(excelData != null){
                this.rowNum = excelData.getRowNum();
                this.excelData = excelData;
            }
        }

        public void addResult(String field, String code, String message) {
            if(!"".equals(code)){
                Map<String, String> detail = new HashMap<>();
                detail.put("code", code);
                detail.put("message", message);
                this.status.put(field, detail);
            }
        }
    }

    /*private boolean success = true;
    private List<ExcelData> rawData = new ArrayList<>();
    private List<RowError> errors = new ArrayList<>();

    public void addError(RowError error) {
        this.errors.add(error);
    }

    @Data
    public static class RowError {
        private int rowNum;
        private String id;
        private String name;
        private String email;
        private String mobile;
        private String home;
        private String city;
        private List<String> messages = new ArrayList<>();

        public RowError(int rowNum) {
            this.rowNum = rowNum;
        }

        public void addMessage(String message) {
            this.messages.add(message);
        }

        // Getter와 Setter 추가 -> 필요 없는 것은 세팅 안하기 위함(결과 데이터에 안나오게)
        /*public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }*/
}
