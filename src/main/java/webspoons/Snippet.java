package webspoons;

import java.util.Date;
    
public class Snippet {
    public String name;
    public Date expires_at;
    public String snippet;
    public String url;
    public String password;

    public Snippet(String name, int expiry, String content,String url) {
         this.name = name;
         Date now = new Date();
         this.expires_at = new Date(now.getTime() + (expiry * 1000));
         this.snippet = content;
         this.url = url;
    }

    public boolean hasExpired(){
      Date now = new Date();
      return (now.getTime() > expires_at.getTime());
    }
 }