package com.saicone.itemmatcher;

import com.saicone.itemmatcher.util.Utils;

import java.util.List;
import java.util.Objects;

public abstract class Comparator {

    protected static final Comparator DEFAULT = new Comparator() {
        @Override
        public boolean match(Object object1, Object object2) {
            return false;
        }
    };

    public static Comparator of(String name) {
        return of(name, DEFAULT);
    }

    public static Comparator of(String name, Comparator def) {
        switch (name.trim().toLowerCase()) {
            case "equal":
            case "equals":
            case "same":
            case "==":
                return Equal.INSTANCE;
            case "equalignorecase":
            case "equalsignorecase":
            case "(i)?==":
                return EqualIgnoreCase.INSTANCE;
            case "notequal":
            case "notsame":
            case "!=":
                return NotEqual.INSTANCE;
            case "notequalignorecase":
            case "notequalsignorecase":
            case "(i)?!=":
                return NotEqualIgnoreCase.INSTANCE;
            case "contains":
                return Contains.INSTANCE;
            case "startswith":
            case "startwith":
            case "starts":
            case "start":
                return StartsWith.INSTANCE;
            case "endswith":
            case "endwith":
            case "ends":
            case "end":
                return EndsWith.INSTANCE;
            case "greater":
            case "greaterthan":
            case "more":
            case "morethan":
            case ">":
                return Greater.INSTANCE;
            case "greaterorequal":
            case "greaterthanorequal":
            case "moreorequal":
            case "morethanorequal":
            case ">=":
                return GreaterOrEqual.INSTANCE;
            case "less":
            case "lessthan":
            case "<":
                return Less.INSTANCE;
            case "lessorequal":
            case "lessthanorequal":
            case "<=":
                return LessOrEqual.INSTANCE;
            case "regex":
                return RegEx.INSTANCE;
            default:
                return def;
        }
    }

    public boolean match(Object object1, Object object2) {
        if (object1 != null && object1.getClass().isInstance(object2)) {
            if (object1 instanceof String) {
                return matchString((String) object1, (String) object2);
            } else if (object1 instanceof Byte) {
                return matchNumber((byte) object1, (byte) object2);
            } else if (object1 instanceof Short) {
                return matchNumber((short) object1, (short) object2);
            } else if (object1 instanceof Integer) {
                return matchNumber((int) object1, (int) object2);
            } else if (object1 instanceof Long) {
                return matchNumber((long) object1, (long) object2);
            } else if (object1 instanceof Float) {
                return matchNumber((float) object1, (float) object2);
            } else if (object1 instanceof Double) {
                return matchNumber((double) object1, (double) object2);
            }
        }
        return matchObject(object1, object2);
    }

    public boolean matchObject(Object object1, Object object2) {
        return false;
    }

    public boolean matchString(String string1, String string2) {
        return false;
    }

    public boolean matchNumber(byte number1, byte number2) {
        return false;
    }

    public boolean matchNumber(short number1, short number2) {
        return false;
    }

    public boolean matchNumber(int number1, int number2) {
        return false;
    }

    public boolean matchNumber(long number1, long number2) {
        return false;
    }

    public boolean matchNumber(float number1, float number2) {
        return false;
    }

    public boolean matchNumber(double number1, double number2) {
        return false;
    }

    public <T> boolean matchValue(List<T> list1, T object2) {
        for (T object1 : list1) {
            if (match(object1, object2)) {
                return true;
            }
        }
        return false;
    }

    public <T> boolean matchValueAll(List<T> list1, T object2) {
        for (T object1 : list1) {
            if (!match(object1, object2)) {
                return false;
            }
        }
        return true;
    }

    public <T> boolean matchValueExact(List<T> list1, T object2) {
        return list1.size() == 1 && match(list1.get(0), object2);
    }

    public <T> boolean matchList(List<T> list1, List<T> list2) {
        for (T object1 : list1) {
            for (T object2 : list2) {
                if (match(object1, object2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public <T> boolean matchListAll(List<T> list1, List<T> list2) {
        for (T object2 : list2) {
            boolean match = false;
            for (T object1 : list1) {
                if ((match = match(object1, object2))) {
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public <T> boolean matchListInverseAll(List<T> list1, List<T> list2) {
        for (T object1 : list1) {
            boolean match = false;
            for (T object2 : list2) {
                if ((match = match(object1, object2))) {
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public <T> boolean matchListExact(List<T> list1, List<T> list2) {
        if (list1.size() == list2.size()) {
            for (int i = 0; i < list1.size(); i++) {
                if (!match(list1.get(i), list2.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static class Equal extends Comparator {

        public static final Equal INSTANCE = new Equal();

        @Override
        public boolean matchObject(Object object1, Object object2) {
            return Objects.equals(object1, object2);
        }

        @Override
        public boolean matchString(String string1, String string2) {
            return string1.equals(string2);
        }

        @Override
        public boolean matchNumber(byte number1, byte number2) {
            return number1 == number2;
        }

        @Override
        public boolean matchNumber(short number1, short number2) {
            return number1 == number2;
        }

        @Override
        public boolean matchNumber(int number1, int number2) {
            return number1 == number2;
        }

        @Override
        public boolean matchNumber(long number1, long number2) {
            return number1 == number2;
        }

        @Override
        public boolean matchNumber(float number1, float number2) {
            return number1 == number2;
        }

        @Override
        public boolean matchNumber(double number1, double number2) {
            return number1 == number2;
        }
    }

    public static class EqualIgnoreCase extends Comparator {

        public static final EqualIgnoreCase INSTANCE = new EqualIgnoreCase();

        @Override
        public boolean matchString(String string1, String string2) {
            return string1.equalsIgnoreCase(string2);
        }
    }

    public static class NotEqual extends Comparator {

        public static final NotEqual INSTANCE = new NotEqual();

        @Override
        public boolean matchObject(Object object1, Object object2) {
            return !Objects.equals(object1, object2);
        }

        @Override
        public boolean matchString(String string1, String string2) {
            return !string1.equals(string2);
        }

        @Override
        public boolean matchNumber(byte number1, byte number2) {
            return number1 != number2;
        }

        @Override
        public boolean matchNumber(short number1, short number2) {
            return number1 != number2;
        }

        @Override
        public boolean matchNumber(int number1, int number2) {
            return number1 != number2;
        }

        @Override
        public boolean matchNumber(long number1, long number2) {
            return number1 != number2;
        }

        @Override
        public boolean matchNumber(float number1, float number2) {
            return number1 != number2;
        }

        @Override
        public boolean matchNumber(double number1, double number2) {
            return number1 != number2;
        }
    }

    public static class NotEqualIgnoreCase extends Comparator {

        public static final NotEqualIgnoreCase INSTANCE = new NotEqualIgnoreCase();

        @Override
        public boolean matchString(String string1, String string2) {
            return !string1.equalsIgnoreCase(string2);
        }
    }

    public static class Contains extends Comparator {

        public static final Contains INSTANCE = new Contains();

        @Override
        public boolean matchString(String string1, String string2) {
            return string1.contains(string2);
        }
    }

    public static class StartsWith extends Comparator {

        public static final StartsWith INSTANCE = new StartsWith();

        @Override
        public boolean matchString(String string1, String string2) {
            return string1.startsWith(string2);
        }
    }

    public static class EndsWith extends Comparator {

        public static final EndsWith INSTANCE = new EndsWith();

        @Override
        public boolean matchString(String string1, String string2) {
            return string1.endsWith(string2);
        }
    }

    public static class Greater extends Comparator {

        public static final Greater INSTANCE = new Greater();

        @Override
        public boolean matchNumber(byte number1, byte number2) {
            return number1 > number2;
        }

        @Override
        public boolean matchNumber(short number1, short number2) {
            return number1 > number2;
        }

        @Override
        public boolean matchNumber(int number1, int number2) {
            return number1 > number2;
        }

        @Override
        public boolean matchNumber(long number1, long number2) {
            return number1 > number2;
        }

        @Override
        public boolean matchNumber(float number1, float number2) {
            return number1 > number2;
        }

        @Override
        public boolean matchNumber(double number1, double number2) {
            return number1 > number2;
        }
    }

    public static class GreaterOrEqual extends Comparator {

        public static final GreaterOrEqual INSTANCE = new GreaterOrEqual();

        @Override
        public boolean matchNumber(byte number1, byte number2) {
            return number1 >= number2;
        }

        @Override
        public boolean matchNumber(short number1, short number2) {
            return number1 >= number2;
        }

        @Override
        public boolean matchNumber(int number1, int number2) {
            return number1 >= number2;
        }

        @Override
        public boolean matchNumber(long number1, long number2) {
            return number1 >= number2;
        }

        @Override
        public boolean matchNumber(float number1, float number2) {
            return number1 >= number2;
        }

        @Override
        public boolean matchNumber(double number1, double number2) {
            return number1 >= number2;
        }
    }

    public static class Less extends Comparator {

        public static final Less INSTANCE = new Less();

        @Override
        public boolean matchNumber(byte number1, byte number2) {
            return number1 < number2;
        }

        @Override
        public boolean matchNumber(short number1, short number2) {
            return number1 < number2;
        }

        @Override
        public boolean matchNumber(int number1, int number2) {
            return number1 < number2;
        }

        @Override
        public boolean matchNumber(long number1, long number2) {
            return number1 < number2;
        }

        @Override
        public boolean matchNumber(float number1, float number2) {
            return number1 < number2;
        }

        @Override
        public boolean matchNumber(double number1, double number2) {
            return number1 < number2;
        }
    }

    public static class LessOrEqual extends Comparator {

        public static final LessOrEqual INSTANCE = new LessOrEqual();

        @Override
        public boolean matchNumber(byte number1, byte number2) {
            return number1 <= number2;
        }

        @Override
        public boolean matchNumber(short number1, short number2) {
            return number1 <= number2;
        }

        @Override
        public boolean matchNumber(int number1, int number2) {
            return number1 <= number2;
        }

        @Override
        public boolean matchNumber(long number1, long number2) {
            return number1 <= number2;
        }

        @Override
        public boolean matchNumber(float number1, float number2) {
            return number1 <= number2;
        }

        @Override
        public boolean matchNumber(double number1, double number2) {
            return number1 <= number2;
        }
    }

    public static class RegEx extends Comparator {

        public static final RegEx INSTANCE = new RegEx();

        @Override
        public boolean matchString(String string1, String string2) {
            return Utils.regexMatches(string2, string1);
        }
    }
}
