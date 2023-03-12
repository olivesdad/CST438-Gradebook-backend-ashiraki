package com.cst438.controllers;

import com.cst438.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
public class InstructorController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    AssignmentGradeRepository assignmentGradeRepository;

    @Autowired
    CourseRepository courseRepository;


    // As an instructor, I can change the name of the assignment for my course.
    @PutMapping("/assignments/{id}")
    @Transactional
    public void updateAssignment (@RequestBody AssignmentListDTO.AssignmentDTO a1, @PathVariable("id") Integer assignmentId ) {

                Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
                if (a == null) {
                    throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid grade primary key. "+assignmentId);
                }
                a.setName(a1.assignmentName);
                System.out.printf("%s\n", a.toString());
                assignmentRepository.save(a);
        }

    //  As an instructor, I can delete an assignment  for my course (only if there are no grades for the assignment).
    //  As an instructor for a course , I can add a new assignment for my course.  The assignment has a name and a due date.
    @PostMapping ("/addassignment")
    @Transactional
    public void addAssignment (@RequestBody AssignmentListDTO.AssignmentDTO a) {

        Assignment assignment = new Assignment();
        assignment.setName(a.assignmentName);
        assignment.setDueDate(java.sql.Date.valueOf(a.dueDate));
        assignment.setCourse( courseRepository.findById(a.courseId).orElse(null));
        assignment.setNeedsGrading(1);
        assignmentRepository.save(assignment);
    }
    }
