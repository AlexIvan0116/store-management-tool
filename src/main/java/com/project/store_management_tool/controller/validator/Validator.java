package com.project.store_management_tool.controller.validator;

public class Validator {
    public static boolean UUIDValidator(String id) {
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        return id.matches(regex);
    }

    public static boolean priceValidator(String price) {
        String regex = "(-?\\d+(\\.\\d*)?)|(-?\\.\\d+)";
        return price.matches(regex);
    }
}
