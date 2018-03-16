import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class RecommendationSystem{

    public static void main(String[] args) {
        Map<Integer, String> movies = Movies.getData("..\\movielens dataset\\u.item");
        Map<Integer, Map<Integer, Integer>> userRatings = Users.getData("..\\movielens dataset\\u.data");
        
        /** 
         * For my dataset
        Map<Integer, String> movies = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> userRatings = new HashMap<>();
        
        data d = new data();
        String[] moviesData = d.movies;
        int cntr = 1;
        for(String str: moviesData){
            movies.put(cntr,str);
            cntr++;
        }*/

        /** 
         * To check if recommendation is working good in my dataset
        char[] moviesData = d.movieGenre;
        int cntr = 1;
        for(char str: moviesData){
            movies.put(cntr,str+"");
            cntr++;
        }
        */
        /**
         * My dataset
        int[][] userRatingArr = new int[d.numMovies][d.numUsers];
        userRatingArr = d.generateData();

        for(int i=0;i<d.numUsers;i++){
            Map<Integer, Integer> usrMovies = new HashMap<>();
            for(int j=0;j<d.numMovies;j++){
                usrMovies.put(j+1,userRatingArr[j][i]);
            }
            userRatings.put(i+1,usrMovies);
        }
        */

        Iterator itr = movies.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry movie = (Map.Entry) itr.next();
            System.out.println(movie.getValue());
        }
        System.out.println(movies.size()+" "+userRatings.size());
        /*
        itr = userRatings.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry user = (Map.Entry) itr.next();
            System.out.println("U - "+user.getKey());
            Map<Integer, Integer> userMovies = (HashMap) user.getValue();
            Iterator itr2 = userMovies.entrySet().iterator();
            while(itr2.hasNext()){
                Map.Entry movie = (Map.Entry) itr2.next();
                System.out.println(movie.getValue());
            }
        }
        */

        int NUM_NEWUSERRATEDMOVIES = 10;
        int NUM_RECOMMENDEDMOVIES = 5;

        Map<Integer, Integer> newUserRatedMovies = new HashMap<>();

        Random r = new Random();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        for(int i=0;i<NUM_NEWUSERRATEDMOVIES;i++){
            int movieId, rating = 0;
            do{
                movieId = r.nextInt(movies.size());
            }while(newUserRatedMovies.containsKey(movieId));
            System.out.println("Please rate "+movies.get(movieId)+" in a scale of 1-5");
            do{
                rating = 1+r.nextInt(5);
                System.out.println(rating);
            }while(rating < 1 || rating > 5);
            newUserRatedMovies.put(movieId, rating);
        }

        int K = 10; 
        Map<Integer, Double> similarUsers = getSimilarUsers(newUserRatedMovies,K,movies,userRatings);
        Map<Integer, Double> recommendedMovies = getRecommendations(similarUsers, newUserRatedMovies, movies, userRatings);

        System.out.println("Recommended movies: ");
        int cnt = 0;

        Iterator itrRecomm = recommendedMovies.entrySet().iterator();
        while(itrRecomm.hasNext() && cnt<NUM_RECOMMENDEDMOVIES){
            Map.Entry movie = (Map.Entry) itrRecomm.next();
            System.out.println(movies.get(movie.getKey()) + " - " + movie.getValue());
            cnt++;
        }
    }

    /**
     * Get predictions of each movie by a user giving some ratings and its neighbourhood:
     *   r(u,i) = r(u) + sum(sim(u,v) * (r(v,i) - r(v))) / sum(abs(sim(u,v)))
     *     sim(u,v): similarity between u and v users
     *     r(u,i): rating of the movie i by the user u
     *     r(u): average rating of the user u
     * @param userRatings ratings of the user
     * @param neighbourhoods nearest neighbourhoods
     * @param movies movies in the database
     * @return predictions for each movie
     */
    static Map<Integer, Double> getRecommendations(Map<Integer, Double> similarUsers, Map<Integer, Integer> ratedMovies, 
                                                    Map<Integer, String> movies, Map<Integer, Map<Integer, Integer>> userRatings)
    {
        Map<Integer, Double> recommendations = new HashMap<>();
        Map<Integer, Double> sortedRecommendations;

        double newUserAvg = getAverage(ratedMovies);
        double num = 0, den = 0;

        for(int movieId : movies.keySet()){
            if(!ratedMovies.containsKey(movieId)){
                for(int userId: similarUsers.keySet()){
                    Map<Integer, Integer> userMovies = userRatings.get(userId);
                    double userRatingAvg = getAverage(userMovies);

                    if(userMovies.containsKey(movieId)){
                        double sim = similarUsers.get(userId);
                        num += (sim*(userMovies.get(movieId)-userRatingAvg)); 
                        den += Math.abs(sim);         
                    }
                }
                
                double predictedRating = 0;
                if(den > 0){
                    predictedRating = newUserAvg + num/den;
                    if(predictedRating > 5)
                        predictedRating = 5;
                }
                recommendations.put(movieId, predictedRating);
            }
        }

        sortedRecommendations = MapUtil.sortByValue(recommendations);
        return sortedRecommendations;
    }

    /**
     * Getting similar users to the new user using pearson correlation coefficient
     * formula = num / den , where: 
     * num = sum((r(n,m)-avgR(n))*(r(g,m)-avgR(g)))
     * den = sqrt(sum((r(n,m)-avgR(n))^2))*sqrt(sum((r(g,m)-avgR(g))^2))
     * r(n,m) is the rating by new user to movie m
     * r(g,m) is the rating by given user to movie m 
     * avgR(n) is the average rating by new user to movies
     * avgR(g) is the average rating by given user to movies
     * @param ratedMovies new user rated movies
     * @param k k-nearest neighbours
     * @param movies 
     * @param userRatings user ratings to movies
     * @return similar users sorted in descending order on the basis of similarity 
     */
    static Map<Integer, Double> getSimilarUsers(Map<Integer, Integer> ratedMovies, int k, Map<Integer, String> movies, 
                                                Map<Integer, Map<Integer, Integer>> userRatings)
    {
        double newUserAvgRating = getAverage(ratedMovies);
        Map<Integer, Double> similarUsers = new HashMap<>();
        Map<Integer, Double> sortedSimilarUsers, kSortedSimilarUsers = new HashMap<>();

        Iterator itr = userRatings.entrySet().iterator(); 
        while(itr.hasNext()){
            Map.Entry user = (Map.Entry) itr.next();
            int userId = (int) user.getKey();
            double similarity, num = 0, den1 = 0, den2 = 0;
            Map<Integer, Integer> userMovies = (HashMap) user.getValue();
            double userAvgRating = getAverage(userMovies);

            Iterator itr2 = userMovies.entrySet().iterator();
            while(itr2.hasNext()){
                Map.Entry movie = (Map.Entry) itr2.next();
                int movieId = (int) movie.getKey();
                if(ratedMovies.containsKey(movieId)){
                    double n = ratedMovies.get(movieId) - newUserAvgRating;
                    double g = userMovies.get(movieId) - userAvgRating;
                    
                    num += n*g;
                    den1 += n*n;
                    den2 += g*g;
                }
            }
            if(den1 == 0 || den2 == 0)
                similarity = 0;
            else
                similarity = num/(Math.sqrt(den1)+Math.sqrt(den2));
            
            similarUsers.put(userId, similarity);
        }   
        
        sortedSimilarUsers = MapUtil.sortByValue(similarUsers);
        
        int cnt = 0;
        Iterator temp = sortedSimilarUsers.entrySet().iterator();
        while(temp.hasNext() && cnt<k){
            Map.Entry user = (Map.Entry) temp.next();
            kSortedSimilarUsers.put((Integer)user.getKey(), (Double)user.getValue());
            cnt++;
        }

        return kSortedSimilarUsers;
    }

    /**
     * Average user rating to movies
     * @param movies user rated movies
     * @return average user rating
     */
    static double getAverage(Map<Integer, Integer> movies){
        double avg = 0;
        Iterator itr = movies.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry movie = (Map.Entry) itr.next();
            avg += (double) ((int)movie.getValue());
        }
        avg = avg/movies.size();
        return avg;
    }
}

class MapUtil {
    /**
     * 
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}