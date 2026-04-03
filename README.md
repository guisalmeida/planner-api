## Planner API
Planner is an application to help users planning their trips by inviting others, creating activities, saving links and having fun. 

## Todo (WIP)
- ### Add unit tests where it is possible
- ### Add validation for date fields
  - Ensure the trip start date is earlier than the trip end date.
    - Ensure activity dates fall between the trip start and end dates.  
      > **Example:**  
       Trip between July 20th and 25th in Rio de Janeiro:
      >  - ~~Activity on July 19th~~ (invalid)
      >  - Activity on July 21st (valid)
- ### Extract trip core logic into a Service class
- ### Map application exceptions
  - With personalized error handling 
- ### Migrate to postgres