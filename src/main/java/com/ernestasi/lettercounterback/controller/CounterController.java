package com.ernestasi.lettercounterback.controller;

import com.ernestasi.lettercounterback.exceptions.BadRequestDataException;
import com.ernestasi.lettercounterback.model.LetterCounterResults;
import com.ernestasi.lettercounterback.service.LetterCounterService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@AllArgsConstructor
public class CounterController {
    private final LetterCounterService letterCounterService;

    @GetMapping(path = "/change-range")
    public ResponseEntity<String> getFiles(@RequestParam(name ="rangeIndex") Integer rangeIndex,
                                           @RequestParam(name ="min") Integer min,
                                           @RequestParam(name ="max") Integer max ) {
        return letterCounterService.changeRange(rangeIndex, min, max);
    }

    @GetMapping(path = "/ranges")
    public ResponseEntity<Map<Integer, Integer[]>> getRanges() {
        return letterCounterService.getRanges();
    }

    @GetMapping(path="/change-limit")
    public ResponseEntity<String> getFiles(@RequestParam(name ="limit") Integer limit) {
        return letterCounterService.changeLimit(limit);
    }

    @GetMapping(path="/file-names")
    public List<String> getFiles() {
        return letterCounterService.getFileNames();
    }

    @PutMapping(path="/clear-data")
    public ResponseEntity<String> clearData() {
        return letterCounterService.claerData();
    }

    @GetMapping(path="/maps")
    public List<Map<String, Integer>> getMaps() {
        return letterCounterService.getAllMaps();
    }

    @PostMapping("/upload")
    public ResponseEntity<LetterCounterResults> uploadFileOnAttourney(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestDataException("file is empty");
        }
        return letterCounterService.addFile(file);
//        return ResponseEntity.status(HttpStatus.OK).body();
    }
}
