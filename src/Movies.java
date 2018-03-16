import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class Movies{
    /*
     * @param Movie data file name
     * @return The id of movies and their name   
     */
    static Map<Integer, String> getData(String fileName){
        Map<Integer, String> movies = new HashMap<>();

        try{
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){
                String[] splitStrs = line.split("\\|");
                Integer movieId = Integer.parseInt(splitStrs[0]);
                String movieName = splitStrs[1];
                movies.put(movieId, movieName);
                line = br.readLine();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
        return movies;
    } 
}