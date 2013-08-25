package ru.albemuth.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Replacement {

    public abstract String getReplacement(Matcher m);

    public static String replace(String s, Pattern p, Replacement replacement) {
        StringBuilder sb = new StringBuilder(s);
        return replace(sb, p, replacement).toString();
    }

    public static StringBuffer replace(StringBuffer sb, Pattern p, Replacement replacement) {
        StringBuilder builder = new StringBuilder(sb);
        return new StringBuffer(replace(builder, p, replacement));
    }

    public static StringBuilder replace(StringBuilder sb, Pattern p, Replacement replacement) {
        Matcher m = p.matcher(sb);

        /*
        for(int from = 0; m.find(from); ) {
            String replacementString = replacement.getReplacement(m);
            sb.replace(m.start(), m.end(), replacementString);
            from = m.start() + replacementString.length();
        }
        return sb;
        */

        List<Range> ranges = new ArrayList<Range>();
        int retLength = sb.length();
        for(int from = 0; m.find(from); ) {
            String replacementString = replacement.getReplacement(m);
            retLength = retLength - (m.end() - m.start()) + replacementString.length();
            ranges.add(new Range(replacementString, m.start(), m.end()));
            from = m.end();
        }

        StringBuilder ret = new StringBuilder(retLength);
        int offset = 0;
        char[] buffer = new char[1024];
        for (Range range: ranges) {
            append(sb, ret, offset, range.start, buffer);
            ret.append(range.replacement);
            offset = range.end;

        }
        append(sb, ret, offset, sb.length(), buffer);
        return ret;
    }

    private static int append(StringBuilder source, StringBuilder dest, int start, int end, char[] buffer) {
        int length;
        for (; start < end; ) {
            length = end - start > buffer.length ? buffer.length : end - start;
            source.getChars(start, start + length, buffer, 0);
            dest.append(buffer, 0, length);
            start += length;
        }
        return start;
    }

    public static String replace(String s, String regex, final String replacement) {
        return replace(s, Pattern.compile(regex, Pattern.DOTALL), new Replacement() {
            public String getReplacement(Matcher m) {
                return replacement;
            }
        });
    }

    public static StringBuffer replace(StringBuffer sb, String regex, final String replacement) {
        return replace(sb, Pattern.compile(regex, Pattern.DOTALL), new Replacement() {
            public String getReplacement(Matcher m) {
                return replacement;
            }
        });
    }

    public static StringBuilder replace(StringBuilder sb, String regex, final String replacement) {
        return replace(sb, Pattern.compile(regex, Pattern.DOTALL), new Replacement() {
            public String getReplacement(Matcher m) {
                return replacement;
            }
        });
    }

    private static class Range {
        private String replacement;
        private int start;
        private int end;

        public Range(String replacement, int start, int end) {
            this.replacement = replacement;
            this.start = start;
            this.end = end;
        }

        public String replacement() {
            return replacement;
        }

        public int start() {
            return start;
        }

        public int end() {
            return end;
        }

    }

}