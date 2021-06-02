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

    // 디비 연결
    ConnectionString connString = new ConnectionString(
            "mongodb+srv://inu:1123@cluster0.zedfu.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"
    );
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build();

    MongoClient client = MongoClients.create(settings);
    MongoDatabase database = client.getDatabase("software-engineering");

    // 전체 유저 출력 or 해당 유저 영화 평점 출력
    @GetMapping("/users")
    List<?> userRating(@RequestParam(value="uid", required = false) String uid){
        MongoCollection<Document> users = database.getCollection("users");
        ArrayList<Object> allUserList = new ArrayList<>();
        List<Document> userRatingList = new ArrayList<>();
        List<HashMap<String, String>> statusList = new ArrayList<>();
        HashMap<String, String> status = new HashMap<>();
        JSONParser jsonParser = new JSONParser();
        boolean flag = false;

        if(uid == null) {
            for(Document doc: users.find()) {
                try {
                    allUserList.add(jsonParser.parse(doc.toJson()));
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
            return allUserList;
        } else {
            Document doc = new Document("uid", uid);
            Document document = users.find(doc).first();

            if(document != null) {
                userRatingList = document.getList("info", Document.class);
                flag = true;
            }  else {
                status.put("result", "Cannot find user");
                statusList.add(status);
            }

            if(flag) {
                if(userRatingList == null) {
                    userRatingList = new ArrayList<>();
                }
                return userRatingList;
            } else {
                return statusList;
            }
        }
    }

    // 유저 수 출력
    @GetMapping("/users/_count_")
    long userCount(){
        MongoCollection<Document> users = database.getCollection("users");
        return users.countDocuments();
    }

    // 해당 유저가 매긴 영화 평점 출력
    @GetMapping("/users/{userid}/ratings")
    List<?> list_ratings2(@PathVariable("userid") String uid)
    {
        MongoCollection<Document> users = database.getCollection("users");
        List<Document> list = new ArrayList<>();
        List<HashMap<String, String>> list2 = new ArrayList<>();
        HashMap<String, String> status = new HashMap<>();
        boolean flag = false;

        Document doc = new Document("uid", uid);
        Document document = users.find(doc).first();

        if(document != null) {
            list = document.getList("info", Document.class);
            flag = true;
        } else {
            status.put("result", "Cannot find user");
            list2.add(status);
        }

        if(flag) {
            if(list == null) {
                list = new ArrayList<>();
            }
            return list;
        } else {
            return list2;
        }
    }

    // 유저 삭제
    // curl -X DELETE “http://localhost:8080/users?uid=username”
    @DeleteMapping("/users")
    @ResponseBody
    HashMap deleteUser(@RequestParam String uid)
    {
        MongoCollection<Document> users = database.getCollection("users");
        HashMap<String, String> status= new HashMap<String, String>();
        Document doc = new Document("uid", uid);
        boolean flag = false;

        Document _doc = users.find(doc).first();

        if(_doc != null) {
            try {
                users.findOneAndDelete(doc);
                flag = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //기존 리스트에 유저가 없는 경우 처리
        if(flag)
            status.put("result","SUCCESS");
        else
            status.put("result","FAILED");

        return status;
    }

    // 유저 삭제
    // curl -X DELETE “http://localhost:8080/users/username”
    @DeleteMapping("/users/{uid}")
    @ResponseBody
    HashMap deleteUser2(@PathVariable("uid") String uid)
    {
        MongoCollection<Document> users = database.getCollection("users");
        HashMap<String, String> status= new HashMap<String, String>();
        Document doc = new Document("uid", uid);
        boolean flag = false;

        Document _doc = users.find(doc).first();

        if(_doc != null) {
            try {
                users.findOneAndDelete(doc);
                flag = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //기존 리스트에 유저가 없는 경우 처리
        if(flag)
            status.put("result","SUCCESS");
        else
            status.put("result","FAILED");

        return status;
    }

    // 유저 추가
    @PutMapping("/users")
    @ResponseBody
    HashMap newUser2(@RequestParam String uid, @RequestParam String passwd)
    {
        HashMap<String, String> status= new HashMap<String, String>();
        MongoCollection<Document> users = database.getCollection("users");
        boolean flag = false;

        Document doc_ = new Document("uid", uid);
        doc_ = users.find(doc_).first();
        System.out.println(doc_);

        if(doc_ == null) {
            try {
                Document doc = new Document("uid",uid)
                        .append("passwd", passwd);
                users.insertOne(doc);
                flag = true;
            }
            catch(Exception e) {
                System.out.println("error : " + e.toString());
            }
        }

        if(flag) {
            status.put("result", "SUCCESS");
        } else {
            status.put("result","FAILED");
        }

        return status;
    }

    // 해당 유저의 영화 평점 입력
    @PutMapping("/users/{uid}/ratings")
    @ResponseBody
    HashMap findRating(@PathVariable("uid") String uid, @RequestParam String movie, @RequestParam String rating)
    {
        MongoCollection<Document> users = database.getCollection("users");
        Document doc_ = null;
        Document doc = new Document("uid", uid);

        BufferedReader br = null;
        ArrayList<String> tmpList = new ArrayList<String>();
        String array[];

        HashMap<String, String> status = new HashMap<>();
        boolean flag = false;
        boolean findMovie = false;

        // 해당 유저가 존재하는지 검사
        try {
            doc_ = users.find(doc).first();
        } catch (Exception e) {
            System.out.println(e);
        }

        // 영화아이디가 존재하는지 검사
        if(doc_ != null) {
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
             double r = Double.parseDouble(rating);

             System.out.println(r);

            if(r >= 1 && r <= 5) {
                // 해당 영화에 대한 평점이 있는지 검사, 있으면 그 부분 업데이트
                try {
                    List<Document> list = doc_.getList("info", Document.class);

                    if(list != null) {
                        for(Document d : list) {
                            if(d.get("movie").equals(movie)) {
                                Document update = new Document("$pull", new Document("info", new Document("movie", movie)));
                                users.updateOne(doc, update);
                            }
                        }
                    }

                    Document update = new Document("info", new Document("movie", movie).append("rating", rating));
                    Document updateQuery = new Document("$push", update);
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

}