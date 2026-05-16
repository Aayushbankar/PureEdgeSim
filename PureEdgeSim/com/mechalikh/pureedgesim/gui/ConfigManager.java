package com.mechalikh.pureedgesim.gui;

import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import java.lang.reflect.Field;

public class ConfigManager {
    public static void updateParameter(String key, String value) {
        try {
            Field field = SimulationParameters.class.getField(key);
            field.setAccessible(true);
            
            Class<?> type = field.getType();
            if (type == int.class || type == Integer.class) {
                field.set(null, Integer.parseInt(value));
            } else if (type == double.class || type == Double.class) {
                field.set(null, Double.parseDouble(value));
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(null, Boolean.parseBoolean(value));
            } else if (type == String.class) {
                field.set(null, value);
            }
            System.out.println("Parameter Updated: " + key + " = " + value);
        } catch (Exception e) {
            System.err.println("Failed to update parameter " + key + ": " + e.getMessage());
        }
    }
}
