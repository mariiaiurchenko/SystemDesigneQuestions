package com.spring.tutorial.systemDesignQuestions;

import com.spring.tutorial.systemDesignQuestions.cache.DataContainer;

public class App 
{
    public static void main( String[] args )
    {
        DataContainer<Integer, String> cache = new DataContainer<>(5);
        cache.insert(1, "String 1");
        cache.insert(2, "String 2");
        cache.insert(3, "String 3");
        cache.insert(4, "String 4");
        cache.insert(5, "String 5");
        cache.insert(6, "String 6");
    }
}
