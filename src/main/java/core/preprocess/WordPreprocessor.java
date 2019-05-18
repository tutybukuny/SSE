package core.preprocess;

import org.apache.log4j.Logger;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WordPreprocessor {
    private static final Logger LOGGER = Logger.getLogger(WordPreprocessor.class);
    private static WordPreprocessor WORD_PREPROCESSOR = null;
    private static String[] stw = new String[]{
            "bị", "bởi", "cả", "các", "cái", "cần", "càng", "chỉ", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "có_thể",
            "cứ", "của", "cùng", "cũng", "đã", "đang", "đây", "để", "đến_nỗi", "đều", "điều", "do", "đó", "được", "dưới",
            "gì", "khi", "không", "là", "lại", "lên", "lúc", "mà", "mỗi", "một_cách", "này", "nên", "nếu", "ngay", "nhiều",
            "như", "nhưng", "những", "nơi", "nữa", "phải", "qua", "ra", "rằng", "rằng", "rất", "rất", "rồi", "sau", "sẽ",
            "so", "sự", "tại", "theo", "thì", "trên", "trước", "từ", "từng", "và", "vẫn", "vào", "vậy", "vì", "việc", "với",
            "vừa", "nhận", "rằng", "cao", "nhà", "quá", "riêng", "gì", "muốn", "rồi", "số", "thấy", "hay", "lên", "lần", "nào",
            "qua", "bằng", "điều", "biết", "lớn", "khác", "vừa", "nếu", "thời_gian", "họ", "từng", "đây", "tháng", "trước",
            "chính", "cả", "việc", "chưa", "do", "nói", "ra", "nên", "đều", "đi", "tới", "tôi", "có_thể", "cùng", "vì", "làm",
            "lại", "mới", "ngày", "đó", "vẫn", "mình", "chỉ", "thì", "đang", "còn", "bị", "mà", "năm", "nhất", "hơn", "sau",
            "ông", "rất", "anh", "phải", "như", "trên", "tại", "theo", "khi", "nhưng", "vào", "đến", "nhiều", "người", "từ",
            "sẽ", "ở", "cũng", "không", "về", "để", "này", "những", "một", "các", "cho", "được", "với", "có", "trong", "đã",
            "là", "và", "của", "thực_sự", "ở_trên", "tất_cả", "dưới", "hầu_hết", "luôn", "giữa", "bất_kỳ", "hỏi", "bạn", "cô",
            "tôi", "tớ", "cậu", "bác", "chú", "dì", "thím", "cậu", "mợ", "ông", "bà", "em", "thường", "ai", "cảm_ơn"
    };

    public static WordPreprocessor getInstance() {
        if (WORD_PREPROCESSOR == null)
            WORD_PREPROCESSOR = new WordPreprocessor();
        return WORD_PREPROCESSOR;
    }

    private static boolean isStopWord(String tok) {
        for (String str : stw) {
            if (str.equals(tok)) {
                return true;
            }
        }

        return false;
    }

    public String convertStringToURL(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll("đ", "d");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public HashMap<String, WordSegment> createWordSegmentDictionary(ArrayList<String> sentences) {
        HashMap<String, WordSegment> map = new HashMap<>();
        try {
            for (int i = 0; i < sentences.size(); i++) {
                ArrayList<String> words = getWords(sentences.get(i));
                for (String word : words) {
                    putToMap(map, word, i);
                    if (word.compareTo(convertStringToURL(word)) != 0)
                        putToMap(map, convertStringToURL(word), i);
                }
            }
        } catch (NullPointerException ex) {
            LOGGER.error(ex);
        }

        return map;
    }

    public HashMap<String, WordSegment> createWordSegmentDictionary(String pathToDataData) {
        HashMap<String, WordSegment> map = null;

        try {
            Scanner inp = new Scanner(new File(pathToDataData));
            ArrayList<String> sentences = new ArrayList<>();
            while (inp.hasNext()) {
                sentences.add(inp.nextLine());
            }
            map = createWordSegmentDictionary(sentences);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }

        return map;
    }

    private void putToMap(HashMap<String, WordSegment> map, String word, int documentIndex) {
        try {
            WordSegment wordSegment = map.get(word);
            if (wordSegment == null) {
                wordSegment = new WordSegment(word);
            }
            wordSegment.addDocumentIndex(documentIndex);
            map.put(word, wordSegment);
        } catch (NullPointerException ex) {
            LOGGER.error(ex);
        }
    }

    public ArrayList<String> getWords(String sentence) {
        ArrayList<String> wordList = new ArrayList<>();
        String[] words = sentence.split("\\s+");
        for (int j = 0; j < words.length; j++) {
            String word = words[j].replaceAll("[!@#$%^&*(),.?\":\\{\\}|<>\\-+/]", " ").trim().toLowerCase();
            if (!word.isEmpty())
                wordList.add(word);
        }

        return wordList;
    }
}
