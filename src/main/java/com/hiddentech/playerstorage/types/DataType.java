package com.hiddentech.playerstorage.types;

import com.hiddentech.playerstorage.PlayerStorage;

import java.util.HashMap;
import java.util.Map;

public enum DataType {
    STRING {
        @Override
        public Map<String, ?> deSerialize(Map<String, String> strings) {
            Map<String, String> sortedStrings = new HashMap<>();
            for (String key : strings.keySet()) {
                if (PlayerStorage.getPlugin().getRegistry().getTypes().get(key) == null)continue;
                if (PlayerStorage.getPlugin().getRegistry().getTypes().get(key).equals(DataType.STRING)) {
                    sortedStrings.put(key, strings.get(key));
                }
            }
            return sortedStrings;
        }
    },
    BOOLEAN {
        @Override
        public Map<String, ?> deSerialize(Map<String, String> strings) {
            Map<String, Boolean> sortedBooleans = new HashMap<>();
            for (String key : strings.keySet()) {
                if (PlayerStorage.getPlugin().getRegistry().getTypes().get(key) == null)continue;
                if (PlayerStorage.getPlugin().getRegistry().getTypes().get(key).equals(DataType.BOOLEAN)) {
                    sortedBooleans.put(key, Boolean.valueOf(strings.get(key)));
                }
            }
            return sortedBooleans;
        }
    },
    INTEGER {
        @Override
        public Map<String, ?> deSerialize(Map<String, String> strings) {
            Map<String, Integer> sortedIntegers = new HashMap<>();
            for (String key : strings.keySet()) {
                if (PlayerStorage.getPlugin().getRegistry().getTypes().get(key) == null)continue;
                if (PlayerStorage.getPlugin().getRegistry().getTypes().get(key).equals(DataType.INTEGER)) {
                    sortedIntegers.put(key, Integer.valueOf(strings.get(key)));
                }
            }
            return sortedIntegers;
        }
    };


    public abstract Map<String, ?> deSerialize(Map<String, String> strings);
}
