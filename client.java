package chat4all;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class client 
{
    String username;
    PrintWriter pw;
    BufferedReader br;
    Socket chatUsers;

    public client(String serverIP) throws Exception 
    {
        chatUsers = new Socket(serverIP, 80); // Client'i sunucuya bağlayan kısım burası.
        br = new BufferedReader(new InputStreamReader(chatUsers.getInputStream(), StandardCharsets.UTF_8)); // kullanıcı girdisi.
        pw = new PrintWriter(new OutputStreamWriter(chatUsers.getOutputStream(), StandardCharsets.UTF_8), true); // sunucuya gönderilecek mesaj.
        acceptUsername(); // geçerli bir kullanıcı adı girildiğinin kontrolünü yapan kısım.
        new MessageTraffic().start();
    }

    public void acceptUsername() // Kullanıcı adı alma ve kontrol etme işlemleri!
    {
        while (true) 
        {
            Scanner scanner = new Scanner(System.in);  // kullanıcıdan girdi alacağımız için scanner açtığımız kısım.
            System.out.println("Eşsiz bir kullanıcı adı giriniz: ");
            String uname = scanner.nextLine(); // kullanıcının girdiği veriyi "uname" adlı değişkene atıyoruz.
            
            pw.println(uname); // sunucuya göndereceğimiz mesaja bu kullanıcı adını yazıyoruz.
            String response;
            try 
            {
                response = br.readLine(); // sunucudan geriye dönen mesajı response adlı string değişkene atıyoruz.
                if (response.equals("İsim kabul edildi!")) //sunucudan geri dönen bilgi eğer "İsim kabul edildi!" ise!
                {
                    this.username = uname; // kullanıcı adını başarıyla bu istemcinin kullanıcı adı bilgisine yazıyoruz.
                    System.out.println("İsim kabul edildi!"); // kullanıcıya isim girişinin başarılı olduğunu geri bildiriyoruz.
                    break;
                }
                System.out.println(response); // sunucudan gelen mesajı ekrana yazdırıyoruz (eğer isim girişi başarısız olursa buraya gelebiliyor).
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String ... args) 
    {
        System.out.print("\033[H\033[2J"); // client açıldığında konsoldaki önceki tüm yazıları sil.
        System.out.flush();
        Scanner scanner = new Scanner(System.in); // Sunucuyu buldurmamız gerek.
        System.out.print("Sunucu adresini girin: ");
        String serverIP = scanner.nextLine(); // kullanıcıdan bir sunucu adresi istediğimiz kısım.

        try 
        {
            client client = new client(serverIP); // girilen sunucu adresi ile bağlantıyı açıyoruz. -> daha sonra kullanıcı adı tanımlıyor bkz. 19. satır.
            System.out.println("Çıkış yapmak için \"çıkış\" yazabilirsiniz!"); // kullanıcıya çıkış yapabilmesi için gerekli bilgiyi veriyoruz.

            while (true) 
            {
                String input = scanner.nextLine().trim(); // Client açık olduğu sürece kullanıcı girdisini alıyoruz.
                if (!input.isEmpty() && !input.isBlank()) // Eğer mesaj boş ya da tamamen boşluk karakterinden oluşmuyorsa
                {
                    System.out.print("\033[2K"); // Satırı temizle
                    System.out.print("\033[1F"); // Satırın başına git
                    client.pw.println(input); // kullanıcı girdisini geri yazıyoruz.
                    System.out.println("Sen : " + input); // kullanıcıya kendi mesajını göster.
                } 
                else 
                {
                    System.out.println("Böyle bir mesaj gönderemezsiniz."); // Yanlış bir mesaj formatı girilirse.
                }
                if (input.equals("çıkış"))  // çıkış yazılmışsa
                {
                    main(args); // çalışmayı durdur.
                    break;
                }
            }
        } 
        catch(Exception ex) 
        {
            ex.printStackTrace();
        }
    }

    class MessageTraffic extends Thread 
    {
        @Override
        public void run() 
        {
            String line;
            try 
            {
                while (true) 
                {
                    line = br.readLine(); //kullanıcı girdisini oku.
                    System.out.println(line); //kullanıcı girdisini yaz.
                }
            } 
            catch(Exception ex) 
            {
                ex.printStackTrace();
            }
        }
    }
}
