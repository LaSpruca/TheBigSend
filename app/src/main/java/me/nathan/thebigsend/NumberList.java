package me.nathan.thebigsend;

import java.util.ArrayList;
import java.util.List;

public class NumberList {
    public String name;
    public List<Number> numbers;

    public NumberList(String name, List<Number> numbers) {
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NumberList{" +
                "name='" + name + '\'' +
                ", numbers=" + numbers +
                '}';
    }
}
