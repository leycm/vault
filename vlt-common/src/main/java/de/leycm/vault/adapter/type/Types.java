/**
 * LECP-LICENSE NOTICE
 * <br><br>
 * This Sourcecode is under the LECP-LICENSE. <br>
 * License at: <a href="https://github.com/leycm/leycm/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) LeyCM <leycm@proton.me> <br>
 * Copyright (c) maintainers <br>
 * Copyright (c) contributors
 */
package de.leycm.vault.adapter.type;

import de.leycm.vault.Config;
import de.leycm.vault.adapter.TypeAdapter;

public interface Types {


    class IntegerAdapter implements TypeAdapter<Integer> {
        @Override
        public Integer fromObject(Config root, String path, Object raw) {
            if (raw instanceof Number) return ((Number) raw).intValue();
            if (raw instanceof String) {
                try { return Integer.parseInt((String) raw); }
                catch (NumberFormatException e) { return null; }
            }
            return null;
        }

        @Override
        public Object toObject(Config root, String path, Integer value) {
            return value;
        }
    }

    class LongAdapter implements TypeAdapter<Long> {
        @Override
        public Long fromObject(Config root, String path, Object raw) {
            if (raw instanceof Number) return ((Number) raw).longValue();
            if (raw instanceof String) {
                try { return Long.parseLong((String) raw); }
                catch (NumberFormatException e) { return null; }
            }
            return null;
        }

        @Override
        public Object toObject(Config root, String path, Long value) {
            return value;
        }
    }

    class DoubleAdapter implements TypeAdapter<Double> {
        @Override
        public Double fromObject(Config root, String path, Object raw) {
            if (raw instanceof Number) return ((Number) raw).doubleValue();
            if (raw instanceof String) {
                try { return Double.parseDouble((String) raw); }
                catch (NumberFormatException e) { return null; }
            }
            return null;
        }

        @Override
        public Object toObject(Config root, String path, Double value) {
            return value;
        }
    }

    class FloatAdapter implements TypeAdapter<Float> {
        @Override
        public Float fromObject(Config root, String path, Object raw) {
            if (raw instanceof Number) return ((Number) raw).floatValue();
            if (raw instanceof String) {
                try { return Float.parseFloat((String) raw); }
                catch (NumberFormatException e) { return null; }
            }
            return null;
        }

        @Override
        public Object toObject(Config root, String path, Float value) {
            return value;
        }
    }

    class BooleanAdapter implements TypeAdapter<Boolean> {
        @Override
        public Boolean fromObject(Config root, String path, Object raw) {
            if (raw instanceof Boolean) return (Boolean) raw;
            if (raw instanceof String) return Boolean.parseBoolean((String) raw);
            if (raw instanceof Number) return ((Number) raw).intValue() != 0;
            return null;
        }

        @Override
        public Object toObject(Config root, String path, Boolean value) {
            return value;
        }
    }

    class StringAdapter implements TypeAdapter<String> {
        @Override
        public String fromObject(Config root, String path, Object raw) {
            return raw == null ? null : raw.toString();
        }

        @Override
        public Object toObject(Config root, String path, String value) {
            return value;
        }
    }
}
