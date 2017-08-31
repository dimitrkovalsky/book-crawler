package com.liberty.book.crawler.service;

import com.liberty.book.crawler.common.LivelibRequestHelper;
import com.liberty.book.crawler.repository.AuthorFaceLivelibRepository;
import com.liberty.book.crawler.repository.AuthorImageRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 30.08.2017.
 */
@Component
public class ImageService {


    private Set<String> downloadedImagesHash = new HashSet<>();
    private static String basePath = "D:\\flibusta\\img\\temp";

    public byte[] loadFile(String url){
        InputStream stream = LivelibRequestHelper.executeRequest(url);
        byte[] array;
        try {
            array = IOUtils.toByteArray(stream);
            return array;
        }catch (IOException e){
            System.out.println(e);
        }
        return new byte[0];
    }

    private String calculateHash(byte[] bytes){
        try{
        System.out.println("Start MD5 Digest");
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        byte[] hash = md.digest();
        return returnHex(hash);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    static String returnHex(byte[] inBytes) throws Exception {
        String hexString = "";
        for (int i=0; i < inBytes.length; i++) { //for loop ID:1
            hexString +=
                    Integer.toString( ( inBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
        }                                   // Belongs to for loop ID:1
        return hexString;
    }

    public static void main(String[] args) {

    }

    public String loadAndSaveImage(String url,String id){
        url = url.replaceAll("[ \n\r]","");
        System.out.println("Loading "+ url);
        URI uri = URI.create(url);
        String [] path = uri.getPath().split("/");
        String filename = path[path.length-1];
        byte[] image = loadFile(url);
        String hash = calculateHash(image);
        String inBaseLink = "\\100\\"+id+"\\"+filename;
        if(downloadedImagesHash.contains(hash)){
            System.out.println("Picture with hash "+hash+" already saved");
            return null;
        }else {

            saveFileToDisk(basePath+"\\authors"+inBaseLink,image);
            downloadedImagesHash.add(hash);
            return inBaseLink.replaceAll("\\\\","/");
        }
    }

    public void saveFileToDisk(String filePath, byte[] bytes){

        try {
            Path targetPath = new File(filePath).toPath();
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.write(targetPath, bytes, StandardOpenOption.CREATE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
