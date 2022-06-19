package com.swp.backend.utils;

import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Lazy(value = true)
@Getter
public class TimeMappingHelper {
    private final HashMap<Integer, String> timeMapping = new HashMap<>() {{
        put(0, "00:00");
        put(30, "00:30");
        put(60, "01:00");
        put(90, "01:30");
        put(120, "02:00");
        put(150, "02:30");
        put(180, "03:00");
        put(210, "03:30");
        put(240, "04:00");
        put(270, "04:30");
        put(300, "05:00");
        put(330, "05:30");
        put(360, "06:00");
        put(390, "06:30");
        put(420, "07:00");
        put(450, "07:30");
        put(480, "08:00");
        put(510, "08:30");
        put(540, "09:00");
        put(570, "09:30");
        put(600, "10:00");
        put(630, "10:30");
        put(660, "11:00");
        put(690, "11:30");
        put(720, "12:00");
        put(750, "12:30");
        put(780, "13:00");
        put(810, "13:30");
        put(840, "14:00");
        put(870, "14:30");
        put(900, "15:00");
        put(930, "15:30");
        put(960, "16:00");
        put(990, "16:30");
        put(1020, "17:00");
        put(1050, "17:30");
        put(1080, "18:00");
        put(1110, "18:30");
        put(1140, "19:00");
        put(1170, "19:30");
        put(1200, "20:00");
        put(1230, "20:30");
        put(1260, "21:00");
        put(1290, "21:30");
        put(1320, "22:00");
        put(1350, "22:30");
        put(1380, "23:00");
        put(1410, "23:30");
    }};
}
