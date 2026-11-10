/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * JsonValidator
 *
 * @since 2019-02-18
 */
public class JsonValidator {
    private CharacterIterator it;
    private char c;
    private int col;

    /**
     * 无参构造
     *
     */
    public JsonValidator() {
    }

    /**
     * 数据校验
     *
     * @param input input
     * @return boolean 布尔值
     */
    public boolean validate(String input) {
        String temp = input.trim();
        return valid(temp);
    }

    private boolean valid(String input) {
        if ("".equals(input)) {
            return true;
        }
        boolean isRet = true;
        it = new StringCharacterIterator(input);
        c = it.first();
        col = 1;
        if (!value()) {
            isRet = error("value", 1);
        } else {
            skipWhiteSpace();
            if (c != CharacterIterator.DONE) {
                isRet = error("end", col);
            }
        }
        return isRet;
    }

    private boolean value() {
        return literal("true") || literal("false") || literal("null") || string() || number() || object() || array();
    }

    private boolean literal(String text) {
        CharacterIterator ci = new StringCharacterIterator(text);
        char t = ci.first();
        if (c != t) {
            return false;
        }

        int start = col;
        boolean isRet = true;
        for (t = ci.next(); t != CharacterIterator.DONE; t = ci.next()) {
            if (t != nextCharacter()) {
                isRet = false;
                break;
            }
        }
        nextCharacter();
        if (!isRet) {
            error("literal " + text, start);
        }
        return isRet;
    }

    private boolean array() {
        return isAggregate('[', ']', false);
    }

    private boolean object() {
        return isAggregate('{', '}', true);
    }

    private boolean isAggregate(char entryCharacter, char exitCharacter, boolean isPrefix) {
        if (c != entryCharacter) {
            return false;
        }
        nextCharacter();
        skipWhiteSpace();
        if (c == exitCharacter) {
            nextCharacter();
            return true;
        }

        for (; ;) {
            if (isPrefix) {
                int start = col;
                if (!string()) {
                    return error("string", start);
                }
                skipWhiteSpace();
                if (c != ':') {
                    return error("colon", col);
                }
                nextCharacter();
                skipWhiteSpace();
            }
            if (value()) {
                skipWhiteSpace();
                if (c == ',') {
                    nextCharacter();
                } else if (c == exitCharacter) {
                    break;
                } else {
                    return error("comma or " + exitCharacter, col);
                }
            } else {
                return error("value", col);
            }
            skipWhiteSpace();
        }

        nextCharacter();
        return true;
    }

    private boolean number() {
        if (!Character.isDigit(c) && c != '-') {
            return false;
        }
        int start = col;
        if (c == '-') {
            nextCharacter();
        }
        if (c == '0') {
            nextCharacter();
        } else if (Character.isDigit(c)) {
            while (Character.isDigit(c)) {
                nextCharacter();
            }
        } else {
            return error("number", start);
        }
        if (c == '.') {
            nextCharacter();
            if (Character.isDigit(c)) {
                while (Character.isDigit(c)) {
                    nextCharacter();
                }
            } else {
                return error("number", start);
            }
        }
        if (c == 'e' || c == 'E') {
            nextCharacter();
            if (c == '+' || c == '-') {
                nextCharacter();
            }
            if (Character.isDigit(c)) {
                while (Character.isDigit(c)) {
                    nextCharacter();
                }
            } else {
                return error("number", start);
            }
        }
        return true;
    }

    private boolean string() {
        if (c != '"') {
            return false;
        }
        int start = col;
        boolean isEscaped = false;
        for (nextCharacter(); c != CharacterIterator.DONE; nextCharacter()) {
            if (!isEscaped && c == '\\') {
                isEscaped = true;
            } else if (isEscaped) {
                if (!escape()) {
                    return false;
                }
                isEscaped = false;
            } else if (c == '"') {
                nextCharacter();
                return true;
            }
        }
        return error("quoted string", start);
    }

    private boolean escape() {
        int start = col - 1;
        if (" \\\"/bfnrtu".indexOf(c) < 0) {
            return error("escape sequence  \\\",\\\\,\\/,\\b,\\f,\\n,\\r,\\t  or  \\uxxxx ", start);
        }
        if (c == 'u') {
            if (!ishex(nextCharacter())
                    || !ishex(nextCharacter())
                    || !ishex(nextCharacter())
                    || !ishex(nextCharacter())) {
                return error("unicode escape sequence  \\uxxxx ", start);
            }
        }
        return true;
    }

    private boolean ishex(char d) {
        return "0123456789abcdefABCDEF".indexOf(c) >= 0;
    }

    private char nextCharacter() {
        c = it.next();
        ++col;
        return c;
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(c)) {
            nextCharacter();
        }
    }

    private boolean error(String type, int col) {
        return false;
    }
}
