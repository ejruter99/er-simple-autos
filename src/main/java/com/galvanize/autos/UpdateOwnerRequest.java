package com.galvanize.autos;

public class UpdateOwnerRequest {

    private String Owner;
    private String Color;

//    public UpdateOwnerRequest(String owner, String color) {
//        Owner = owner;
//        Color = color;
//    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

}
