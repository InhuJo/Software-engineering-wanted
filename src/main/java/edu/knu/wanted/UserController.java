package edu.knu.wanted;


import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserRepository repository;

    UserController(UserRepository repository){
        this.repository = repository;
    }

    @GetMapping("/users")
    List<User> all(){

        return repository.findAll();
    }

    @DeleteMapping("/users")
    HashMap deleteUser(@RequestParam String uid)
    {
        List<User> userList = repository.findAll();
        Iterator<User> iter = userList.iterator();
        HashMap<String, String> status= new HashMap<String, String>();

        boolean flag = false;
        while (iter.hasNext())
        {
            User user = iter.next();
            if (user.getUid().equals(uid)) {
                try {
                    flag=true;
                    repository.delete(user);
                }
                catch(Exception e){
                    flag=false;
                    System.out.println("error : "+e.toString());
                }
                break;
            }
        }
        //기존 리스트에 유저가 없는 경우 처리
        if(flag)
            status.put("result","SUCCESS");
        else
            status.put("result","FAILED");
        return status;
    }

    @PutMapping("/users")
    HashMap newUser(@RequestParam String uid, @RequestParam String passwd)
    {
        User newUser = new User(uid, passwd);
        HashMap<String, String> status= new HashMap<String, String>();

        try {
            repository.save(newUser); //throw?
            status.put("result","SUCCESS");
        }
        catch(Exception e){
            System.out.println("error : "+e.toString());
            status.put("result","FAILED");
        }

        return status;
    }

    @GetMapping("/users/_count_")
    long userCount(){
        return repository.count();
    }
}