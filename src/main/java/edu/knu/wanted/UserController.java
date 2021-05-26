package edu.knu.wanted;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.sun.istack.Nullable;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class UserController {
    private final UserRepository repository;

    UserController(UserRepository repository){
        this.repository = repository;
    }

    ConnectionString connString = new ConnectionString(
            "mongodb+srv://inu:1123@cluster0.zedfu.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"
    );
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build();

    MongoClient client = MongoClients.create(settings);

    @GetMapping("/users")
    List<?> userRating(@RequestParam(value="uid", required = false) String uid){

        if(uid == null) {
            return repository.findAll();
        }

        BufferedReader br = null;
        ArrayList<String> tmpList = new ArrayList<String>();
        String array[];

        try{

            br = Files.newBufferedReader(Paths.get("./ml-latest-small/ratings.csv"));
            String line = "";
            while((line = br.readLine()) != null) {

                array = line.split(",");

                if (array[0].equals(uid)) {

                    tmpList.add(array[1]);

                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        return tmpList;
    }

    @GetMapping("/users/{userid}/ratings")
    ArrayList<String> list_ratings(@PathVariable("userid") String uid)
    {
        BufferedReader br = null;
        ArrayList<String> tmpList = new ArrayList<String>();
        String array[];

        try{

            br = Files.newBufferedReader(Paths.get("./ml-latest-small/ratings.csv"));
            String line = "";
            while((line = br.readLine()) != null) {

                array = line.split(",");

                if (array[0].equals(uid)) {

                    tmpList.add(array[2]);

                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        return tmpList;
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

    @DeleteMapping("/users/{uid}")
    @ResponseBody
    HashMap deleteUser2(@PathVariable("uid") String uid)
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

    @PutMapping("/{uid}")
    @ResponseBody
    HashMap newUser2(@PathVariable("uid") String uid, @RequestParam String passwd)
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

    @PutMapping("/users/{uid}/ratings")
    @ResponseBody
    HashMap findRating(@PathVariable("uid") String uid, @RequestParam String movie, @RequestParam String rating)
    {
        List<User> userlist = repository.findAll();
        Iterator<User> iter = userlist.iterator();
        HashMap<String, String> status= new HashMap<String, String>();

        BufferedReader br = null;
        ArrayList<String> tmpList = new ArrayList<String>();
        String array[];

        boolean flag = false;

        while (iter.hasNext())
        {
            User user = iter.next();
            if (user.getUid().equals(uid)) {
                try {
                    System.out.println("file");
                    br = Files.newBufferedReader(Paths.get("./ml-latest-small/movies.csv"));
                    String line = "";
                    while((line = br.readLine()) != null) {

                        array = line.split(",");

                        if (array[0].equals(movie)) {
                            System.out.println(array[0]);
                            int r = Integer.getInteger(rating);

                            if(r >= 1 && r <= 5) {
                                //user.setMovieId(movie);
                                //user.setRating(rating);
                                flag = true;
                                break;
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(br != null) {
                            br.close();
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if(flag == false)
                break;
        }

        if(flag == false) {
            status.put("result","FAILED");
        } else if (flag == true) {
            status.put("result","SUCCESS");
        }

        return status;
    }

    @GetMapping("/users/_count_")
    long userCount(){
        return repository.count();
    }
}