package com.campus.services;

import com.campus.entity.Student;
import com.campus.enums.UserRoles;
import com.campus.model.LoginRequest;
import com.campus.model.RegisterStudentReq;
import com.campus.model.StudentResponse;
import com.campus.model.UserResponse;
import com.campus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public ResponseEntity<?> studentAuthentication(LoginRequest loginRequest) {
        Optional<Student> optionalStudent = studentRepository.findByEmail(loginRequest.getEmail());
        if(optionalStudent.isEmpty()) { //no student found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("student not found");
        }
        Student student = optionalStudent.get();
        if(!student.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid password"); //invalid password
        }
        return ResponseEntity.ok(new StudentResponse(student.getId(), student.getRegister_id(), student.getFullName(), student.getEmail(), student.getMobile(), student.getStreams(), student.getResume(), student.getImage()));
    }

    //create student
    public StudentResponse addStudent(RegisterStudentReq req) {
        Student student = new Student();
        student.setRegister_id(req.getRegister_id());
        student.setFullName(req.getFullName());
        student.setEmail(req.getEmail());
        student.setMobile(req.getMobile());
        student.setPassword(req.getPassword());
        student.setStreams(req.getStreams());
        student =  studentRepository.save(student);
        return new StudentResponse(student.getId(), student.getRegister_id(), student.getFullName(), student.getEmail(), student.getMobile(), student.getStreams(), student.getResume(), student.getImage());
    }

    public StudentResponse getStudentById(long id) {
        Student student =studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + id));
        return new StudentResponse(student.getId(), student.getRegister_id(), student.getFullName(), student.getEmail(), student.getMobile(), student.getStreams(), student.getResume(), student.getImage());
    }

    public List<StudentResponse> getAllStudents() {
        List<Student> student = studentRepository.findAll();
        return student.stream().map(s -> new StudentResponse(s.getId(), s.getRegister_id(), s.getFullName(), s.getEmail(), s.getMobile(), s.getStreams(), s.getResume(), s.getImage())).toList();
    }

    public StudentResponse updateStudent(long id, Student student) {
        Optional<Student> dbStudent = studentRepository.findById(id);
        Student existingStudent = getStudent(id, student, dbStudent);
        Student studentRes = studentRepository.save(existingStudent);
        return new StudentResponse(studentRes.getId(), studentRes.getRegister_id(), studentRes.getFullName(), studentRes.getEmail(), studentRes.getMobile(), studentRes.getStreams(), studentRes.getResume(), studentRes.getImage());
    }

    //method for updating student
    private static Student getStudent(long id, Student student, Optional<Student> dbStudent) {
        Student existingStudent = dbStudent.orElseThrow(() -> new NotFoundException("Student not found with id " + id));
        existingStudent.setFullName(student.getFullName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setMobile(student.getMobile());
        existingStudent.setPassword(student.getPassword());
        existingStudent.setStreams(student.getStreams());
        existingStudent.setImage(student.getImage());
        existingStudent.setResume(student.getResume());
        return existingStudent;
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }
}
