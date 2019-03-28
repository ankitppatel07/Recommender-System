# Recommender-System

- Implemented a program that takes user preference rating data (an incomplete matrix) and predicts the rating for products that have not been rated.
- Analyzed 80,000 records and used the Pearson coefficient of correlation to measure the similarity between users based on product ratings as it reduced the execution time improved the recommendation quality.

To compile the code type: make

To run the code type: java dm_project

- The file train_all_txt.txt contains User_Id, Product_Id, Rating pairs. 
- Not all the products have been rated.
- The program predicts missing rating values for the whole train_all_txt.txt
