package com.rkb.springbootcamp.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hellowithpathvariable/{name}")
    public String helloWithPathVariable(@PathVariable(name = "name")  String name) {
        return "Hello World, "+name;
    }

    @GetMapping("/hellofromrequestparam")
    public String helloFromRequestParam(@RequestParam (name = "fName") String firstName,
                                        @RequestParam (name = "lName", required = false) String lastName) {
        return "Hello World, "+firstName;
    }
}
