Recommendation System using Collaborative filtering 

Using the movie lens data set, I have tried to make a recommendation system using Collaborative filtering (K nearest neighbours using Pearson correlation coefficient). 

I couldn't check if the predictions given by my model are correct because of less knowledge about English old movies. So I
myself wrote a small list of Hindi movies and generated a data of Movie and user mapping using movie genre and user type.
Example - If a user likes romantic movies, then it rates a romantic genre movie with 3-5, otherwise 1 or 2. Generated the 
dataset through dataGeneration.java and used it in RecommendationSystem.java . The code working on my made custom dataset has
been commented.

The present code works with movie lens dataset and recommends amazing recommended movies! :)