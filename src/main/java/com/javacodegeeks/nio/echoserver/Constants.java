package com.javacodegeeks.nio.echoserver;

import java.util.Date;

public final class Constants {

    public static final String END_MESSAGE_MARKER = ":END";

    private Constants() {
        throw new IllegalStateException("Instantiation not allowed.");
    }

    public static final String TEXT_FIRST_SEGMENT = "Время создания пакета: " + new Date() + System.lineSeparator() +
            "Координаты: " + "широта 65 градусов северной широты, долгота 35 градусов восточной долготы" + System.lineSeparator() +
            "Высота:" + "10 км" + System.lineSeparator() +
            "Направление: " + "юго-западное" + System.lineSeparator() +
            "Количество видимых спутников: " + "04" + System.lineSeparator() +
            "Скорость в км/ч: " + "600 км/ч" + System.lineSeparator() +
            "Состояние дискретных (цифровых) входов: " + "включены" + System.lineSeparator() +
            "Состояние аналоговых входов: " + "включены" + System.lineSeparator() +
            "Уровень сигнала GSM: " + "высокий";
}

