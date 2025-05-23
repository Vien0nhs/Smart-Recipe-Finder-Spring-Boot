package com.vien.smart_recipe_finder.model;

import lombok.Getter;

@Getter
public enum RecipeType {
    CANH("Canh"),
    KHO("Kho"),
    XAO("Xào"),
    CHIEN_LUOC("Chiên/Luộc"),
    NUONG_HAP("Nướng/Hấp");

    private final String displayName;

    RecipeType(String displayName) {
        this.displayName = displayName;
    }

}