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
    //In order to verify that the instructor is the instructor for the course which we are attempting to change the assignemtn name
    // the url entered requirest a request parameter of ?email=<email address of instructor>
    @PutMapping("/assignments/{id}")
    @Transactional
    public void updateAssignment (@RequestBody AssignmentListDTO.AssignmentDTO a1, @PathVariable("id") Integer assignmentId, @RequestParam String email ) {
                Course course = courseRepository.findById(a1.courseId).orElse(null);
                if ((course == null) || (!course.getInstructor().equals(email))){
                     throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid instructor -> course correlation. "+email +"!=" + course.getInstructor());
                }
                Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
                if (a == null) {
                    throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid grade primary key. "+assignmentId);
                }
                a.setName(a1.assignmentName);
                System.out.printf("%s\n", a.toString());
                assignmentRepository.save(a);
        }


    //  As an instructor for a course , I can add a new assignment for my course.  The assignment has a name and a due date.
    //In order to verify that the instructor is the instructor for the course which we are attempting to add an assignment for
    // the url entered request a request parameter of ?email=<email address of instructor>
    @PostMapping ("/addassignment")
    @Transactional
    public AssignmentListDTO.AssignmentDTO addAssignment (@RequestBody AssignmentListDTO.AssignmentDTO a, @RequestParam String email) {
        Course course = courseRepository.findById(a.courseId).orElse(null);

        if ((course == null) || (!course.getInstructor().equals(email))){
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid instructor -> course correlation. "+email +"!=" + course.getInstructor());
        }
        Assignment assignment = new Assignment();
        assignment.setName(a.assignmentName);
        assignment.setDueDate(java.sql.Date.valueOf(a.dueDate));
        assignment.setCourse(course);
      //  assignment.setCourse( courseRepository.findById(a.courseId).orElse(null));
        assignment.setNeedsGrading(1);
        assignmentRepository.save(assignment);
        return a;
    }

    //  As an instructor, I can delete an assignment  for my course (only if there are no grades for the assignment).
    @PostMapping ("/deleteassignment")
    @Transactional
    public void deleteAssignment (@RequestBody AssignmentListDTO.AssignmentDTO a) {
        //GET the entry to make sure it exists
        Assignment as = assignmentRepository.findById(a.assignmentId).orElse(null);
        //If it doesnt exist throw exception return 400
        if (as == null) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid assignment id: . "+a.assignmentId);
        }

        assignmentRepository.deleteById(as.getId());
    }

}//END OF CONTROLLER CLASS



