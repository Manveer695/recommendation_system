import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class Users{
    /*
     * @param Movie data file name
     * @return The id of movies and their name   
     */
    static Map<Integer, Map<Integer, Integer>> getData(String fileName){
        Map<Integer, Map<Integer, Integer>> users = new HashMap<>();

        try{
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){
                String[] splitStrs = line.split("\t");
                Integer userId = Integer.parseInt(splitStrs[0]);
                Integer movieId = Integer.parseInt(splitStrs[1]);
                Integer rating = Integer.parseInt(splitStrs[2]);
                
                if(users.containsKey(userId)){
                    users.get(userId).put(movieId, rating);
                }
                else{
                    Map<Integer, Integer> movieRating = new HashMap<>();
                    movieRating.put(movieId, rating);
                    users.put(userId, movieRating); 
                }
                line = br.readLine();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return users;
    } 
}