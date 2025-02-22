package com.wikicoding.maintenance;

import com.wikicoding.maintenance.persistence.datamodel.RoleEnum;
import com.wikicoding.maintenance.persistence.datamodel.User;
import com.wikicoding.maintenance.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MaintenanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaintenanceApplication.class, args);
	}
}
