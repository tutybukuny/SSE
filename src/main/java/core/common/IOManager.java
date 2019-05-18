package core.common;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class IOManager {
    private FileInputStream fileInputStream = null;
    private ObjectInputStream objectInputStream = null;
    private FileOutputStream fileOutputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private String pathToFile;

    public IOManager(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void openInputStream() throws IOException {
        closeOutputStream();
        fileInputStream = new FileInputStream(pathToFile);
        objectInputStream = new ObjectInputStream(fileInputStream);
    }

    public void closeInputStream() throws IOException {
        if(objectInputStream != null)
            objectInputStream.close();
        if(fileInputStream != null)
            fileInputStream.close();
    }

    public Object readInput() throws IOException, ClassNotFoundException {
        if(fileInputStream == null || objectInputStream == null)
            throw new NullPointerException("you didnt open file to read!");
        return objectInputStream.readObject();
    }

    public void openOutputStream() throws IOException {
        closeInputStream();
        fileOutputStream = new FileOutputStream(pathToFile);
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
    }

    public void flushOutputStream() throws IOException {
        if(objectOutputStream != null)
            objectOutputStream.flush();
        if(fileOutputStream != null)
            fileOutputStream.flush();
    }

    public void closeOutputStream() throws IOException {
        if(objectOutputStream != null)
            objectOutputStream.close();
        if(fileOutputStream != null)
            fileOutputStream.close();
    }

    public void writeOutput(Object object) throws IOException {
        if(fileOutputStream == null || objectOutputStream == null)
            throw new NullPointerException("you didnt open file to write!");
        objectOutputStream.writeObject(object);
    }

    public ArrayList<String> readLines(String pathToFile) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<>();
        Scanner inp = new Scanner(new File(pathToFile));
        while (inp.hasNext()) {
            lines.add(inp.nextLine());
        }

        return lines;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
}
