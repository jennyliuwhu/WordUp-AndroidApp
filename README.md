This is an Android App for helping people to learn English words. 

We use wordsapi (https://www.wordsapi.com/) as first time search. Then we store the word in our own MySQL database. 

With the database on the server side, the client communicate with the server using restful service.

To run this project, here is the instruction:

1. Import code/server/WordUp to your eclipse. Modify the PASSWORD in org.wordup.DBConnectionHandler to your own MySQL root password. Besides, modify the key in org.wordup.note to your own WordsApi access key. 

2. Import code/client/WordUp_V11 to your android stutio. Modify the ip in com.example.jialingliu.wordup.server.App to your own computer's private ip address. 

3. Run command "mysql -u root -p<PASSWORD> < wordup.sql"

4. Start the server and client respectively. 

5. Follow the instructions on our WordUp App screen and test output.

Enjoy your journey with WordUp. :)