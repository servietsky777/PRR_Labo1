/**
 * Projet : Labo 1 PRR
 * Auteur : Antoine Drabble & Simon Baehler
 * Date : 22.09.2016
 * Description : Master
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        long decalage = 0;
        byte[] longTampon = new byte[8];

        MulticastSocket multicastSocket = new MulticastSocket(4445);
        InetAddress groupe = InetAddress.getByName("228.5.6.7");
        multicastSocket.joinGroup(groupe);

        DatagramSocket pointToPointSocket = new DatagramSocket();

        int cnt = 0;
        while (cnt < 1000) {
            // Retrieve master current time
            DatagramPacket paquet = new DatagramPacket(longTampon, longTampon.length);
            multicastSocket.receive(paquet);
            long valeurRecue = 0;
            byte[] byteRecu = paquet.getData();
            for (int i = 0; i < byteRecu.length; i++)
            {
                valeurRecue = (valeurRecue << 8) + (byteRecu[i] & 0xff);
            }
            System.out.println("Valeur recue : " + valeurRecue);

            // Calculate new decalage
            decalage = System.currentTimeMillis() - valeurRecue;

            // Send new decalage
            longTampon = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(decalage).array();
            InetAddress address = InetAddress.getByName("127.0.0.1");
            DatagramPacket paquet2 = new DatagramPacket(longTampon, longTampon.length, address,  4444);
            pointToPointSocket.send(paquet2);
            System.out.println("Decalage envoye : " + decalage);

            // Receive new decalage
            DatagramPacket paquet3 = new DatagramPacket(longTampon, longTampon.length);
            multicastSocket.receive(paquet3);
            long valeurRecue2 = 0;
            byte[] byteRecu2 = paquet3.getData();
            for (int i = 0; i < byteRecu2.length; i++)
            {
                valeurRecue2 = (valeurRecue2 << 8) + (byteRecu2[i] & 0xff);
            }
            System.out.println("Valeur recue : " + valeurRecue2);
            decalage = valeurRecue2 - System.currentTimeMillis();
            System.out.println("Nouveau décalage : " + decalage);

            cnt++;
        }
        multicastSocket.leaveGroup(groupe);
        multicastSocket.close();
        pointToPointSocket.close();
    }
}