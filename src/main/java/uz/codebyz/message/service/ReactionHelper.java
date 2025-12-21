package uz.codebyz.message.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionHelper {
    public static Map<String, Long> toCounts(List<Object[]> rows) {
        Map<String, Long> m = new HashMap<>();
        if (rows == null) return m;
        for (Object[] r : rows) {
            String emoji = (String) r[0];
            Long cnt = (Long) r[1];
            m.put(emoji, cnt);
        }
        return m;
    }
}
