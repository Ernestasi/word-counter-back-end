package com.ernestasi.lettercounterback.service;

import com.ernestasi.lettercounterback.exceptions.LetterCounterException;
import com.ernestasi.lettercounterback.model.LetterCounterResults;
import com.google.common.base.CharMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LetterCounterServiceImpl implements LetterCounterService {

    private static final CharMatcher CHARACTERS = CharMatcher.anyOf("ABCDEFGHIJKLMNOPQWRTUVWXYZabcdefghijklmnopqrstuvwxyzĄČĘĖĮŠŲŪŽąčęėįšųūž");
    private List<String> fileNames = new ArrayList<>();
    private Integer LIMIT = 50;
    private String fromLetter1 = "a";
    private String toLetter1 = "g";
    private String fromLetter2 = "h";
    private String toLetter2 = "n";
    private String fromLetter3 = "o";
    private String toLetter3 = "u";
    private String fromLetter4 = "v";
    private String toLetter4 = "z";
    private HashMap<String, Integer> wordMap = new HashMap();
    private HashMap<String, Integer> agWordMap = new HashMap();
    private HashMap<String, Integer> hnWordMap = new HashMap();
    private HashMap<String, Integer> ouWordMap = new HashMap();
    private HashMap<String, Integer> vzWordMap = new HashMap();

    private Map<Integer, Integer[]> rangeMap = createRangeMap();

    private Map<Integer, Integer[]> createRangeMap() {
        HashMap<Integer, Integer[]> map = new HashMap<>();
        Integer[] values1 = {97, 103};
        map.put(0, values1);
        Integer[] values2 = {104, 110};
        map.put(1, values2);
        Integer[] values3 = {111, 117};
        map.put(2, values3);
        Integer[] values4 = {118, 125};
        map.put(3, values4);
        return map;
    }

    @Override
    public ResponseEntity<LetterCounterResults> addFile(MultipartFile file) {
        try {
            String content = new String(file.getBytes());
            String[] unfilteredStrings = content.trim().split("\\s+");
            List<String> filteredStrings = new ArrayList<>();
            for (String word : unfilteredStrings) {
                String lowerCaseWord = word.toLowerCase(Locale.ROOT);
                if (meetsFilterRequirements(word)) {
                    filteredStrings.add(lowerCaseWord);
                } else {
                    filteredStrings.add(fixWord(lowerCaseWord));
                }
            }

            for (String word : filteredStrings) {
                if (wordMap.containsKey(word)) {
                    wordMap.put(word, wordMap.get(word) + 1);
                } else {
                    wordMap.put(word, 1);
                }
            }

            recalculateMaps();

        } catch (Exception e) {
            throw new LetterCounterException(e.getMessage());
        }


        fileNames.add(file.getOriginalFilename());
        return null;
    }

    private void recalculateMaps() {
        MyComparator comparator = new MyComparator(wordMap);
        TreeMap<String, Integer> treeMap = new TreeMap(comparator);
        treeMap.putAll(wordMap);

        SortedMap<String, Integer> agMap = null;
        SortedMap<String, Integer> hnMap = null;
        SortedMap<String, Integer> ouMap = null;
        SortedMap<String, Integer> vzMap = null;


        for (Integer integerKey : rangeMap.keySet()) {
            Integer[] range = rangeMap.get(integerKey);
            switch (integerKey) {
                case 0: {
                    agMap = treeMap.subMap(parseLetterIndexToAsciiValue(range[0]), true, parseLetterIndexToAsciiValue(range[1]), true);
                    break;
                }
                case 1: {
                    hnMap = treeMap.subMap(parseLetterIndexToAsciiValue(range[0]), true, parseLetterIndexToAsciiValue(range[1]), true);
                    break;
                }
                case 2: {
                    ouMap = treeMap.subMap(parseLetterIndexToAsciiValue(range[0]), true, parseLetterIndexToAsciiValue(range[1]), true);
                    break;
                }
                case 3: {
                    vzMap = treeMap.subMap(parseLetterIndexToAsciiValue(range[0]), true, parseLetterIndexToAsciiValue(range[1]), true);
                    break;
                }
            }
        }


        agWordMap.putAll(agMap);
        hnWordMap.putAll(hnMap);
        ouWordMap.putAll(ouMap);
        vzWordMap.putAll(vzMap);

        agWordMap = getSorterMapByValue(agWordMap);
        hnWordMap = getSorterMapByValue(hnWordMap);
        ouWordMap = getSorterMapByValue(ouWordMap);
        vzWordMap = getSorterMapByValue(vzWordMap);
    }

    private String parseLetterIndexToAsciiValue(Integer integer) {
        char b = (char) integer.intValue();
        return String.valueOf(b);
    }

    private HashMap<String, Integer> getSorterMapByValue(HashMap<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(LIMIT)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public List<String> getFileNames() {
        return fileNames;
    }

    @Override
    public List<Map<String, Integer>> getAllMaps() {
        List<Map<String, Integer>> mapList = new ArrayList<>();
        mapList.add(agWordMap);
        mapList.add(hnWordMap);
        mapList.add(ouWordMap);
        mapList.add(vzWordMap);
        return mapList;
    }

    @Override
    public ResponseEntity<String> claerData() {
        wordMap.clear();
        fileNames.clear();
        agWordMap.clear();
        hnWordMap.clear();
        ouWordMap.clear();
        vzWordMap.clear();
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @Override
    public ResponseEntity<String> changeLimit(Integer limit) {
        this.LIMIT = limit;
        recalculateMaps();
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @Override
    public ResponseEntity<String> changeRange(Integer rangeIndex, Integer min, Integer max) {
        //cause range from eg. "a" to "b" will include only word "a" and will not include "apple"
        if (min-max == 0) {
            max = max+1;
        }
        Integer[] range = {min, max};
        rangeMap.put(rangeIndex, range);

        agWordMap.clear();
        hnWordMap.clear();
        ouWordMap.clear();
        vzWordMap.clear();

        recalculateMaps();

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @Override
    public ResponseEntity<Map<Integer, Integer[]>> getRanges() {
        return ResponseEntity.status(HttpStatus.OK).body(rangeMap);
    }

    private String fixWord(String word) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : word.getBytes()) {
            char c = ((char) b);
            if (Character.isAlphabetic(c)) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private boolean meetsFilterRequirements(String word) {
        return CHARACTERS.matchesAllOf(word);
    }

    class MyComparator implements Comparator {
        Map map;

        public MyComparator(Map map) {
            this.map = map;
        }

        @Override
        public int compare(Object o1, Object o2) {
            return (o1.toString()).compareTo(o2.toString());
        }
    }
}
