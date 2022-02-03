package com.bubblebot.main;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;

/**Bubble Bot
 * A discord bot made without Discord4J or Orianna
 * To learn more about APIs/REST/HTTP(S) requests
 *
 * Initializes the bot, commands, and listeners to handle functionality
 *
 * Discord4J and Orianna use given args, where
 * - arg[0] is the Discord bot token
 * - arg[1] is the Discord server ID
 * - arg[2] is the Riot API key
 *
 * References:
 * HttpsUrlConnection example: https://mkyong.com/java/java-https-client-httpsurlconnection-example/
 * HTTP Crash Course & Exploration: https://youtu.be/iYM2zFP3Zn0
 */
public class Bot {
  public static String
    botToken = null,
    guildId = null,
    riotAPIKey = null;

  public static void main(final String[] args) {
    assert(args.length == 3);
    botToken = args[0];
    guildId = args[1];
    riotAPIKey = args[2];

//    final String httpsUrl = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/fatalelement";
    final String httpsUrl = "https://www.google.com/";
    final URL url;

    try {
      url = new URL(httpsUrl);

      final HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

      printHttpsCertificate(con);

      printConnectionContent(con);
    } catch(final MalformedURLException e) {
      e.printStackTrace();
    } catch(final IOException e) {
      e.printStackTrace();
    }
  }

  public static void printHttpsCertificate(final HttpsURLConnection con) {
    assert con != null;

    try {
      System.out.println("CERTIFICATE");
      System.out.printf(" Response Code: %d%n", con.getResponseCode());
      System.out.printf(" Cipher Suite: %s%n", con.getCipherSuite());

      final Certificate[] certs = con.getServerCertificates();
      for(final Certificate cert : certs) {
        System.out.printf(" - Cert Type: %s%n", cert.getType());
        System.out.printf("   Cert Hash Code: %d%n", cert.hashCode());
        System.out.printf("   Cert Public Key Algorithm: %s%n", cert.getPublicKey().getAlgorithm());
        System.out.printf("   Cert Public Key Format: %s%n", cert.getPublicKey().getFormat());
        System.out.println();
      }
    } catch(SSLPeerUnverifiedException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  public static void printConnectionContent(final URLConnection con) {
    assert con != null;

    try {
      System.out.println("CONTENT OF URL");

      final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

      String content;

      while((content = reader.readLine()) != null) {
        System.out.printf(" %s%n", content);
      }

      reader.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}
