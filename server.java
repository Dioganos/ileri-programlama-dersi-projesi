package chat4all;
import java.io.*;
import chat4all.ClientUser;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class server 
{
    public static server sv = null; // kendi türünden bir değişken açıyoruz.
    List<ClientUser> clientList = new ArrayList<>();
    Set<String> users = new HashSet<>();
    
    private server()
    {
        System.out.print("\033[H\033[2J"); // server açıldığında konsoldaki önceki tüm yazıları sil.
        System.out.flush();
        sv = this; // nesne oluşturulduğunda public sv değişkeni de kendisine atanıyor.
    }
    
    public static synchronized server getInstance() // bu kod ile farklı class'lardan server kodlarımıza ulaşabiliyoruz.
    {
        if(sv == null)
            sv = new server();
        return sv;
    }
    
    public static void main(String... args) 
    { 
        try 
        {
            server sv = new server(); // sunucu nesnemizi oluşturuyoruz.
            sv.startServer(); // sunucuyu başlatıyoruz.
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    public void startServer() throws Exception 
    {
        ServerSocket server = new ServerSocket(80, 10); // SocketServer kullanarak yeni bir bağlantı açıyor ve gerekli bilgilerimizi giriyoruz.
        System.out.println("Sunucu aktif!");
        while (true) 
        {
            Socket client = server.accept(); // Sunucu açık olduğu sürece yeni client bağlantılarını kabul etmek için.
            ClientUser manageUser = new ClientUser(client);
            clientList.add(manageUser);
        }
    }

    public void sendFromToClients(String user, String message) // Clientlerden gönderilen mesajları diğer clientlere göndermek için.
    {
        for (ClientUser client : clientList) 
        {
            if (!client.getChatUsers().equals(user)) 
            {
                client.sendMessage(user, message);
            }
        }
    }
    
    public void sendServerMessage(String user, String message)  // Sunucudan bağlantı durumu gibi bilgileri tüm clientlere göndermek için.
    {
        for (ClientUser client : clientList) 
        {
            if (!client.getChatUsers().equals(user))
            {
                client.sendServerMsg(user, message);
            }
        }
    }
}