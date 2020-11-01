package me.nathan.thebigsend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NumberList implements Serializable {
    public String name;
    public List<Number> numbers;

    public NumberList(String name, List<Number> numbers) {
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }
    public List<Number> getNumbers() {
        return numbers;
    }

    @Override
    public String toString() {
        return "NumberList{" +
                "name='" + name + '\'' +
                ", numbers=" + numbers +
                '}';
    }
}
