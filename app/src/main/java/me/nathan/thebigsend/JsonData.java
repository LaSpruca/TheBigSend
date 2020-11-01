package me.nathan.thebigsend;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class JsonData {
    @Expose()
    public ArrayList<NumberList> numbersLists;
    public JsonData() {

    }
}
