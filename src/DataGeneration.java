import java.util.*;
import java.lang.*;
import java.sql.Time;

class data{
    String[] movies = {
                        "ddlj","dilwaale","dabang","singham","wanted","barfi","hase to fase","lootera","baby","satya",
                        "ek tha tigr","krish","jab we met","love story","don","body guard","kl ho na ho","veer zraa","maine pyar","don2",
                        "ashqui2","hum tum","dhoom","love ajj kl","rowdy rathore","bang bang","dhoom2","silsila","dhoom 3","ashqui",
                        "gangs of wasepur","2 states","jthj","omkara","kkkg","avenger","chlte chlte","shivaay","bajirao","dark knight",
                        "border","matrix","tere naam","rasleela","khiladi","baby","dil se","gadar","hostage","ranjhana"
                      };
    char[] movieGenre = {
                            'R','R','R','A','A','R','R','R','A','A',
                            'A','A','R','R','A','A','R','R','R','A',
                            'R','R','A','R','A','A','A','R','A','R',
                            'A','R','R','A','R','A','R','A','R','A',
                            'A','A','R','R','A','R','R','A','A','R'
                        };                  

    char[] userGenre =  {
                            'R','A','A','R','A','A','A','A','R','R',
                            'R','R','A','A','R','A','R','A','R','A',
                            'R','R','R','R','A','A','A','A','R','R',
                            'A','R','A','R','A','R','R','A','A','R'
                        };

    int numMovies = 50;
    int numUsers = 40;
    int flag = 1;

    int[][] movieUserMap = new int[numMovies][numUsers];
    
    Random r = new Random();
    
    int[][] generateData(){
        long startTime = System.currentTimeMillis();
        for(int i=0;i<numMovies;i++){
            for(int j=0;j<numUsers;j++){
                // if(System.currentTimeMillis() > startTime + 1)
                // {
                //     startTime = System.currentTimeMillis();
                //     flag = ((flag == 0)?1:0);
                // }
                // if(flag == 1)
                movieUserMap[i][j] = (movieGenre[i] == userGenre[j]?5-r.nextInt(3):r.nextInt(3));
                if(movieUserMap[i][j] == 0)
                {
                    movieUserMap[i][j] = (movieGenre[i] == userGenre[j]?5-r.nextInt(3):r.nextInt(3));
                    if(movieUserMap[i][j] == 0)
                    {
                        movieUserMap[i][j] = (movieGenre[i] == userGenre[j]?5-r.nextInt(3):r.nextInt(3));
                    }
                }    
            }
        }
        return movieUserMap;
    }

    
}