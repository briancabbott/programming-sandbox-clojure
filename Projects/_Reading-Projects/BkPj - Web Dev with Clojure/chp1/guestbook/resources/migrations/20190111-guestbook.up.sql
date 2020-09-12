-- Primary Guestbook Table.
CREATE TABLE guestbook (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30),
    message VARCHAR(200),
    timestamp TIMESTAMP
);



become familiar with the application logic and interface. 
You should log in as admin, create account and first user, then login with this user, 
create rtemplate (), ctemplate (), qtemplate (), program, add a patient, assign a program to him, 
finish this program within NomiHealth Mobile, evaluate his answers as a doctor. 

Also look at how our API works, graphql libraries on the server side and on the client side. 
After this you should be able to re-use existing code to create something new, not inventing 
the wheel.


program
    - program of theraputic units
       series of sessions or steps that a patient goes through with a given purpose
          - post surgery for example 
             / for example, check in with a doctor every 

            program:
                collection of program-steps
                    q-template -- question template
                    time series
            session - instance of program-step

            question-template
                - series of questions
                - text component
                - answer-type
                    - answer on video
                    - multiple choice
                    - multi-select 
                    - free-text-response
                    - misc.
                        - preparation length
                        - text-length
                        - answer-length (video)


Create a set of canned programs