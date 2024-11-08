package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
//Upload Image code in Separate Service class
//We have all the code working with files here.
public class FileServiceImpl implements FileService
{
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException
    {

        //1. File name of current/ original file
        //This will give exact file name along with extension
        String originalFilename = file.getOriginalFilename();


        //2.Generate a Unique File name using UUID Class.
        String randomId = UUID.randomUUID().toString();
        //Here we are getting substring of original file name until it's last index.
        //Eg: File Name is --- mat.jpg-->1234(RandomId) ---> 1234.jpg(New File Name)
        //We are appending the random id.
        String fileName = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf('.')));
        //Path Seperator is / we also a chance to try this --> path+"/"+filePath it won't work all operating systems.
        //But using pathSeparator we will get a ; in the image path in the images folder so use separator instead to avoid this pblm.
        String filePath = path + File.separator + fileName;

        //3. Check if path exist and create.
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdir();

        //4. Upload to Server
        //file convert into the InputStream and Paths class and get the new file Path.
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //5.Return the file Name
        return fileName;
    }

}
