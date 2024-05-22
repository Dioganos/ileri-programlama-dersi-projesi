package chat4all;
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class ClientUser extends Thread 
{
    String userName = "";
    BufferedReader input;
    PrintWriter output;

    public ClientUser(Socket client) throws Exception // Her bir kullanıcının bilgilerini tutmak ve işlemlerini gerçekleştirmek için gerekli class.
    {
        input = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8)); //Kullanıcı girdisi.
        output = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true); //Kullanıcıya gidecek mesaj.

        while (true) 
        {
            String uname = input.readLine(); // kullanıcı girdisini oku.
            synchronized (server.getInstance().users) 
            {
                if (!server.getInstance().users.contains(uname)) // eğer sunucuda bu isim kullanılmıyorsa.
                {
                    server.getInstance().users.add(uname); // sunucudaki isim listesine ekle.
                    userName = uname;
                    output.println("İsim kabul edildi!"); //isim girme işlemi sonrası client'e döndürülecek mesaj.
                    server.getInstance().sendServerMessage(userName,userName + " odaya giriş yaptı."); // tüm clientlere kullanıcı girişini gönderen kod.
                    break;
                }
                output.println("Bu kullanıcı adı daha önce alınmış. Lütfen yeni bir tane seçin: "); // eğer aynı isim varsa kullanıcıya geri bildir.
            }
        }
        start();
    }
    public String getChatUsers() 
    {
        return userName;
    }

    public void sendMessage(String chatUser, String chatMsg) // mesajı diğer kullanıcılara göndermek için output'a yazdığımız metot.
    {
        output.println(chatUser + ": " + chatMsg);
    }
    public void sendServerMsg(String chatUser,String chatmsg) // Kullanıcı giriş çıkış gibi bilgileri yollamak için kullandığımız metot.
    {
        output.println(chatmsg);
    }

    @Override
    public void run() 
    {
        String line;
        try 
        {
            while (true) 
            {
                line = input.readLine(); //kullanıcı girdisini oku.
                if (line.equals("çıkış")) // eğer çıkış yazıldıysa.
                {
                    server.getInstance().clientList.remove(this); // sunucudan bu kullanıcıyı çıkart.
                    server.getInstance().sendServerMessage(userName,userName + " odadan ayrıldı."); // diğer tüm kullanıcılara bilgilendirme mesajı yolla.
                    break;
                }
                server.getInstance().sendFromToClients(userName, line); // kullanıcı mesajını diğer kullanıcılara yolla.
            }
        } 
        catch (Exception ex) 
        {
            System.out.println(ex.getMessage());
        } 
        finally 
        {
            synchronized (server.getInstance().users) 
            {
                server.getInstance().users.remove(userName); //kullanıcı oturumu kapanırsa sunucudan kullanıcıyı çıkart.
            }
        }
    }
}
