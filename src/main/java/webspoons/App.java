package webspoons;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class App {

    static ConcurrentHashMap<String, Snippet> snippets = new ConcurrentHashMap<>(); 
    static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    public static void main(String[] args) {

        get("/", (req, res) -> {
            res.type("application/json");
            res.status(200);
            return gson.toJson(findAll());
        });

        post("/snippets", (req, res) -> {
            HashMap<String,Object> data = gson.fromJson(req.body(), HashMap.class);
            String path = req.scheme()+"://"+req.host();
            Snippet snippet = add(data, path);
            res.type("application/json");
            res.status(201);
            return gson.toJson(snippet);
        });

        put("/snippets/:name", (req, res) -> {
          String name = req.params("name");
            HashMap<String,Object> data = gson.fromJson(req.body(), HashMap.class);
            int updated = update(name,data);
          
               res.type("application/json");
               res.status(updated);
           
            return gson.toJson(data);
        });

        get("/snippets/:name", (req, res) -> {
            String name = req.params("name");
            Snippet snippet = find(name);
            if(snippet == null){
                res.status(404);
                return null;
            }
            res.type("application/json");
            res.status(200);
            return gson.toJson(snippet);
        });
    }

    private static Snippet add(HashMap<String,Object> data, String path) {  
        String name = (String)data.get("name");
        String content = (String)data.get("snippet");
        System.out.println(data);
        int expiry = (int)((double)data.get("expires_in"));
        String url = path+"/snippets/"+name;
        
        Snippet snippet = new Snippet(name, expiry, content,url);
        snippet.password = (String)data.get("password");
        snippets.put(name, snippet);
        return snippet;
    }

  private static Snippet find(String name) {
        Snippet s = snippets.get(name);
        if(s == null) return null;
        if(s.hasExpired()) {
          snippets.remove(name);
          return null;
        }
        s.expires_at = new Date(s.expires_at.getTime() + (30 * 1000));
        snippets.put(name, s);
        return s;
    }

    private static ArrayList<Snippet> findAll() {
        ArrayList<Snippet> records = new ArrayList<>();
        Iterator<ConcurrentHashMap.Entry<String, Snippet>>  it;
        it = snippets.entrySet().iterator(); 
        while (it.hasNext()) { 
            ConcurrentHashMap.Entry<String, Snippet> entry;
            entry = it.next(); 
           if(!entry.getValue().hasExpired()){
                records.add(entry.getValue());
            }
           ;
        } 
        return records;
    }

    private static int update(String name ,HashMap<String,Object> data) {
       
        String content = (String)data.get("snippet");
        Snippet snippet = find(name);
        if(snippet == null) return 404;
        if(data.get("password") == null) return 401;
        if(!data.get("password").equals(snippet.password)) return 401;
       if(content != null) {
          snippet.snippet = content;
          snippet.expires_at = new Date(snippet.expires_at.getTime() + (30 * 1000));
          snippets.put(name, snippet);
        }
        return 200;
    }
}