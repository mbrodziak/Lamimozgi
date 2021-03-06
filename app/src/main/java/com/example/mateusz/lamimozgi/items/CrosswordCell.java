package com.example.mateusz.lamimozgi.items;

import android.support.annotation.NonNull;

public class CrosswordCell implements Cell{
    private String solution;
    private String value;
    private boolean isEven;
    private boolean isSelected;
    private boolean isHighlighted;

    public CrosswordCell(String value, String solution, boolean isEven) {
        this.value = value;
        this.solution = solution;
        this.isEven = isEven;
        this.isHighlighted = false;
        this.isSelected = false;
    }

    public boolean check(){
        if (isEven){
            return value.equals(solution);
        }else{
            return true;
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (String) value;
    }

    @Override
    public boolean isEven() {
        return isEven;
    }

    @Override
    public boolean isHighlighted() {
        return isHighlighted;
    }

    @Override
    public void setHighlighted(boolean highlight) {
        this.isHighlighted = highlight;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        if (value.equals(" ")) {
            return "";
        } else {
            return value;
        }
    }
}
