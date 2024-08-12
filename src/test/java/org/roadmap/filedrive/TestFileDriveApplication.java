package org.roadmap.filedrive;

import org.springframework.boot.SpringApplication;

public class TestFileDriveApplication {

    public static void main(String[] args) {
        SpringApplication.from(FileDriveApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
