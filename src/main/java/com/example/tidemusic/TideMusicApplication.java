package com.example.tidemusic;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TideMusicApplication {

    public static void main(String[] args) {
        //.env 파일 불러오기
        Dotenv dotenv = Dotenv.load();
        //시스템 속성으로 전달
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(TideMusicApplication.class, args);
    }

}
