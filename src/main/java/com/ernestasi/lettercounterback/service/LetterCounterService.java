package com.ernestasi.lettercounterback.service;

import com.ernestasi.lettercounterback.model.LetterCounterResults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LetterCounterService {
    ResponseEntity<LetterCounterResults> addFile(MultipartFile file);

    List<String> getFileNames();

    List<Map<String, Integer>> getAllMaps();

    ResponseEntity<String> claerData();

    ResponseEntity<String> changeLimit(Integer limit);

    ResponseEntity<String> changeRange(Integer rangeIndex, Integer min, Integer max);

    ResponseEntity<Map<Integer, Integer[]>> getRanges();
}
