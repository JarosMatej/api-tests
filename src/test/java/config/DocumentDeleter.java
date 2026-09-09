package config;

import java.util.*;


public class DocumentDeleter {

    private static final ArrayList<String> docTokens = new ArrayList<>();

    public static void addToken(String token) {
        docTokens.add(token);
    }

    public static ArrayList<String> getDocTokens() {
        return docTokens;
    }

    public static String serializeDocTokens() {
        StringBuilder sb = new StringBuilder();

        if (docTokens.isEmpty()) {
            return "[]";
        }

        sb.append('[');
        sb.append('\"');
        sb.append(docTokens.getFirst());
        sb.append('\"');
        for (var token : docTokens) {
            sb.append(",\"");
            sb.append(token);
            sb.append('\"');
        }

        sb.append(']');

        return sb.toString();
    }
}
