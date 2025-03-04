package com.example.tidemusic;

import org.springframework.boot.SpringApplication;

public class TestTideMusicApplication {

    public static void main(String[] args) {
        SpringApplication.from(TideMusicApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
