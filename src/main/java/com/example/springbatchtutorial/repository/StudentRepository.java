package com.example.springbatchtutorial.repository;

import com.example.springbatchtutorial.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}