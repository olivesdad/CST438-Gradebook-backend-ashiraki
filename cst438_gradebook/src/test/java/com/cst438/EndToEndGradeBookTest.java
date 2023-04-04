package com.cst438;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

import static org.junit.jupiter.api.Assertions.*;

/*
 * This example shows how to use selenium testing using the web driver
 * with Chrome browser.
 *
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *
 *  In SpringBootTest environment, the test program may use Spring repositories to
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndGradeBookTest {

    public static final String CHROME_DRIVER_FILE_LOCATION = "/usr/bin/geckodriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    //$$$$$$$$$$$$$$$$$ THINGS TO FILL IN $$$$$$$$$$$$$$$$$$$$$$$$$$$$
    public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
    public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
    public static final String TEST_COURSE_ID = "999001";
    public static final String TEST_DUE_DATE = "2023-03-04";
    //$$$$$$$$$$$$$$$$$ THINGS TO FILL IN $$$$$$$$$$$$$$$$$$$$$$$$$$$$

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    AssignmentGradeRepository assignnmentGradeRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Test
    public void createAssignment() throws Exception {
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        // --> (gradebook service) as an Instructor I can create a new assignment for a course that I teach <--
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        // START THE THINGS///
        System.setProperty("webdriver.firefox.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new FirefoxDriver();
        // Puts an Implicit wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);


        try {

            // Get the course were going to use make sure its valid
            Course c = courseRepository.findById(Integer.parseInt(TEST_COURSE_ID)).orElse(null);
            assertNotNull(c);

            //Make sure the course that we are inserting doesnt already exist
            assertNull(assignmentRepository.findByNameAndCourseId(TEST_ASSIGNMENT_NAME, c).orElse(null));
            //click button to go to add assignment page
            WebElement button = driver.findElement(By.id("add"));
            button.click();
            Thread.sleep(SLEEP_DURATION);

            //Enter the info
            driver.findElement(By.name("assignmentName")).sendKeys(TEST_ASSIGNMENT_NAME);
            driver.findElement(By.name("instructorEmail")).sendKeys(TEST_INSTRUCTOR_EMAIL);
            driver.findElement(By.name("courseId")).sendKeys(TEST_COURSE_ID);
            driver.findElement(By.name("dueDate")).sendKeys(TEST_DUE_DATE);

            //push the button
            driver.findElement(By.id("Submit")).click();
            Thread.sleep(SLEEP_DURATION);

            //check if it's there

            Assignment a = assignmentRepository.findByNameAndCourseId(TEST_ASSIGNMENT_NAME, c).orElse(null);
            //check assignment isnt null
            assertNotNull(a);
            //check values
            assertEquals(a.getName(), TEST_ASSIGNMENT_NAME);

            //delete the entry
            assignmentRepository.delete(a);
            //make sure its gone
            assertNull(assignmentRepository.findByNameAndCourseId(TEST_ASSIGNMENT_NAME, c).orElse(null));


        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.close();
	    driver.quit();

        }

    }
}
