package com.study.Sunny.domain.excel.service;

import com.study.Sunny.domain.excel.dto.ExcelData;
import com.study.Sunny.domain.excel.dto.ExcelResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelService {

    /**
     * 엑셀 업로드
     */
    public ExcelResult excelUpload(MultipartFile file) throws Exception {
        // success true로 생성. 오류나면 false로 바꿈
        ExcelResult result = new ExcelResult(true);
        // id는 중복 X
        Map<String, Integer> uniqueIds = new HashMap<>();
        // 파일 Original 이름의 파일 확장자만 가져오기
        String fileExtsn = FilenameUtils.getExtension(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            // 엑셀 데이터 ExcelData에 저장
            List<ExcelData> excelDataList = this.excelToDatas(inputStream, fileExtsn);
            // 결과에 엑셀데이터(raw) 저장
            result.setRawData(excelDataList);

            // validation
            for (int i = 0; i < excelDataList.size(); i++) {
                ExcelData excelData = excelDataList.get(i);
                ExcelResult.RowError rowError = new ExcelResult.RowError(i + 1);

                if (excelData.getId() == null || excelData.getId().isEmpty()) {
                    rowError.addMessage("아이디는 필수 항목입니다.");
                    rowError.setId("EMPTY");
                } else if (uniqueIds.containsKey(excelData.getId())) {
                    rowError.addMessage("아이디가 중복되었습니다. (중복 행: " + uniqueIds.get(excelData.getId()) + ")");
                    //String idStatus = rowError.getId();
                    //rowError.setId(idStatus+" DUPLICATE");
                    rowError.setId("DUPLICATE");
                } else {
                    uniqueIds.put(excelData.getId(), i + 1);
                }

                if (excelData.getName() == null || excelData.getName().isEmpty()) {
                    rowError.addMessage("이름은 필수 항목입니다.");
                    //String nameStatus = rowError.getName();
                    //rowError.setId(nameStatus+" EMPTY");
                    rowError.setName("EMPTY");
                }

                if (excelData.getEmail() == null || excelData.getEmail().isEmpty()) {
                    rowError.addMessage("이메일은 필수 항목입니다.");
                    rowError.setEmail("EMPTY");
                } else if (!excelData.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    rowError.addMessage("유효한 이메일 형식이어야 합니다.");
                    rowError.setEmail("INVALID_FORMAT");
                }

                if (excelData.getMobilePhone() == null || excelData.getMobilePhone().isEmpty()) {
                    rowError.addMessage("휴대폰 번호는 필수 항목입니다.");
                    rowError.setMobile("EMPTY");
                } else if (!excelData.getMobilePhone().matches("\\d{10,11}")) {
                    rowError.addMessage("유효한 휴대폰 번호 형식이어야 합니다.");
                    rowError.setMobile("INVALID_FORMAT");
                }

                if (!rowError.getMessages().isEmpty()) {
                    result.addError(rowError);
                    result.setSuccess(false);
                }
            }

            if (result.isSuccess()) {
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            ExcelResult.RowError error = new ExcelResult.RowError(-1);
            error.addMessage("파일 처리 실패: " + e.getMessage());
            result.addError(error);
        }

        return result;
    }

    public static List<ExcelData> excelToDatas(InputStream is, String fileExtsn) {
        try {
            Workbook workbook = null;
            // 확장자별 세팅. 엑셀 97 - 2003: HSSF(xls),  엑셀 2007 이상: XSSF(xlsx)
            if ("xls".equals(fileExtsn)) {
                workbook = new HSSFWorkbook(is);
            } else {
                workbook = new XSSFWorkbook(is);
            }
            
            // 첫번째 시트 -> 여러시트로 바꿔야함
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ExcelData> excelDataList = new ArrayList<>();

            // 데이터가 시작되는 rowNumber -> 나중에 파라미터로 가져와서 초기 세팅할 수 있음
            int rowNumber = 1; // 테스트 파일에는 header가 있으므로 1

            // 다음 row 데이터 있을때까지
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                ExcelData excelData = new ExcelData();

                // 데이터 rownum 세팅
                excelData.setRowNum(currentRow.getRowNum());

                // 마지막 cell index 불러오기(column)
                int lastCellIndex = currentRow.getLastCellNum();
                for (int columnIndex = 0; columnIndex < lastCellIndex; columnIndex++) {
                    Cell currentCell = currentRow.getCell(columnIndex);
                    // 일단 편의를 위해 셀 데이터가 모두 String이라고 가정함 -> 데이터 타입별로 세팅 작업 필요
                    String value = "";
                    // 셀 데이터가 null로 들어오는 경우 nullPointEception발생으로 데이터 ""로 치환 작업
                    if(null == currentCell || CellType.BLANK == currentCell.getCellType() ){
                        value = "";
                    } else{
                        value = currentCell.getStringCellValue(); // 해당 셀 데이터
                    }

                    // column별로 데이터 세팅
                    switch (columnIndex) {
                        case 0: // id
                            excelData.setId(value);
                            break;
                        case 1: // 이름
                            excelData.setName(value);
                            break;
                        case 2: // 이메일
                            excelData.setEmail(value);
                            break;
                        case 3: // 휴대폰
                            excelData.setMobilePhone(value);
                            break;
                        case 4: // 집번호
                            excelData.setHomePhone(value);
                            break;
                        case 5: // 거주도시
                            excelData.setCity(value);
                            break;
                        default:
                            break;
                    }
                }
                excelDataList.add(excelData);
            }

            workbook.close();
            return excelDataList;

        } catch (Exception e) {
            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage());
        }
    }
}
