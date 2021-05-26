package edu.knu.wanted;


import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.sun.istack.Nullable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.awt.SystemColor.info;

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
    MongoDatabase database = client.getDatabase("software-engineering");

    @GetMapping("/users")
    List<?> userRating(@RequestParam(value="uid", required = false) String uid){
        // 전체 유저 출력
        if(uid == null) {
            MongoCollection<Document> users = database.getCollection("users");
            ArrayList<Object> list = new ArrayList<>();

            JSONParser jsonParser = new JSONParser();

            for(Document doc: users.find()) {
                try {
                    list.add(jsonParser.parse(doc.toJson()));
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
            return list;
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

    // 유저 수 출력
    @GetMapping("/users/_count_")
    long userCount(){
        MongoCollection<Document> users = database.getCollection("users");
        return users.countDocuments();
    }

    // 해당 유저 영화 평점 입력
    @GetMapping("/users/{userid}/ratings")
    ArrayList<String> list_ratings(@PathVariable("userid") String uid)
    {
        MongoCollection<Document> users = database.getCollection("users");
        MongoCursor<Document> it = null;
        BufferedReader br = null;
        ArrayList<String> tmpList = new ArrayList<String>();
        String array[];

        try {
            Document doc = new Document("uid", uid);
            it = users.find(doc).iterator();
        } catch (Exception e) {
            System.out.println(e);
        }

        if(it != null) {
            try {
                br = Files.newBufferedReader(Paths.get("./ml-latest-small/ratings.csv"));
                String line = "";
                while((line = br.readLine()) != null) {
                    array=line.split(",");

                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

//        try{
//            br = Files.newBufferedReader(Paths.get("./ml-latest-small/ratings.csv"));
//            String line = "";
//            while((line = br.readLine()) != null) {
//
//
//            }
//
//        } catch(Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if(br != null) {
//                    br.close();
//                }
//            }catch(Exception e) {
//                e.printStackTrace();
//            }
//        }

        return tmpList;
    }

    // 해당 유저 영화 평점 입력
    @PutMapping("/users/{uid}/ratings")
    @ResponseBody
    HashMap findRating(@PathVariable("uid") String uid, @RequestParam String movie, @RequestParam String rating)
    {
        MongoCollection<Document> users = database.getCollection("users");
        MongoCursor<Document> it = null;
        Document doc = new Document("uid", uid);

        BufferedReader br = null;
        ArrayList<String> tmpList = new ArrayList<String>();
        String array[];

        HashMap<String, String> status = new HashMap<>();
        boolean flag = false;
        boolean findMovie = false;

        // 해당 유저가 존재하는지 검사
        try {
            it = users.find(doc).iterator();
        } catch (Exception e) {
            System.out.println(e);
        }

        // 영화아이디가 존재하는지 검사
        if(it != null) {
            try {
                br = Files.newBufferedReader(Paths.get("./ml-latest-small/ratings.csv"));
                String line = "";
                while((line = br.readLine()) != null) {
                    array=line.split(",");

                    if(array[0].equals(movie)) {
                        findMovie = true;
                        break;
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        // 올바른 평점인지 검사 후 업데이트
        if(findMovie){
            int r = Integer.parseInt(rating);

            if(r >= 1 && r <= 5) {
                // 해당 영화에 대한 평점이 있는지 검사
                try {
//                    Document document = users.find().first();
//                    List<Document> list = document.getList("info", Document.class);
//
//                    for(Document d : list) {
//                        if(d.get("movie").equals(movie)) {
//                            users.updateOne(d, new Document("$set", new Document("rating", rating)));
//                        }
//                    }

                    Document doc_ = new Document("info", new Document("movie", movie).append("rating", rating));
                    Document updateQuery = new Document("$push", doc_);
                    users.updateOne(doc, updateQuery);
                    flag = true;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        if(flag)
            status.put("result","SUCCESS");
        else
            status.put("result","FAILED");

        return status;

    }

    // 유저 삭제
    @DeleteMapping("/users/{uid}")
    @ResponseBody
    HashMap deleteUser2(@PathVariable("uid") String uid)
    {
        MongoCollection<Document> users = database.getCollection("users");
        HashMap<String, String> status= new HashMap<String, String>();
        boolean flag = false;

        try {
            Document doc = new Document("uid", uid);
            users.findOneAndDelete(doc);
            flag = true;
        } catch(Exception e) {
            System.out.println(e);
        }

        //기존 리스트에 유저가 없는 경우 처리
        if(flag)
            status.put("result","SUCCESS");
        else
            status.put("result","FAILED");

        return status;
    }

//    @DeleteMapping("/users")
//    HashMap deleteUser(@RequestParam String uid)
//    {
//        List<User> userList = repository.findAll();
//        Iterator<User> iter = userList.iterator();
//        HashMap<String, String> status= new HashMap<String, String>();
//
//        boolean flag = false;
//        while (iter.hasNext())
//        {
//            User user = iter.next();
//            if (user.getUid().equals(uid)) {
//                try {
//                    flag=true;
//                    repository.delete(user);
//                }
//                catch(Exception e){
//                    flag=false;
//                    System.out.println("error : "+e.toString());
//                }
//                break;
//            }
//        }
//        //기존 리스트에 유저가 없는 경우 처리
//        if(flag)
//            status.put("result","SUCCESS");
//        else
//            status.put("result","FAILED");
//        return status;
//    }

    // 유저 추가
    @PutMapping("/{uid}")
    @ResponseBody
    HashMap newUser2(@PathVariable("uid") String uid, @RequestParam String passwd)
    {
        HashMap<String, String> status= new HashMap<String, String>();
        MongoCollection<Document> users = database.getCollection("users");
        boolean flag = false;

        try {
            Document doc = new Document("uid",uid)
                    .append("passwd", passwd)
                    .append("movie", "")
                    .append("rating", "");
            users.insertOne(doc);
            flag = true;
        }
        catch(Exception e){
            System.out.println("error : "+e.toString());
        }

        if(flag) {
            status.put("result", "SUCCESS");
        } else {
            status.put("result","FAILED");
        }

        return status;
    }

//    @PutMapping("/users")
//    HashMap newUser(@RequestParam String uid, @RequestParam String passwd)
//    {
//        User newUser = new User(uid, passwd);
//        HashMap<String, String> status= new HashMap<String, String>();
//
//        try {
//            repository.save(newUser); //throw?
//            status.put("result","SUCCESS");
//        }
//        catch(Exception e){
//            System.out.println("error : "+e.toString());
//            status.put("result","FAILED");
//        }
//
//        return status;
//    }
}