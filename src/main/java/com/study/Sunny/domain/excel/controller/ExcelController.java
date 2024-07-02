package com.study.Sunny.domain.excel.controller;

import com.study.Sunny.domain.excel.dto.ExcelResult;
import com.study.Sunny.domain.excel.service.ExcelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/excel")

public class ExcelController {
    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<ExcelResult> readExcel(@RequestPart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
        ExcelResult rslt = excelService.excelUpload(file);
        return new ResponseEntity<ExcelResult>(rslt, HttpStatus.OK);
    }
}
